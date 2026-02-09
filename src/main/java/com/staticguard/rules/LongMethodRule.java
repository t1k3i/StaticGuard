package com.staticguard.rules;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.common.RuleContext;
import com.staticguard.common.RuleVisitor;
import com.staticguard.visitors.c.CLongFunctionVisitor;
import com.staticguard.visitors.c.CLoopNestingVisitor;
import com.staticguard.visitors.java.LongMethodVisitor;
import com.staticguard.visitors.java.LoopNestingVisitor;
import org.antlr.v4.runtime.tree.ParseTree;

public class LongMethodRule<T> implements RuleVisitor<T> {
    private final int maxLines;

    public LongMethodRule(int maxLines) {
        this.maxLines = maxLines;
    }

    @Override
    public void run(T astRoot, RuleContext context) {
        if (astRoot instanceof CompilationUnit root) {
            root.accept(new LongMethodVisitor(maxLines), context);
        }

        if (astRoot instanceof ParseTree root) {
            root.accept(new CLongFunctionVisitor(context, maxLines));
        }
    }
}
