package com.staticguard.cli;

import java.util.Set;

public class CLIOptionsConfig {
    private final boolean runAll;
    private final boolean addComments;
    private final boolean development;
    private final Set<String> forbiddenMethods;

    public CLIOptionsConfig(boolean runAll, boolean addComments, boolean development, Set<String> forbiddenMethods) {
        this.runAll = runAll;
        this.addComments = addComments;
        this.development = development;
        this.forbiddenMethods = forbiddenMethods;
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
}
