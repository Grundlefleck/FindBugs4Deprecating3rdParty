package com.youdevise.fbplugins.deprecate3rdparty.method;

public class CallsNonDeprecatedStaticMethodOfClassWhichAlsoContainsDeprecatedMethod {
    
    public static void callMethodWhichIsNotDeprecated() {
        MyClassWithDeprecatedStaticMethod.iAmNotDeprecated();
    }
}
