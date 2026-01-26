package com.staticguard.visitors.java;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.staticguard.common.RuleContext;

public class PrimitiveTypeVisitor extends VoidVisitorAdapter<RuleContext> {
    public enum Mode {
        ONLY_PRIMITIVE,
        NO_PRIMITIVE
    }

    private final Mode mode;

    public PrimitiveTypeVisitor(Mode mode) {
        this.mode = mode;
    }

    @Override
    public void visit(FieldDeclaration n, RuleContext ctx) {
        super.visit(n, ctx);
        checkType(n.getElementType(), n, ctx);
    }

    @Override
    public void visit(VariableDeclarator n, RuleContext ctx) {
        super.visit(n, ctx);
        checkType(n.getType(), n, ctx);
    }

    @Override
    public void visit(Parameter n, RuleContext ctx) {
        super.visit(n, ctx);
        checkType(n.getType(), n, ctx);
    }

    @Override
    public void visit(MethodDeclaration n, RuleContext ctx) {
        super.visit(n, ctx);
        checkType(n.getType(), n, ctx);
    }

    private void checkType(Type type, Node node, RuleContext ctx) {
        boolean isPrimitive = type.isPrimitiveType();

        if (mode == Mode.ONLY_PRIMITIVE && !isPrimitive) {
            ctx.report(
                    "Only primitive types are allowed, found: " + type,
                    node.getBegin().map(p -> p.line).orElse(-1)
            );
        }

        if (mode == Mode.NO_PRIMITIVE && isPrimitive) {
            ctx.report(
                    "Primitive types are not allowed, found: " + type,
                    node.getBegin().map(p -> p.line).orElse(-1)
            );
        }
    }
}
