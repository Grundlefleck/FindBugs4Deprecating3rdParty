package com.youdevise.fbplugins.deprecate3rdparty.method;

public class CallsDeprecatedInstanceMethod {

    public void doSomething() {
        new MyClassWithDeprecatedInstanceMethod().iAmDeprecated();
    }
    
}
