package com.staticguard.visitors.java;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.staticguard.common.RuleContext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UnusedLocalVariablesVisitor extends VoidVisitorAdapter<RuleContext> {
    private final Map<String, Integer> declaredVars = new HashMap<>();
    private final Set<String> usedVars = new HashSet<>();

    @Override
    public void visit(MethodDeclaration n, RuleContext ctx) {
        declaredVars.clear();
        usedVars.clear();

        super.visit(n, ctx);

        for (var entry : declaredVars.entrySet()) {
            String var = entry.getKey();
            int line = entry.getValue();

            if (!usedVars.contains(var)) {
                ctx.report(
                        "Unused local variable: " + var,
                        line
                );
            }
        }
    }

    @Override
    public void visit(VariableDeclarator n, RuleContext ctx) {
        declaredVars.put(
                n.getNameAsString(),
                n.getBegin().map(p -> p.line).orElse(-1)
        );
        super.visit(n, ctx);
    }

    @Override
    public void visit(Parameter n, RuleContext ctx) {
        declaredVars.put(
                n.getNameAsString(),
                n.getBegin().map(p -> p.line).orElse(-1)
        );
        super.visit(n, ctx);
    }

    @Override
    public void visit(NameExpr n, RuleContext ctx) {
        usedVars.add(n.getNameAsString());
        super.visit(n, ctx);
    }
}