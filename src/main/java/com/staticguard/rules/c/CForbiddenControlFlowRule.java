package com.staticguard.rules.c;

import com.staticguard.common.RuleContext;
import com.staticguard.common.RuleVisitor;
import com.staticguard.enums.ControlFlowRule;
import com.staticguard.visitors.c.CForbiddenControlFlowVisitor;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Set;

public class CForbiddenControlFlowRule implements RuleVisitor<ParseTree> {
    private final Set<ControlFlowRule> forbiddenRules;

    public CForbiddenControlFlowRule(Set<ControlFlowRule> forbiddenRules) {
        this.forbiddenRules = forbiddenRules;
    }

    @Override
    public void run(ParseTree astRoot, RuleContext context) {
        astRoot.accept(new CForbiddenControlFlowVisitor(context, forbiddenRules));
    }
}
