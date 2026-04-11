package com.vericode.checker;

import java.util.ArrayList;
import java.util.List;

public class CheckResult {

    private List<Violation> violations;
    private boolean passed;

    public CheckResult() {
        this.violations = new ArrayList<>();
        this.passed = true;
    }

    public void addViolation(Violation violation) {
        this.violations.add(violation);
        if ("ERROR".equals(violation.getSeverity())) {
            this.passed = false;
        }
    }

    public void addAll(List<Violation> violations) {
        for (Violation v : violations) {
            addViolation(v);
        }
    }

    public List<Violation> getViolations() { return violations; }
    public boolean isPassed() { return passed; }

    public int getErrorCount() {
        return (int) violations.stream()
                .filter(v -> "ERROR".equals(v.getSeverity()))
                .count();
    }

    public int getWarningCount() {
        return (int) violations.stream()
                .filter(v -> "WARNING".equals(v.getSeverity()))
                .count();
    }

    public boolean hasSyntaxError() {
        return violations.stream()
                .anyMatch(v -> "SYNTAX".equals(v.getType()) ||
                        ("SYSTEM".equals(v.getType()) && "ERROR".equals(v.getSeverity())));
    }
}