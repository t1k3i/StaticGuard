package com.staticguard.rules;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.common.RuleContext;
import com.staticguard.visitors.c.CLoopNestingVisitor;
import com.staticguard.visitors.java.LoopNestingVisitor;
import org.antlr.v4.runtime.tree.ParseTree;

public class LoopNestingRule<T> implements RuleVisitor<T> {
    private int maxDepth;

    @Override
    public void run(T astRoot, RuleContext context) {
        if (astRoot instanceof CompilationUnit root) {
            LoopNestingVisitor visitor = new LoopNestingVisitor();
            root.accept(visitor, null);
            maxDepth = visitor.getMaxDepth();
        }

        if (astRoot instanceof ParseTree root) {
            CLoopNestingVisitor visitor = new CLoopNestingVisitor();
            root.accept(visitor);
            maxDepth = visitor.getMaxDepth();
        }
    }

    public int getMaxDepth() {
        return maxDepth;
    }
}
