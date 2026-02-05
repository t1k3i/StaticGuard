package com.staticguard.visitors.c;

import com.staticguard.CBaseVisitor;
import com.staticguard.CParser;
import com.staticguard.common.RuleContext;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Map;
import java.util.Set;

public class DeniedCallsVisitor extends CBaseVisitor<Void> {
    private final RuleContext context;
    private final Map<String, Set<String>> deniedCalls;

    public DeniedCallsVisitor(RuleContext context, Map<String, Set<String>> deniedCalls) {
        this.context = context;
        this.deniedCalls = deniedCalls;
    }

    private String currentFunction = null;

    private void report(String message, ParserRuleContext ctx) {
        if (ctx != null && ctx.getStart() != null) {
            context.report(message, ctx.getStart().getLine());
        } else {
            context.report(message, -1);
        }
    }

    @Override
    public Void visitFunctionDefinition(CParser.FunctionDefinitionContext ctx) {
        var decl = ctx.declarator();
        if (decl != null && decl.directDeclarator() != null) {
            var id = decl.directDeclarator()
                    .directDeclarator()
                    .Identifier();

            if (id != null) {
                currentFunction = id.getText();
            }
        }

        super.visitFunctionDefinition(ctx);
        currentFunction = null;
        return null;
    }

    @Override
    public Void visitPostfixExpression(CParser.PostfixExpressionContext ctx) {
        if (currentFunction != null
                && ctx.primaryExpression() != null
                && ctx.primaryExpression().Identifier() != null
                && ctx.LeftParen() != null) {

            String calledFunction =
                    ctx.primaryExpression().Identifier().getText();

            Set<String> forbidden = deniedCalls.get(currentFunction);
            if (forbidden != null && forbidden.contains(calledFunction)) {
                report(
                        "Function '" + currentFunction +
                                "' is not allowed to call '" + calledFunction + "'",
                        ctx
                );
            }
        }

        return super.visitPostfixExpression(ctx);
    }
}
