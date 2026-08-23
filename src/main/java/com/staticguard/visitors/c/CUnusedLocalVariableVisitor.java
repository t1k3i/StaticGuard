package com.staticguard.visitors.c;

import com.staticguard.CBaseVisitor;
import com.staticguard.CParser;
import com.staticguard.common.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CUnusedLocalVariableVisitor extends CBaseVisitor<Void> {
    private final RuleContext context;

    public CUnusedLocalVariableVisitor(RuleContext context) {
        this.context = context;
    }

    private static class Variable {
        final String name;
        final int line;
        boolean used;

        Variable(String name, int line) {
            this.name = name;
            this.line = line;
        }
    }

    private final List<Variable> declaredVars = new ArrayList<>();
    private final Deque<Map<String, Variable>> scopes = new ArrayDeque<>();

    private boolean inFunction = false;

    @Override
    public Void visitFunctionDefinition(CParser.FunctionDefinitionContext ctx) {
        declaredVars.clear();
        scopes.clear();
        inFunction = true;

        scopes.push(new HashMap<>());

        var declarator = ctx.declarator();
        if (declarator != null) {
            collectParameters(declarator);
        }

        super.visitFunctionDefinition(ctx);

        // Report unused locals and parameters.
        for (Variable variable : declaredVars) {
            if (!variable.used) {
                context.report(
                        "Unused local variable: " + variable.name,
                        variable.line
                );
            }
        }

        scopes.clear();
        inFunction = false;

        return null;
    }

    @Override
    public Void visitCompoundStatement(CParser.CompoundStatementContext ctx) {
        if (!inFunction) {
            return super.visitCompoundStatement(ctx);
        }

        scopes.push(new HashMap<>());

        super.visitCompoundStatement(ctx);

        scopes.pop();

        return null;
    }

    @Override
    public Void visitDeclarator(CParser.DeclaratorContext ctx) {
        if (!inFunction) {
            return super.visitDeclarator(ctx);
        }

        if (ctx.directDeclarator() != null
                && ctx.directDeclarator().Identifier() != null
                && !isStructOrUnionMember(ctx)) {

            String name = ctx.directDeclarator().Identifier().getText();
            int line = ctx.getStart().getLine();

            Map<String, Variable> currentScope = scopes.peek();

            if (currentScope != null && !currentScope.containsKey(name)) {
                Variable variable = new Variable(name, line);

                currentScope.put(name, variable);
                declaredVars.add(variable);
            }
        }

        return super.visitDeclarator(ctx);
    }

    @Override
    public Void visitPrimaryExpression(CParser.PrimaryExpressionContext ctx) {
        if (!inFunction) {
            return super.visitPrimaryExpression(ctx);
        }

        if (ctx.Identifier() != null) {
            String name = ctx.Identifier().getText();

            Variable variable = findVariable(name);

            if (variable != null) {
                variable.used = true;
            }
        }

        return super.visitPrimaryExpression(ctx);
    }

    private void collectParameters(CParser.DeclaratorContext ctx) {
        var direct = ctx.directDeclarator();

        if (direct == null) {
            return;
        }

        if (direct.parameterTypeList() != null) {
            for (var param : direct.parameterTypeList()
                    .parameterList()
                    .parameterDeclaration()) {

                var decl = param.declarator();

                if (decl != null
                        && decl.directDeclarator() != null
                        && decl.directDeclarator().Identifier() != null) {

                    String name =
                            decl.directDeclarator().Identifier().getText();

                    int line = decl.getStart().getLine();

                    Map<String, Variable> functionScope = scopes.peek();

                    if (functionScope != null
                            && !functionScope.containsKey(name)) {

                        Variable variable = new Variable(name, line);

                        functionScope.put(name, variable);
                        declaredVars.add(variable);
                    }
                }
            }
        }
    }

    private Variable findVariable(String name) {
        for (Map<String, Variable> scope : scopes) {
            Variable variable = scope.get(name);

            if (variable != null) {
                return variable;
            }
        }

        return null;
    }

    private boolean isStructOrUnionMember(CParser.DeclaratorContext ctx) {
        ParseTree parent = ctx.getParent();

        while (parent != null) {
            if (parent instanceof CParser.StructOrUnionSpecifierContext) {
                return true;
            }

            parent = parent.getParent();
        }

        return false;
    }
}