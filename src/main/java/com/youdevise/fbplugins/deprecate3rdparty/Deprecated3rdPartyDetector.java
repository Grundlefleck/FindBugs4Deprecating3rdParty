package com.youdevise.fbplugins.deprecate3rdparty;

import java.util.ArrayList;
import java.util.List;

import org.apache.xalan.xsltc.compiler.util.Type;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.MethodAnnotation;
import edu.umd.cs.findbugs.Priorities;
import edu.umd.cs.findbugs.asm.FBClassReader;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.classfile.CheckedAnalysisException;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import edu.umd.cs.findbugs.classfile.DescriptorFactory;
import edu.umd.cs.findbugs.classfile.Global;
import edu.umd.cs.findbugs.classfile.MethodDescriptor;

public class Deprecated3rdPartyDetector implements Detector {

	private final Detector thisPluginDetector;
	private final BugReporter bugReporter;
	private final DeprecatedSettings settings;

	public Deprecated3rdPartyDetector(Detector thisPluginDetector, BugReporter bugReporter, DeprecatedSettings settings) {
		this.thisPluginDetector = thisPluginDetector;
		this.bugReporter = bugReporter;
		this.settings = settings;
	}

	@Override
	public void report() {
	}

	@Override
	public void visitClassContext(ClassContext classContext) {
		List<String> deprecatedClasses = settings.deprecatedClasses();

		ClassDescriptor classDescriptor = classContext.getClassDescriptor();

		FBClassReader reader = null;
		DeprecatedClassVisitor deprecatedClassVisitor = new DeprecatedClassVisitor(deprecatedClasses, classDescriptor);
		try {
			reader = Global.getAnalysisCache().getClassAnalysis(FBClassReader.class, classDescriptor);
			reader.accept(deprecatedClassVisitor, 0);
		} catch (CheckedAnalysisException e) {
			e.printStackTrace();
		}

		for (BugInstance bug : deprecatedClassVisitor.deprecatedUsageBugs()) {
			bugReporter.reportBug(bug);
		}
	}

	private class DeprecatedClassVisitor implements ClassVisitor, FieldVisitor, MethodVisitor {

		private final List<String> deprecatedClasses;
		private List<BugInstance> deprecatedUsageBugs = new ArrayList<BugInstance>();
		private final ClassDescriptor classToAnalyseDescriptor;
		private MethodDescriptor currentMethodDescriptor;

		public DeprecatedClassVisitor(List<String> deprecatedClasses, ClassDescriptor classToAnalyseDescriptor) {
			this.deprecatedClasses = deprecatedClasses;
			this.classToAnalyseDescriptor = classToAnalyseDescriptor;
		}

		public Iterable<BugInstance> deprecatedUsageBugs() {
			return deprecatedUsageBugs;
		}

		@Override 
		public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) { 
			ClassDescriptor superclassDescriptor = DescriptorFactory.createClassDescriptor(superName);
			if (deprecatedClasses.contains(superclassDescriptor.getDottedClassName())) {
				BugInstance extendsDeprecatedClass = new BugInstance(thisPluginDetector, "DEPRECATED_3RD_PARTY_CLASS", Priorities.HIGH_PRIORITY)
														 	.addClass(classToAnalyseDescriptor);
				deprecatedUsageBugs.add(extendsDeprecatedClass);
			}
			
			for (String interfaceName : interfaces) {
				ClassDescriptor interfaceDescripter = DescriptorFactory.createClassDescriptor(interfaceName);
				if (deprecatedClasses.contains(interfaceDescripter.getDottedClassName())) {
					BugInstance extendsDeprecatedClass = new BugInstance(thisPluginDetector, "DEPRECATED_3RD_PARTY_CLASS", Priorities.HIGH_PRIORITY)
															 	.addClass(classToAnalyseDescriptor);
					deprecatedUsageBugs.add(extendsDeprecatedClass);
				}
			}
		}
		
		@Override
		public FieldVisitor visitField(int access, String fieldName, String desc, String signature, Object value) {
			String typeOfFieldDottedClassName = desc.substring(1).replace("/", ".").replace(";", "");
			if (deprecatedClasses.contains(typeOfFieldDottedClassName)) {
				BugInstance hasDeprecatedField = new BugInstance(thisPluginDetector, "DEPRECATED_3RD_PARTY_CLASS", Priorities.HIGH_PRIORITY);
				hasDeprecatedField.addClass(classToAnalyseDescriptor);
				hasDeprecatedField.addField(classToAnalyseDescriptor.getDottedClassName(), fieldName, desc, (access & Type.ACC_STATIC) == 0);
				deprecatedUsageBugs.add(hasDeprecatedField);

			}
			return this;
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
			currentMethodDescriptor = new MethodDescriptor(classToAnalyseDescriptor.getClassName(), 
					name, desc,
					(access & Type.ACC_STATIC) != 0);

			return this;
		}

		@Override
		public void visitLineNumber(int lineNumber, Label label) {
			System.out.printf("[%d]%n", lineNumber);
		}

		@Override
		public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
			ClassDescriptor classDescriptor = DescriptorFactory.createClassDescriptorFromSignature(desc);
			if (deprecatedClasses.contains(classDescriptor.getDottedClassName())) {
				BugInstance hasDeprecatedLocalVariable = new BugInstance(thisPluginDetector, "DEPRECATED_3RD_PARTY_CLASS",
						Priorities.HIGH_PRIORITY);
				hasDeprecatedLocalVariable.addClass(classToAnalyseDescriptor);
				hasDeprecatedLocalVariable.addMethod((MethodAnnotation) MethodAnnotation.fromMethodDescriptor(currentMethodDescriptor));
				deprecatedUsageBugs.add(hasDeprecatedLocalVariable);
			}
		}

		@Override
		public AnnotationVisitor visitAnnotationDefault() {
			return null;
		}

		@Override public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
			return null;
		}

		@Override public void visitCode() { }
		
		@Override public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) { }

		@Override public void visitInsn(int opcode) { }

		@Override public void visitIntInsn(int opcode, int operand) { }

		@Override public void visitVarInsn(int opcode, int var) { }

		@Override public void visitTypeInsn(int opcode, String type) { }

		@Override public void visitFieldInsn(int opcode, String owner, String name, String desc) { }

		@Override public void visitMethodInsn(int opcode, String owner, String name, String desc) { }

		@Override public void visitJumpInsn(int opcode, Label label) { }

		@Override public void visitLabel(Label label) { }

		@Override public void visitLdcInsn(Object cst) { }

		@Override public void visitIincInsn(int var, int increment) { }

		@Override public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels) { }

		@Override public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) { }

		@Override public void visitMultiANewArrayInsn(String desc, int dims) { }

		@Override public void visitTryCatchBlock(Label start, Label end, Label handler, String type) { }

		@Override public void visitMaxs(int maxStack, int maxLocals) { }

		@Override public void visitSource(String source, String debug) { }

		@Override public void visitOuterClass(String owner, String name, String desc) { }

		@Override public AnnotationVisitor visitAnnotation(String desc, boolean visible) { return null; }

		@Override public void visitAttribute(Attribute attr) { }

		@Override public void visitInnerClass(String name, String outerName, String innerName, int access) { }

		@Override public void visitEnd() { }

	}

}
