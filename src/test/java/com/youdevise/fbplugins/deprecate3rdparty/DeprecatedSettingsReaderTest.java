package com.youdevise.fbplugins.deprecate3rdparty;

import static com.youdevise.fbplugins.deprecate3rdparty.DeprecatedSettings.settingsFromTxtContent;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DeprecatedSettingsReaderTest {

    
    @Test
    public void invalidWhenTheFileDoesNotExist() throws Exception {
        DeprecatedSettings settings = DeprecatedSettings.settingsFromTxtFile("/invalid-file-doesnt-exist.txt");
        assertFalse(settings.isValid());
    }
    
    @Test
    public void invalidWhenTheFileHasNoContents() throws Exception {
        DeprecatedSettings settings = DeprecatedSettings.settingsFromTxtFile("/invalid-nothing-deprecated.txt");
        assertFalse(settings.isValid());
    }
    
    @Test
    public void validWhenSettingsHasAClassToDeprecate() throws Exception {
        DeprecatedSettings settings = settingsFromTxtContent(asList("java.util.List"));
        assertTrue(settings.isValid());
    }
    
    @Test
    public void validWhenSettingsHasAClassToDeprecateAndAReason() throws Exception {
        DeprecatedSettings settings = settingsFromTxtContent(asList("java.util.List, i don't like lists!"));
        assertTrue(settings.isValid());
        Deprecation deprecation = settings.deprecations().get(0);
        assertEquals(deprecation.dottedClassName, "java.util.List");
        assertEquals(deprecation.reason, "i don't like lists!");
    }
    
    @Test
    public void invalidWhenLineDoesntContainValidClassName() throws Exception {
        assertFalse(settingsFromTxtContent(asList("not a valid class name")).isValid());
        assertFalse(settingsFromTxtContent(asList("one.valid.ClassName", "one line of nonsense")).isValid());
    }
    
    @Test
    public void validWhenContainsValidClassNameAndSomeBlankLines() throws Exception {
        assertTrue(settingsFromTxtContent(asList("", "java.util.List", "")).isValid());
    }
    
    @Test
    public void validWhenContainsValidClassAndMethodNameFormat() throws Exception {
        assertTrue(settingsFromTxtContent(asList("", "java.util.List#clear", "")).isValid());
    }

    @Test
    public void validWhenContainsValidClassAndMethodNameFormatAndComment() throws Exception {
        assertTrue(settingsFromTxtContent(asList("java.util.List#clear, is bad, m'kay")).isValid());
    }

    @Test
    public void deprecationOfMethodReturnsCorrectType() throws Exception {
        Deprecation deprecation = settingsFromTxtContent(asList("java.util.List#clear, is bad m'kay")).deprecations().get(0);
        
        assertThat(deprecation, is(instanceOf(MethodDeprecation.class)));
        
        MethodDeprecation methodDeprecation = (MethodDeprecation) deprecation;
        
        assertThat(methodDeprecation.dottedClassName, is("java.util.List"));
        assertThat(methodDeprecation.methodName, is("clear"));
        assertThat(methodDeprecation.reason, is("is bad m'kay"));
    }
    
}
