package com.staticguard.visitors.c;

import com.staticguard.CBaseVisitor;
import com.staticguard.CParser;
import com.staticguard.common.RuleContext;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.regex.Pattern;

import static com.staticguard.visitors.c.CVisitorHelper.report;

public class CNamingVisitor extends CBaseVisitor<Void> {

    private static final Pattern CAMEL_CASE =
            Pattern.compile("^[a-z][a-z0-9]*(?:[A-Z][a-z0-9]*)*$");
    private static final Pattern PASCAL_CASE =
            Pattern.compile("^[A-Z][a-z0-9]*(?:[A-Z][a-z0-9]*)*$");
    private static final Pattern UPPER_SNAKE_CASE =
            Pattern.compile("^[A-Z][A-Z0-9]*(?:_[A-Z0-9]+)*$");

    private final RuleContext context;

    public CNamingVisitor(RuleContext context) {
        this.context = context;
    }

    private void checkCamelCase(String name, String type, ParserRuleContext ctx) {
        if (!CAMEL_CASE.matcher(name).matches()) {
            report(type + " name should be camelCase: " + name, ctx, context);
        }
    }

    private void checkPascalCase(String name, String type, ParserRuleContext ctx) {
        if (!PASCAL_CASE.matcher(name).matches()) {
            report(type + " name should be PascalCase: " + name, ctx, context);
        }
    }

    private void checkUpperSnakeCase(String name, String type, ParserRuleContext ctx) {
        if (!UPPER_SNAKE_CASE.matcher(name).matches()) {
            report(type + " name should be UPPER_SNAKE_CASE: " + name, ctx, context);
        }
    }

    @Override
    public Void visitFunctionDefinition(CParser.FunctionDefinitionContext ctx) {
        String text = ctx.declarator().directDeclarator().getText();

        int parenthesis = text.indexOf('(');

        if (parenthesis > 0) {
            String name = text.substring(0, parenthesis);

            if (!CAMEL_CASE.matcher(name).matches()) {
                report(
                        "Function name should be camelCase: " + name,
                        ctx,
                        context
                );
            }
        }

        return super.visitFunctionDefinition(ctx);
    }

    @Override
    public Void visitParameterDeclaration(CParser.ParameterDeclarationContext ctx) {
        if (ctx.declarator() != null &&
                ctx.declarator().directDeclarator() != null &&
                ctx.declarator().directDeclarator().Identifier() != null) {

            String name = ctx.declarator()
                    .directDeclarator()
                    .Identifier()
                    .getText();

            checkCamelCase(
                    name,
                    "Parameter",
                    ctx
            );
        }

        return super.visitParameterDeclaration(ctx);
    }

    @Override
    public Void visitDeclaration(CParser.DeclarationContext ctx) {
        if (ctx.initDeclaratorList() != null) {

            boolean isConst = ctx.getText().startsWith("const");

            for (CParser.InitDeclaratorContext init :
                    ctx.initDeclaratorList().initDeclarator()) {

                if (init.declarator() == null ||
                        init.declarator().directDeclarator() == null) {
                    continue;
                }

                var direct = init.declarator().directDeclarator();

                if (direct.Identifier() == null) {
                    continue;
                }

                String name = direct.Identifier().getText();

                if (isConst) {
                    checkUpperSnakeCase(
                            name,
                            "Constant",
                            ctx
                    );
                } else {
                    checkCamelCase(
                            name,
                            "Variable",
                            ctx
                    );
                }
            }
        }

        return super.visitDeclaration(ctx);
    }

    @Override
    public Void visitStructOrUnionSpecifier(CParser.StructOrUnionSpecifierContext ctx) {
        if (ctx.Identifier() != null) {
            String name = ctx.Identifier().getText();

            checkPascalCase(
                    name,
                    "Struct/Union",
                    ctx
            );
        }

        return super.visitStructOrUnionSpecifier(ctx);
    }

    @Override
    public Void visitEnumSpecifier(CParser.EnumSpecifierContext ctx) {
        if (ctx.Identifier() != null) {
            String name = ctx.Identifier().getText();

            checkPascalCase(
                    name,
                    "Enum",
                    ctx
            );
        }

        if (ctx.enumeratorList() != null) {
            for (CParser.EnumeratorContext e :
                    ctx.enumeratorList().enumerator()) {

                if (e.enumerationConstant() != null &&
                        e.enumerationConstant().Identifier() != null) {

                    String name = e.enumerationConstant()
                            .Identifier()
                            .getText();

                    checkUpperSnakeCase(
                            name,
                            "Enum constant",
                            e
                    );
                }
            }
        }

        return super.visitEnumSpecifier(ctx);
    }

    @Override
    public Void visitTypedefName(CParser.TypedefNameContext ctx) {
        if (ctx.Identifier() != null) {
            String name = ctx.Identifier().getText();

            checkPascalCase(
                    name,
                    "Typedef",
                    ctx
            );
        }

        return super.visitTypedefName(ctx);
    }
}