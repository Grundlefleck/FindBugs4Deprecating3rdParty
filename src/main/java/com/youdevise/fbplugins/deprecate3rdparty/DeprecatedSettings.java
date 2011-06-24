package com.youdevise.fbplugins.deprecate3rdparty;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class DeprecatedSettings {
    
    private final boolean isValid;
    private final List<String> invalidLines;
    private final List<String> deprecatedClasses;

    public DeprecatedSettings(boolean isValid) {
        this.isValid = isValid;
        invalidLines = new ArrayList<String>();
        deprecatedClasses = new ArrayList<String>();
    }

    public DeprecatedSettings(List<String> deprecatedClasses, List<String> invalidLines) {
        this.deprecatedClasses = deprecatedClasses;
        this.invalidLines = invalidLines;
        this.isValid = !deprecatedClasses.isEmpty() && invalidLines.isEmpty();
    }

    public boolean isValid() {
        return isValid;
    }
    
    public Iterable<String> invalidLines() {
        return Collections.unmodifiableList(invalidLines);
    }
    
    public List<String> deprecatedClasses() {
        return Collections.unmodifiableList(deprecatedClasses);
    }
    
    public static DeprecatedSettings settingsFromTxtFile(String fileName) throws Exception {
        BufferedReader reader = null;
        File deprecatedListFile = new File(fileName);
        if (!deprecatedListFile.exists()) {
            System.out.printf("Could not find settings file [%s]. Deprecated 3rd Party detector will not run.%n", fileName);
            return new DeprecatedSettings(false);
        }
        
        List<String> lines = new ArrayList<String>();
        try {
            reader = new BufferedReader(new FileReader(deprecatedListFile));
    
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.printf("Reading line: [%s]%n", line);
                lines.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if ( reader != null) reader.close();
        }
        
        return settingsFromTxtContent(lines);
    }

    public static DeprecatedSettings settingsFromTxtContent(List<String> lines) {
        List<String> deprecatedClasses = new ArrayList<String>();
        List<String> invalidLines = new ArrayList<String>();
        for (String line : lines) {
            if (StringUtils.isBlank(line)) {
                continue;
            }
            
            if (isValidClassName(line)) {
                deprecatedClasses.add(line);
            } else {
                invalidLines.add(line);
            }
        }
        
        return new DeprecatedSettings(deprecatedClasses, invalidLines);
    }

    private static boolean isValidClassName(String line) {
        String fullyQualifiedClassNameMatcher = "([\\p{L}_$][\\p{L}\\p{N}_$]*\\.)*[\\p{L}_$][\\p{L}\\p{N}_$]*";
        // http://stackoverflow.com/questions/5205339/
            
        return Pattern.matches(fullyQualifiedClassNameMatcher, line);
    }

    
}
