package com.staticguard.rules;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.common.RuleContext;
import com.staticguard.common.RuleVisitor;
import com.staticguard.enums.TypeContext;
import com.staticguard.visitors.c.CUsedTypesVisitor;
import com.staticguard.visitors.java.UsedTypesVisitor;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UsedTypesRule<T> implements RuleVisitor<T> {
    private final Map<String, Set<TypeContext>> usedTypes = new HashMap<>();

    @Override
    public void run(T astRoot, RuleContext context) {
        if (astRoot instanceof CompilationUnit root) {
            root.accept(new UsedTypesVisitor(usedTypes), null);
        }

        if (astRoot instanceof ParseTree root) {
            root.accept(new CUsedTypesVisitor(usedTypes));
        }
    }

    public Map<String, Set<TypeContext>> getUsedTypes() {
        return usedTypes;
    }
}
