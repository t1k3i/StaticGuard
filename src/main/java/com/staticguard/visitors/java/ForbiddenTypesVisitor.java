package com.staticguard.visitors.java;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.type.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.types.ResolvedType;
import com.staticguard.common.RuleContext;
import com.staticguard.enums.TypeContext;

import java.util.Set;

public class ForbiddenTypesVisitor extends VoidVisitorAdapter<RuleContext> {
    private final Set<String> forbiddenTypes;
    private final Set<TypeContext> forbiddenContexts;

    public ForbiddenTypesVisitor(Set<String> forbiddenTypes,
                                 Set<TypeContext> forbiddenContexts) {
        this.forbiddenTypes = forbiddenTypes;
        this.forbiddenContexts = forbiddenContexts;
    }

    private void check(Type type, TypeContext context, Node node, RuleContext ctx) {
        if (type == null) return;

        if (forbiddenContexts != null && !forbiddenContexts.contains(context)) return;

        String simpleName = type.isClassOrInterfaceType()
                ? type.asClassOrInterfaceType().getNameAsString()
                : type.asString();

        if (forbiddenTypes.contains(simpleName)) {
            report(simpleName, context, node, ctx);
            return;
        }

        if (type.isClassOrInterfaceType()) {
            try {
                ResolvedType resolved =
                        type.asClassOrInterfaceType().resolve();

                if (resolved.isReferenceType()) {
                    String qualifiedName =
                            resolved.asReferenceType().getQualifiedName();

                    if (forbiddenTypes.contains(qualifiedName)) {
                        report(qualifiedName, context, node, ctx);
                    }
                }
            } catch (RuntimeException ignored) {
            }
        }
    }

    @Override
    public void visit(FieldDeclaration n, RuleContext ctx) {
        check(n.getElementType(), TypeContext.FIELD, n, ctx);
        super.visit(n, ctx);
    }

    @Override
    public void visit(VariableDeclarator n, RuleContext ctx) {
        if (!(n.getParentNode().orElse(null) instanceof FieldDeclaration)) {
            check(n.getType(), TypeContext.LOCAL_VARIABLE, n, ctx);
        }
        super.visit(n, ctx);
    }

    @Override
    public void visit(Parameter n, RuleContext ctx) {
        check(n.getType(), TypeContext.PARAMETER, n, ctx);
        super.visit(n, ctx);
    }

    @Override
    public void visit(MethodDeclaration n, RuleContext ctx) {
        check(n.getType(), TypeContext.RETURN_TYPE, n, ctx);
        n.getThrownExceptions()
                .forEach(t -> check(t, TypeContext.THROWS, n, ctx));
        super.visit(n, ctx);
    }

    @Override
    public void visit(ObjectCreationExpr n, RuleContext ctx) {
        check(n.getType(), TypeContext.INSTANTIATION, n, ctx);
        super.visit(n, ctx);
    }

    @Override
    public void visit(CastExpr n, RuleContext ctx) {
        check(n.getType(), TypeContext.CAST, n, ctx);
        super.visit(n, ctx);
    }

    @Override
    public void visit(ArrayType n, RuleContext ctx) {
        check(n.getComponentType(), TypeContext.ARRAY_COMPONENT, n, ctx);
        super.visit(n, ctx);
    }

    @Override
    public void visit(ClassOrInterfaceType n, RuleContext ctx) {
        n.getTypeArguments().ifPresent(args ->
                args.forEach(t -> check(t, TypeContext.GENERIC_ARGUMENT, n, ctx))
        );
        super.visit(n, ctx);
    }

    @Override
    public void visit(CatchClause n, RuleContext ctx) {
        check(n.getParameter().getType(), TypeContext.CATCH, n, ctx);
        super.visit(n, ctx);
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, RuleContext ctx) {
        n.getExtendedTypes().forEach(t -> check(t, TypeContext.EXTENDS, n, ctx));
        n.getImplementedTypes().forEach(t -> check(t, TypeContext.IMPLEMENTS, n, ctx));
        super.visit(n, ctx);
    }

    @Override
    public void visit(RecordDeclaration n, RuleContext ctx) {
        n.getParameters()
                .forEach(parameter ->
                        check(
                                parameter.getType(),
                                TypeContext.RECORD_COMPONENT,
                                parameter,
                                ctx
                        )
                );

        super.visit(n, ctx);
    }

    @Override
    public void visit(MethodCallExpr n, RuleContext ctx) {
        try {
            var method = n.resolve();

            if (method.isStatic()) {
                String qualifiedName = method
                        .declaringType()
                        .getQualifiedName();

                checkStaticType(
                        qualifiedName,
                        TypeContext.STATIC_METHOD_CALL,
                        n,
                        ctx
                );
            }
        } catch (RuntimeException ignored) {
        }

        super.visit(n, ctx);
    }

    @Override
    public void visit(FieldAccessExpr n, RuleContext ctx) {
        try {
            var resolved = n.resolve();

            if (resolved.isField()) {
                var field = resolved.asField();

                if (field.isStatic()) {
                    String qualifiedName = field
                            .declaringType()
                            .getQualifiedName();

                    checkStaticType(
                            qualifiedName,
                            TypeContext.STATIC_FIELD_ACCESS,
                            n,
                            ctx
                    );
                }
            } else if (resolved.isEnumConstant()) {
                String qualifiedName = resolved
                        .asEnumConstant()
                        .getType()
                        .asReferenceType()
                        .getQualifiedName();

                checkStaticType(
                        qualifiedName,
                        TypeContext.STATIC_FIELD_ACCESS,
                        n,
                        ctx
                );
            }
        } catch (RuntimeException ignored) {
        }

        super.visit(n, ctx);
    }

    private void checkStaticType(
            String qualifiedName,
            TypeContext context,
            Node node,
            RuleContext ctx) {

        if (forbiddenContexts != null
                && !forbiddenContexts.contains(context)) {
            return;
        }

        String simpleName = qualifiedName.substring(
                qualifiedName.lastIndexOf('.') + 1
        );

        if (forbiddenTypes.contains(simpleName)) {
            report(simpleName, context, node, ctx);
        }

        if (forbiddenTypes.contains(qualifiedName)) {
            report(qualifiedName, context, node, ctx);
        }
    }

    private void report(String typeName, TypeContext context, Node node, RuleContext ctx) {
        ctx.report(
                "Forbidden type usage: " + typeName + " in context " + context,
                node.getBegin().map(p -> p.line).orElse(-1)
        );
    }
}