package com.vericode.checker;

import java.util.HashMap;
import java.util.Map;

public class CheckRulePool {

    private static final Map<String, CheckRule> ruleCache = new HashMap<>();

    // Returns cached rule instance, never creates duplicates
    public static CheckRule getRule(String language, String ruleName) {
        String key = language.toUpperCase() + ":" + ruleName;

        return ruleCache.computeIfAbsent(key, k -> {
            System.out.println("Creating new rule: " + k);
            return new CheckRule(language, ruleName);
        });
    }

    public static int getCacheSize() {
        return ruleCache.size();
    }

    // Inner class representing a single check rule
    public static class CheckRule {
        private final String language;
        private final String name;

        CheckRule(String language, String name) {
            this.language = language;
            this.name = name;
        }

        public String getLanguage() { return language; }
        public String getName() { return name; }

        @Override
        public String toString() {
            return "CheckRule{" + language + ":" + name + "}";
        }
    }
}
