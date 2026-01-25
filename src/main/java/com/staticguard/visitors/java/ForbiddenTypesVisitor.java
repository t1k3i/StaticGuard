package com.staticguard.visitors.java;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.staticguard.common.RuleContext;
import com.github.javaparser.ast.type.*;
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

        String name = type.toString();

        if (forbiddenTypes.contains(name)
                && (forbiddenContexts == null || forbiddenContexts.contains(context))) {

            ctx.report(
                    "Forbidden type usage: " + name + " in context " + context,
                    node.getBegin().map(p -> p.line).orElse(-1)
            );
        }
    }

    /* =========================
       Fields
       ========================= */
    @Override
    public void visit(FieldDeclaration n, RuleContext ctx) {
        check(n.getElementType(), TypeContext.FIELD, n, ctx);
        super.visit(n, ctx);
    }

    /* =========================
       Local variables
       ========================= */
    @Override
    public void visit(VariableDeclarator n, RuleContext ctx) {
        if (!(n.getParentNode().orElse(null) instanceof FieldDeclaration)) {
            check(n.getType(), TypeContext.LOCAL_VARIABLE, n, ctx);
        }
        super.visit(n, ctx);
    }

    /* =========================
       Method parameters
       ========================= */
    @Override
    public void visit(Parameter n, RuleContext ctx) {
        check(n.getType(), TypeContext.PARAMETER, n, ctx);
        super.visit(n, ctx);
    }

    /* =========================
       Method return types
       ========================= */
    @Override
    public void visit(MethodDeclaration n, RuleContext ctx) {
        check(n.getType(), TypeContext.RETURN_TYPE, n, ctx);
        n.getThrownExceptions()
                .forEach(t -> check(t, TypeContext.THROWS, n, ctx));
        super.visit(n, ctx);
    }

    /* =========================
       Object instantiation
       ========================= */
    @Override
    public void visit(ObjectCreationExpr n, RuleContext ctx) {
        check(n.getType(), TypeContext.INSTANTIATION, n, ctx);
        super.visit(n, ctx);
    }

    /* =========================
       Casts
       ========================= */
    @Override
    public void visit(CastExpr n, RuleContext ctx) {
        check(n.getType(), TypeContext.CAST, n, ctx);
        super.visit(n, ctx);
    }

    /* =========================
       Arrays
       ========================= */
    @Override
    public void visit(ArrayType n, RuleContext ctx) {
        check(n.getComponentType(), TypeContext.ARRAY_COMPONENT, n, ctx);
        super.visit(n, ctx);
    }

    /* =========================
       Generics
       ========================= */
    @Override
    public void visit(ClassOrInterfaceType n, RuleContext ctx) {
        n.getTypeArguments().ifPresent(args ->
                args.forEach(t -> check(t, TypeContext.GENERIC_ARGUMENT, n, ctx))
        );
        super.visit(n, ctx);
    }

    /* =========================
       Catch clauses
       ========================= */
    @Override
    public void visit(CatchClause n, RuleContext ctx) {
        check(n.getParameter().getType(), TypeContext.CATCH, n, ctx);
        super.visit(n, ctx);
    }

    /* =========================
       Inheritance
       ========================= */
    @Override
    public void visit(ClassOrInterfaceDeclaration n, RuleContext ctx) {
        n.getExtendedTypes().forEach(t -> check(t, TypeContext.EXTENDS, n, ctx));
        n.getImplementedTypes().forEach(t -> check(t, TypeContext.IMPLEMENTS, n, ctx));
        super.visit(n, ctx);
    }
}
