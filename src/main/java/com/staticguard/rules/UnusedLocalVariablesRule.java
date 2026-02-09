package com.staticguard.rules;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.common.RuleContext;
import com.staticguard.common.RuleVisitor;
import com.staticguard.visitors.c.CUnusedLocalVariableVisitor;
import com.staticguard.visitors.java.UnusedLocalVariablesVisitor;
import org.antlr.v4.runtime.tree.ParseTree;

public class UnusedLocalVariablesRule<T> implements RuleVisitor<T> {
    @Override
    public void run(T astRoot, RuleContext context) {
        if (astRoot instanceof CompilationUnit root) {
            root.accept(new UnusedLocalVariablesVisitor(), context);
        }

        if (astRoot instanceof ParseTree root) {
            root.accept(new CUnusedLocalVariableVisitor(context));
        }
    }
}
