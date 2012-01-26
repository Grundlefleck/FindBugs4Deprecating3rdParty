package com.youdevise.fbplugins.deprecate3rdparty.method;

public class CallsNonDeprecatedInterfaceMethodOfInterfaceWhichAlsoContainsDeprecatedMethod {

    public void callMethodWhichIsNotDeprecated(MyInterfaceWithDeprecatedMethod interfaceWithDeprecated) {
        interfaceWithDeprecated.iAmNotDeprecated();
    }
    
}
