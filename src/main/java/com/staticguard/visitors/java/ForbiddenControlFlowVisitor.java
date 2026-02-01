package com.staticguard.visitors.java;

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
            ctx.report(
                    "Forbidden control flow statement: break",
                    n.getBegin().map(p -> p.line).orElse(-1)
            );
        }
        super.visit(n, ctx);
    }

    @Override
    public void visit(ContinueStmt n, RuleContext ctx) {
        if (forbiddenRules.contains(ControlFlowRule.CONTINUE)) {
            ctx.report(
                    "Forbidden control flow statement: continue",
                    n.getBegin().map(p -> p.line).orElse(-1)
            );
        }
        super.visit(n, ctx);
    }

    @Override
    public void visit(ReturnStmt n, RuleContext ctx) {
        if (forbiddenRules.contains(ControlFlowRule.RETURN)) {
            ctx.report(
                    "Forbidden control flow statement: return",
                    n.getBegin().map(p -> p.line).orElse(-1)
            );
        }
        super.visit(n, ctx);
    }

    @Override
    public void visit(InstanceOfExpr n, RuleContext ctx) {
        if (forbiddenRules.contains(ControlFlowRule.INSTANCEOF)) {
            ctx.report(
                    "Forbidden language construct: instanceof",
                    n.getBegin().map(p -> p.line).orElse(-1)
            );
        }
        super.visit(n, ctx);
    }
}
