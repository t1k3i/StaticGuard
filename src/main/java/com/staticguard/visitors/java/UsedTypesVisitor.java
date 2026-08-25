package com.staticguard.visitors.java;

import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.type.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.types.ResolvedType;
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

        if (type.isTypeParameter()) {
            return;
        }

        String typeName;

        try {
            if (type.isArrayType()) {
                Type component = type.asArrayType().getComponentType();

                String componentName = resolveName(component);

                if (componentName == null) {
                    return;
                }

                typeName = componentName + "[]";
            } else {
                typeName = resolveName(type);
                if (typeName == null) {
                    return;
                }
            }
        } catch (RuntimeException e) {
            typeName = type.asString();
        }

        usedTypes
                .computeIfAbsent(typeName, k -> new HashSet<>())
                .add(context);
    }

    private String resolveName(Type type) {
        try {
            ResolvedType resolved = type.resolve();

            if (resolved.isTypeVariable()) {
                return null;
            }

            if (resolved.isReferenceType()) {
                return resolved
                        .asReferenceType()
                        .getQualifiedName();
            }

        } catch (RuntimeException ignored) {
        }

        return type.asString();
    }
    @Override
    public void visit(FieldDeclaration n, RuleContext ctx) {
        record(n.getElementType(), TypeContext.FIELD);
        super.visit(n, ctx);
    }

    @Override
    public void visit(VariableDeclarator n, RuleContext ctx) {
        if (!(n.getParentNode().orElse(null) instanceof FieldDeclaration)) {
            record(n.getType(), TypeContext.LOCAL_VARIABLE);
        }
        super.visit(n, ctx);
    }

    @Override
    public void visit(Parameter n, RuleContext ctx) {
        record(n.getType(), TypeContext.PARAMETER);
        super.visit(n, ctx);
    }

    @Override
    public void visit(MethodDeclaration n, RuleContext ctx) {
        record(n.getType(), TypeContext.RETURN_TYPE);
        n.getThrownExceptions()
                .forEach(t ->
                        record(t, TypeContext.THROWS)
                );
        super.visit(n, ctx);
    }

    @Override
    public void visit(ConstructorDeclaration n, RuleContext ctx) {
        n.getThrownExceptions()
                .forEach(t ->
                        record(t, TypeContext.THROWS)
                );
        super.visit(n, ctx);
    }

    @Override
    public void visit(ObjectCreationExpr n, RuleContext ctx) {
        record(n.getType(), TypeContext.INSTANTIATION);
        super.visit(n, ctx);
    }

    @Override
    public void visit(CastExpr n, RuleContext ctx) {
        record(n.getType(), TypeContext.CAST);
        super.visit(n, ctx);
    }

    @Override
    public void visit(ArrayType n, RuleContext ctx) {
        record(n.getComponentType(), TypeContext.ARRAY_COMPONENT);
        super.visit(n, ctx);
    }

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

    @Override
    public void visit(CatchClause n, RuleContext ctx) {
        record(
                n.getParameter().getType(),
                TypeContext.CATCH
        );
        super.visit(n, ctx);
    }

    @Override
    public void visit(ThrowStmt n, RuleContext ctx) {
        n.getExpression()
                .ifObjectCreationExpr(expr ->
                        record(expr.getType(), TypeContext.THROWS)
                );
        super.visit(n, ctx);
    }

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

    @Override
    public void visit(RecordDeclaration n, RuleContext ctx) {
        n.getParameters()
                .forEach(parameter ->
                        record(
                                parameter.getType(),
                                TypeContext.RECORD_COMPONENT
                        )
                );

        super.visit(n, ctx);
    }

    @Override
    public void visit(FieldAccessExpr n, RuleContext ctx) {
        try {
            var resolved = n.resolve();

            if (resolved.isField()) {
                var field = resolved.asField();

                if (field.isStatic()) {
                    String typeName = field
                            .declaringType()
                            .getQualifiedName();

                    usedTypes
                            .computeIfAbsent(typeName, k -> new HashSet<>())
                            .add(TypeContext.STATIC_FIELD_ACCESS);
                }
            } else if (resolved.isEnumConstant()) {
                String typeName = resolved
                        .asEnumConstant()
                        .getType()
                        .asReferenceType()
                        .getQualifiedName();

                usedTypes
                        .computeIfAbsent(typeName, k -> new HashSet<>())
                        .add(TypeContext.STATIC_FIELD_ACCESS);
            }
        } catch (RuntimeException ignored) {
        }

        super.visit(n, ctx);
    }

    @Override
    public void visit(MethodCallExpr n, RuleContext ctx) {
        try {
            var method = n.resolve();

            if (method.isStatic()) {
                String typeName = method
                        .declaringType()
                        .getQualifiedName();

                usedTypes
                        .computeIfAbsent(typeName, k -> new HashSet<>())
                        .add(TypeContext.STATIC_METHOD_CALL);
            }
        } catch (RuntimeException ignored) {
        }

        super.visit(n, ctx);
    }
}