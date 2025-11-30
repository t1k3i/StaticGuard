package com.staticguard.analyzers.c;

import com.staticguard.common.RuleContext;
import com.staticguard.analyzers.Analyzer;
import com.staticguard.visitors.c.CNamingVisitor;
import org.antlr.v4.runtime.tree.ParseTree;

public class CNamingVisitorAnalyzer implements Analyzer<ParseTree> {
    private final RuleContext arg;

    public CNamingVisitorAnalyzer(RuleContext arg) {
        this.arg = arg;
    }

    @Override
    public void runVisitor(ParseTree root) {
        root.accept(new CNamingVisitor(arg));
    }

    @Override
    public void postVisit(ParseTree root) {
        arg.getIssues().forEach(System.out::println);
    }
}
