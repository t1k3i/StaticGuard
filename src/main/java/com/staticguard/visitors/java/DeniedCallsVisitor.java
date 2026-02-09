package com.staticguard.visitors.java;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.staticguard.common.RuleContext;

import java.util.Map;
import java.util.Set;

public class DeniedCallsVisitor extends VoidVisitorAdapter<RuleContext> {

    private final Map<String, Set<String>> deniedCalls;

    private String currentMethod = null;

    public DeniedCallsVisitor(Map<String, Set<String>> deniedCalls) {
        this.deniedCalls = deniedCalls;
    }

    @Override
    public void visit(MethodDeclaration n, RuleContext ctx) {
        currentMethod = n.getNameAsString();
        super.visit(n, ctx);
        currentMethod = null;
    }

    @Override
    public void visit(MethodCallExpr n, RuleContext ctx) {
        super.visit(n, ctx);

        if (currentMethod == null) return;

        String call = n.getNameAsString();

        Set<String> forbidden = deniedCalls.get(currentMethod);
        if (forbidden != null && forbidden.contains(call)) {
            ctx.report(
                    "Method '" + currentMethod + "' is not allowed to call '" + call + "'",
                    n.getBegin().map(p -> p.line).orElse(-1)
            );
        }
    }
}
