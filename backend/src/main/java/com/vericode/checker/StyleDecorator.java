package com.vericode.checker;

public class StyleDecorator extends CheckerDecorator {

    public StyleDecorator(CodeChecker wrapped) {
        super(wrapped);
    }

    @Override
    public CheckResult check(String code) {
        CheckResult result = super.check(code);

        // Style checks: line length, naming, formatting
        String[] lines = code.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];

            if (line.length() > 120) {
                result.addViolation(new Violation("STYLE", "Line exceeds 120 characters", i + 1, "WARNING"));
            }

            if (line.contains("\t")) {
                result.addViolation(new Violation("STYLE", "Tab character found, use spaces", i + 1, "WARNING"));
            }

            if (!line.isEmpty() && line.endsWith(" ")) {
                result.addViolation(new Violation("STYLE", "Trailing whitespace detected", i + 1, "INFO"));
            }
        }

        return result;
    }
}