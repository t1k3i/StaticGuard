package com.staticguard.handlers;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.common.RuleContext;
import com.staticguard.cli.CLIOptionsConfig;
import com.staticguard.analyzers.VisitorManager;
import com.staticguard.analyzers.java.JavaNamingVisitorAnalyzer;
import com.staticguard.analyzers.java.LoopNestingVisitorAnalyzer;

public class JavaHandler implements LanguageHandler {
    @Override
    public void handle(Object root, CLIOptionsConfig config) {
        CompilationUnit cu = (CompilationUnit) root;
        VisitorManager<CompilationUnit> manager = new VisitorManager(cu);

        if (config.isDevelopment()) {
            manager.addVisitor(new LoopNestingVisitorAnalyzer());
            manager.runVisitors();
            return;
        }

        if (config.isRunAll()) {
            RuleContext context = new RuleContext();
            manager.addVisitor(new JavaNamingVisitorAnalyzer(context));

            manager.addVisitor(new LoopNestingVisitorAnalyzer());
        }

        manager.runVisitors();
    }
}
