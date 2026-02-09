package com.staticguard.rules.java;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.common.RuleContext;
import com.staticguard.common.RuleVisitor;
import com.staticguard.visitors.java.PrimitiveTypeVisitor;

public class PrimitiveTypeRule<T extends CompilationUnit> implements RuleVisitor<T> {
    private final PrimitiveTypeVisitor.Mode mode;

    public PrimitiveTypeRule(PrimitiveTypeVisitor.Mode mode) {
        this.mode = mode;
    }

    @Override
    public void run(T astRoot, RuleContext context) {
        astRoot.accept(new PrimitiveTypeVisitor(mode), context);
    }
}
