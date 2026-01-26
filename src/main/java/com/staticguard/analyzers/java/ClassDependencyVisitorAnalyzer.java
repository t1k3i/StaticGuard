package com.staticguard.analyzers.java;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.analyzers.Analyzer;
import com.staticguard.common.RuleContext;
import com.staticguard.enums.TypeContext;
import com.staticguard.visitors.java.ClassDependencyVisitor;
import com.staticguard.visitors.java.ProjectClassCollectorVisitor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ClassDependencyVisitorAnalyzer implements Analyzer<CompilationUnit> {

    private final Set<String> projectClasses;
    private final Map<String, Map<String, Set<TypeContext>>> dependencies = new HashMap<>();

    public ClassDependencyVisitorAnalyzer(final Set<String> projectClasses) {
        this.projectClasses = projectClasses;
    }

    @Override
    public void runVisitor(CompilationUnit cu) {
        cu.accept(new ClassDependencyVisitor(projectClasses, dependencies), null);
    }

    @Override
    public void postVisit(CompilationUnit cu) {
        System.out.println("=== Project Class Dependencies ===");

        for (String clazz : dependencies.keySet()) {
            Map<String, Set<TypeContext>> usedClasses = dependencies.get(clazz);

            if (usedClasses.isEmpty()) {
                System.out.println(clazz + " → (no dependencies)");
                continue;
            }

            System.out.println(clazz + " uses:");
            for (Map.Entry<String, Set<TypeContext>> entry : usedClasses.entrySet()) {
                String usedClass = entry.getKey();
                Set<TypeContext> contexts = entry.getValue();

                // Join the contexts as comma-separated
                String contextStr = String.join(", ",
                        contexts.stream().map(Enum::name).toList());

                System.out.println("  - " + usedClass + " [" + contextStr + "]");
            }
            System.out.println();
        }
    }
}
