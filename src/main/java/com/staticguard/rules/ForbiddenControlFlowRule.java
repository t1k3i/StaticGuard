package com.staticguard.rules;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.common.RuleContext;
import com.staticguard.common.RuleVisitor;
import com.staticguard.enums.ControlFlowRule;
import com.staticguard.visitors.c.CForbiddenControlFlowVisitor;
import com.staticguard.visitors.c.CForbiddenFunctionVisitor;
import com.staticguard.visitors.java.ForbiddenControlFlowVisitor;
import com.staticguard.visitors.java.ForbiddenMethodVisitor;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Set;

public class ForbiddenControlFlowRule<T> implements RuleVisitor<T> {
    private final Set<ControlFlowRule> forbiddenRules;

    public ForbiddenControlFlowRule(Set<ControlFlowRule> forbiddenRules) {
        this.forbiddenRules = forbiddenRules;
    }

    @Override
    public void run(T astRoot, RuleContext context) {
        if (astRoot instanceof CompilationUnit root) {
            root.accept(new ForbiddenControlFlowVisitor(forbiddenRules), context);
        }

        if (astRoot instanceof ParseTree root) {
            root.accept(new CForbiddenControlFlowVisitor(context, forbiddenRules));
        }
    }
}
