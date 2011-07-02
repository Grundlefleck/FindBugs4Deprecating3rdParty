package com.youdevise.fbplugins.deprecate3rdparty;

public class Deprecation {

	public final String dottedClassName;
	public final String reason;

	public Deprecation(String dottedClassName, String reason) {
		this.dottedClassName = dottedClassName;
		this.reason = reason;
	}

	public static Deprecation of(String dottedClassName, String reason) {
		return new Deprecation(dottedClassName, reason);
	}
	
}
