package com.staticguard.rules.c;

import com.staticguard.common.RuleContext;
import com.staticguard.common.RuleVisitor;
import com.staticguard.visitors.c.CLongFunctionVisitor;
import org.antlr.v4.runtime.tree.ParseTree;

public class CLongFunctionRule implements RuleVisitor<ParseTree> {
    private final int maxLines;

    public CLongFunctionRule(int maxLines) {
        this.maxLines = maxLines;
    }
    @Override
    public void run(ParseTree astRoot, RuleContext context) {
        astRoot.accept(new CLongFunctionVisitor(context, maxLines));
    }
}
