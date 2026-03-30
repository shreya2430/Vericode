package com.vericode.checker;

/**
 * Bridges CheckStrategy into the CodeChecker interface so that
 * language-specific strategies can be used as the base of the
 * existing decorator chain (Lint, Style, Security).
 */
public class StrategyCheckerAdapter implements CodeChecker {

    private final CheckStrategy strategy;

    public StrategyCheckerAdapter(CheckStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public CheckResult check(String code) {
        return strategy.execute(code);
    }
}
