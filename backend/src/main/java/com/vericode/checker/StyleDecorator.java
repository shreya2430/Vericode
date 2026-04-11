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

        boolean lineTooLongFlagged = false;
        boolean tabFlagged = false;

        String[] lines = code.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String raw = lines[i];
            String stripped = raw.stripTrailing();

            // Check raw length so genuine trailing whitespace counts toward line length
            // Only report once — no need to repeat the same violation for every long line
            if (!lineTooLongFlagged && raw.length() > 120) {
                result.addViolation(new Violation(lineLengthRule.getCategory(),
                        lineLengthRule.getMessage(), i + 1, lineLengthRule.getSeverity()));
                lineTooLongFlagged = true;
            }

            if (!tabFlagged && stripped.contains("\t")) {
                result.addViolation(new Violation(tabRule.getCategory(),
                        tabRule.getMessage(), i + 1, tabRule.getSeverity()));
                tabFlagged = true;
            }
        }

        return result;
    }
}