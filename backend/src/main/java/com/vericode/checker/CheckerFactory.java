package com.vericode.checker;

import com.vericode.model.Language;

public class CheckerFactory {

    // Creates a fully decorated checker for the given language
    public static CodeChecker createChecker(Language language) {
        BaseChecker base = new BaseChecker(language);

        // Wrap in decorator chain: Security -> Style -> Lint -> Base
        // Execution order: Base runs first, then Security, then Style, then Lint
        return new LintDecorator(
                new StyleDecorator(
                        new SecurityDecorator(base)));
    }
}