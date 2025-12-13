package com.staticguard.common;

public class Issue {
    private final String file;
    private final String message;
    private final int line;

    public Issue(String file, String message, int line) {
        this.file = file;
        this.message = message;
        this.line = line;
    }

    public String getFile() {
        return file;
    }

    public String getMessage() {
        return message;
    }

    public int getLine() {
        return line;
    }

    @Override
    public String toString() {
        return file + ":" + "Line" + line + ": " + message;
    }
}
