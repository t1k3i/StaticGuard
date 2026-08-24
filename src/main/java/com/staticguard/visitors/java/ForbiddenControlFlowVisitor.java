package com.staticguard.visitors.java;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.InstanceOfExpr;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.ContinueStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.staticguard.common.RuleContext;
import com.staticguard.enums.ControlFlowRule;

import java.util.Set;

public class ForbiddenControlFlowVisitor extends VoidVisitorAdapter<RuleContext> {
    private final Set<ControlFlowRule> forbiddenRules;

    public ForbiddenControlFlowVisitor(Set<ControlFlowRule> forbiddenRules) {
        this.forbiddenRules = forbiddenRules;
    }

    @Override
    public void visit(BreakStmt n, RuleContext ctx) {
        if (forbiddenRules.contains(ControlFlowRule.BREAK)) {
            report(ctx, "Forbidden control flow statement: break", n);
        }
        super.visit(n, ctx);
    }

    @Override
    public void visit(ContinueStmt n, RuleContext ctx) {
        if (forbiddenRules.contains(ControlFlowRule.CONTINUE)) {
            report(ctx, "Forbidden control flow statement: continue", n);
        }
        super.visit(n, ctx);
    }

    @Override
    public void visit(ReturnStmt n, RuleContext ctx) {
        if (forbiddenRules.contains(ControlFlowRule.RETURN)) {
            report(ctx, "Forbidden control flow statement: return", n);
        }
        super.visit(n, ctx);
    }

    @Override
    public void visit(InstanceOfExpr n, RuleContext ctx) {
        if (forbiddenRules.contains(ControlFlowRule.INSTANCEOF)) {
            report(ctx, "Forbidden language construct: instanceof", n);
        }
        super.visit(n, ctx);
    }

    private void report(RuleContext ctx, String message, Node node) {
        ctx.report(
                message,
                node.getBegin().map(p -> p.line).orElse(-1)
        );
    }
}
