package com.staticguard.visitors.c;

import com.staticguard.CBaseVisitor;
import com.staticguard.CParser;
import com.staticguard.common.RuleContext;
import com.staticguard.enums.TypeContext;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;
import java.util.Set;

import static com.staticguard.visitors.c.CVisitorHelper.getTypeName;
import static com.staticguard.visitors.c.CVisitorHelper.isTypedef;

public class CForbiddenTypesVisitor extends CBaseVisitor<Void> {
    private final RuleContext context;
    private final Set<String> forbiddenTypes;
    private final Set<TypeContext> forbiddenContexts;

    private boolean insideFunction = false;

    public CForbiddenTypesVisitor(RuleContext context,
                                  Set<String> forbiddenTypes,
                                  Set<TypeContext> forbiddenContexts) {
        this.context = context;
        this.forbiddenTypes = forbiddenTypes;
        this.forbiddenContexts = forbiddenContexts;
    }

    private void check(String typeName, TypeContext ctxType, ParserRuleContext ctx) {
        if (typeName == null) return;

        if (forbiddenTypes.contains(typeName)
                && (forbiddenContexts == null || forbiddenContexts.contains(ctxType))) {

            int line = ctx.getStart() != null ? ctx.getStart().getLine() : -1;
            context.report(
                    "Forbidden type usage: " + typeName + " in context " + ctxType,
                    line
            );
        }
    }

    @Override
    public Void visitFunctionDefinition(CParser.FunctionDefinitionContext ctx) {

        if (ctx.declarationSpecifiers() != null) {
            String returnType = getTypeName(ctx.declarationSpecifiers().declarationSpecifier());
            check(returnType, TypeContext.RETURN_TYPE, ctx);
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

        check(typeName, TypeContext.PARAMETER, ctx);

        return super.visitParameterDeclaration(ctx);
    }

    @Override
    public Void visitStructDeclaration(CParser.StructDeclarationContext ctx) {

        if (ctx.specifierQualifierList() != null) {

            String typeName = getTypeName(ctx.specifierQualifierList());

            check(typeName, TypeContext.FIELD, ctx);
        }

        return super.visitStructDeclaration(ctx);
    }

    @Override
    public Void visitCastExpression(CParser.CastExpressionContext ctx) {

        if (ctx.typeName() != null) {

            String typeName = getTypeName(ctx.typeName().specifierQualifierList());

            check(typeName, TypeContext.CAST, ctx);
        }

        return super.visitCastExpression(ctx);
    }

    @Override
    public Void visitDeclaration(CParser.DeclarationContext ctx) {

        if (ctx.declarationSpecifiers() == null)
            return super.visitDeclaration(ctx);

        String typeName = getTypeName(ctx.declarationSpecifiers().declarationSpecifier());

        if (isTypedef(ctx)) {

            List<CParser.DeclarationSpecifierContext> specifiers = ctx.declarationSpecifiers().declarationSpecifier();

            CParser.DeclarationSpecifierContext last = specifiers.get(specifiers.size() - 1);

            if (last.typeSpecifier() != null && last.typeSpecifier().typedefName() != null) {

                specifiers = specifiers.subList(0, specifiers.size() - 1);

                typeName = getTypeName(specifiers);
            }

            check(typeName, TypeContext.TYPEDEF, ctx);
        } else if (ctx.initDeclaratorList() == null) {
            return super.visitDeclaration(ctx);
        } else if (insideFunction) {
            check(typeName, TypeContext.LOCAL_VARIABLE, ctx);
        } else {
            check(typeName, TypeContext.GLOBAL_VARIABLE, ctx);
        }

        return super.visitDeclaration(ctx);
    }
}
