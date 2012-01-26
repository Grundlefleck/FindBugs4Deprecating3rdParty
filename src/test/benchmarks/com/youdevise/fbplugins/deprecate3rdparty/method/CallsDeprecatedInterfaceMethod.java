package com.youdevise.fbplugins.deprecate3rdparty.method;

public class CallsDeprecatedInterfaceMethod {

    public void doSomething(MyInterfaceWithDeprecatedMethod interfaceWithDeprecated) {
        interfaceWithDeprecated.iAmDeprecated();
    }
    
}
