package com.staticguard.rules.c;

import com.staticguard.common.RuleContext;
import com.staticguard.common.RuleVisitor;
import com.staticguard.visitors.c.CallGraphVisitor;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CCallGraphRule implements RuleVisitor<ParseTree> {
    private final Map<String, Set<String>> callGraph = new HashMap<>();

    @Override
    public void run(ParseTree astRoot, RuleContext context) {
        astRoot.accept(new CallGraphVisitor(callGraph));
    }

    public Map<String, Set<String>> getCallGraph() {
        return callGraph;
    }
}
