package com.staticguard.rules;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.common.RuleContext;
import com.staticguard.common.RuleVisitor;
import com.staticguard.visitors.c.CForbiddenFunctionVisitor;
import com.staticguard.visitors.java.ForbiddenMethodVisitor;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Set;

public class ForbiddenFunctionRule<T> implements RuleVisitor<T> {
    private final Set<String> forbiddenCalls;

    public ForbiddenFunctionRule(Set<String> forbiddenCalls) {
        this.forbiddenCalls = forbiddenCalls;
    }

    @Override
    public void run(T astRoot, RuleContext context) {
        if (astRoot instanceof CompilationUnit root) {
            root.accept(new ForbiddenMethodVisitor(forbiddenCalls), context);
        }

        if (astRoot instanceof ParseTree root) {
            root.accept(new CForbiddenFunctionVisitor(context, forbiddenCalls));
        }
    }
}
