package com.youdevise.fbplugins.deprecate3rdparty;

import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import com.youdevise.fbplugins.deprecate3rdparty.method.CallsNonDeprecatedInstanceMethodOfClassWhichAlsoContainsDeprecatedMethod;
import com.youdevise.fbplugins.deprecate3rdparty.method.CallsNonDeprecatedStaticMethodOfClassWhichAlsoContainsDeprecatedMethod;
import com.youdevise.fbplugins.tdd4fb.DetectorAssert;

import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;

@RunWith(Theories.class)
public class Deprecated3rdPartyDetectorFalsePositivesTest {

    private BugReporter bugReporter;
    private Detector detector;
    private Detector detectorToRegisterBugsAs;
	private DeprecatedSettings settings;
	
	@DataPoints public static Class<?>[] expectNoBug = new Class[] { 
		Deprecated3rdPartyDetector.class,
        CallsNonDeprecatedInstanceMethodOfClassWhichAlsoContainsDeprecatedMethod.class,
        CallsNonDeprecatedStaticMethodOfClassWhichAlsoContainsDeprecatedMethod.class,
	};

    @Before
    public void setUp() {
        bugReporter = DetectorAssert.bugReporterForTesting();
        detectorToRegisterBugsAs = mock(Detector.class);
        
        settings = new DeprecatedSettingsForTesting().settings();
        detector = new Deprecated3rdPartyDetector(detectorToRegisterBugsAs, bugReporter, settings); 
    }

    @Theory
    public void expectNoBugForAllClassesListed(Class<?> usingDeprecatedClass) throws Exception {
        DetectorAssert.assertNoBugsReported(usingDeprecatedClass, detector, bugReporter);
    }
    
}
