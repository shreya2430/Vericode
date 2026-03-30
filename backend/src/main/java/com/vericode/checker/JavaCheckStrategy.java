package com.vericode.checker;

public class JavaCheckStrategy implements CheckStrategy {

    private final CheckstyleAdapter checkstyleAdapter = new CheckstyleAdapter();

    @Override
    public CheckResult execute(String code) {
        return checkstyleAdapter.check(code);
    }
}