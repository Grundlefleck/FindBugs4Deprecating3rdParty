package com.youdevise.fbplugins.deprecate3rdparty;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import com.youdevise.fbplugins.tdd4fb.DetectorAssert;

import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;

public class Deprecated3rdPartyDetectorTest {

    private BugReporter bugReporter;
    private Detector detector;
    private Detector detectorToRegisterBugsAs;
	private DeprecatedSettings settings;

    @Before
    public void setUp() {
        bugReporter = DetectorAssert.bugReporterForTesting();
        detectorToRegisterBugsAs = mock(Detector.class);
        settings = new DeprecatedSettings(asList(MyDeprecatedClass.class.getCanonicalName()), Collections.<String>emptyList());
        detector = new Deprecated3rdPartyDetector(detectorToRegisterBugsAs, bugReporter, settings); 
    }

    @Test
    public void reportsBugWhenFieldIsADeprecatedClass() throws Exception {
        DetectorAssert.assertBugReported(HasDeprecatedClassForAField.class, detector, bugReporter);
    }
    
    @Test
    public void reportsBugWhenLocalVariableIsADeprecatedClass() throws Exception {
        DetectorAssert.assertBugReported(HasADeprecatedClassForALocalVariable.class, detector, bugReporter);
    }

}
