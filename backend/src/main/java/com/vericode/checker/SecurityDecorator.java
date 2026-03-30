package com.vericode.checker;

public class SecurityDecorator extends CheckerDecorator {

    private final CheckRulePool.CheckRule passwordRule = CheckRulePool.getRule("SECURITY", "HardcodedPassword");
    private final CheckRulePool.CheckRule apiKeyRule = CheckRulePool.getRule("SECURITY", "HardcodedApiKey");
    private final CheckRulePool.CheckRule sqlInjectionRule = CheckRulePool.getRule("SECURITY", "SqlInjection");
    private final CheckRulePool.CheckRule evalRule = CheckRulePool.getRule("SECURITY", "EvalUsage");

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

            if (line.contains("\"select") && line.contains("\" +")) {
                result.addViolation(new Violation(sqlInjectionRule.getCategory(),
                        sqlInjectionRule.getMessage(), i + 1, sqlInjectionRule.getSeverity()));
            }

            if (line.contains("eval(")) {
                result.addViolation(new Violation(evalRule.getCategory(),
                        evalRule.getMessage(), i + 1, evalRule.getSeverity()));
            }
        }

        return result;
    }
}