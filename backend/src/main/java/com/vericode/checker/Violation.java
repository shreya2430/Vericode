package com.vericode.checker;

public class Violation {

    private String type;     // LINT, STYLE, SECURITY
    private String message;
    private int line;
    private String severity; // ERROR, WARNING, INFO

    public Violation(String type, String message, int line, String severity) {
        this.type = type;
        this.message = message;
        this.line = line;
        this.severity = severity;
    }

    public String getType() { return type; }
    public String getMessage() { return message; }
    public int getLine() { return line; }
    public String getSeverity() { return severity; }

    @Override
    public String toString() {
        return "[" + type + "] Line " + line + ": " + message + " (" + severity + ")";
    }
}