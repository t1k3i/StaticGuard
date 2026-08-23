package com.staticguard.visitors.java;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.staticguard.common.RuleContext;

import java.util.HashSet;
import java.util.Set;

public class UnusedImportsVisitor extends VoidVisitorAdapter<RuleContext> {
    private final Set<String> usedTypes = new HashSet<>();

    @Override
    public void visit(CompilationUnit cu, RuleContext ctx) {

        for (ClassOrInterfaceType type : cu.findAll(ClassOrInterfaceType.class)) {
            resolveType(type);
        }

        for (MethodCallExpr methodCall : cu.findAll(MethodCallExpr.class)) {
            methodCall.getScope().ifPresent(this::resolveBaseScope);
        }

        for (FieldAccessExpr fieldAccess : cu.findAll(FieldAccessExpr.class)) {
            resolveBaseScope(fieldAccess.getScope());
        }

        for (AnnotationExpr annotation : cu.findAll(AnnotationExpr.class)) {
            resolveAnnotation(annotation);
        }

        for (ImportDeclaration imp : cu.getImports()) {

            if (imp.isStatic()) {
                continue;
            }

            String importName = imp.getNameAsString();

            if (imp.isAsterisk()) {
                boolean used = usedTypes.stream()
                        .anyMatch(type -> type.startsWith(importName + "."));

                if (!used) {
                    reportUnused(imp, ctx);
                }
            } else if (!usedTypes.contains(importName)) {
                reportUnused(imp, ctx);
            }
        }
    }

    private void resolveType(ClassOrInterfaceType type) {
        try {
            addResolvedType(type.resolve());
        } catch (Exception ignored) {
            // Do not crash
        }
    }

    private void resolveBaseScope(Expression expression) {

        if (expression == null) {
            return;
        }

        try {
            switch (expression) {
                case NameExpr nameExpr ->
                    addResolvedType(nameExpr.calculateResolvedType());
                case FieldAccessExpr fieldAccessExpr ->
                    resolveBaseScope(fieldAccessExpr.getScope());
                case MethodCallExpr methodCallExpr -> methodCallExpr.getScope().ifPresent(this::resolveBaseScope);
                default -> {}
            }
        } catch (Exception ignored) {
            // Do not crash
        }
    }

    private void resolveAnnotation(AnnotationExpr annotation) {
        try {
            usedTypes.add(annotation.resolve().getQualifiedName());
        } catch (Exception ignored) {
            // Do not crash
        }
    }

    private void addResolvedType(ResolvedType resolvedType) {

        if (resolvedType == null || !resolvedType.isReferenceType()) {
            return;
        }

        ResolvedReferenceType referenceType = resolvedType.asReferenceType();

        referenceType.getTypeDeclaration()
                .ifPresent(declaration ->
                        usedTypes.add(declaration.getQualifiedName()));
    }

    private void reportUnused(ImportDeclaration imp, RuleContext ctx) {

        String importName = imp.getNameAsString();

        if (imp.isAsterisk()) {
            importName += ".*";
        }

        ctx.report(
                "Unused import: " + importName,
                imp.getBegin().map(p -> p.line).orElse(-1)
        );
    }
}