package com.youdevise.fbplugins.deprecate3rdparty;

import static edu.umd.cs.findbugs.classfile.DescriptorFactory.createClassDescriptorFromDottedClassName;

import java.util.ArrayList;
import java.util.List;

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
	private final List<ClassDescriptor> deprecatedClasses;

	public Deprecated3rdPartyDetector(Detector thisPluginDetector, BugReporter bugReporter, DeprecatedSettings settings) {
		this.thisPluginDetector = thisPluginDetector;
		this.bugReporter = bugReporter;
		this.deprecatedClasses = deprecatedClassesFrom(settings);
	}

	private List<ClassDescriptor> deprecatedClassesFrom(DeprecatedSettings settings) {
		List<ClassDescriptor> descriptors = new ArrayList<ClassDescriptor>();
		for (String deprecatedClass : settings.deprecatedClasses()) {
			ClassDescriptor descriptor = createClassDescriptorFromDottedClassName(deprecatedClass);
			descriptors.add(descriptor);
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
		for (ClassDescriptor deprecatedClass : deprecatedClasses) {
			if (poolEntry.contains(deprecatedClass.getClassName())) {
				BugInstance bugInstance = new BugInstance(thisPluginDetector, "DEPRECATED_3RD_PARTY_CLASS", Priorities.HIGH_PRIORITY)
										  	      .addClass(analyzedClassDescriptor);
				bugReporter.reportBug(bugInstance);
			}
		}
	}
}
