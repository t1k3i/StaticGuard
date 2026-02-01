package com.staticguard.visitors.java;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.staticguard.common.RuleContext;

import java.util.HashSet;
import java.util.Set;

public class UnusedImportsVisitor extends VoidVisitorAdapter<RuleContext> {
    private final Set<String> usedTypes = new HashSet<>();

    @Override
    public void visit(ClassOrInterfaceType n, RuleContext ctx) {
        usedTypes.add(n.getNameAsString());
        super.visit(n, ctx);
    }

    @Override
    public void visit(CompilationUnit cu, RuleContext ctx) {
        super.visit(cu, ctx);

        for (ImportDeclaration imp : cu.getImports()) {
            if (imp.isAsterisk()) continue;

            String simpleName = imp.getName().getIdentifier();

            if (!usedTypes.contains(simpleName)) {
                ctx.report(
                        "Unused import: " + imp.getNameAsString(),
                        imp.getBegin().map(p -> p.line).orElse(-1)
                );
            }
        }
    }
}
