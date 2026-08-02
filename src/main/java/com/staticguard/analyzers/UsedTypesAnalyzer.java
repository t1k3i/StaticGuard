package com.staticguard.analyzers;

import com.staticguard.common.RuleContext;
import com.staticguard.enums.TypeContext;
import com.staticguard.rules.UsedTypesRule;

import java.util.Map;
import java.util.Set;

public class UsedTypesAnalyzer<T> implements Analyzer<T> {
    private final RuleContext context;
    private final UsedTypesRule<T> usedTypesRule;

    public UsedTypesAnalyzer(RuleContext context, UsedTypesRule<T> usedTypesRule) {
        this.context = context;
        this.usedTypesRule = usedTypesRule;
    }

    @Override
    public void runVisitor(T ast) {
        usedTypesRule.run(ast, context);
    }

    @Override
    public void postVisit() {
        // Print a header
        System.out.println("===== Used Types Report =====");

        Map<String, Set<TypeContext>> usedTypesMap = usedTypesRule.getUsedTypes();

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
