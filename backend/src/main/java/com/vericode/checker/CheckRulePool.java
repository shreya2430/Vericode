package com.vericode.checker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckRulePool {

    private static final Map<String, CheckRule> ruleCache = new HashMap<>();

    static {
        registerSecurityRules();
        registerStyleRules();
        registerLintRules();
    }

    public static CheckRule getRule(String category, String ruleName) {
        String key = category.toUpperCase() + ":" + ruleName;
        return ruleCache.get(key);
    }

    public static List<CheckRule> getRulesByCategory(String category) {
        List<CheckRule> rules = new ArrayList<>();
        String prefix = category.toUpperCase() + ":";
        for (Map.Entry<String, CheckRule> entry : ruleCache.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                rules.add(entry.getValue());
            }
        }
        return rules;
    }

    public static int getCacheSize() {
        return ruleCache.size();
    }

    private static void register(String category, String name, String severity, String message) {
        String key = category.toUpperCase() + ":" + name;
        ruleCache.putIfAbsent(key, new CheckRule(category, name, severity, message));
    }

    private static void registerSecurityRules() {
        register("SECURITY", "HardcodedPassword", "ERROR", "Possible hardcoded password");
        register("SECURITY", "HardcodedApiKey", "ERROR", "Possible hardcoded API key");
        register("SECURITY", "SqlInjection", "ERROR", "Possible SQL injection - use parameterized queries");
    }

    private static void registerStyleRules() {
        register("STYLE", "LineTooLong", "WARNING", "Line too long - check for excessive spacing or indentation");
        register("STYLE", "TabCharacter", "WARNING", "Tab character found, use spaces");
        register("STYLE", "TrailingWhitespace", "INFO", "Trailing whitespace detected");
    }

    private static void registerLintRules() {
        register("LINT", "EmptyBlock", "WARNING", "Empty code block detected");
        register("LINT", "DebugPrint", "WARNING", "Debug print statement found");
    }

    public static class CheckRule {
        private final String category;
        private final String name;
        private final String severity;
        private final String message;

        CheckRule(String category, String name, String severity, String message) {
            this.category = category;
            this.name = name;
            this.severity = severity;
            this.message = message;
        }

        public String getCategory() { return category; }
        public String getName() { return name; }
        public String getSeverity() { return severity; }
        public String getMessage() { return message; }

        @Override
        public String toString() {
            return "CheckRule{" + category + ":" + name + "}";
        }
    }
}
