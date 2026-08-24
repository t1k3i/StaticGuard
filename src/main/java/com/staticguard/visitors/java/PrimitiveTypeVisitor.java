package com.staticguard.visitors.java;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.ArrayCreationExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.staticguard.common.RuleContext;

import java.util.Collections;
import java.util.Set;

public class PrimitiveTypeVisitor extends VoidVisitorAdapter<RuleContext> {
    public enum Mode {
        ONLY_PRIMITIVE,
        NO_PRIMITIVE
    }

    private final Mode mode;
    private final Set<String> exceptions;

    public PrimitiveTypeVisitor(Mode mode) {
        this(mode, Collections.emptySet());
    }

    public PrimitiveTypeVisitor(Mode mode, Set<String> exceptions) {
        this.mode = mode;
        this.exceptions = exceptions != null ? exceptions : Collections.emptySet();
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

    @Override
    public void visit(ObjectCreationExpr n, RuleContext ctx) {
        super.visit(n, ctx);
        checkType(n.getType(), n, ctx);
    }

    @Override
    public void visit(ArrayCreationExpr n, RuleContext ctx) {
        super.visit(n, ctx);
        checkType(n.getElementType(), n, ctx);
    }

    private void checkType(Type type, Node node, RuleContext ctx) {
        if (type == null || type.isVoidType()) {
            return;
        }

        boolean isPrimitive = type.isPrimitiveType();
        boolean isExc = isException(type);

        if (mode == Mode.ONLY_PRIMITIVE && !isPrimitive && !isExc) {
            ctx.report(
                    "Only primitive types are allowed, found: " + type,
                    node.getBegin().map(p -> p.line).orElse(-1)
            );
        }

        if (mode == Mode.NO_PRIMITIVE && isPrimitive && !isExc) {
            ctx.report(
                    "Primitive types are not allowed, found: " + type,
                    node.getBegin().map(p -> p.line).orElse(-1)
            );
        }
    }

    private boolean isException(Type type) {
        if (exceptions == null || exceptions.isEmpty() || type == null) {
            return false;
        }
        String typeStr = type.asString();
        if (exceptions.contains(typeStr)) {
            return true;
        }
        int lastDot = typeStr.lastIndexOf('.');
        if (lastDot != -1) {
            String simpleName = typeStr.substring(lastDot + 1);
            if (exceptions.contains(simpleName)) {
                return true;
            }
        }
        return false;
    }
}

