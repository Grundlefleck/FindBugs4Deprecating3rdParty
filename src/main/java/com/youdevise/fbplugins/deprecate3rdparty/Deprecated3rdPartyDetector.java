package com.youdevise.fbplugins.deprecate3rdparty;

import static edu.umd.cs.findbugs.classfile.DescriptorFactory.createClassDescriptorFromDottedClassName;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantInterfaceMethodref;
import org.apache.bcel.classfile.ConstantMethodref;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.Visitor;
import org.apache.bcel.generic.ConstantPoolGen;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.Priorities;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;
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
			String className = deprecation.dottedClassName;
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
//			    if (poolEntry.contains(methodDeprecation.dottedClassName)) {
//			        // need to look for pool entry containing 'Methodref' (may be InterfaceMethodref)
//			        // look up entry at class index to match class name 
//			        // and entry 
//			    }
			    detectInvocationOfDeprecatedMethod(methodDeprecation, finalConstantPool);
			}
		}
	}
	

	private void detectInvocationOfDeprecatedMethod(MethodDeprecation methodDeprecation, final ConstantPool finalConstantPool) {

	    Visitor visitor = new PreorderVisitor() {
	        @Override
	        public void visit(ConstantMethodref obj) {
	            super.visit(obj);
	            
	            Constant constantClass = finalConstantPool.getConstant(obj.getClassIndex());
	            Constant constantNameAndType = finalConstantPool.getConstant(obj.getNameAndTypeIndex());
	            Constant constantTag = finalConstantPool.getConstant(obj.getTag());
	        }
	        
	        @Override
	        public void visitConstantInterfaceMethodref(ConstantInterfaceMethodref obj) {
	            super.visitConstantInterfaceMethodref(obj);
	            Constant constantClass = finalConstantPool.getConstant(obj.getClassIndex());
                Constant constantNameAndType = finalConstantPool.getConstant(obj.getNameAndTypeIndex());
                Constant constantTag = finalConstantPool.getConstant(obj.getTag());
	        }
	        
        };
        finalConstantPool.accept(visitor);
    }

    private ClassDescriptor deprecatedClassDescriptorFrom(Deprecation deprecation) {
        return createClassDescriptorFromDottedClassName(deprecation.dottedClassName);
	}
    private Map<ClassDescriptor, String> deprecatedClassesFrom(DeprecatedSettings settings) {
        Map<ClassDescriptor, String> descriptors = new HashMap<ClassDescriptor, String>();
        for (Deprecation deprecatedClass : settings.deprecations()) {
            ClassDescriptor descriptor = createClassDescriptorFromDottedClassName(deprecatedClass.dottedClassName);
            descriptors.put(descriptor, deprecatedClass.reason);
        }
        return descriptors;
    }
}
