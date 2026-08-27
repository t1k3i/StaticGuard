package com.staticguard.visitors.c;

import com.staticguard.CParser;
import com.staticguard.common.RuleContext;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;
import java.util.List;

public class CVisitorHelper {
    protected static void report(String message, ParserRuleContext ctx, RuleContext context) {
        int line = ctx != null && ctx.getStart() != null ? ctx.getStart().getLine() : -1;
        context.report(message, line);
    }

    protected static String getTypeName(CParser.SpecifierQualifierListContext ctx) {

        if (ctx == null) return null;

        List<String> types = new ArrayList<>();

        if (ctx.typeSpecifier() != null) {

            CParser.TypeSpecifierContext typeSpecifier = ctx.typeSpecifier();

            // struct / union
            if (typeSpecifier.structOrUnionSpecifier() != null) {

                CParser.StructOrUnionSpecifierContext struct = typeSpecifier.structOrUnionSpecifier();

                String result = struct.structOrUnion().getText();

                if (struct.Identifier() != null)
                    result += " " + struct.Identifier().getText();

                types.add(result);
            }

            // enum
            else if (typeSpecifier.enumSpecifier() != null) {

                CParser.EnumSpecifierContext enumCtx = typeSpecifier.enumSpecifier();

                String result = "enum";

                if (enumCtx.Identifier() != null)
                    result += " " + enumCtx.Identifier().getText();

                types.add(result);
            }

            // normal type
            else {
                types.add(typeSpecifier.getText());
            }
        }

        if (ctx.specifierQualifierList() != null) {

            String nested = getTypeName(ctx.specifierQualifierList());

            if (nested != null && !nested.isEmpty())
                types.add(nested);
        }

        return String.join(" ", types);
    }

    protected static String getTypeName(List<CParser.DeclarationSpecifierContext> specifiers) {

        return specifiers.stream()
                .filter(specifier -> specifier.typeSpecifier() != null)
                .map(specifier -> {

                    CParser.TypeSpecifierContext typeSpecifier = specifier.typeSpecifier();

                    if (typeSpecifier.structOrUnionSpecifier() != null) {

                        CParser.StructOrUnionSpecifierContext struct = typeSpecifier.structOrUnionSpecifier();

                        String result = struct.structOrUnion().getText();

                        if (struct.Identifier() != null)
                            result += " " + struct.Identifier().getText();

                        return result;
                    }

                    if (typeSpecifier.enumSpecifier() != null) {
                        CParser.EnumSpecifierContext enumCtx = typeSpecifier.enumSpecifier();

                        String result = "enum";

                        if (enumCtx.Identifier() != null)
                            result += " " + enumCtx.Identifier().getText();

                        return result;
                    }

                    return typeSpecifier.getText();
                })
                .collect(java.util.stream.Collectors.joining(" "));
    }

    protected static boolean isTypedef(CParser.DeclarationContext ctx) {

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
}
