package com.youdevise.fbplugins.deprecate3rdparty;


public class DeprecatedInGenerics {

    public static class GenericType<T> { }
    
    public static class HasDeprecatedClassInGenericsOfTypeSignature extends GenericType<MyDeprecatedClass> { }
    
    public static class HasDeprecatedClassInGenericsOfField {
        public GenericType<MyDeprecatedClass> deprecatedField;
    }
    
}
