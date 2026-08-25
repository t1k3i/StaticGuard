package com.staticguard.visitors.c;

import com.staticguard.CBaseVisitor;
import com.staticguard.CParser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CCallGraphVisitor extends CBaseVisitor<Void> {
    private final Map<String, Set<String>> callGraph;

    public CCallGraphVisitor(Map<String, Set<String>> callGraph) {
        this.callGraph = callGraph;
    }

    private String currentFunction = null;

    @Override
    public Void visitFunctionDefinition(CParser.FunctionDefinitionContext ctx) {
        var decl = ctx.declarator();
        if (decl != null && decl.directDeclarator() != null) {
            var idNode = decl.directDeclarator()
                    .directDeclarator()
                    .Identifier();
            if (idNode != null) {
                currentFunction = idNode.getText();
                callGraph.putIfAbsent(currentFunction, new HashSet<>());
            }
        }

        super.visitFunctionDefinition(ctx);
        currentFunction = null;
        return null;
    }

    @Override
    public Void visitPostfixExpression(CParser.PostfixExpressionContext ctx) {

        if (currentFunction == null)
            return null;

        boolean isDirectFunctionCall = false;

        for (int i = 0; i < ctx.getChildCount(); i++) {

            ParseTree child = ctx.getChild(i);

            if (child instanceof TerminalNode terminal) {

                if (terminal.getSymbol().getType()
                        == CParser.LeftParen) {

                    isDirectFunctionCall = true;
                    break;
                }
            }
        }

        if (isDirectFunctionCall) {

            var primary = ctx.primaryExpression();

            if (primary != null &&
                    primary.Identifier() != null) {

                String calledFunction =
                        primary.Identifier().getText();

                callGraph
                        .computeIfAbsent(
                                currentFunction,
                                k -> new HashSet<>()
                        )
                        .add(calledFunction);
            }
        }

        return super.visitPostfixExpression(ctx);
    }
}
