package com.staticguard.analyzers.java;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.analyzers.Analyzer;
import com.staticguard.common.RuleContext;
import com.staticguard.visitors.java.PrimitiveTypeVisitor;

public class PrimitiveTypeVisitorAnalyzer implements Analyzer<CompilationUnit> {
    private final PrimitiveTypeVisitor.Mode mode;
    private final RuleContext arg;

    public PrimitiveTypeVisitorAnalyzer(PrimitiveTypeVisitor.Mode mode, RuleContext arg) {
        this.mode = mode;
        this.arg = arg;
    }

    @Override
    public void runVisitor(CompilationUnit cu) {
        cu.accept(new PrimitiveTypeVisitor(mode), arg);
    }

    @Override
    public void postVisit(CompilationUnit cu) {
        arg.getIssues().forEach(System.out::println);
    }
}
