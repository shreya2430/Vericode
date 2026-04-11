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

            if (line.contains("password") && line.contains("=") && !line.contains("getpassword")) {
                result.addViolation(new Violation(passwordRule.getCategory(),
                        passwordRule.getMessage(), i + 1, passwordRule.getSeverity()));
            }

            if (line.matches(".*api[_-]?key\\s*=\\s*[\"'].*[\"'].*")) {
                result.addViolation(new Violation(apiKeyRule.getCategory(),
                        apiKeyRule.getMessage(), i + 1, apiKeyRule.getSeverity()));
            }

            // Java string concat: "select..."+var or "select..." + var
            if (line.contains("\"select") && (line.contains("\" +") || line.contains("\"+") || line.contains("\" +"))) {
                result.addViolation(new Violation(sqlInjectionRule.getCategory(),
                        sqlInjectionRule.getMessage(), i + 1, sqlInjectionRule.getSeverity()));
            }

            // Python f-string: f"SELECT...{var}" or f'SELECT...{var}'
            String rawLine = lines[i];
            if ((rawLine.contains("f\"") || rawLine.contains("f'")) && line.contains("select") && rawLine.contains("{")) {
                result.addViolation(new Violation(sqlInjectionRule.getCategory(),
                        sqlInjectionRule.getMessage(), i + 1, sqlInjectionRule.getSeverity()));
            }

            // JS template literal: `SELECT...${var}`
            if (rawLine.contains("`") && line.contains("select") && rawLine.contains("${")) {
                result.addViolation(new Violation(sqlInjectionRule.getCategory(),
                        sqlInjectionRule.getMessage(), i + 1, sqlInjectionRule.getSeverity()));
            }

        }

        return result;
    }
}