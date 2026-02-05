package com.staticguard.rules.java;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.common.RuleContext;
import com.staticguard.common.RuleVisitor;
import com.staticguard.visitors.java.CallGraphVisitor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CallGraphRule implements RuleVisitor<CompilationUnit> {
    private final Map<String, Set<String>> callGraph = new HashMap<>();

    @Override
    public void run(CompilationUnit astRoot, RuleContext context) {
        astRoot.accept(new CallGraphVisitor(callGraph), null);
    }

    public Map<String, Set<String>> getCallGraph() {
        return callGraph;
    }
}
