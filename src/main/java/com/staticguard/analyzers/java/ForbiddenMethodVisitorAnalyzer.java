package com.staticguard.analyzers.java;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.analyzers.Analyzer;
import com.staticguard.common.RuleContext;
import com.staticguard.visitors.java.ForbiddenMethodVisitor;

import java.util.Set;

public class ForbiddenMethodVisitorAnalyzer implements Analyzer<CompilationUnit> {
    private final RuleContext arg;
    private final Set<String> forbiddenMethods;

    public ForbiddenMethodVisitorAnalyzer(RuleContext arg, Set<String> forbiddenMethods) {
        this.arg = arg;
        this.forbiddenMethods = forbiddenMethods;
    }

    @Override
    public void runVisitor(CompilationUnit cu) {
        cu.accept(new ForbiddenMethodVisitor(forbiddenMethods), arg);
    }

    @Override
    public void postVisit(CompilationUnit cu) {
        arg.getIssues().forEach(System.out::println);
    }
}
