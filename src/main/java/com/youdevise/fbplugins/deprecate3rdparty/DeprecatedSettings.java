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
    private final List<Deprecation> deprecations;

    public DeprecatedSettings(boolean isValid) {
        this.isValid = isValid;
        this.invalidLines = new ArrayList<String>();
        this.deprecatedClasses = new ArrayList<String>();
        this.deprecations = new ArrayList<Deprecation>();
    }

    public DeprecatedSettings(List<Deprecation> deprecated, List<String> deprecatedClasses, List<String> invalidLines) {
        this.deprecations = deprecated;
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
    
	public List<Deprecation> deprecations() {
		return Collections.unmodifiableList(deprecations);
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
        List<Deprecation> deprecated = new ArrayList<Deprecation>();
        List<String> invalidLines = new ArrayList<String>();
        for (String line : lines) {
            if (StringUtils.isBlank(line)) {
                continue;
            }
            
            String[] split = line.split(",");
            String[] classAndPossiblyMethodName = split[0].trim().split("#");
            String reason = "";
            if (split.length > 1) {
            	reason = split[1].trim();
            }
            
            String className = classAndPossiblyMethodName[0];
            
            if (isValidClassName(className)) {
                deprecatedClasses.add(className);
                if (noMethodNameSpecified(classAndPossiblyMethodName)) {
                    deprecated.add(Deprecation.of(className, reason));
                } else if (includesValidMethod(classAndPossiblyMethodName)) {
                    deprecated.add(MethodDeprecation.ofMethod(className, classAndPossiblyMethodName[1], reason));
                }
            } else {
                invalidLines.add(line);
            }
        }
        
        return new DeprecatedSettings(deprecated, deprecatedClasses, invalidLines);
    }

    private static boolean noMethodNameSpecified(String[] classAndPossiblyMethodName) {
        return classAndPossiblyMethodName.length < 2;
    }

    private static boolean includesValidMethod(String[] classAndPossiblyMethodName) {
        String methodName = classAndPossiblyMethodName[1];

        // http://stackoverflow.com/questions/2008279/validate-a-javascript-function-name
        String methodNameMatcher = "^[A-Za-z_][0-9A-Za-z_]*";
        
        return Pattern.matches(methodNameMatcher, methodName);
    }

    private static boolean isValidClassName(String className) {
        // http://stackoverflow.com/questions/5205339/
        String fullyQualifiedClassNameMatcher = "([\\p{L}_$][\\p{L}\\p{N}_$]*\\.)*[\\p{L}_$][\\p{L}\\p{N}_$]*";
            
        return Pattern.matches(fullyQualifiedClassNameMatcher, className);
    }

    
}
