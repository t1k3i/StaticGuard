package com.staticguard.visitors.c;

import com.staticguard.CBaseVisitor;
import com.staticguard.CParser;
import com.staticguard.enums.TypeContext;

import java.util.*;

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

    private String getTypeName(
            CParser.SpecifierQualifierListContext ctx) {

        if (ctx == null) {
            return null;
        }

        List<String> types = new ArrayList<>();

        if (ctx.typeSpecifier() != null) {

            CParser.TypeSpecifierContext typeSpecifier =
                    ctx.typeSpecifier();

            // struct / union
            if (typeSpecifier.structOrUnionSpecifier() != null) {

                CParser.StructOrUnionSpecifierContext struct =
                        typeSpecifier.structOrUnionSpecifier();

                String result =
                        struct.structOrUnion().getText();

                if (struct.Identifier() != null) {
                    result += " " + struct.Identifier().getText();
                }

                types.add(result);
            }

            // enum
            else if (typeSpecifier.enumSpecifier() != null) {

                CParser.EnumSpecifierContext enumCtx =
                        typeSpecifier.enumSpecifier();

                String result = "enum";

                if (enumCtx.Identifier() != null) {
                    result += " " + enumCtx.Identifier().getText();
                }

                types.add(result);
            }

            // normal type
            else {
                types.add(typeSpecifier.getText());
            }
        }

        if (ctx.specifierQualifierList() != null) {

            String nested =
                    getTypeName(ctx.specifierQualifierList());

            if (nested != null && !nested.isEmpty()) {
                types.add(nested);
            }
        }

        return String.join(" ", types);
    }

    private String getTypeName(
            List<CParser.DeclarationSpecifierContext> specifiers) {

        return specifiers.stream()
                .filter(specifier -> specifier.typeSpecifier() != null)
                .map(specifier -> {

                    CParser.TypeSpecifierContext typeSpecifier =
                            specifier.typeSpecifier();

                    if (typeSpecifier.structOrUnionSpecifier() != null) {

                        CParser.StructOrUnionSpecifierContext struct =
                                typeSpecifier.structOrUnionSpecifier();

                        return struct.structOrUnion().getText()
                                + " "
                                + struct.Identifier().getText();
                    }

                    if (typeSpecifier.enumSpecifier() != null) {
                        CParser.EnumSpecifierContext enumCtx =
                                typeSpecifier.enumSpecifier();

                        String result = "enum";

                        if (enumCtx.Identifier() != null) {
                            result += " " + enumCtx.Identifier().getText();
                        }

                        return result;
                    }

                    return typeSpecifier.getText();
                })
                .collect(java.util.stream.Collectors.joining(" "));
    }

    @Override
    public Void visitFunctionDefinition(
            CParser.FunctionDefinitionContext ctx) {

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
    public Void visitParameterDeclaration(
            CParser.ParameterDeclarationContext ctx) {

        String typeName = null;

        if (ctx.declarationSpecifiers() != null) {
            typeName = getTypeName(ctx.declarationSpecifiers().declarationSpecifier());
        }
        else if (ctx.declarationSpecifiers2() != null) {
            typeName = getTypeName(ctx.declarationSpecifiers2().declarationSpecifier());
        }

        record(typeName, TypeContext.PARAMETER);

        return super.visitParameterDeclaration(ctx);
    }

    @Override
    public Void visitStructDeclaration(
            CParser.StructDeclarationContext ctx) {

        if (ctx.specifierQualifierList() != null) {

            String typeName = getTypeName(ctx.specifierQualifierList());

            record(typeName, TypeContext.FIELD);
        }

        return super.visitStructDeclaration(ctx);
    }

    @Override
    public Void visitDeclaration(
            CParser.DeclarationContext ctx) {

        if (ctx.declarationSpecifiers() == null) {
            return super.visitDeclaration(ctx);
        }

        String typeName = getTypeName(ctx.declarationSpecifiers().declarationSpecifier());

        if (isTypedef(ctx)) {

            List<CParser.DeclarationSpecifierContext> specifiers =
                    ctx.declarationSpecifiers().declarationSpecifier();

            // Check if the last declaration specifier is a typedefName
            CParser.DeclarationSpecifierContext last =
                    specifiers.get(specifiers.size() - 1);

            if (last.typeSpecifier() != null
                    && last.typeSpecifier().typedefName() != null) {

                // Remove the typedef name from the type
                specifiers = specifiers.subList(0, specifiers.size() - 1);

                typeName = getTypeName(specifiers);
            }

            record(typeName, TypeContext.TYPEDEF);
        }
        else if (ctx.initDeclaratorList() == null) {
            return super.visitDeclaration(ctx);
        }
        else if (insideFunction) {
            record(typeName, TypeContext.LOCAL_VARIABLE);
        }
        else {
            record(typeName, TypeContext.GLOBAL_VARIABLE);
        }

        return super.visitDeclaration(ctx);
    }

    @Override
    public Void visitCastExpression(
            CParser.CastExpressionContext ctx) {

        if (ctx.typeName() != null) {

            String typeName =
                    getTypeName(ctx.typeName().specifierQualifierList());

            record(typeName, TypeContext.CAST);
        }

        return super.visitCastExpression(ctx);
    }

    private boolean isTypedef(CParser.DeclarationContext ctx) {

        for (CParser.DeclarationSpecifierContext specifier : ctx.declarationSpecifiers().declarationSpecifier()) {

            if (specifier.storageClassSpecifier() != null
                    && specifier.storageClassSpecifier()
                    .getText()
                    .equals("typedef")) {

                return true;
            }
        }

        return false;
    }

    private boolean isTypeDefinition(CParser.DeclarationContext ctx) {

        for (CParser.DeclarationSpecifierContext specifier
                : ctx.declarationSpecifiers().declarationSpecifier()) {

            if (specifier.typeSpecifier() != null) {

                CParser.TypeSpecifierContext typeSpecifier =
                        specifier.typeSpecifier();

                if (typeSpecifier.structOrUnionSpecifier() != null) {

                    CParser.StructOrUnionSpecifierContext struct =
                            typeSpecifier.structOrUnionSpecifier();

                    // Has {...} -> actual struct/union definition
                    if (struct.LeftBrace() != null) {
                        return true;
                    }
                }

                if (typeSpecifier.enumSpecifier() != null) {

                    CParser.EnumSpecifierContext enumCtx =
                            typeSpecifier.enumSpecifier();

                    // Has {...} -> enum definition
                    if (enumCtx.LeftBrace() != null) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}