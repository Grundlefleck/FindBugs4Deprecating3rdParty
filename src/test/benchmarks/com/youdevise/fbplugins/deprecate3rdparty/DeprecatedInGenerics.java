package com.youdevise.fbplugins.deprecate3rdparty;

import java.lang.ref.Reference;
import java.util.concurrent.Future;

public class DeprecatedInGenerics {

    public static class GenericType<T> { }
    
    public static class HasDeprecatedClassInGenericsOfTypeSignature extends GenericType<MyDeprecatedClass> { }

    
}
