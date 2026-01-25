package com.staticguard.analyzers.java;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.analyzers.Analyzer;
import com.staticguard.common.RuleContext;
import com.staticguard.visitors.java.AllowedCallsVisitor;

import java.util.Map;
import java.util.Set;

public class AllowedCallsVisitorAnalyzer implements Analyzer<CompilationUnit>  {
    private final RuleContext arg;
    private final Map<String, Set<String>> allowedCalls;

    public AllowedCallsVisitorAnalyzer(RuleContext arg, Map<String, Set<String>> allowedCalls) {
        this.arg = arg;
        this.allowedCalls = allowedCalls;
    }

    @Override
    public void runVisitor(CompilationUnit cu) {
        cu.accept(new AllowedCallsVisitor(allowedCalls), arg);
    }

    @Override
    public void postVisit(CompilationUnit cu) {
        arg.getIssues().forEach(System.out::println);
    }
}
