package com.staticguard.visitors.c;

import com.staticguard.CBaseVisitor;
import com.staticguard.CParser;
import com.staticguard.common.RuleContext;
import com.staticguard.enums.ControlFlowRule;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Set;

public class CForbiddenControlFlowVisitor extends CBaseVisitor<Void> {
    private final RuleContext context;
    private final Set<ControlFlowRule> forbiddenRules;

    public CForbiddenControlFlowVisitor(RuleContext context, Set<ControlFlowRule> forbiddenRules) {
        this.context = context;
        this.forbiddenRules = forbiddenRules;
    }

    private void report(String message, ParserRuleContext ctx, RuleContext context) {
        int line = ctx != null && ctx.getStart() != null ? ctx.getStart().getLine() : -1;
        context.report(message, line);
    }

    @Override
    public Void visitJumpStatement(CParser.JumpStatementContext ctx) {
        if (ctx.Return() != null && forbiddenRules.contains(ControlFlowRule.RETURN)) {
            report("Forbidden control flow statement: return", ctx, context);
        }

        if (ctx.Break() != null && forbiddenRules.contains(ControlFlowRule.BREAK)) {
            report("Forbidden control flow statement: break", ctx, context);
        }

        if (ctx.Continue() != null && forbiddenRules.contains(ControlFlowRule.CONTINUE)) {
            report("Forbidden control flow statement: continue", ctx, context);
        }

        if (ctx.Goto() != null && forbiddenRules.contains(ControlFlowRule.GOTO)) {
            report("Forbidden control flow statement: goto", ctx, context);
        }

        return super.visitJumpStatement(ctx);
    }
}
