package com.staticguard.analyzers.java;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.analyzers.Analyzer;
import com.staticguard.common.RuleContext;
import com.staticguard.enums.ControlFlowRule;
import com.staticguard.visitors.java.ForbiddenControlFlowVisitor;

import java.util.Set;

public class ForbiddenControlFlowVisitorAnalyzer implements Analyzer<CompilationUnit> {
    private final RuleContext arg;
    private final Set<ControlFlowRule> forbiddenRules;

    public ForbiddenControlFlowVisitorAnalyzer(Set<ControlFlowRule> forbiddenRules, RuleContext arg) {
        this.arg = arg;
        this.forbiddenRules = forbiddenRules;
    }

    @Override
    public void runVisitor(CompilationUnit cu) {
        cu.accept(new ForbiddenControlFlowVisitor(forbiddenRules), arg);
    }

    @Override
    public void postVisit(CompilationUnit cu) {
        arg.getIssues().forEach(System.out::println);
    }
}
