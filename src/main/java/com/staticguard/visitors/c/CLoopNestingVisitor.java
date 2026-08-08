package com.staticguard.visitors.c;

import com.staticguard.CBaseVisitor;
import com.staticguard.CParser;

public class CLoopNestingVisitor extends CBaseVisitor<Void> {

    private int currentDepth;
    private int maxDepth;

    public int getMaxDepth() {
        return maxDepth;
    }

    private void enterLoop() {
        currentDepth++;
        if (currentDepth > maxDepth) {
            maxDepth = currentDepth;
        }
    }

    private void exitLoop() {
        currentDepth--;
    }

    @Override
    public Void visitIterationStatement(CParser.IterationStatementContext ctx) {
        enterLoop();
        super.visitIterationStatement(ctx);
        exitLoop();
        return null;
    }
}
