package com.staticguard.analyzers;

import java.util.ArrayList;
import java.util.List;

public class VisitorManager<T> {
    private final List<Analyzer<T>> visitors;
    private final T root;

    public VisitorManager(T root) {
        this.visitors = new ArrayList<>();
        this.root = root;
    }

    public void addVisitor(Analyzer<T> visitor) {
        visitors.add(visitor);
    }

    public void runVisitors() {
        for (Analyzer<T> visitor : visitors) {
            visitor.runVisitor(root);
            visitor.postVisit();
        }
    }
}
