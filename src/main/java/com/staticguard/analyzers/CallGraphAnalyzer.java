package com.staticguard.analyzers;

import com.staticguard.rules.CallGraphRule;

import java.util.Map;
import java.util.Set;

public class CallGraphAnalyzer<T> implements Analyzer<T> {
    private final CallGraphRule<T> rule;

    public CallGraphAnalyzer(CallGraphRule<T> rule) {
        this.rule = rule;
    }

    @Override
    public void runVisitor(T ast) {
        rule.run(ast, null);
    }

    @Override
    public void postVisit() {
        printCallGraph(rule.getCallGraph());
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
