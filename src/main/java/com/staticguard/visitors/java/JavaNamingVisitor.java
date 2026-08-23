package com.staticguard.visitors.java;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.type.TypeParameter;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.staticguard.common.RuleContext;

import java.util.regex.Pattern;

public class JavaNamingVisitor extends VoidVisitorAdapter<RuleContext> {
    private static final Pattern CAMEL_CASE =
            Pattern.compile("^[a-z][a-z0-9]*(?:[A-Z][a-z0-9]*)*$");
    private static final Pattern PASCAL_CASE =
            Pattern.compile("^[A-Z][a-z0-9]*(?:[A-Z][a-z0-9]*)*$");
    private static final Pattern UPPER_SNAKE_CASE =
            Pattern.compile("^[A-Z][A-Z0-9]*(?:_[A-Z0-9]+)*$");
    private static final Pattern TYPE_PARAM =
            Pattern.compile("^[A-Z]$");

    public JavaNamingVisitor() {
        super();
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, RuleContext arg) {
        super.visit(n, arg);

        String name = n.getNameAsString();
        if (!PASCAL_CASE.matcher(name).matches())
            report(n, arg,
                    "Class/Interface name should be PascalCase: " + name);
    }

    @Override
    public void visit(EnumDeclaration n, RuleContext arg) {
        super.visit(n, arg);

        String name = n.getNameAsString();
        if (!PASCAL_CASE.matcher(name).matches())
            report(n, arg,
                    "Enum name should be PascalCase: " + name);
    }

    @Override
    public void visit(EnumConstantDeclaration n, RuleContext arg) {
        super.visit(n, arg);

        String name = n.getNameAsString();
        if (!UPPER_SNAKE_CASE.matcher(name).matches())
            report(n, arg,
                    "Enum constant should be UPPER_SNAKE_CASE: " + name);
    }

    @Override
    public void visit(MethodDeclaration n, RuleContext arg) {
        super.visit(n, arg);

        String name = n.getNameAsString();
        if (!CAMEL_CASE.matcher(name).matches())
            report(n, arg,
                    "Method name should be camelCase: " + name);
    }

    @Override
    public void visit(FieldDeclaration n, RuleContext arg) {
        super.visit(n, arg);

        boolean isConstant = n.isStatic() && n.isFinal();

        for (VariableDeclarator v : n.getVariables()) {
            String name = v.getNameAsString();

            if (isConstant) {
                if (!UPPER_SNAKE_CASE.matcher(name).matches())
                    report(n, arg,
                            "Constant name should be UPPER_SNAKE_CASE: " + name);
            } else {
                if (!CAMEL_CASE.matcher(name).matches())
                    report(n, arg,
                            "Field name should be camelCase: " + name);
            }
        }
    }

    @Override
    public void visit(VariableDeclarator n, RuleContext arg) {
        super.visit(n, arg);

        if (!(n.getParentNode().orElse(null) instanceof FieldDeclaration)) {
            String name = n.getNameAsString();
            if (!CAMEL_CASE.matcher(name).matches())
                report(n, arg,
                        "Variable name should be camelCase: " + name);
        }
    }

    @Override
    public void visit(Parameter n, RuleContext arg) {
        super.visit(n, arg);

        String name = n.getNameAsString();
        if (!CAMEL_CASE.matcher(name).matches())
            report(n, arg, "Parameter name should be camelCase: " + name);
    }

    @Override
    public void visit(TypeParameter n, RuleContext arg) {
        super.visit(n, arg);

        String name = n.getNameAsString();
        if (!TYPE_PARAM.matcher(name).matches())
            report(n, arg,
                    "Type parameter should be a single uppercase letter: " + name);
    }

    private void report(Node node, RuleContext arg, String message) {
        arg.report(message, node.getBegin().map(p -> p.line).orElse(-1));
    }
}
