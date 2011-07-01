package com.youdevise.fbplugins.deprecate3rdparty;

public class UsesDeprecatedClassInInstanceOfCheck {

	public void someMethod(Object obj) {
		if (obj instanceof MyDeprecatedClass) { } 
	}
	
}
