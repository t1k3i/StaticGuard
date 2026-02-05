package com.staticguard.rules.java;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.common.RuleContext;
import com.staticguard.common.RuleVisitor;
import com.staticguard.visitors.java.LongMethodVisitor;

public class LongMethodRule implements RuleVisitor<CompilationUnit> {
    private final int maxLines;

    public LongMethodRule(int maxLines) {
        this.maxLines = maxLines;
    }

    @Override
    public void run(CompilationUnit astRoot, RuleContext context) {
        astRoot.accept(new LongMethodVisitor(maxLines), context);
    }
}
