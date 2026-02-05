package com.staticguard.rules.java;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.common.RuleContext;
import com.staticguard.common.RuleVisitor;
import com.staticguard.visitors.java.JavaNamingVisitor;

public class NamingRule implements RuleVisitor<CompilationUnit> {
    @Override
    public void run(CompilationUnit astRoot, RuleContext context) {
        astRoot.accept(new JavaNamingVisitor(), context);
    }
}
