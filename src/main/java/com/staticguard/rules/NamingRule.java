package com.staticguard.rules;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.common.RuleContext;
import com.staticguard.visitors.c.CNamingVisitor;
import com.staticguard.visitors.java.JavaNamingVisitor;
import org.antlr.v4.runtime.tree.ParseTree;

public class NamingRule<T> implements RuleVisitor<T> {
    @Override
    public void run(T astRoot, RuleContext context) {
        if (astRoot instanceof CompilationUnit root) {
            root.accept(new JavaNamingVisitor(), context);
        }

        if (astRoot instanceof ParseTree root) {
            root.accept(new CNamingVisitor(context));
        }
    }
}
