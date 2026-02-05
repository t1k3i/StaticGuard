package com.staticguard.visitors.java;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.*;

public class CallGraphVisitor extends VoidVisitorAdapter<Void> {
    Map<String, Set<String>> callGraph;

    public CallGraphVisitor(Map<String, Set<String>> callGraph) {
        this.callGraph = callGraph;
    }

    private String currentMethod = null;

    @Override
    public void visit(MethodDeclaration n, Void arg) {
        currentMethod = n.getNameAsString();
        callGraph.putIfAbsent(currentMethod, new HashSet<>());

        super.visit(n, arg);

        currentMethod = null;
    }

    @Override
    public void visit(MethodCallExpr n, Void arg) {
        super.visit(n, arg);

        if (currentMethod == null)
            return;

        String calledMethod = n.getNameAsString();
        callGraph
                .computeIfAbsent(currentMethod, k -> new HashSet<>())
                .add(calledMethod);
    }

    public Map<String, Set<String>> getCallGraph() {
        return callGraph;
    }
}
