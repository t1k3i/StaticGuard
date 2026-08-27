package com.staticguard.analyzers.java;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.analyzers.Analyzer;
import com.staticguard.common.RuleContext;
import com.staticguard.enums.TypeContext;
import com.staticguard.rules.java.ClassDependencyRule;

import java.util.Map;
import java.util.Set;

public class ClassDependencyAnalyzer<T extends CompilationUnit> implements Analyzer<T> {

    private final RuleContext context;
    private final ClassDependencyRule<T> rule;

    public ClassDependencyAnalyzer(
            RuleContext context,
            ClassDependencyRule<T> rule
    ) {
        this.context = context;
        this.rule = rule;
    }

    @Override
    public void runVisitor(T ast) {
        rule.run(ast, context);
    }

    @Override
    public void postVisit() {
        var dependencies = rule.getDependencies();

        System.out.println();
        System.out.println("Class Dependencies:");

        if (dependencies.isEmpty()) {
            System.out.println("  (empty)");
            return;
        }

        for (String clazz : dependencies.keySet()) {
            Map<String, Set<TypeContext>> usedClasses = dependencies.get(clazz);

            if (usedClasses.isEmpty()) {
                System.out.println("  " + clazz + " -> (no dependencies)");
                continue;
            }

            System.out.println("  " + clazz + " uses:");
            for (Map.Entry<String, Set<TypeContext>> entry : usedClasses.entrySet()) {
                String usedClass = entry.getKey();
                Set<TypeContext> contexts = entry.getValue();

                String contextStr = String.join(", ",
                        contexts.stream().map(Enum::name).toList());

                System.out.println("    - " + usedClass + " [" + contextStr + "]");
            }
        }
    }
}
