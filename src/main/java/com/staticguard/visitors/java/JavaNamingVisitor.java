package com.staticguard.visitors.java;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.type.TypeParameter;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.staticguard.common.RuleContext;

import java.util.regex.Pattern;

public class JavaNamingVisitor extends VoidVisitorAdapter<RuleContext> {
    private static final Pattern CAMEL_CASE = Pattern.compile("^[a-z][a-zA-Z0-9]*$");
    private static final Pattern PASCAL_CASE = Pattern.compile("^[A-Z][a-zA-Z0-9]*$");
    private static final Pattern UPPER_SNAKE_CASE = Pattern.compile("^[A-Z0-9_]+$");
    private static final Pattern TYPE_PARAM = Pattern.compile("^[A-Z]$");

    private final boolean addComments;

    public JavaNamingVisitor() {
        super();
        this.addComments = false;
    }

    public JavaNamingVisitor(boolean addComments) {
        super();
        this.addComments = addComments;
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, RuleContext arg) {
        super.visit(n, arg);

        String name = n.getNameAsString();
        if (!PASCAL_CASE.matcher(name).matches())
            reportAndComment(n, arg,
                    "Class/Interface name should be UpperCamelCase: " + name);
    }

    @Override
    public void visit(EnumDeclaration n, RuleContext arg) {
        super.visit(n, arg);

        String name = n.getNameAsString();
        if (!PASCAL_CASE.matcher(name).matches())
            reportAndComment(n, arg,
                    "Enum name should be UpperCamelCase: " + name);
    }

    @Override
    public void visit(EnumConstantDeclaration n, RuleContext arg) {
        super.visit(n, arg);

        String name = n.getNameAsString();
        if (!UPPER_SNAKE_CASE.matcher(name).matches())
            reportAndComment(n, arg,
                    "Enum constant should be UPPER_SNAKE_CASE: " + name);
    }

    @Override
    public void visit(MethodDeclaration n, RuleContext arg) {
        super.visit(n, arg);

        String name = n.getNameAsString();
        if (!CAMEL_CASE.matcher(name).matches())
            reportAndComment(n, arg,
                    "Method name should be lowerCamelCase: " + name);
    }

    @Override
    public void visit(FieldDeclaration n, RuleContext arg) {
        super.visit(n, arg);

        boolean isConstant = n.isStatic() && n.isFinal();

        for (VariableDeclarator v : n.getVariables()) {
            String name = v.getNameAsString();

            if (isConstant) {
                if (!UPPER_SNAKE_CASE.matcher(name).matches())
                    reportAndComment(n, arg,
                            "Constant name should be UPPER_SNAKE_CASE: " + name);
            } else {
                if (!CAMEL_CASE.matcher(name).matches())
                    reportAndComment(n, arg,
                            "Variable name should be lowerCamelCase: " + name);
            }
        }
    }

    @Override
    public void visit(VariableDeclarator n, RuleContext arg) {
        super.visit(n, arg);

        if (!(n.getParentNode().orElse(null) instanceof FieldDeclaration)) {
            String name = n.getNameAsString();
            if (!CAMEL_CASE.matcher(name).matches())
                reportAndComment(n, arg,
                        "Local variable should be lowerCamelCase: " + name);
        }
    }

    @Override
    public void visit(Parameter n, RuleContext arg) {
        super.visit(n, arg);

        String name = n.getNameAsString();
        if (!CAMEL_CASE.matcher(name).matches())
            reportAndComment(n, arg, "Parameter name should be lowerCamelCase: " + name);
    }

    @Override
    public void visit(TypeParameter n, RuleContext arg) {
        super.visit(n, arg);

        String name = n.getNameAsString();
        if (!TYPE_PARAM.matcher(name).matches())
            reportAndComment(n, arg,
                    "Type parameter should be a single uppercase letter: " + name);
    }

    private void reportAndComment(Node node, RuleContext arg, String message) {
        arg.report(message, node.getBegin().map(p -> p.line).orElse(-1));

        if (addComments) {
            node.getParentNode().ifPresent(parent -> parent.setBlockComment(message));
        }
    }
}
