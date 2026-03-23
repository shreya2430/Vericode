package com.vericode.checker;

public abstract class CheckerDecorator implements CodeChecker {

    protected final CodeChecker wrapped;

    public CheckerDecorator(CodeChecker wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public CheckResult check(String code) {
        return wrapped.check(code);
    }
}