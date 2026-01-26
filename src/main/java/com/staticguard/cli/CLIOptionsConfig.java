package com.staticguard.cli;

import com.staticguard.visitors.java.PrimitiveTypeVisitor;

import java.util.Map;
import java.util.Set;

public class CLIOptionsConfig {
    private final boolean runAll;
    private final boolean addComments;
    private final boolean development;
    private final Set<String> forbiddenMethods;
    private final Set<String> forbiddenTypes;
    private final Map<String, Set<String>> allowedCalls;
    private final PrimitiveTypeVisitor.Mode mode;

    public CLIOptionsConfig(boolean runAll, boolean addComments, boolean development, Set<String> forbiddenMethods, Set<String> forbiddenTypes, Map<String, Set<String>> allowedCalls, PrimitiveTypeVisitor.Mode mode) {
        this.runAll = runAll;
        this.addComments = addComments;
        this.development = development;
        this.forbiddenMethods = forbiddenMethods;
        this.forbiddenTypes = forbiddenTypes;
        this.allowedCalls = allowedCalls;
        this.mode = mode;
    }

    public boolean isRunAll() {
        return runAll;
    }

    public boolean isAddComments() {
        return addComments;
    }

    public boolean isDevelopment() {
        return development;
    }

    public Set<String> getForbiddenMethods() {
        return forbiddenMethods;
    }

    public Set<String> getForbiddenTypes() {
        return forbiddenTypes;
    }

    public Map<String, Set<String>> getAllowedCalls() {
        return allowedCalls;
    }

    public PrimitiveTypeVisitor.Mode getMode() {
        return mode;
    }
}
