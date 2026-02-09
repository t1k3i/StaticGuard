package com.staticguard.rules;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.common.RuleContext;
import com.staticguard.common.RuleVisitor;
import com.staticguard.visitors.c.CLoopNestingVisitor;
import com.staticguard.visitors.java.LoopNestingVisitor;
import org.antlr.v4.runtime.tree.ParseTree;

public class LoopNestingRule<T> implements RuleVisitor<T> {
    private final Integer maxDepth = 0;

    @Override
    public void run(T astRoot, RuleContext context) {
        if (astRoot instanceof CompilationUnit root) {
            root.accept(new LoopNestingVisitor(maxDepth), null);
        }

        if (astRoot instanceof ParseTree root) {
            root.accept(new CLoopNestingVisitor(maxDepth));
        }
    }

    public int getMaxDepth() {
        return maxDepth;
    }
}
