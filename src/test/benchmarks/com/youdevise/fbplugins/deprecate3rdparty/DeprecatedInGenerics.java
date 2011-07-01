package com.youdevise.fbplugins.deprecate3rdparty;


public class DeprecatedInGenerics {


	public static class GenericType<T> { }
    
    public static class HasDeprecatedClassInGenericsOfTypeSignature extends GenericType<MyDeprecatedClass> { }
    
    public static class HasDeprecatedClassInGenericsOfField {
        public GenericType<MyDeprecatedClass> deprecatedField;
    }

    public class HasDeprecatedClassInGenericsOfLocalVariable {
    	public void someMethod() {
    		GenericType<MyDeprecatedClass> deprecatedVariable = null;
    		System.out.println(deprecatedVariable);
    	}
    }
    
    public static class HasDeprecatedClassInMethodParameter {
    	public void someMethod(GenericType<MyDeprecatedClass> deprecatedParameter) { }
    }
    
}
