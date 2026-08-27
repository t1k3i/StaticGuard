package com.staticguard.visitors.c;

import com.staticguard.CBaseVisitor;
import com.staticguard.CParser;
import com.staticguard.enums.TypeContext;

import java.util.*;

import static com.staticguard.visitors.c.CVisitorHelper.getTypeName;
import static com.staticguard.visitors.c.CVisitorHelper.isTypedef;

public class CUsedTypesVisitor extends CBaseVisitor<Void> {

    private final Map<String, Set<TypeContext>> usedTypes;

    private boolean insideFunction = false;

    public CUsedTypesVisitor(Map<String, Set<TypeContext>> usedTypes) {
        this.usedTypes = usedTypes;
    }

    private void record(String typeName, TypeContext context) {
        if (typeName == null || typeName.isEmpty()) {
            return;
        }

        usedTypes
                .computeIfAbsent(typeName, k -> new HashSet<>())
                .add(context);
    }

    @Override
    public Void visitFunctionDefinition(CParser.FunctionDefinitionContext ctx) {

        if (ctx.declarationSpecifiers() != null) {
            String returnType = getTypeName(ctx.declarationSpecifiers().declarationSpecifier());

            record(returnType, TypeContext.RETURN_TYPE);
        }

        insideFunction = true;

        super.visitFunctionDefinition(ctx);

        insideFunction = false;

        return null;
    }

    @Override
    public Void visitParameterDeclaration(CParser.ParameterDeclarationContext ctx) {

        String typeName = null;

        if (ctx.declarationSpecifiers() != null) {
            typeName = getTypeName(ctx.declarationSpecifiers().declarationSpecifier());
        } else if (ctx.declarationSpecifiers2() != null) {
            typeName = getTypeName(ctx.declarationSpecifiers2().declarationSpecifier());
        }

        record(typeName, TypeContext.PARAMETER);

        return super.visitParameterDeclaration(ctx);
    }

    @Override
    public Void visitStructDeclaration(CParser.StructDeclarationContext ctx) {

        if (ctx.specifierQualifierList() != null) {

            String typeName = getTypeName(ctx.specifierQualifierList());

            record(typeName, TypeContext.FIELD);
        }

        return super.visitStructDeclaration(ctx);
    }

    @Override
    public Void visitDeclaration(CParser.DeclarationContext ctx) {

        if (ctx.declarationSpecifiers() == null) {
            return super.visitDeclaration(ctx);
        }

        String typeName = getTypeName(ctx.declarationSpecifiers().declarationSpecifier());

        if (isTypedef(ctx)) {

            List<CParser.DeclarationSpecifierContext> specifiers = ctx.declarationSpecifiers().declarationSpecifier();

            CParser.DeclarationSpecifierContext last = specifiers.get(specifiers.size() - 1);

            if (last.typeSpecifier() != null && last.typeSpecifier().typedefName() != null) {

                specifiers = specifiers.subList(0, specifiers.size() - 1);

                typeName = getTypeName(specifiers);
            }

            record(typeName, TypeContext.TYPEDEF);
        } else if (ctx.initDeclaratorList() == null) {
            return super.visitDeclaration(ctx);
        } else if (insideFunction) {
            record(typeName, TypeContext.LOCAL_VARIABLE);
        } else {
            record(typeName, TypeContext.GLOBAL_VARIABLE);
        }

        return super.visitDeclaration(ctx);
    }

    @Override
    public Void visitCastExpression(CParser.CastExpressionContext ctx) {

        if (ctx.typeName() != null) {

            String typeName = getTypeName(ctx.typeName().specifierQualifierList());

            record(typeName, TypeContext.CAST);
        }

        return super.visitCastExpression(ctx);
    }
}