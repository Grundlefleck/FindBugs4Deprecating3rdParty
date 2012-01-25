package com.youdevise.fbplugins.deprecate3rdparty;

import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import com.youdevise.fbplugins.deprecate3rdparty.method.CallsDeprecatedInstanceMethod;
import com.youdevise.fbplugins.deprecate3rdparty.method.CallsDeprecatedStaticMethod;
import com.youdevise.fbplugins.tdd4fb.DetectorAssert;

import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;

@RunWith(Theories.class)
public class Deprecated3rdPartyDetectorTest {

    private BugReporter bugReporter;
    private Detector detector;
    private Detector detectorToRegisterBugsAs;
	private DeprecatedSettings settings;
	
	@DataPoints public static Class<?>[] expectABug = new Class[] { 
//	       HasDeprecatedClassForAField.class,
//	        HasADeprecatedClassForALocalVariable.class,
//	        HasADeprecatedClassAsAMethodParameter.class,
//	        HasADeprecatedClassAsSuperclass.class,
//	        ImplementsADeprecatedInterface.class,
//	        CallsStaticMethodOfDeprecatedClass.class,
//	        ConstructsNewInstanceOfDeprecatedClass.class,
//	        DeprecatedInGenerics.HasDeprecatedClassInGenericsOfTypeSignature.class,
//	        DeprecatedInGenerics.HasDeprecatedClassInGenericsOfField.class,
//	        DeprecatedInGenerics.HasDeprecatedClassInGenericsOfLocalVariable.class,
//	        DeprecatedInGenerics.HasDeprecatedClassInMethodParameter.class,
//	        UsesDeprecatedClassInInstanceOfCheck.class,
	        CallsDeprecatedInstanceMethod.class,
	        CallsDeprecatedStaticMethod.class
	};

    @Before
    public void setUp() {
        bugReporter = DetectorAssert.bugReporterForTesting();
        detectorToRegisterBugsAs = mock(Detector.class);
        
        settings = new DeprecatedSettingsForTesting().settings();
        detector = new Deprecated3rdPartyDetector(detectorToRegisterBugsAs, bugReporter, settings); 
    }

    @Theory
    public void expectBugForAllClassesListed(Class<?> usingDeprecatedClass) throws Exception {
        DetectorAssert.assertBugReported(usingDeprecatedClass, detector, bugReporter);
    }
    
}
