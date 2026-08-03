package com.staticguard.visitors.c;

import com.staticguard.CBaseVisitor;
import com.staticguard.CParser;
import com.staticguard.enums.TypeContext;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CUsedTypesVisitor extends CBaseVisitor<Void> {
    private final Map<String, Set<TypeContext>> usedTypes;

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

    /* ===== Function return types ===== */
    @Override
    public Void visitFunctionDefinition(CParser.FunctionDefinitionContext ctx) {

        if (ctx.declarationSpecifiers() != null) {

            String returnType =
                    ctx.declarationSpecifiers().getText();

            if (!returnType.equals("void")) {
                record(returnType, TypeContext.RETURN_TYPE);
            }
        }

        return super.visitFunctionDefinition(ctx);
    }

    /* ===== Function parameters ===== */
    @Override
    public Void visitParameterDeclaration(
            CParser.ParameterDeclarationContext ctx) {

        if (ctx.declarationSpecifiers() != null) {

            String type =
                    ctx.declarationSpecifiers().getText();
            record(type, TypeContext.PARAMETER);
        }
        return super.visitParameterDeclaration(ctx);
    }

    /* ===== Global/local variables + typedefs ===== */
    @Override
    public Void visitDeclaration(
            CParser.DeclarationContext ctx) {

        if (ctx.declarationSpecifiers() == null) {
            return super.visitDeclaration(ctx);
        }

        String specifiers = ctx.declarationSpecifiers().getText();

        // ===== Typedef =====
        if (specifiers.startsWith("typedef")) {

            String type = specifiers.substring("typedef".length());
            record(
                    type,
                    TypeContext.TYPEDEF
            );

        } else {

            // ===== Global/local variable =====
            TypeContext context;
            if (ctx.getParent()
                    instanceof CParser.TranslationUnitContext) {
                context = TypeContext.GLOBAL_VARIABLE;
            } else {
                context = TypeContext.LOCAL_VARIABLE;
            }
            record(
                    specifiers,
                    context
            );
        }
        return super.visitDeclaration(ctx);
    }

    @Override
    public Void visitStructOrUnionSpecifier(
            CParser.StructOrUnionSpecifierContext ctx) {

        String text = ctx.getText();
        if (text.startsWith("struct")) {
            record(
                    text,
                    TypeContext.STRUCT
            );
        } else if (text.startsWith("union")) {
            record(
                    text,
                    TypeContext.UNION
            );
        }

        return super.visitStructOrUnionSpecifier(ctx);
    }

    /* ===== Enum types ===== */
    @Override
    public Void visitEnumSpecifier(
            CParser.EnumSpecifierContext ctx) {

        if (ctx.Identifier() != null) {
            record(
                    "enum " + ctx.Identifier().getText(),
                    TypeContext.ENUM
            );
        }
        return super.visitEnumSpecifier(ctx);
    }

    /* ===== Struct fields ===== */
    @Override
    public Void visitStructDeclaration(
            CParser.StructDeclarationContext ctx) {

        if (ctx.specifierQualifierList() != null) {
            record(
                    ctx.specifierQualifierList().getText(),
                    TypeContext.FIELD
            );
        }
        return super.visitStructDeclaration(ctx);
    }

    /* ===== Pointer types ===== */
    @Override
    public Void visitPointer(
            CParser.PointerContext ctx) {

        record(
                ctx.getText(),
                TypeContext.POINTER
        );
        return super.visitPointer(ctx);
    }

    /* ===== Function pointers ===== */
    @Override
    public Void visitDeclarator(
            CParser.DeclaratorContext ctx) {

        String text = ctx.getText();
        if (text.contains("(")
                && text.contains("*")) {

            record(
                    text,
                    TypeContext.FUNCTION_POINTER
            );
        }
        return super.visitDeclarator(ctx);
    }

    /* ===== Casts ===== */
    @Override
    public Void visitCastExpression(
            CParser.CastExpressionContext ctx) {

        if (ctx.typeName() != null) {
            record(
                    ctx.typeName().getText(),
                    TypeContext.CAST
            );
        }
        return super.visitCastExpression(ctx);
    }

    /* ===== Arrays ===== */
    @Override
    public Void visitDirectDeclarator(
            CParser.DirectDeclaratorContext ctx) {

        if (ctx.LeftBracket() != null) {
            record(
                    ctx.getParent().getText(),
                    TypeContext.ARRAY_COMPONENT
            );
        }
        return super.visitDirectDeclarator(ctx);
    }

    /* ===== sizeof ===== */
    @Override
    public Void visitUnaryExpression(
            CParser.UnaryExpressionContext ctx) {

        String text = ctx.getText();
        if (text.startsWith("sizeof")) {
            record(
                    text.replace("sizeof", "")
                            .replace("(", "")
                            .replace(")", ""),
                    TypeContext.SIZEOF
            );
        }
        return super.visitUnaryExpression(ctx);
    }
}