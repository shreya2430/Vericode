package com.vericode.checker;

import com.vericode.model.Language;

public class CheckerFactory {

    /**
     * Creates a fully decorated checker for the given language.
     *
     * Uses the Strategy pattern to pick the right checker based on language,
     * then wraps it in the Decorator chain: Security -> Style -> Lint
     */
    public static CodeChecker createChecker(Language language) {
        CheckStrategy strategy = createStrategy(language);
        CodeChecker base = new StrategyCheckerAdapter(strategy);

        return new LintDecorator(
                new StyleDecorator(
                        new SecurityDecorator(base)));
    }

    private static CheckStrategy createStrategy(Language language) {
        return switch (language) {
            case JAVA -> new JavaCheckStrategy();
            case PYTHON -> new PythonCheckStrategy();
            case JAVASCRIPT -> new JSCheckStrategy();
        };
    }
}