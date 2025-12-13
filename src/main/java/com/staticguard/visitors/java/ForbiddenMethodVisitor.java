package com.staticguard.visitors.java;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.staticguard.common.RuleContext;

import java.util.Set;

public class ForbiddenMethodVisitor extends VoidVisitorAdapter<RuleContext> {
    private final Set<String> forbiddenCalls;

    public ForbiddenMethodVisitor(final Set<String> forbiddenCalls) {
        super();
        this.forbiddenCalls = forbiddenCalls;
    }

    @Override
    public void visit(MethodCallExpr n, RuleContext ctx) {
        super.visit(n, ctx);

        String call = resolveCallName(n);

        if (forbiddenCalls.contains(call)) {
            ctx.report(
                    "Forbidden method call: " + call,
                    n.getBegin().map(p -> p.line).orElse(-1)
            );
        }
    }

    private String resolveCallName(MethodCallExpr n) {
        if (n.getScope().isPresent()) {
            return n.getScope().get().toString() + "." + n.getNameAsString();
        }
        return n.getNameAsString();
    }
}
