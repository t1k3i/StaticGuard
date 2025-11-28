package com.staticguard.analyzer;

public class Issue {
    private final String message;
    private final int line;

    public Issue(String message, int line) {
        this.message = message;
        this.line = line;
    }

    public String getMessage() {
        return message;
    }

    public int getLine() {
        return line;
    }

    @Override
    public String toString() {
        return "Line " + line + ": " + message;
    }
}
