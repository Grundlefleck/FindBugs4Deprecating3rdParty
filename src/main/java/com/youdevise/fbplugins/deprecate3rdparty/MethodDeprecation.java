package com.youdevise.fbplugins.deprecate3rdparty;

public class MethodDeprecation extends Deprecation {

    public final String methodName;

    private MethodDeprecation(String dottedClassName, String methodName, String reason) {
        super(dottedClassName, reason);
        this.methodName = methodName;
    }

    public static Deprecation ofMethod(String className, String methodName, String reason) {
        return new MethodDeprecation(className, methodName, reason);
    }
    
}
