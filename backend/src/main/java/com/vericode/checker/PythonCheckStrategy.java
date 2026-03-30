package com.vericode.checker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class PythonCheckStrategy implements CheckStrategy {

    private static final String DEFAULT_URL = "http://localhost:5001/check";
    private static final int TIMEOUT_SECONDS = 10;

    private final String checkerUrl;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(TIMEOUT_SECONDS))
            .build();

    public PythonCheckStrategy() {
        this(DEFAULT_URL);
    }

    public PythonCheckStrategy(String checkerUrl) {
        this.checkerUrl = checkerUrl;
    }

    @Override
    public CheckResult execute(String code) {
        CheckResult result = new CheckResult();

        if (code == null || code.isBlank()) {
            result.addViolation(new Violation("LINT", "Code is empty", 0, "ERROR"));
            return result;
        }

        try {
            String requestBody = objectMapper.writeValueAsString(
                    java.util.Map.of("code", code));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(checkerUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                parseViolations(response.body(), result);
            } else {
                result.addViolation(new Violation("SYSTEM",
                        "Python checker returned status " + response.statusCode(), 0, "WARNING"));
            }

        } catch (Exception e) {
            result.addViolation(new Violation("SYSTEM",
                    "Python checker service unavailable - skipping Pylint analysis", 0, "WARNING"));
        }

        return result;
    }

    private void parseViolations(String responseBody, CheckResult result) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode violations = root.get("violations");

            if (violations != null && violations.isArray()) {
                for (JsonNode v : violations) {
                    String type = v.has("type") ? v.get("type").asText() : "LINT";
                    String message = v.has("message") ? v.get("message").asText() : "Unknown issue";
                    int line = v.has("line") ? v.get("line").asInt() : 0;
                    String severity = v.has("severity") ? v.get("severity").asText() : "WARNING";

                    result.addViolation(new Violation(type, message, line, severity));
                }
            }
        } catch (Exception e) {
            result.addViolation(new Violation("SYSTEM",
                    "Failed to parse Python checker response", 0, "WARNING"));
        }
    }
}
