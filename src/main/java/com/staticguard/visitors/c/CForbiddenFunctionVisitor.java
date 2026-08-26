package com.staticguard.visitors.c;

import com.staticguard.CBaseVisitor;
import com.staticguard.CParser;
import com.staticguard.common.RuleContext;

import java.util.Set;

import static com.staticguard.visitors.c.CVisitorHelper.report;

public class CForbiddenFunctionVisitor extends CBaseVisitor<Void> {
    private final RuleContext context;
    private final Set<String> forbiddenCalls;

    public CForbiddenFunctionVisitor(RuleContext context, Set<String> forbiddenCalls) {
        this.context = context;
        this.forbiddenCalls = forbiddenCalls;
    }

    @Override
    public Void visitPostfixExpression(CParser.PostfixExpressionContext ctx) {
        super.visitPostfixExpression(ctx);

        if (ctx.primaryExpression() != null &&
                ctx.primaryExpression().Identifier() != null &&
                ctx.LeftParen() != null && !ctx.LeftParen().isEmpty()) {

            String functionName = ctx.primaryExpression().Identifier().getText();

            if (forbiddenCalls.contains(functionName)) {
                report("Forbidden function call: " + functionName, ctx, context);
            }
        }

        return null;
    }
}
