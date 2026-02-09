package com.staticguard.rules;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.common.RuleContext;
import com.staticguard.common.RuleVisitor;
import com.staticguard.visitors.c.CCallGraphVisitor;
import com.staticguard.visitors.java.CallGraphVisitor;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CallGraphRule<T> implements RuleVisitor<T> {
    private final Map<String, Set<String>> callGraph = new HashMap<>();

    @Override
    public void run(T astRoot, RuleContext context) {
        if (astRoot instanceof CompilationUnit root) {
            root.accept(new CallGraphVisitor(callGraph), null);
        }

        if (astRoot instanceof ParseTree root) {
            root.accept(new CCallGraphVisitor(callGraph));
        }
    }

    public Map<String, Set<String>> getCallGraph() {
        return callGraph;
    }
}
