package com.vericode.checker;

import com.vericode.model.Language;

public class BaseChecker implements CodeChecker {

    private final Language language;

    public BaseChecker(Language language) {
        this.language = language;
    }

    @Override
    public CheckResult check(String code) {
        CheckResult result = new CheckResult();

        // Basic checks that apply to all languages
        if (code == null || code.isBlank()) {
            result.addViolation(new Violation("LINT", "Code is empty", 0, "ERROR"));
        }

        return result;
    }

    public Language getLanguage() {
        return language;
    }
}