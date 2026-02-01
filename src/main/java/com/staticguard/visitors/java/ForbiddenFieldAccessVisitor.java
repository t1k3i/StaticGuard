package com.staticguard.visitors.java;

import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.staticguard.common.RuleContext;

public class ForbiddenFieldAccessVisitor extends VoidVisitorAdapter<RuleContext> {
    @Override
    public void visit(FieldAccessExpr n, RuleContext ctx) {
        ctx.report(
                "Forbidden direct field access: " + n.toString(),
                n.getBegin().map(p -> p.line).orElse(-1)
        );
        super.visit(n, ctx);
    }
}
