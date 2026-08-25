package com.staticguard.visitors.java;

import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.type.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.types.ResolvedType;
import com.staticguard.enums.TypeContext;

import java.util.*;

public class ClassDependencyVisitor extends VoidVisitorAdapter<Object> {
    private final Set<String> projectClasses;
    private final Map<String, Map<String, Set<TypeContext>>> dependencies;

    private String currentClass;

    public ClassDependencyVisitor(
            Set<String> projectClasses,
            Map<String, Map<String, Set<TypeContext>>> dependencies) {
        this.projectClasses = projectClasses;
        this.dependencies = dependencies;
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Object arg) {
        String previousClass = currentClass;

        currentClass = n.getFullyQualifiedName()
                .orElse(n.getNameAsString());

        n.getExtendedTypes()
                .forEach(t -> recordType(t, TypeContext.EXTENDS));

        n.getImplementedTypes()
                .forEach(t -> recordType(t, TypeContext.IMPLEMENTS));

        super.visit(n, arg);

        currentClass = previousClass;
    }

    @Override
    public void visit(FieldDeclaration n, Object arg) {
        recordType(n.getElementType(), TypeContext.FIELD);
        super.visit(n, arg);
    }

    @Override
    public void visit(VariableDeclarator n, Object arg) {
        if (!(n.getParentNode().orElse(null) instanceof FieldDeclaration)) {
            recordType(n.getType(), TypeContext.LOCAL_VARIABLE);
        }

        super.visit(n, arg);
    }

    @Override
    public void visit(MethodDeclaration n, Object arg) {
        recordType(n.getType(), TypeContext.RETURN_TYPE);

        n.getThrownExceptions()
                .forEach(t -> recordType(t, TypeContext.THROWS));

        super.visit(n, arg);
    }

    @Override
    public void visit(MethodCallExpr n, Object arg) {
        try {
            var method = n.resolve();

            if (method.isStatic()) {
                String qualifiedName = method
                        .declaringType()
                        .getQualifiedName();

                addDependency(
                        qualifiedName,
                        TypeContext.STATIC_METHOD_CALL
                );
            }
        } catch (UnsolvedSymbolException ignored) {
        }

        super.visit(n, arg);
    }

    @Override
    public void visit(FieldAccessExpr n, Object arg) {
        try {
            var resolved = n.resolve();

            if (resolved.isField()) {
                var field = resolved.asField();

                if (field.isStatic()) {
                    String qualifiedName = field
                            .declaringType()
                            .getQualifiedName();

                    addDependency(
                            qualifiedName,
                            TypeContext.STATIC_FIELD_ACCESS
                    );
                }
            } else if (resolved.isEnumConstant()) {
                String qualifiedName = resolved
                        .asEnumConstant()
                        .getType()
                        .asReferenceType()
                        .getQualifiedName();

                addDependency(
                        qualifiedName,
                        TypeContext.STATIC_FIELD_ACCESS
                );
            }
        } catch (UnsolvedSymbolException ignored) {
        }

        super.visit(n, arg);
    }

    @Override
    public void visit(Parameter n, Object arg) {
        if (!(n.getParentNode().orElse(null) instanceof RecordDeclaration)) {
            recordType(n.getType(), TypeContext.PARAMETER);
        }

        super.visit(n, arg);
    }

    @Override
    public void visit(ObjectCreationExpr n, Object arg) {
        recordType(n.getType(), TypeContext.INSTANTIATION);
        super.visit(n, arg);
    }

    @Override
    public void visit(CastExpr n, Object arg) {
        recordType(n.getType(), TypeContext.CAST);
        super.visit(n, arg);
    }

    @Override
    public void visit(ArrayType n, Object arg) {
        recordType(n.getComponentType(), TypeContext.ARRAY_COMPONENT);
        super.visit(n, arg);
    }

    @Override
    public void visit(ClassOrInterfaceType n, Object arg) {
        n.getTypeArguments().ifPresent(args ->
                args.forEach(t ->
                        recordType(t, TypeContext.GENERIC_ARGUMENT)
                )
        );

        super.visit(n, arg);
    }

    @Override
    public void visit(CatchClause n, Object arg) {
        recordType(
                n.getParameter().getType(),
                TypeContext.CATCH
        );

        super.visit(n, arg);
    }

    @Override
    public void visit(RecordDeclaration n, Object arg) {
        String previousClass = currentClass;

        currentClass = n.getFullyQualifiedName()
                .orElse(n.getNameAsString());

        n.getParameters()
                .forEach(parameter ->
                        recordType(
                                parameter.getType(),
                                TypeContext.RECORD_COMPONENT
                        )
                );

        super.visit(n, arg);

        currentClass = previousClass;
    }

    private void recordType(Type type, TypeContext context) {
        if (currentClass == null) return;

        try {
            ResolvedType resolvedType = type.resolve();

            if (!resolvedType.isReferenceType()) {
                return;
            }

            String qualifiedName = resolvedType
                    .asReferenceType()
                    .getQualifiedName();

            addDependency(qualifiedName, context);
        } catch (UnsolvedSymbolException ignored) {
        }
    }

    private void addDependency(String qualifiedName, TypeContext context) {
        if (currentClass == null) return;
        if (currentClass.equals(qualifiedName)) return;
        if (!projectClasses.contains(qualifiedName)) return;

        dependencies
                .computeIfAbsent(currentClass, k -> new HashMap<>())
                .computeIfAbsent(
                        qualifiedName,
                        k -> EnumSet.noneOf(TypeContext.class)
                )
                .add(context);
    }
}