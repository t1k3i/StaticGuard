package com.staticguard.visitors.java;

import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.TypeParameter;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.staticguard.common.RuleContext;
import com.staticguard.enums.TypeContext;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UsedTypesVisitor extends VoidVisitorAdapter<RuleContext> {

    private final Map<String, Set<TypeContext>> usedTypes;

    public UsedTypesVisitor(Map<String, Set<TypeContext>> usedTypes) {
        this.usedTypes = usedTypes;
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

    /* ===== Method return type + throws ===== */
    @Override
    public void visit(MethodDeclaration n, RuleContext ctx) {
        record(n.getType(), TypeContext.RETURN_TYPE);
        n.getThrownExceptions()
                .forEach(exception ->
                        record(exception, TypeContext.THROWS)
                );
        super.visit(n, ctx);
    }

    /* ===== Constructors + throws ===== */
    @Override
    public void visit(ConstructorDeclaration n, RuleContext ctx) {
        n.getThrownExceptions()
                .forEach(exception ->
                        record(exception, TypeContext.THROWS)
                );
        super.visit(n, ctx);
    }

    /* ===== Object creation ===== */
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
        n.getTypeArguments()
                .ifPresent(args ->
                        args.forEach(type ->
                                record(type, TypeContext.GENERIC_ARGUMENT)
                        )
                );
        super.visit(n, ctx);
    }

    /* ===== Method call generic types ===== */
    @Override
    public void visit(MethodCallExpr n, RuleContext ctx) {
        n.getTypeArguments()
                .ifPresent(args ->
                        args.forEach(type ->
                                record(type, TypeContext.METHOD_GENERIC_ARGUMENT)
                        )
                );
        super.visit(n, ctx);
    }

    /* ===== Arrays ===== */
    @Override
    public void visit(ArrayType n, RuleContext ctx) {
        record(n.getComponentType(), TypeContext.ARRAY_COMPONENT);
        super.visit(n, ctx);
    }

    /* ===== Throw new Exception ===== */
    @Override
    public void visit(ThrowStmt n, RuleContext ctx) {
        n.getExpression()
                .ifObjectCreationExpr(expr ->
                        record(expr.getType(), TypeContext.THROWS)
                );
        super.visit(n, ctx);
    }

    /* ===== Inheritance ===== */
    @Override
    public void visit(ClassOrInterfaceDeclaration n, RuleContext ctx) {
        n.getExtendedTypes()
                .forEach(type ->
                        record(type, TypeContext.EXTENDS)
                );
        n.getImplementedTypes()
                .forEach(type ->
                        record(type, TypeContext.IMPLEMENTS)
                );
        super.visit(n, ctx);
    }

    /* ===== instanceof ===== */
    @Override
    public void visit(InstanceOfExpr n, RuleContext ctx) {
        record(n.getType(), TypeContext.INSTANCEOF);
        super.visit(n, ctx);
    }

    /* ===== Class literals ===== */
    @Override
    public void visit(ClassExpr n, RuleContext ctx) {
        record(n.getType(), TypeContext.CLASS_LITERAL);
        super.visit(n, ctx);
    }

    /* ===== Generic type parameters ===== */
    @Override
    public void visit(TypeParameter n, RuleContext ctx) {
        n.getTypeBound()
                .forEach(bound ->
                        record(bound, TypeContext.GENERIC_BOUND)
                );
        super.visit(n, ctx);
    }

    /* ===== Records ===== */
    @Override
    public void visit(RecordDeclaration n, RuleContext ctx) {
        n.getParameters()
                .forEach(parameter ->
                        record(parameter.getType(), TypeContext.RECORD_COMPONENT)
                );
        super.visit(n, ctx);
    }
}