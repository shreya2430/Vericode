package com.vericode.checker;

import com.vericode.model.Language;

public class CheckerFactory {

    public static CodeChecker createChecker(Language language) {
        CheckStrategy strategy = createStrategy(language);
        CodeChecker base = new StrategyCheckerAdapter(strategy);

        CodeChecker decorated = new LintDecorator(
                new StyleDecorator(
                        new SecurityDecorator(base)));

        return new CachingCheckerProxy(decorated);
    }

    private static CheckStrategy createStrategy(Language language) {
        return switch (language) {
            case JAVA -> new JavaCheckStrategy();
            case PYTHON -> new PythonCheckStrategy();
            case JAVASCRIPT -> new JSCheckStrategy();
        };
    }
}