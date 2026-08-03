package com.staticguard.rules.java;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.common.RuleContext;
import com.staticguard.rules.RuleVisitor;
import com.staticguard.visitors.java.PrimitiveTypeVisitor;

import java.util.Collections;
import java.util.Set;

public class PrimitiveTypeRule<T extends CompilationUnit> implements RuleVisitor<T> {
    private final PrimitiveTypeVisitor.Mode mode;
    private final Set<String> exceptions;

    public PrimitiveTypeRule(PrimitiveTypeVisitor.Mode mode) {
        this(mode, Collections.emptySet());
    }

    public PrimitiveTypeRule(PrimitiveTypeVisitor.Mode mode, Set<String> exceptions) {
        this.mode = mode;
        this.exceptions = exceptions != null ? exceptions : Collections.emptySet();
    }

    @Override
    public void run(T astRoot, RuleContext context) {
        astRoot.accept(new PrimitiveTypeVisitor(mode, exceptions), context);
    }
}
