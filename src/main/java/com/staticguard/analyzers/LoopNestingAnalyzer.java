package com.staticguard.analyzers;

import com.staticguard.common.RuleContext;
import com.staticguard.rules.LoopNestingRule;

public class LoopNestingAnalyzer<T> implements Analyzer<T> {
    private final RuleContext context;
    private final LoopNestingRule<T> rule;

    public LoopNestingAnalyzer(
            RuleContext context,
            LoopNestingRule<T> rule
    ) {
        this.context = context;
        this.rule = rule;
    }

    @Override
    public void runVisitor(T ast) {
        rule.run(ast, context);
    }

    @Override
    public void postVisit() {
        System.out.println(rule.getMaxDepth());
    }
}
