package com.staticguard.analyzers;

import java.util.ArrayList;
import java.util.List;

import com.staticguard.common.RuleContext;

public class VisitorManager<T> {
    private final List<Analyzer<T>> visitors;
    private final T root;
    private final RuleContext context;

    public VisitorManager(T root, RuleContext context) {
        this.visitors = new ArrayList<>();
        this.root = root;
        this.context = context;
    }

    public void addVisitor(Analyzer<T> visitor) {
        visitors.add(visitor);
    }

    public void runVisitors() {
        for (Analyzer<T> visitor : visitors) {
            visitor.runVisitor(root);
            visitor.postVisit();
        }

        printIssues();
    }

    private void printIssues() {
        var issues = context.getIssues();

        System.out.println();
        System.out.println("Issues:");

        if (issues.isEmpty()) {
            System.out.println("  (empty)");
            return;
        }

        issues.forEach(System.out::println);
    }
}
