package com.staticguard.analyzers.java;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.common.RuleContext;
import com.staticguard.analyzers.Analyzer;
import com.staticguard.visitors.java.JavaNamingVisitor;

public class JavaNamingVisitorAnalyzer implements Analyzer<CompilationUnit> {
    private final boolean addComments;
    private final RuleContext arg;

    public JavaNamingVisitorAnalyzer(RuleContext arg, boolean addComments) {
        this.arg = arg;
        this.addComments = addComments;
    }

    public JavaNamingVisitorAnalyzer(RuleContext arg) {
        this.arg = arg;
        this.addComments = false;
    }

    @Override
    public void runVisitor(CompilationUnit cu) {
        cu.accept(new JavaNamingVisitor(addComments), arg);
    }

    @Override
    public void postVisit(CompilationUnit cu) {
        arg.getIssues().forEach(System.out::println);
    }
}
