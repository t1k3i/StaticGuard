package com.staticguard.analyzers.java;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.analyzers.Analyzer;
import com.staticguard.visitors.java.LoopNestingVisitor;

public class LoopNestingVisitorAnalyzer implements Analyzer<CompilationUnit> {
    private final LoopNestingVisitor loopNestingVisitor;

    public LoopNestingVisitorAnalyzer() {
        this.loopNestingVisitor = new LoopNestingVisitor();
    }

    @Override
    public void runVisitor(CompilationUnit cu) {
        cu.accept(loopNestingVisitor, null);
    }

    @Override
    public void postVisit(CompilationUnit cu) {
        System.out.println(loopNestingVisitor.getMaxDepth());
    }
}
