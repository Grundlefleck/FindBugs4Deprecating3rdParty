package com.youdevise.fbplugins.deprecate3rdparty.method;

public class CallsNonDeprecatedInstanceMethodOfClassWhichAlsoContainsDeprecatedMethod {

    public void callMethodWhichIsNotDeprecated() {
        new MyClassWithDeprecatedInstanceMethod().iAmNotDeprecated();
    }
    
}
