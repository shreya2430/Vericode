package com.vericode.checker;

public interface CheckStrategy {

    CheckResult execute(String code);
}