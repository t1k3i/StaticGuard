package com.staticguard.rules.java;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.common.RuleContext;
import com.staticguard.rules.RuleVisitor;
import com.staticguard.visitors.java.UnusedImportsVisitor;

public class UnusedImportsRule<T extends CompilationUnit> implements RuleVisitor<T> {
    @Override
    public void run(T astRoot, RuleContext context) {
        astRoot.accept(new UnusedImportsVisitor(), context);
    }
}
