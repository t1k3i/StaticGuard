package com.staticguard.common;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RuleContext {
    private final File sourceFile;
    private final List<Issue> issues = new ArrayList<>();

    public RuleContext(File sourceFile) {
        this.sourceFile = sourceFile;
    }

    public void report(String message, int line) {
        issues.add(new Issue(
                sourceFile.getPath(),
                message,
                line
        ));
    }

    public List<Issue> getIssues() {
        return issues;
    }
}
