package com.staticguard.analyzers.java;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.analyzers.Analyzer;
import com.staticguard.common.RuleContext;
import com.staticguard.visitors.java.UnusedLocalVariablesVisitor;

public class UnusedLocalVariablesVisitorAnalyzer implements Analyzer<CompilationUnit> {
    private final RuleContext context;

    public UnusedLocalVariablesVisitorAnalyzer(RuleContext context) {
        this.context = context;
    }

    @Override
    public void runVisitor(CompilationUnit cu) {
        cu.accept(new UnusedLocalVariablesVisitor(), context);
    }

    @Override
    public void postVisit(CompilationUnit cu) {
        context.getIssues().forEach(System.out::println);
    }
}
