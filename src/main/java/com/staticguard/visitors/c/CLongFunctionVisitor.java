package com.staticguard.visitors.c;

import com.staticguard.CBaseVisitor;
import com.staticguard.CParser;
import com.staticguard.common.RuleContext;

public class CLongFunctionVisitor extends CBaseVisitor<Void> {
    private final RuleContext context;
    private final int maxLines;

    public CLongFunctionVisitor(RuleContext context, int maxLines) {
        this.context = context;
        this.maxLines = maxLines;
    }

    @Override
    public Void visitFunctionDefinition(CParser.FunctionDefinitionContext ctx) {
        super.visitFunctionDefinition(ctx);

        int start = ctx.getStart() != null ? ctx.getStart().getLine() : -1;
        int end = ctx.getStop() != null ? ctx.getStop().getLine() : -1;

        if (start >= 0 && end >= 0) {
            int length = end - start + 1;
            if (length > maxLines) {
                context.report(
                        "Function is too long: " + length + " lines (max allowed: " + maxLines + ")",
                        start
                );
            }
        }

        return null;
    }
}
