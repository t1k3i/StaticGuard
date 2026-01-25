package com.staticguard.cli;

import java.util.Map;
import java.util.Set;

public class CLIOptionsConfig {
    private final boolean runAll;
    private final boolean addComments;
    private final boolean development;
    private final Set<String> forbiddenMethods;
    private final Set<String> forbiddenTypes;
    private final Map<String, Set<String>> allowedCalls;

    public CLIOptionsConfig(boolean runAll, boolean addComments, boolean development, Set<String> forbiddenMethods, Set<String> forbiddenTypes, Map<String, Set<String>> allowedCalls) {
        this.runAll = runAll;
        this.addComments = addComments;
        this.development = development;
        this.forbiddenMethods = forbiddenMethods;
        this.forbiddenTypes = forbiddenTypes;
        this.allowedCalls = allowedCalls;
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
}
