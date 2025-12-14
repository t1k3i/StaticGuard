package com.staticguard.analyzers.java;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.analyzers.Analyzer;
import com.staticguard.visitors.java.CallGraphVisitor;

import java.util.Map;
import java.util.Set;

public class CallGraphVisitorAnalyzer implements Analyzer<CompilationUnit> {

    private final CallGraphVisitor visitor = new CallGraphVisitor();

    @Override
    public void runVisitor(CompilationUnit cu) {
        cu.accept(visitor, null);
    }

    @Override
    public void postVisit(CompilationUnit cu) {
        printCallGraph(visitor.getCallGraph());
    }

    private void printCallGraph(Map<String, Set<String>> graph) {
        System.out.println("Call graph:");

        graph.forEach((caller, callees) -> {
            if (callees.isEmpty()) {
                System.out.println("  " + caller + " -> (no calls)");
            } else {
                String joined = String.join(", ", callees);
                System.out.println("  " + caller + " -> " + joined);
            }
        });
    }
}
