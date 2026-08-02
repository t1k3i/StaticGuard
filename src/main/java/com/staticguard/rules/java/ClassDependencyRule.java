package com.staticguard.rules.java;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.common.RuleContext;
import com.staticguard.rules.RuleVisitor;
import com.staticguard.enums.TypeContext;
import com.staticguard.visitors.java.ClassDependencyVisitor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ClassDependencyRule<T extends CompilationUnit> implements RuleVisitor<T> {
    private final Set<String> projectClasses;
    private final Map<String, Map<String, Set<TypeContext>>> dependencies = new HashMap<>();

    public ClassDependencyRule(final Set<String> projectClasses) {
        this.projectClasses = projectClasses;
    }

    public Map<String, Map<String, Set<TypeContext>>> getDependencies() {
        return dependencies;
    }

    @Override
    public void run(T astRoot, RuleContext context) {
        astRoot.accept(new ClassDependencyVisitor(projectClasses, dependencies), context);
    }
}
