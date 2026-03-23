package com.vericode.checker;

public class LintDecorator extends CheckerDecorator {

    public LintDecorator(CodeChecker wrapped) {
        super(wrapped);
    }

    @Override
    public CheckResult check(String code) {
        CheckResult result = super.check(code);

        // Lint checks: unused variables, empty blocks, etc.
        String[] lines = code.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();

            if (line.matches(".*\\{\\s*\\}.*")) {
                result.addViolation(new Violation("LINT", "Empty code block detected", i + 1, "WARNING"));
            }

            if (line.contains("System.out.println") || line.contains("console.log") || line.contains("print(")) {
                result.addViolation(new Violation("LINT", "Debug print statement found", i + 1, "WARNING"));
            }
        }

        return result;
    }
}