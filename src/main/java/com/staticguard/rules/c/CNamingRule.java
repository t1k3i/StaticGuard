package com.staticguard.rules.c;

import com.staticguard.common.RuleContext;
import com.staticguard.common.RuleVisitor;
import com.staticguard.visitors.c.CNamingVisitor;
import org.antlr.v4.runtime.tree.ParseTree;

public class CNamingRule implements RuleVisitor<ParseTree> {
    @Override
    public void run(ParseTree astRoot, RuleContext context) {
        astRoot.accept(new CNamingVisitor(context));
    }
}
