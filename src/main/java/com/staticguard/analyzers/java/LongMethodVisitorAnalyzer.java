package com.staticguard.analyzers.java;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.analyzers.Analyzer;
import com.staticguard.common.RuleContext;
import com.staticguard.visitors.java.LongMethodVisitor;

public class LongMethodVisitorAnalyzer implements Analyzer<CompilationUnit> {
    private final RuleContext context;
    private final int maxLines;

    public LongMethodVisitorAnalyzer(RuleContext context, int maxLines) {
        this.context = context;
        this.maxLines = maxLines;
    }

    @Override
    public void runVisitor(CompilationUnit cu) {
        cu.accept(new LongMethodVisitor(maxLines), context);
    }

    @Override
    public void postVisit(CompilationUnit cu) {
        context.getIssues().forEach(System.out::println);
    }
}
