package com.staticguard.cli;

public class CLIOptionsConfig {
    private final boolean runAll;
    private final boolean addComments;
    private final boolean development;

    public CLIOptionsConfig(boolean runAll, boolean addComments, boolean development) {
        this.runAll = runAll;
        this.addComments = addComments;
        this.development = development;
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
}
