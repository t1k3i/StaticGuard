package com.staticguard.visitors.java;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
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

        Set<String> forbidden = deniedCalls.get(currentMethod);

        if (forbidden == null) return;

        try {
            ResolvedMethodDeclaration method = n.resolve();

            String declaringType = method.declaringType().getQualifiedName();
            String methodName = method.getName();
            String qualifiedCall = declaringType + "." + methodName;

            if (forbidden.contains(methodName)
                    || forbidden.contains(qualifiedCall)) {

                String reportedCall = forbidden.contains(qualifiedCall)
                        ? qualifiedCall
                        : methodName;

                ctx.report(
                        "Method '" + currentMethod +
                                "' is not allowed to call '" + reportedCall + "'",
                        n.getBegin().map(p -> p.line).orElse(-1)
                );
            }
        } catch (RuntimeException ignored) {
        }
    }
}