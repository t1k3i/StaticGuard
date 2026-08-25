package com.staticguard.cli;

import com.staticguard.enums.ControlFlowRule;
import com.staticguard.visitors.java.PrimitiveTypeVisitor;

import java.util.*;

public class CLIOptionsConfig {
    /* GENERAL */
    private final String language;
    private final boolean runAll;
    private final boolean development;

    /* INFO */
    private final boolean runInfo;
    private final boolean callGraph;
    private final boolean classDependencies;
    private final boolean usedTypes;
    private final boolean loopNesting;

    /* GOOD PRACTICES */
    private final boolean runGoodPractices;
    private final boolean unusedImports;
    private final boolean unusedLocals;
    private final boolean naming;
    private final Integer longMethodsMaxLines;

    /* FORBIDDEN */
    private final Set<String> forbiddenMethods;
    private final Set<String> forbiddenTypes;
    private final Map<String, Set<String>> forbiddenCalls;
    private final Set<ControlFlowRule> forbiddenControlFlow;
    private final boolean forbidFieldAccess;
    private final PrimitiveTypeVisitor.Mode primitiveMode;
    private final Set<String> primitiveExceptions;

    public CLIOptionsConfig(
            String language,
            boolean runAll,
            boolean development,

            boolean runInfo,
            boolean callGraph,
            boolean classDependencies,
            boolean usedTypes,
            boolean loopNesting,

            boolean runGoodPractices,
            boolean unusedImports,
            boolean unusedLocals,
            boolean naming,
            Integer longMethodsMaxLines,
            boolean longMethodsEnabled,

            Set<String> forbiddenMethods,
            Set<String> forbiddenTypes,
            Map<String, Set<String>> forbiddenCalls,
            Set<ControlFlowRule> forbiddenControlFlow,
            boolean forbidFieldAccess,

            PrimitiveTypeVisitor.Mode primitiveMode,
            Set<String> primitiveExceptions
    ) {
        this.language = language;
        this.runAll = runAll;
        this.development = development;

        this.runInfo = runInfo;
        this.callGraph = callGraph;
        this.classDependencies = classDependencies;
        this.usedTypes = usedTypes;
        this.loopNesting = loopNesting;

        this.runGoodPractices = runGoodPractices;
        this.unusedImports = unusedImports;
        this.unusedLocals = unusedLocals;
        this.naming = naming;
        if (longMethodsEnabled)
            this.longMethodsMaxLines = longMethodsMaxLines;
        else
            this.longMethodsMaxLines = null;

        this.forbiddenMethods = forbiddenMethods;
        this.forbiddenTypes = forbiddenTypes;
        this.forbiddenCalls = forbiddenCalls;
        this.forbiddenControlFlow = forbiddenControlFlow;
        this.forbidFieldAccess = forbidFieldAccess;

        this.primitiveMode = primitiveMode;
        this.primitiveExceptions = primitiveExceptions != null ? primitiveExceptions : Collections.emptySet();
    }

    public static CLIOptionsConfig fromCLI(CLIOptions cli) {
        return new CLIOptionsConfig(
                cli.language,
                cli.runAll,
                cli.development,
                cli.runInfo,
                cli.callGraph,
                cli.classDependencies,
                cli.usedTypes,
                cli.loopNesting,
                cli.runGoodPractices,
                cli.unusedImports,
                cli.unusedLocals,
                cli.naming,
                cli.longMethodsMaxLines,
                cli.longMethodsEnabled,
                new HashSet<>(cli.forbiddenMethods),
                new HashSet<>(cli.forbiddenTypes),
                buildForbiddenCalls(cli.forbiddenCalls),
                cli.forbiddenControlFlow,
                cli.forbidFieldAccess,
                cli.primitiveMode,
                new HashSet<>(cli.primitiveExceptions)
        );
    }

    private CLIOptionsConfig(Builder builder) {
        this.language = builder.language;
        this.runAll = builder.runAll;
        this.development = builder.development;

        this.runInfo = builder.runInfo;
        this.callGraph = builder.callGraph;
        this.classDependencies = builder.classDependencies;
        this.usedTypes = builder.usedTypes;
        this.loopNesting = builder.loopNesting;

        this.runGoodPractices = builder.runGoodPractices;
        this.unusedImports = builder.unusedImports;
        this.unusedLocals = builder.unusedLocals;
        this.naming = builder.naming;
        this.longMethodsMaxLines = builder.longMethodsMaxLines;

        this.forbiddenMethods = builder.forbiddenMethods;
        this.forbiddenTypes = builder.forbiddenTypes;
        this.forbiddenCalls = builder.forbiddenCalls;
        this.forbiddenControlFlow = builder.forbiddenControlFlow;
        this.forbidFieldAccess = builder.forbidFieldAccess;

        this.primitiveMode = builder.primitiveMode;
        this.primitiveExceptions = builder.primitiveExceptions != null ? builder.primitiveExceptions : Collections.emptySet();
    }

