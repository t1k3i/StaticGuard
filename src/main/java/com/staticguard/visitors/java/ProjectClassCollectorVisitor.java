package com.staticguard.visitors.java;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.RecordDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.Set;

public class ProjectClassCollectorVisitor extends VoidVisitorAdapter<Set<String>> {
    @Override
    public void visit(ClassOrInterfaceDeclaration n, Set<String> classes) {
        super.visit(n, classes);
        classes.add(n.getFullyQualifiedName().orElse(n.getNameAsString()));
    }

    @Override
    public void visit(EnumDeclaration n, Set<String> classes) {
        super.visit(n, classes);
        classes.add(n.getFullyQualifiedName().orElse(n.getNameAsString()));
    }

    @Override
    public void visit(RecordDeclaration n, Set<String> classes) {
        classes.add(n.getFullyQualifiedName()
                .orElse(n.getNameAsString()));
        super.visit(n, classes);
    }
}
