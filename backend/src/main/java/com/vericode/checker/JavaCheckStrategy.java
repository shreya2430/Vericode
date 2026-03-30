package com.vericode.checker;

public class JavaCheckStrategy implements CheckStrategy {

    @Override
    public CheckResult execute(String code) {
        CheckResult result = new CheckResult();

        if (code == null || code.isBlank()) {
            result.addViolation(new Violation("LINT", "Code is empty", 0, "ERROR"));
            return result;
        }
        
        String[] lines = code.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];

            if (line.contains("public class") && Character.isLowerCase(line.charAt(line.indexOf("class ") + 6))) {
                result.addViolation(new Violation("STYLE",
                        "Class name should start with uppercase", i + 1, "WARNING"));
            }

            if (line.contains("catch") && line.contains("Exception e") && lines.length > i + 1
                    && lines[i + 1].trim().equals("}")) {
                result.addViolation(new Violation("LINT",
                        "Empty catch block - exception is swallowed", i + 1, "WARNING"));
            }
        }

        return result;
    }
}
