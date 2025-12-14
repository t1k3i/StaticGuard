package com.staticguard.handlers;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.analyzers.java.CallGraphVisitorAnalyzer;
import com.staticguard.analyzers.java.ForbiddenMethodVisitorAnalyzer;
import com.staticguard.common.RuleContext;
import com.staticguard.cli.CLIOptionsConfig;
import com.staticguard.analyzers.VisitorManager;
import com.staticguard.analyzers.java.JavaNamingVisitorAnalyzer;
import com.staticguard.analyzers.java.LoopNestingVisitorAnalyzer;

import java.io.File;

public class JavaHandler implements LanguageHandler {
    @Override
    public void handle(Object root, CLIOptionsConfig config, File sourceFile) {
        CompilationUnit cu = (CompilationUnit) root;
        VisitorManager<CompilationUnit> manager = new VisitorManager<>(cu);

        if (config.isDevelopment()) {
            manager.addVisitor(new LoopNestingVisitorAnalyzer());
            manager.runVisitors();
            return;
        }

        RuleContext context = new RuleContext(sourceFile);

        if (config.isRunAll()) {
            manager.addVisitor(new JavaNamingVisitorAnalyzer(context));

            manager.addVisitor(new LoopNestingVisitorAnalyzer());

            manager.addVisitor(new CallGraphVisitorAnalyzer());
        }

        if (!config.getForbiddenMethods().isEmpty()) {
            manager.addVisitor(new ForbiddenMethodVisitorAnalyzer(context, config.getForbiddenMethods()));
        }

        manager.runVisitors();
    }
}
