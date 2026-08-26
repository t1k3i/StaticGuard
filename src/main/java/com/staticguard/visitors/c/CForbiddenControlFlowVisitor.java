package com.staticguard.visitors.c;

import com.staticguard.CBaseVisitor;
import com.staticguard.CParser;
import com.staticguard.common.RuleContext;
import com.staticguard.enums.ControlFlowRule;

import java.util.Set;

import static com.staticguard.visitors.c.CVisitorHelper.report;

public class CForbiddenControlFlowVisitor extends CBaseVisitor<Void> {
    private final RuleContext context;
    private final Set<ControlFlowRule> forbiddenRules;

    public CForbiddenControlFlowVisitor(RuleContext context, Set<ControlFlowRule> forbiddenRules) {
        this.context = context;
        this.forbiddenRules = forbiddenRules;
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
