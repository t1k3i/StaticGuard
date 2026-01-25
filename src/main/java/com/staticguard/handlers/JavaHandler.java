package com.staticguard.handlers;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.analyzers.java.*;
import com.staticguard.common.RuleContext;
import com.staticguard.cli.CLIOptionsConfig;
import com.staticguard.analyzers.VisitorManager;

import java.io.File;

public class JavaHandler implements LanguageHandler {
    @Override
    public void handle(Object root, CLIOptionsConfig config, File sourceFile) {
        CompilationUnit cu = (CompilationUnit) root;
        VisitorManager<CompilationUnit> manager = new VisitorManager<>(cu);

        RuleContext context = new RuleContext(sourceFile);

        if (config.isDevelopment()) {
            manager.addVisitor(new UsedTypesVisitorAnalyzer(context));
            manager.runVisitors();
            return;
        }

        if (config.isRunAll()) {
            manager.addVisitor(new JavaNamingVisitorAnalyzer(context));

            manager.addVisitor(new LoopNestingVisitorAnalyzer());

            manager.addVisitor(new CallGraphVisitorAnalyzer());

            manager.addVisitor(new UsedTypesVisitorAnalyzer(context));
        }

        if (!config.getForbiddenMethods().isEmpty()) {
            manager.addVisitor(new ForbiddenMethodVisitorAnalyzer(context, config.getForbiddenMethods()));
        }

        if (!config.getForbiddenTypes().isEmpty()) {
            manager.addVisitor(new ForbiddenTypesVisitorAnalyzer(context, config.getForbiddenTypes(), null));
        }

        if (!config.getAllowedCalls().isEmpty()) {
            manager.addVisitor(new AllowedCallsVisitorAnalyzer(context, config.getAllowedCalls()));
        }

        manager.runVisitors();
    }
}
