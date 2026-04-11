package com.vericode.checker;

public class LintDecorator extends CheckerDecorator {

    private final CheckRulePool.CheckRule emptyBlockRule = CheckRulePool.getRule("LINT", "EmptyBlock");
    private final CheckRulePool.CheckRule debugPrintRule = CheckRulePool.getRule("LINT", "DebugPrint");

    public LintDecorator(CodeChecker wrapped) {
        super(wrapped);
    }

    @Override
    public CheckResult check(String code) {
        CheckResult result = super.check(code);

        if (result.hasSyntaxError()) {
            return result;
        }

        String[] lines = code.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();

            if (line.matches(".*\\{\\s*\\}.*")) {
                result.addViolation(new Violation(emptyBlockRule.getCategory(),
                        emptyBlockRule.getMessage(), i + 1, emptyBlockRule.getSeverity()));
            }

            if (line.contains("System.out.println") || line.contains("print(")) {
                result.addViolation(new Violation(debugPrintRule.getCategory(),
                        debugPrintRule.getMessage(), i + 1, debugPrintRule.getSeverity()));
            }
        }

        return result;
    }
}