package com.staticguard.visitors.java;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.staticguard.common.RuleContext;

public class ForbiddenFieldAccessVisitor extends VoidVisitorAdapter<RuleContext> {
    @Override
    public void visit(FieldAccessExpr n, RuleContext ctx) {
        try {
            ResolvedFieldDeclaration field = n.resolve().asField();

            ResolvedReferenceTypeDeclaration declaringType =
                    (ResolvedReferenceTypeDeclaration) field.declaringType();

            ClassOrInterfaceDeclaration currentClass =
                    n.findAncestor(ClassOrInterfaceDeclaration.class)
                            .orElse(null);

            if (currentClass == null) {
                return;
            }

            ResolvedReferenceTypeDeclaration currentType =
                    currentClass.resolve();

            if (!declaringType.getQualifiedName()
                    .equals(currentType.getQualifiedName())) {

                ctx.report(
                        "Forbidden direct field access: " + n,
                        n.getBegin().map(p -> p.line).orElse(-1)
                );
            }
        } catch (Exception ignored) {
            // Could not resolve the field.
        }

        super.visit(n, ctx);
    }
}
