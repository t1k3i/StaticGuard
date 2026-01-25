package com.staticguard.visitors.java;

import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.staticguard.common.RuleContext;
import com.staticguard.enums.TypeContext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UsedTypesVisitor extends VoidVisitorAdapter<RuleContext> {
    private final Map<String, Set<TypeContext>> usedTypes = new HashMap<>();

    public Map<String, Set<TypeContext>> getUsedTypes() {
        return usedTypes;
    }

    private void record(Type type, TypeContext context) {
        if (type == null) return;

        String name = type.toString();
        usedTypes
                .computeIfAbsent(name, k -> new HashSet<>())
                .add(context);
    }

    /* ===== Fields ===== */
    @Override
    public void visit(FieldDeclaration n, RuleContext ctx) {
        record(n.getElementType(), TypeContext.FIELD);
        super.visit(n, ctx);
    }

    /* ===== Local variables ===== */
    @Override
    public void visit(VariableDeclarator n, RuleContext ctx) {
        if (!(n.getParentNode().orElse(null) instanceof FieldDeclaration)) {
            record(n.getType(), TypeContext.LOCAL_VARIABLE);
        }
        super.visit(n, ctx);
    }

    /* ===== Method parameters ===== */
    @Override
    public void visit(Parameter n, RuleContext ctx) {
        record(n.getType(), TypeContext.PARAMETER);
        super.visit(n, ctx);
    }

    /* ===== Method return type ===== */
    @Override
    public void visit(MethodDeclaration n, RuleContext ctx) {
        record(n.getType(), TypeContext.RETURN_TYPE);
        super.visit(n, ctx);
    }

    /* ===== Object instantiation ===== */
    @Override
    public void visit(ObjectCreationExpr n, RuleContext ctx) {
        record(n.getType(), TypeContext.INSTANTIATION);
        super.visit(n, ctx);
    }

    /* ===== Casts ===== */
    @Override
    public void visit(CastExpr n, RuleContext ctx) {
        record(n.getType(), TypeContext.CAST);
        super.visit(n, ctx);
    }

    /* ===== Generic arguments ===== */
    @Override
    public void visit(ClassOrInterfaceType n, RuleContext ctx) {
        n.getTypeArguments().ifPresent(args ->
                args.forEach(t -> record(t, TypeContext.GENERIC_ARGUMENT))
        );
        super.visit(n, ctx);
    }

    /* ===== Arrays ===== */
    @Override
    public void visit(ArrayType n, RuleContext ctx) {
        record(n.getComponentType(), TypeContext.ARRAY_COMPONENT);
        super.visit(n, ctx);
    }

    /* ===== Throws ===== */
    @Override
    public void visit(ThrowStmt n, RuleContext ctx) {
        n.getExpression().ifObjectCreationExpr(expr ->
                record(expr.getType(), TypeContext.THROWS)
        );
        super.visit(n, ctx);
    }

    /* ===== Inheritance ===== */
    @Override
    public void visit(ClassOrInterfaceDeclaration n, RuleContext ctx) {
        n.getExtendedTypes().forEach(t -> record(t, TypeContext.EXTENDS));
        n.getImplementedTypes().forEach(t -> record(t, TypeContext.IMPLEMENTS));
        super.visit(n, ctx);
    }
}
