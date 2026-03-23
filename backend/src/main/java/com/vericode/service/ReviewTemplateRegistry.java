package com.vericode.service;

import com.vericode.model.ReviewTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewTemplateRegistry {

    private static final Map<String, ReviewTemplate> templates = new HashMap<>();

    static {
        templates.put("STANDARD", new ReviewTemplate("Standard Review", List.of(
                "Code compiles without errors",
                "No hardcoded credentials",
                "Follows naming conventions",
                "Has appropriate comments",
                "No unused imports or variables"
        )));

        templates.put("SECURITY", new ReviewTemplate("Security Review", List.of(
                "No SQL injection vulnerabilities",
                "Input validation present",
                "No hardcoded passwords or API keys",
                "Proper error handling (no stack traces exposed)",
                "Authentication checks in place",
                "Sensitive data is encrypted"
        )));

        templates.put("QUICK", new ReviewTemplate("Quick Review", List.of(
                "Code compiles without errors",
                "No obvious bugs",
                "Follows basic style guidelines"
        )));
    }

    // Always returns a clone, never the original
    public static ReviewTemplate getTemplate(String type) {
        ReviewTemplate template = templates.get(type.toUpperCase());
        if (template == null) {
            throw new IllegalArgumentException("Unknown template type: " + type);
        }
        return template.clone();
    }

    public static List<String> getAvailableTemplates() {
        return List.copyOf(templates.keySet());
    }
}