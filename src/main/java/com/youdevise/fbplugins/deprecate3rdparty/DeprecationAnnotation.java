package com.youdevise.fbplugins.deprecate3rdparty;

import edu.umd.cs.findbugs.ClassAnnotation;

public class DeprecationAnnotation extends ClassAnnotation {

	private final String reason;

	public DeprecationAnnotation(String className, String reason) {
		super(className);
		this.reason = reason;
	}
	
	@Override
	protected String formatPackageMember(String key, ClassAnnotation primaryClass) {
		if (key.equals("reason")) {
			return reason;
		}
		return super.formatPackageMember(key, primaryClass);
	}

	@Override
	public boolean isSignificant() {
		return false;
	}
}
