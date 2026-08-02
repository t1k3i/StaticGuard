package com.staticguard.visitors.java;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.staticguard.enums.TypeContext;

import java.util.*;

public class ClassDependencyVisitor extends VoidVisitorAdapter<Object> {
    private final Set<String> projectClasses;
    private final Map<String, Map<String, Set<TypeContext>>> dependencies;
    private final Set<String> classesInFile = new HashSet<>();

    private String currentClass;

    public ClassDependencyVisitor(
            Set<String> projectClasses,
            Map<String, Map<String, Set<TypeContext>>> dependencies) {
        this.projectClasses = projectClasses;
        this.dependencies = dependencies;
    }

    @Override
    public void visit(CompilationUnit cu, Object arg) {
        // Pre-collect all classes in the file before processing dependencies
        if (projectClasses.isEmpty()) {
            cu.findAll(ClassOrInterfaceDeclaration.class)
                    .forEach(cls -> classesInFile.add(
                            cls.getFullyQualifiedName()
                                    .orElse(cls.getNameAsString())
                    ));
        }
        super.visit(cu, arg);
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Object arg) {
        currentClass = n.getFullyQualifiedName()
                .orElse(n.getNameAsString());

        super.visit(n, arg);
        currentClass = null;
    }

    @Override
    public void visit(FieldDeclaration n, Object arg) {
        super.visit(n, arg);
        recordType(n.getElementType(), TypeContext.FIELD);
    }

    @Override
    public void visit(MethodDeclaration n, Object arg) {
        super.visit(n, arg);
        recordType(n.getType(), TypeContext.RETURN_TYPE);
    }

    @Override
    public void visit(Parameter n, Object arg) {
        super.visit(n, arg);
        recordType(n.getType(), TypeContext.PARAMETER);
    }

    @Override
    public void visit(ObjectCreationExpr n, Object arg) {
        super.visit(n, arg);
        recordType(n.getType(), TypeContext.INSTANTIATION);
    }

    private void recordType(Type type, TypeContext context) {
        recordType(type.asString(), context);
    }

    private void recordType(String typeName, TypeContext context) {
        if (currentClass == null) return;

        Set<String> classesToCheck = projectClasses.isEmpty() ? classesInFile : projectClasses;
        if (!classesToCheck.contains(typeName)) return;

        dependencies
                .computeIfAbsent(currentClass, k -> new HashMap<>())
                .computeIfAbsent(typeName, k -> EnumSet.noneOf(TypeContext.class))
                .add(context);
    }
}
