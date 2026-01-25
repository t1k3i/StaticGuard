package com.staticguard.analyzers.java;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.analyzers.Analyzer;
import com.staticguard.common.RuleContext;
import com.staticguard.enums.TypeContext;
import com.staticguard.visitors.java.UsedTypesVisitor;

import java.util.Map;
import java.util.Set;

public class UsedTypesVisitorAnalyzer implements Analyzer<CompilationUnit> {

    private final RuleContext arg;
    private final UsedTypesVisitor usedTypesVisitor = new UsedTypesVisitor();

    public UsedTypesVisitorAnalyzer(RuleContext arg) {
        this.arg = arg;
    }

    @Override
    public void runVisitor(CompilationUnit cu) {
        cu.accept(usedTypesVisitor, arg);
    }

    @Override
    public void postVisit(CompilationUnit cu) {
        // Print a header
        System.out.println("===== Used Types Report =====");

        Map<String, Set<TypeContext>> usedTypesMap = usedTypesVisitor.getUsedTypes();

        if (usedTypesMap.isEmpty()) {
            System.out.println("No types used.");
        } else {
            // Sort types alphabetically
            usedTypesMap.keySet().stream()
                    .sorted()
                    .forEach(typeName -> {
                        Set<TypeContext> contexts = usedTypesMap.get(typeName);
                        String contextList = contexts.stream()
                                .sorted()
                                .map(Enum::name)
                                .reduce((a, b) -> a + ", " + b)
                                .orElse("");
                        System.out.println(typeName + " → used in: " + contextList);
                    });
        }

        System.out.println("===== End of Report =====");
    }
}
