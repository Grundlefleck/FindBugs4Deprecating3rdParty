package com.youdevise.fbplugins.deprecate3rdparty;

import static edu.umd.cs.findbugs.classfile.DescriptorFactory.createClassDescriptorFromDottedClassName;

import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantCP;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantInterfaceMethodref;
import org.apache.bcel.classfile.ConstantMethodref;
import org.apache.bcel.classfile.ConstantNameAndType;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.generic.ConstantPoolGen;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.Priorities;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import edu.umd.cs.findbugs.classfile.DescriptorFactory;
import edu.umd.cs.findbugs.visitclass.PreorderVisitor;

public class Deprecated3rdPartyDetector implements Detector {

	private final Detector thisPluginDetector;
	private final BugReporter bugReporter;
	private final Iterable<Deprecation> deprecatedClasses;

	public Deprecated3rdPartyDetector(Detector thisPluginDetector, BugReporter bugReporter, DeprecatedSettings settings) {
		this.thisPluginDetector = thisPluginDetector;
		this.bugReporter = bugReporter;
		this.deprecatedClasses = settings.deprecations();
	}

	@Override
	public void report() { }

	@Override
	public void visitClassContext(ClassContext classContext) {
		ClassDescriptor classDescriptor = classContext.getClassDescriptor();
		
		ConstantPoolGen constantPoolGen = classContext.getConstantPoolGen();
		ConstantPool finalConstantPool = constantPoolGen.getFinalConstantPool();
		
		int poolSize = constantPoolGen.getSize();
		for (int i = 1; i < poolSize; i++) {
			Constant constant = finalConstantPool.getConstant(i);
			
			if (constant == null) continue;
			
			String poolEntry = constant.toString();
			reportBugIfDeprecatedTypeIsReferenced(classDescriptor, poolEntry, finalConstantPool);
		}
	}

	private void reportBugIfDeprecatedTypeIsReferenced(ClassDescriptor analyzedClassDescriptor, String poolEntry, ConstantPool finalConstantPool) {
		for (Deprecation deprecation : deprecatedClasses) {
			String reason = deprecation.reason;
			
			ClassDescriptor descriptor = deprecatedClassDescriptorFrom(deprecation);
			
			if (deprecation.getClass().equals(Deprecation.class)) {
    			if (poolEntry.contains(descriptor.getClassName())) {
    				BugInstance bugInstance = new BugInstance(thisPluginDetector, "DEPRECATED_3RD_PARTY_CLASS", Priorities.HIGH_PRIORITY)
    										  	      .addClass(analyzedClassDescriptor)
    											      .add(new DeprecationAnnotation(deprecation.dottedClassName, reason));
    				bugReporter.reportBug(bugInstance);
    			}
			} else if (deprecation instanceof MethodDeprecation) {
			    MethodDeprecation methodDeprecation = (MethodDeprecation) deprecation;
			    detectInvocationOfDeprecatedMethod(analyzedClassDescriptor, methodDeprecation, finalConstantPool);
			}
		}
	}
	

	private void detectInvocationOfDeprecatedMethod(ClassDescriptor analyzedClassDescriptor, MethodDeprecation methodDeprecation, ConstantPool finalConstantPool) {
	    finalConstantPool.accept(deprecatedMethodInvocationVisitor(analyzedClassDescriptor, methodDeprecation, finalConstantPool));
    }

    private PreorderVisitor deprecatedMethodInvocationVisitor(final ClassDescriptor analyzedClassDescriptor,
                                                              final MethodDeprecation methodDeprecation, 
                                                              final ConstantPool finalConstantPool) {
        return new PreorderVisitor() {
	        @Override
	        public void visit(ConstantMethodref obj) {
	            super.visit(obj);
	            reportIfDeprecated(analyzedClassDescriptor, methodDeprecation, finalConstantPool, obj);
	        }
	        
	           
            @Override
            public void visitConstantInterfaceMethodref(ConstantInterfaceMethodref obj) {
                super.visitConstantInterfaceMethodref(obj);
                
                reportIfDeprecated(analyzedClassDescriptor, methodDeprecation, finalConstantPool, obj);
            }

            private void reportIfDeprecated(ClassDescriptor analyzedClassDescriptor, 
                                            MethodDeprecation methodDeprecation,
                                            ConstantPool finalConstantPool, 
                                            ConstantCP obj) {
                if (isDeprecatedMethodReferenced(methodDeprecation, finalConstantPool, obj)) {
	                reportDeprecatedMethodBug(analyzedClassDescriptor, methodDeprecation);
	            }
            }


            private boolean isDeprecatedMethodReferenced(MethodDeprecation methodDeprecation, 
                                                         ConstantPool finalConstantPool,
                                                         ConstantCP obj) {
                
                ConstantClass constantClass = (ConstantClass) finalConstantPool.getConstant(obj.getClassIndex());
	            String constantClassName =  constantClass.getBytes(finalConstantPool);
	            ConstantNameAndType constantNameAndType = (ConstantNameAndType) finalConstantPool.getConstant(obj.getNameAndTypeIndex());
	            String name = constantNameAndType.getName(finalConstantPool);

	            ClassDescriptor classDescriptor = DescriptorFactory.createClassDescriptor(constantClassName);
	            boolean deprecatedMethodIsReferenced = classDescriptor.getDottedClassName().equals(methodDeprecation.dottedClassName)
	                    && name.equals(methodDeprecation.methodName);
                return deprecatedMethodIsReferenced;
            }

            private void reportDeprecatedMethodBug(ClassDescriptor analyzedClassDescriptor, MethodDeprecation methodDeprecation) {
                BugInstance bugInstance = new BugInstance(thisPluginDetector, "DEPRECATED_3RD_PARTY_CLASS", Priorities.HIGH_PRIORITY)
                    .addClass(analyzedClassDescriptor)
                    .add(new DeprecationAnnotation(methodDeprecation.dottedClassName, methodDeprecation.reason));
                bugReporter.reportBug(bugInstance);
            }
	        
        };
    }

    private ClassDescriptor deprecatedClassDescriptorFrom(Deprecation deprecation) {
        return createClassDescriptorFromDottedClassName(deprecation.dottedClassName);
	}
}
