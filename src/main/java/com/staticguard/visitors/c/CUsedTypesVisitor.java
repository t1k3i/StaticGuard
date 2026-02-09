package com.staticguard.visitors.c;

import com.staticguard.CBaseVisitor;
import com.staticguard.CParser;
import com.staticguard.enums.TypeContext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CUsedTypesVisitor extends CBaseVisitor<Void> {
    private final Map<String, Set<TypeContext>> usedTypes;

    public CUsedTypesVisitor(Map<String, Set<TypeContext>> usedTypes) {
        this.usedTypes = usedTypes;
    }

    private void record(String typeName, TypeContext context) {
        if (typeName == null) return;

        usedTypes
                .computeIfAbsent(typeName, k -> new HashSet<>())
                .add(context);
    }

    /* ===== Function return types ===== */
    @Override
    public Void visitFunctionDefinition(CParser.FunctionDefinitionContext ctx) {
        String returnType = ctx.declarationSpecifiers().getText();
        record(returnType, TypeContext.RETURN_TYPE);
        return super.visitFunctionDefinition(ctx);
    }

    /* ===== Function parameters ===== */
    @Override
    public Void visitParameterDeclaration(CParser.ParameterDeclarationContext ctx) {
        if (ctx.declarationSpecifiers() != null) {
            record(ctx.declarationSpecifiers().getText(), TypeContext.PARAMETER);
        }
        return super.visitParameterDeclaration(ctx);
    }

    /* ===== Global + local variables ===== */
    @Override
    public Void visitDeclaration(CParser.DeclarationContext ctx) {
        if (ctx.declarationSpecifiers() != null) {
            record(ctx.declarationSpecifiers().getText(), TypeContext.LOCAL_VARIABLE);
        }
        return super.visitDeclaration(ctx);
    }

    /* ===== Casts ===== */
    @Override
    public Void visitCastExpression(CParser.CastExpressionContext ctx) {
        if (ctx.typeName() != null) {
            record(ctx.typeName().getText(), TypeContext.CAST);
        }
        return super.visitCastExpression(ctx);
    }

    /* ===== Arrays ===== */
    @Override
    public Void visitDirectDeclarator(CParser.DirectDeclaratorContext ctx) {
        if (ctx.LeftBracket() != null) {
            record(ctx.getParent().getText(), TypeContext.ARRAY_COMPONENT);
        }
        return super.visitDirectDeclarator(ctx);
    }

    /* ===== Struct fields ===== */
    @Override
    public Void visitStructDeclaration(CParser.StructDeclarationContext ctx) {
        if (ctx.specifierQualifierList() != null) {
            record(ctx.specifierQualifierList().getText(), TypeContext.FIELD);
        }
        return super.visitStructDeclaration(ctx);
    }
}
