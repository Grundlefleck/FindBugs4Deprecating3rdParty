package com.youdevise.fbplugins.deprecate3rdparty;

import static java.util.Arrays.asList;

import java.util.Collections;

import com.youdevise.fbplugins.deprecate3rdparty.method.MyClassWithDeprecatedInstanceMethod;
import com.youdevise.fbplugins.deprecate3rdparty.method.MyClassWithDeprecatedStaticMethod;

public class DeprecatedSettingsForTesting {

    public DeprecatedSettings settings() {
        Deprecation myDeprecatedClass = Deprecation.of(MyDeprecatedClass.class.getCanonicalName(), "");
        Deprecation myDeprecatedInterface = Deprecation.of(MyDeprecatedInterface.class.getCanonicalName(), "cause i said so");
        Deprecation myDeprecatedStaticMethod = MethodDeprecation.ofMethod(MyClassWithDeprecatedStaticMethod.class.getCanonicalName(), "iAmDeprecated", "");
        Deprecation myDeprecatedInstanceMethod = MethodDeprecation.ofMethod(MyClassWithDeprecatedInstanceMethod.class.getCanonicalName(), "iAmDeprecated", "");
        
        return new DeprecatedSettings(asList(myDeprecatedClass, myDeprecatedInterface, myDeprecatedStaticMethod, myDeprecatedInstanceMethod),
                                      asList(MyDeprecatedClass.class.getCanonicalName(), 
                                             MyDeprecatedInterface.class.getCanonicalName(),
                                             MyClassWithDeprecatedStaticMethod.class.getCanonicalName(),
                                             MyClassWithDeprecatedInstanceMethod.class.getCanonicalName()), 
                                      Collections.<String>emptyList());
    }
    
}
