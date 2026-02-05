package com.staticguard.visitors.c;

import com.staticguard.CBaseVisitor;
import com.staticguard.CParser;
import com.staticguard.common.RuleContext;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Set;

public class CForbiddenFunctionVisitor extends CBaseVisitor<Void> {
    private final RuleContext context;
    private final Set<String> forbiddenCalls;

    public CForbiddenFunctionVisitor(RuleContext context, Set<String> forbiddenCalls) {
        this.context = context;
        this.forbiddenCalls = forbiddenCalls;
    }

    private void report(String message, ParserRuleContext ctx, RuleContext context) {
        int line = ctx != null && ctx.getStart() != null ? ctx.getStart().getLine() : -1;
        context.report(message, line);
    }

    @Override
    public Void visitPostfixExpression(CParser.PostfixExpressionContext ctx) {
        super.visitPostfixExpression(ctx);

        if (ctx.primaryExpression() != null &&
                ctx.primaryExpression().Identifier() != null &&
                ctx.LeftParen() != null) {

            String functionName = ctx.primaryExpression().Identifier().getText();

            if (forbiddenCalls.contains(functionName)) {
                report("Forbidden function call: " + functionName, ctx, context);
            }
        }

        return null;
    }
}
