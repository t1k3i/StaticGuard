package com.staticguard.rules;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.common.RuleContext;
import com.staticguard.enums.TypeContext;
import com.staticguard.visitors.c.CForbiddenTypesVisitor;
import com.staticguard.visitors.java.ForbiddenTypesVisitor;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Set;

public class ForbiddenTypesRule<T> implements RuleVisitor<T> {
    private final Set<String> forbiddenTypes;
    private final Set<TypeContext> forbiddenContexts;

    public ForbiddenTypesRule(Set<String> forbiddenTypes,
                              Set<TypeContext> forbiddenContexts) {
        this.forbiddenTypes = forbiddenTypes;
        this.forbiddenContexts = forbiddenContexts;
    }

    @Override
    public void run(T astRoot, RuleContext context) {
        if (astRoot instanceof CompilationUnit root) {
            root.accept(new ForbiddenTypesVisitor(forbiddenTypes, forbiddenContexts), context);
        }

        if (astRoot instanceof ParseTree root) {
            root.accept(new CForbiddenTypesVisitor(context, forbiddenTypes, forbiddenContexts));
        }
    }
}
