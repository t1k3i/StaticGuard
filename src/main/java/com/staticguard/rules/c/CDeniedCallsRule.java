package com.staticguard.rules.c;

import com.staticguard.common.RuleContext;
import com.staticguard.common.RuleVisitor;
import com.staticguard.visitors.c.DeniedCallsVisitor;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Map;
import java.util.Set;

public class CDeniedCallsRule implements RuleVisitor<ParseTree> {

    private final Map<String, Set<String>> deniedCalls;

    public CDeniedCallsRule(Map<String, Set<String>> deniedCalls) {
        this.deniedCalls = deniedCalls;
    }

    @Override
    public void run(ParseTree astRoot, RuleContext context) {
        astRoot.accept(new DeniedCallsVisitor(context, deniedCalls));
    }
}
