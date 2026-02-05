package com.staticguard.rules.c;

import com.staticguard.common.RuleContext;
import com.staticguard.common.RuleVisitor;
import com.staticguard.visitors.c.CForbiddenFunctionVisitor;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Set;

public class CForbiddenFunctionRule implements RuleVisitor<ParseTree> {
    private final Set<String> forbiddenCalls;

    public CForbiddenFunctionRule(Set<String> forbiddenCalls) {
        this.forbiddenCalls = forbiddenCalls;
    }

    @Override
    public void run(ParseTree astRoot, RuleContext context) {
        astRoot.accept(new CForbiddenFunctionVisitor(context, forbiddenCalls));
    }
}
