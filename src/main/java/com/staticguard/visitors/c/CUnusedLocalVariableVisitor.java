package com.staticguard.visitors.c;

import com.staticguard.CBaseVisitor;
import com.staticguard.CParser;
import com.staticguard.common.RuleContext;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CUnusedLocalVariableVisitor extends CBaseVisitor<Void> {
    private final RuleContext context;

    public CUnusedLocalVariableVisitor(RuleContext context) {
        this.context = context;
    }

    private final Map<String, Integer> declaredVars = new HashMap<>();
    private final Set<String> usedVars = new HashSet<>();
    private boolean inFunction = false;

    /* ===== Function scope ===== */

    @Override
    public Void visitFunctionDefinition(CParser.FunctionDefinitionContext ctx) {
        declaredVars.clear();
        usedVars.clear();
        inFunction = true;

        // Parameters
        var declarator = ctx.declarator();
        if (declarator != null) {
            collectParameters(declarator);
        }

        super.visitFunctionDefinition(ctx);

        // Report unused locals
        for (var entry : declaredVars.entrySet()) {
            String var = entry.getKey();
            int line = entry.getValue();

            if (!usedVars.contains(var)) {
                report(
                        "Unused local variable: " + var,
                        ctx
                );
            }
        }

        inFunction = false;
        return null;
    }

    /* ===== Variable declarations ===== */

    @Override
    public Void visitDeclarator(CParser.DeclaratorContext ctx) {
        if (!inFunction) return super.visitDeclarator(ctx);

        if (ctx.directDeclarator() != null
                && ctx.directDeclarator().Identifier() != null) {

            String name = ctx.directDeclarator().Identifier().getText();
            int line = ctx.getStart().getLine();

            declaredVars.putIfAbsent(name, line);
        }

        return super.visitDeclarator(ctx);
    }

    /* ===== Variable usage ===== */

    @Override
    public Void visitPrimaryExpression(CParser.PrimaryExpressionContext ctx) {
        if (!inFunction) return super.visitPrimaryExpression(ctx);

        if (ctx.Identifier() != null) {
            usedVars.add(ctx.Identifier().getText());
        }

        return super.visitPrimaryExpression(ctx);
    }

    /* ===== Helpers ===== */

    private void collectParameters(CParser.DeclaratorContext ctx) {
        var direct = ctx.directDeclarator();
        if (direct == null) return;

        if (direct.parameterTypeList() != null) {
            for (var param : direct.parameterTypeList().parameterList().parameterDeclaration()) {
                var decl = param.declarator();
                if (decl != null && decl.directDeclarator() != null
                        && decl.directDeclarator().Identifier() != null) {

                    String name = decl.directDeclarator().Identifier().getText();
                    int line = decl.getStart().getLine();
                    declaredVars.putIfAbsent(name, line);
                }
            }
        }
    }

    private void report(String message, ParserRuleContext ctx) {
        int line = ctx != null && ctx.getStart() != null ? ctx.getStart().getLine() : -1;
        context.report(message, line);
    }
}
