package com.vericode.checker;

public class SecurityDecorator extends CheckerDecorator {

    public SecurityDecorator(CodeChecker wrapped) {
        super(wrapped);
    }

    @Override
    public CheckResult check(String code) {
        CheckResult result = super.check(code);

        String[] lines = code.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].toLowerCase();

            // Hardcoded credentials
            if (line.contains("password") && line.contains("=") && !line.contains("getpassword")) {
                result.addViolation(new Violation("SECURITY", "Possible hardcoded password", i + 1, "ERROR"));
            }

            if (line.matches(".*api[_-]?key\\s*=\\s*[\"'].*[\"'].*")) {
                result.addViolation(new Violation("SECURITY", "Possible hardcoded API key", i + 1, "ERROR"));
            }

            // SQL injection risk
            if (line.contains("\"select") && line.contains("\" +")) {
                result.addViolation(new Violation("SECURITY", "Possible SQL injection - use parameterized queries", i + 1, "ERROR"));
            }

            // Eval usage
            if (line.contains("eval(")) {
                result.addViolation(new Violation("SECURITY", "Use of eval() is a security risk", i + 1, "ERROR"));
            }
        }

        return result;
    }
}