    public static Builder builder() {
        return new Builder();
    }

    private static Map<String, Set<String>> buildForbiddenCalls(
            List<Map.Entry<String, Set<String>>> forbiddenCalls
    ) {
        Map<String, Set<String>> result = new HashMap<>();

        if (forbiddenCalls == null) {
            return result;
        }

        for (Map.Entry<String, Set<String>> entry : forbiddenCalls) {
            result
                    .computeIfAbsent(entry.getKey(), k -> new HashSet<>())
                    .addAll(entry.getValue());
        }

        return result;
    }

    /* GENERAL */
    public String getLanguage() {
        return language;
    }

    public boolean isRunAll() {
        return runAll;
    }

    public boolean isDevelopment() {
        return development;
    }

    /* INFO */
    public boolean isRunInfo() {
        return runInfo;
    }

    public boolean isCallGraph() {
        return callGraph;
    }

    public boolean isClassDependencies() {
        return classDependencies;
    }

    public boolean isUsedTypes() {
        return usedTypes;
    }

    public boolean isLoopNesting() {
        return loopNesting;
    }

    /* GOOD PRACTICES */
    public boolean isRunGoodPractices() {
        return runGoodPractices;
    }

    public boolean isUnusedImports() {
        return unusedImports;
    }

    public boolean isUnusedLocals() {
        return unusedLocals;
    }

    public boolean isNaming() {
        return naming;
    }

    public Integer getLongMethodsMaxLines() {
        return longMethodsMaxLines;
    }

    /* FORBIDDEN */
    public Set<String> getForbiddenMethods() {
        return forbiddenMethods;
    }

    public Set<String> getForbiddenTypes() {
        return forbiddenTypes;
    }

    public Map<String, Set<String>> getForbiddenCalls() {
        return forbiddenCalls;
    }

    public Set<ControlFlowRule> getForbiddenControlFlow() {
        return forbiddenControlFlow;
    }

    public boolean isForbidFieldAccess() {
        return forbidFieldAccess;
    }

    public PrimitiveTypeVisitor.Mode getPrimitiveMode() {
        return primitiveMode;
    }

    public Set<String> getPrimitiveExceptions() {
        return primitiveExceptions;
    }

    public static class Builder {

        /* GENERAL */
        private String language;
        private boolean runAll;
        private boolean development;

        /* INFO */
        private boolean runInfo;
        private boolean callGraph;
        private boolean classDependencies;
        private boolean usedTypes;
        private boolean loopNesting;

        /* GOOD PRACTICES */
        private boolean runGoodPractices;
        private boolean unusedImports;
        private boolean unusedLocals;
        private boolean naming;
        private Integer longMethodsMaxLines;

        /* FORBIDDEN */
        private Set<String> forbiddenMethods = new HashSet<>();
        private Set<String> forbiddenTypes = new HashSet<>();
        private Map<String, Set<String>> forbiddenCalls = new HashMap<>();
        private Set<ControlFlowRule> forbiddenControlFlow = new HashSet<>();
        private boolean forbidFieldAccess;
        private PrimitiveTypeVisitor.Mode primitiveMode;
        private Set<String> primitiveExceptions = new HashSet<>();

        private Builder() {}

        public CLIOptionsConfig build() {
            return new CLIOptionsConfig(this);
        }

        public Builder primitiveMode(PrimitiveTypeVisitor.Mode mode) {
            this.primitiveMode = mode;
            return this;
        }

        public Builder primitiveExceptions(Set<String> exceptions) {
            this.primitiveExceptions = exceptions;
            return this;
        }

        public Builder usedTypes(boolean value) {
            this.usedTypes = value;
            return this;
        }

        public Builder language(String language) {
            this.language = language;
            return this;
        }

        public Builder runGoodPractices(boolean value) {
            this.runGoodPractices = value;
            return this;
        }

        public Builder naming(boolean value) {
            this.naming = value;
            return this;
        }

        public Builder development(boolean value) {
            this.development = value;
            return this;
        }
    }
}
