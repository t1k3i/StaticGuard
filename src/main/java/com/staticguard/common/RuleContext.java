package com.staticguard.common;

import java.util.ArrayList;
import java.util.List;

public class RuleContext {
    private final List<Issue> issues = new ArrayList<>();

    public void report(String message, int line) {
        issues.add(new Issue(message, line));
    }

    public List<Issue> getIssues() {
        return issues;
    }
}
