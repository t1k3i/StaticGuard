package com.staticguard.visitors.java;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.staticguard.common.RuleContext;

import java.util.Set;

public class ForbiddenMethodVisitor extends VoidVisitorAdapter<RuleContext> {
    private final Set<String> forbiddenCalls;

    public ForbiddenMethodVisitor(final Set<String> forbiddenCalls) {
        this.forbiddenCalls = forbiddenCalls;
    }

    @Override
    public void visit(MethodCallExpr n, RuleContext ctx) {
        super.visit(n, ctx);

        try {
            ResolvedMethodDeclaration method = n.resolve();

            String declaringType = method.declaringType().getQualifiedName();
            String methodName = method.getName();
            String qualifiedCall = declaringType + "." + methodName;

            if (forbiddenCalls.contains(methodName)
                    || forbiddenCalls.contains(qualifiedCall)) {
                ctx.report(
                        "Forbidden method call: " + qualifiedCall,
                        n.getBegin().map(p -> p.line).orElse(-1)
                );
            }
        } catch (RuntimeException ignored) {
        }
    }
}