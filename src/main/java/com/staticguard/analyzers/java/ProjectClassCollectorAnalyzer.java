package com.staticguard.analyzers.java;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.analyzers.Analyzer;
import com.staticguard.common.ProjectContext;
import com.staticguard.visitors.java.ProjectClassCollectorVisitor;

public class ProjectClassCollectorAnalyzer implements Analyzer<CompilationUnit> {
    ProjectContext projectContext;

    public ProjectClassCollectorAnalyzer(ProjectContext projectContext) {
        this.projectContext = projectContext;
    }

    @Override
    public void runVisitor(CompilationUnit cu) {
        cu.accept(new ProjectClassCollectorVisitor(), projectContext.projectClasses);
    }

    @Override
    public void postVisit() {
        Analyzer.super.postVisit();
    }
}
