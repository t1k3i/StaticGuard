package com.staticguard.visitors.java;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.*;

public class CallGraphVisitor extends VoidVisitorAdapter<Void> {

    private final Map<String, Set<String>> callGraph;

    private final Deque<String> methodStack = new ArrayDeque<>();

    public CallGraphVisitor(Map<String, Set<String>> callGraph) {
        this.callGraph = callGraph;
    }

    @Override
    public void visit(MethodDeclaration n, Void arg) {
        String methodName = n.getNameAsString();

        callGraph.putIfAbsent(methodName, new HashSet<>());

        methodStack.push(methodName);

        try {
            super.visit(n, arg);
        } finally {
            methodStack.pop();
        }
    }

    @Override
    public void visit(MethodCallExpr n, Void arg) {

        if (!methodStack.isEmpty()) {
            String currentMethod = methodStack.peek();

            String calledMethod = n.getNameAsString();

            callGraph
                    .computeIfAbsent(currentMethod, k -> new HashSet<>())
                    .add(calledMethod);
        }

        super.visit(n, arg);
    }

    public Map<String, Set<String>> getCallGraph() {
        return callGraph;
    }
}
