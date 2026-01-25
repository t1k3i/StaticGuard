package com.staticguard.analyzers.java;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.analyzers.Analyzer;
import com.staticguard.common.RuleContext;
import com.staticguard.enums.TypeContext;
import com.staticguard.visitors.java.ForbiddenTypesVisitor;

import java.util.Set;

public class ForbiddenTypesVisitorAnalyzer implements Analyzer<CompilationUnit> {

    private final RuleContext arg;
    private final Set<String> forbiddenTypes;
    private final Set<TypeContext> forbiddenContexts; // optional: can be null

    public ForbiddenTypesVisitorAnalyzer(RuleContext arg,
                                         Set<String> forbiddenTypes,
                                         Set<TypeContext> forbiddenContexts) {
        this.arg = arg;
        this.forbiddenTypes = forbiddenTypes;
        this.forbiddenContexts = forbiddenContexts;
    }

    @Override
    public void runVisitor(CompilationUnit cu) {
        cu.accept(new ForbiddenTypesVisitor(forbiddenTypes, forbiddenContexts), arg);
    }

    @Override
    public void postVisit(CompilationUnit cu) {
        arg.getIssues().forEach(System.out::println);
    }
}
