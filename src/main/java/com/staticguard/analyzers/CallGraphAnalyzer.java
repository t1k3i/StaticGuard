package com.staticguard.analyzers;

import com.staticguard.common.RuleVisitor;
import com.staticguard.rules.c.CCallGraphRule;
import com.staticguard.rules.java.CallGraphRule;

import java.util.Map;
import java.util.Set;

public class CallGraphAnalyzer<T> extends GenericAnalyzer<T> {
    public CallGraphAnalyzer(RuleVisitor<T> visitor) {
        super(null, visitor);
    }

    @Override
    public void postVisit(T ast) {
        if (visitor instanceof CCallGraphRule rule) {
            printCallGraph(rule.getCallGraph());
        }

        if (visitor instanceof CallGraphRule rule) {
            printCallGraph(rule.getCallGraph());
        }
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
