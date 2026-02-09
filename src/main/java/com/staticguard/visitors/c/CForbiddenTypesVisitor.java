package com.staticguard.visitors.c;

import com.staticguard.CBaseVisitor;
import com.staticguard.CParser;
import com.staticguard.common.RuleContext;
import com.staticguard.enums.TypeContext;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Set;

public class CForbiddenTypesVisitor extends CBaseVisitor<Void> {
    private final RuleContext context;
    private final Set<String> forbiddenTypes;
    private final Set<TypeContext> forbiddenContexts;

    public CForbiddenTypesVisitor(RuleContext context,
                                  Set<String> forbiddenTypes,
                                  Set<TypeContext> forbiddenContexts) {
        this.context = context;
        this.forbiddenTypes = forbiddenTypes;
        this.forbiddenContexts = forbiddenContexts;
    }

    /* =========================
       Helper
       ========================= */
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

    /* =========================
       Variables (global & local)
       ========================= */
    @Override
    public Void visitDeclaration(CParser.DeclarationContext ctx) {
        if (ctx.declarationSpecifiers() != null) {
            String type = ctx.declarationSpecifiers().getText();
            check(type, TypeContext.LOCAL_VARIABLE, ctx);
        }
        return super.visitDeclaration(ctx);
    }

    /* =========================
       Function return types
       ========================= */
    @Override
    public Void visitFunctionDefinition(CParser.FunctionDefinitionContext ctx) {
        if (ctx.declarationSpecifiers() != null) {
            String returnType = ctx.declarationSpecifiers().getText();
            check(returnType, TypeContext.RETURN_TYPE, ctx);
        }
        return super.visitFunctionDefinition(ctx);
    }

    /* =========================
       Function parameters
       ========================= */
    @Override
    public Void visitParameterDeclaration(CParser.ParameterDeclarationContext ctx) {
        if (ctx.declarationSpecifiers() != null) {
            String type = ctx.declarationSpecifiers().getText();
            check(type, TypeContext.PARAMETER, ctx);
        }
        return super.visitParameterDeclaration(ctx);
    }

    /* =========================
       Casts
       ========================= */
    @Override
    public Void visitCastExpression(CParser.CastExpressionContext ctx) {
        if (ctx.typeName() != null) {
            String type = ctx.typeName().getText();
            check(type, TypeContext.CAST, ctx);
        }
        return super.visitCastExpression(ctx);
    }

    /* =========================
       Typedefs
       ========================= */
    @Override
    public Void visitTypedefName(CParser.TypedefNameContext ctx) {
        String name = ctx.getText();
        check(name, TypeContext.TYPEDEF, ctx);
        return super.visitTypedefName(ctx);
    }

    @Override
    public Void visitStructOrUnionSpecifier(CParser.StructOrUnionSpecifierContext ctx) {
        if (ctx.Identifier() != null) {
            String name = ctx.Identifier().getText();
            TypeContext type =
                    ctx.structOrUnion().Struct() != null
                            ? TypeContext.STRUCT
                            : TypeContext.UNION;
            check(name, type, ctx);
        }
        return super.visitStructOrUnionSpecifier(ctx);
    }

    @Override
    public Void visitEnumSpecifier(CParser.EnumSpecifierContext ctx) {
        if (ctx.Identifier() != null) {
            check(ctx.Identifier().getText(), TypeContext.ENUM, ctx);
        }
        return super.visitEnumSpecifier(ctx);
    }
}
