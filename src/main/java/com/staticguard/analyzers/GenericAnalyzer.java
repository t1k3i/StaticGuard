package com.staticguard.analyzers;

import com.staticguard.common.RuleContext;
import com.staticguard.rules.RuleVisitor;

public class GenericAnalyzer<T> implements Analyzer<T> {

    private final RuleContext context;
    protected final RuleVisitor<T> visitor;

    public GenericAnalyzer(RuleContext context, RuleVisitor<T> visitor) {
        this.context = context;
        this.visitor = visitor;
    }

    @Override
    public void runVisitor(T ast) {
        visitor.run(ast, context);
    }

    @Override
    public void postVisit() {
        // Do nothing
    }
}
