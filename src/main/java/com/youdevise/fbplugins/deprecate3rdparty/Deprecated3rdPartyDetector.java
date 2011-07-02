package com.youdevise.fbplugins.deprecate3rdparty;

import static edu.umd.cs.findbugs.classfile.DescriptorFactory.createClassDescriptorFromDottedClassName;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.generic.ConstantPoolGen;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.Priorities;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;

public class Deprecated3rdPartyDetector implements Detector {

	private final Detector thisPluginDetector;
	private final BugReporter bugReporter;
	private final Map<ClassDescriptor, String> deprecatedClasses;

	public Deprecated3rdPartyDetector(Detector thisPluginDetector, BugReporter bugReporter, DeprecatedSettings settings) {
		this.thisPluginDetector = thisPluginDetector;
		this.bugReporter = bugReporter;
		this.deprecatedClasses = deprecatedClassesFrom(settings);
	}

	private Map<ClassDescriptor, String> deprecatedClassesFrom(DeprecatedSettings settings) {
		Map<ClassDescriptor, String> descriptors = new HashMap<ClassDescriptor, String>();
		for (Deprecation deprecatedClass : settings.deprecations()) {
			ClassDescriptor descriptor = createClassDescriptorFromDottedClassName(deprecatedClass.dottedClassName);
			descriptors.put(descriptor, deprecatedClass.reason);
		}
		return descriptors;
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
			String poolEntry = constant.toString();
			reportBugIfDeprecatedTypeIsReferenced(classDescriptor, poolEntry);
		}
	}

	private void reportBugIfDeprecatedTypeIsReferenced(ClassDescriptor analyzedClassDescriptor, String poolEntry) {
		for (Entry<ClassDescriptor, String> deprecatedClass : deprecatedClasses.entrySet()) {
			String className = deprecatedClass.getKey().getClassName();
			String reason = deprecatedClass.getValue();
			if (poolEntry.contains(className)) {
				BugInstance bugInstance = new BugInstance(thisPluginDetector, "DEPRECATED_3RD_PARTY_CLASS", Priorities.HIGH_PRIORITY)
										  	      .addClass(analyzedClassDescriptor)
											      .add(new DeprecationAnnotation(deprecatedClass.getKey().getDottedClassName(), reason));
				bugReporter.reportBug(bugInstance);
			}
		}
	}
}
