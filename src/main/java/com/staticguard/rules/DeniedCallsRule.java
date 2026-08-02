package com.staticguard.rules;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.common.RuleContext;
import com.staticguard.visitors.c.CDeniedCallsVisitor;
import com.staticguard.visitors.java.DeniedCallsVisitor;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Map;
import java.util.Set;

public class DeniedCallsRule<T> implements RuleVisitor<T> {

    private final Map<String, Set<String>> deniedCalls;

    public DeniedCallsRule(Map<String, Set<String>> deniedCalls) {
        this.deniedCalls = deniedCalls;
    }

    @Override
    public void run(T astRoot, RuleContext context) {
        if (astRoot instanceof CompilationUnit root) {
            root.accept(new DeniedCallsVisitor(deniedCalls), context);
        }

        if (astRoot instanceof ParseTree root) {
            root.accept(new CDeniedCallsVisitor(context, deniedCalls));
        }
    }
}
