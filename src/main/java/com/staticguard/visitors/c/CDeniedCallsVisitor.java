package com.staticguard.visitors.c;

import com.staticguard.CBaseVisitor;
import com.staticguard.CParser;
import com.staticguard.common.RuleContext;

import java.util.Map;
import java.util.Set;

import static com.staticguard.visitors.c.CVisitorHelper.report;

public class CDeniedCallsVisitor extends CBaseVisitor<Void> {
    private final RuleContext context;
    private final Map<String, Set<String>> deniedCalls;

    public CDeniedCallsVisitor(RuleContext context, Map<String, Set<String>> deniedCalls) {
        this.context = context;
        this.deniedCalls = deniedCalls;
    }

    private String currentFunction = null;

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
                && ctx.LeftParen() != null && !ctx.LeftParen().isEmpty()) {

            String calledFunction =
                    ctx.primaryExpression().Identifier().getText();

            Set<String> forbidden = deniedCalls.get(currentFunction);
            if (forbidden != null && forbidden.contains(calledFunction)) {
                report(
                        "Function '" + currentFunction +
                                "' is not allowed to call '" + calledFunction + "'",
                        ctx,
                        context
                );
            }
        }

        return super.visitPostfixExpression(ctx);
    }
}
