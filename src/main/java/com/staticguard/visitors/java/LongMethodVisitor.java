package com.staticguard.visitors.java;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.staticguard.common.RuleContext;

public class LongMethodVisitor extends VoidVisitorAdapter<RuleContext> {
    private final int maxLines;

    public LongMethodVisitor(int maxLines) {
        this.maxLines = maxLines;
    }

    @Override
    public void visit(MethodDeclaration n, RuleContext ctx) {
        super.visit(n, ctx);

        int start = n.getBegin().map(p -> p.line).orElse(-1);
        int end = n.getEnd().map(p -> p.line).orElse(-1);

        if (start >= 0 && end >= 0) {
            int length = end - start + 1;
            if (length > maxLines) {
                ctx.report(
                        "Method '" + n.getNameAsString() + "' is too long: " + length + " lines (max allowed: " + maxLines + ")",
                        start
                );
            }
        }
    }
}
