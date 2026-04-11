package com.vericode.checker;

public class StyleDecorator extends CheckerDecorator {

    private final CheckRulePool.CheckRule lineLengthRule = CheckRulePool.getRule("STYLE", "LineTooLong");
    private final CheckRulePool.CheckRule tabRule = CheckRulePool.getRule("STYLE", "TabCharacter");
    private final CheckRulePool.CheckRule trailingRule = CheckRulePool.getRule("STYLE", "TrailingWhitespace");

    public StyleDecorator(CodeChecker wrapped) {
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
            String line = lines[i].stripTrailing();

            if (line.length() > 120) {
                result.addViolation(new Violation(lineLengthRule.getCategory(),
                        lineLengthRule.getMessage(), i + 1, lineLengthRule.getSeverity()));
            }

            if (line.contains("\t")) {
                result.addViolation(new Violation(tabRule.getCategory(),
                        tabRule.getMessage(), i + 1, tabRule.getSeverity()));
            }
        }

        return result;
    }
}