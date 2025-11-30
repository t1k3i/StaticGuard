package com.staticguard.visitors.c;

import com.staticguard.CBaseVisitor;
import com.staticguard.common.RuleContext;
import com.staticguard.CParser;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.regex.Pattern;

public class CNamingVisitor extends CBaseVisitor<Void> {
    private static final Pattern CAMEL_CASE = Pattern.compile("^[a-z][a-zA-Z0-9]*$");
    private static final Pattern PASCAL_CASE = Pattern.compile("^[A-Z][a-zA-Z0-9]*$");
    private static final Pattern UPPER_SNAKE_CASE = Pattern.compile("^[A-Z0-9_]+$");

    private final RuleContext context;
    private final boolean addComments;

    public CNamingVisitor(RuleContext context) {
        this.context = context;
        this.addComments = false;
    }

    public CNamingVisitor(RuleContext context, boolean addComments) {
        this.context = context;
        this.addComments = addComments;
    }

    private void report(String message, ParserRuleContext ctx) {
        if (ctx != null && ctx.getStart() != null) {
            int line = ctx.getStart().getLine();
            context.report(message, line);
        } else {
            context.report(message, -1);
        }

        // TODO: add comments
    }

    @Override
    public Void visitFunctionDefinition(CParser.FunctionDefinitionContext ctx) {
        CParser.DeclaratorContext decl = ctx.declarator();
        if (decl != null && decl.directDeclarator() != null) {
            var idNode = decl.directDeclarator().directDeclarator().Identifier();
            if (idNode != null) {
                String name = idNode.getText();
                if (!CAMEL_CASE.matcher(name).matches()) {
                    report("Function name should be lowerCamelCase: " + name, ctx);
                }
            }
        }

        return super.visitFunctionDefinition(ctx);
    }

   @Override
    public Void visitStructOrUnionSpecifier(CParser.StructOrUnionSpecifierContext ctx) {
        if (ctx.Identifier() != null) {
            String name = ctx.Identifier().getText();
            if (!PASCAL_CASE.matcher(name).matches()) {
                report("Struct/Union name should be UpperCamelCase: " + name, ctx);
            }
        }
        return super.visitStructOrUnionSpecifier(ctx);
    }

    @Override
    public Void visitEnumSpecifier(CParser.EnumSpecifierContext ctx) {
        if (ctx.Identifier() != null) {
            String name = ctx.Identifier().getText();
            if (!PASCAL_CASE.matcher(name).matches()) {
                report("Enum name should be UpperCamelCase: " + name, ctx);
            }
        }

        if (ctx.enumeratorList() != null) {
            for (CParser.EnumeratorContext e : ctx.enumeratorList().enumerator()) {
                String enumConst = e.enumerationConstant().Identifier().getText();
                if (!UPPER_SNAKE_CASE.matcher(enumConst).matches()) {
                    report("Enum constant should be UPPER_SNAKE_CASE: " + enumConst, e);
                }
            }
        }

        return super.visitEnumSpecifier(ctx);
    }

    @Override
    public Void visitTypedefName(CParser.TypedefNameContext ctx) {
        if (ctx.Identifier() != null) {
            String name = ctx.Identifier().getText();
            if (!PASCAL_CASE.matcher(name).matches()) {
                report("Typedef name should be UpperCamelCase: " + name, ctx);
            }
        }
        return super.visitTypedefName(ctx);
    }
}
