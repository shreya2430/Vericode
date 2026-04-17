package com.vericode.checker;

public class SecurityDecorator extends CheckerDecorator {

    private final CheckRulePool.CheckRule passwordRule = CheckRulePool.getRule("SECURITY", "HardcodedPassword");
    private final CheckRulePool.CheckRule apiKeyRule = CheckRulePool.getRule("SECURITY", "HardcodedApiKey");
    private final CheckRulePool.CheckRule sqlInjectionRule = CheckRulePool.getRule("SECURITY", "SqlInjection");

    public SecurityDecorator(CodeChecker wrapped) {
        super(wrapped);
    }

    @Override
    public CheckResult check(String code) {
        CheckResult result = super.check(code);

        String[] lines = code.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].toLowerCase();
            String rawLine = lines[i];
            int lineNumber = i + 1;

            // Only flag when a string literal is actually assigned to something named password
            if (line.contains("password") && line.matches(".*password.*=.*[\"'].*") && !line.contains("getpassword")
                    && !alreadyFlagged(result, passwordRule.getCategory(), lineNumber)) {
                result.addViolation(new Violation(passwordRule.getCategory(),
                        passwordRule.getMessage(), lineNumber, passwordRule.getSeverity()));
            }

            if (line.matches(".*api[_-]?key\\s*=\\s*[\"'].*[\"'].*")
                    && !alreadyFlagged(result, apiKeyRule.getCategory(), lineNumber)) {
                result.addViolation(new Violation(apiKeyRule.getCategory(),
                        apiKeyRule.getMessage(), lineNumber, apiKeyRule.getSeverity()));
            }

            // Java string concat: "select..."+var or "select..." + var
            if (line.contains("\"select") && (line.contains("\" +") || line.contains("\"+") || line.contains("\" +"))
                    && !alreadyFlagged(result, sqlInjectionRule.getCategory(), lineNumber)) {
                result.addViolation(new Violation(sqlInjectionRule.getCategory(),
                        sqlInjectionRule.getMessage(), lineNumber, sqlInjectionRule.getSeverity()));
            }

            // Python f-string: f"SELECT...{var}" or f'SELECT...{var}'
            if ((rawLine.contains("f\"") || rawLine.contains("f'")) && line.contains("select") && rawLine.contains("{")
                    && !alreadyFlagged(result, sqlInjectionRule.getCategory(), lineNumber)) {
                result.addViolation(new Violation(sqlInjectionRule.getCategory(),
                        sqlInjectionRule.getMessage(), lineNumber, sqlInjectionRule.getSeverity()));
            }

            // JS template literal: `SELECT...${var}`
            if (rawLine.contains("`") && line.contains("select") && rawLine.contains("${")
                    && !alreadyFlagged(result, sqlInjectionRule.getCategory(), lineNumber)) {
                result.addViolation(new Violation(sqlInjectionRule.getCategory(),
                        sqlInjectionRule.getMessage(), lineNumber, sqlInjectionRule.getSeverity()));
            }
        }

        return result;
    }

    private boolean alreadyFlagged(CheckResult result, String category, int line) {
        return result.getViolations().stream()
                .anyMatch(v -> v.getType().equals(category) && v.getLine() == line);
    }
}