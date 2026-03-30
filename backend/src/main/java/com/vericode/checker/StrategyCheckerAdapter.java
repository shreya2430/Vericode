package com.vericode.checker;

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
