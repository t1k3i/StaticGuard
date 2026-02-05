package com.staticguard.handlers;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.analyzers.CallGraphAnalyzer;
import com.staticguard.analyzers.GenericAnalyzer;
import com.staticguard.analyzers.java.*;
import com.staticguard.common.ProjectContext;
import com.staticguard.common.RuleContext;
import com.staticguard.cli.CLIOptionsConfig;
import com.staticguard.analyzers.VisitorManager;
import com.staticguard.common.RuleVisitor;
import com.staticguard.rules.java.CallGraphRule;
import com.staticguard.rules.java.LongMethodRule;
import com.staticguard.rules.java.NamingRule;

import java.io.File;

public class JavaHandler implements LanguageHandler {
    @Override
    public void handle(Object root, CLIOptionsConfig config, File sourceFile, ProjectContext projectContext) {
        CompilationUnit cu = (CompilationUnit) root;
        VisitorManager<CompilationUnit> manager = new VisitorManager<>(cu);
        RuleContext context = new RuleContext(sourceFile);

        boolean runInfo = config.isRunInfo();
        boolean runGood = config.isRunGoodPractices();

        if (config.isDevelopment()) {
            manager.addVisitor(new ClassDependencyVisitorAnalyzer(projectContext.projectClasses));
            manager.runVisitors();
            return;
        }

        /* =========================
           INFO VISITORS
           ========================= */

        if (runInfo || config.isCallGraph()) {
            manager.addVisitor(new CallGraphAnalyzer<>(new CallGraphRule()));
        }

        if (runInfo || config.isClassDependencies()) {
            manager.addVisitor(
                    new ClassDependencyVisitorAnalyzer(projectContext.projectClasses)
            );
        }

        if (runInfo || config.isUsedTypes()) {
            manager.addVisitor(new UsedTypesVisitorAnalyzer(context));
        }

        if (runInfo || config.isLoopNesting()) {
            manager.addVisitor(new LoopNestingVisitorAnalyzer());
        }

         /* =========================
           GOOD PRACTICES
           ========================= */

        if (runGood || config.isNaming()) {
            RuleVisitor<CompilationUnit> namingRule = new NamingRule();
            manager.addVisitor(new GenericAnalyzer<CompilationUnit>(context, namingRule));
        }

        if (runGood || config.getLongMethodsMaxLines() != null) {
            int maxLines = config.getLongMethodsMaxLines() != null
                    ? config.getLongMethodsMaxLines()
                    : 30;

            manager.addVisitor(
                    new GenericAnalyzer<CompilationUnit>(
                            context,
                            new LongMethodRule(maxLines)
                    )
            );
        }

        if (runGood || config.isUnusedLocals()) {
            manager.addVisitor(new UnusedLocalVariablesVisitorAnalyzer(context));
        }

        if (runGood || config.isUnusedImports()) {
            manager.addVisitor(new UnusedImportsVisitorAnalyzer(context));
        }

        /* =========================
           FORBIDDEN RULES
           ========================= */

        if (!config.getForbiddenMethods().isEmpty()) {
            manager.addVisitor(
                    new ForbiddenMethodVisitorAnalyzer(
                            context,
                            config.getForbiddenMethods()
                    )
            );
        }

        if (!config.getForbiddenTypes().isEmpty()) {
            manager.addVisitor(
                    new ForbiddenTypesVisitorAnalyzer(
                            context,
                            config.getForbiddenTypes(),
                            null
                    )
            );
        }

        if (!config.getForbiddenCalls().isEmpty()) {
            manager.addVisitor(
                    new AllowedCallsVisitorAnalyzer(
                            context,
                            config.getForbiddenCalls()
                    )
            );
        }

        if (config.getPrimitiveMode() != null) {
            manager.addVisitor(
                    new PrimitiveTypeVisitorAnalyzer(
                            config.getPrimitiveMode(),
                            context
                    )
            );
        }

        if (!config.getForbiddenControlFlow().isEmpty()) {
            manager.addVisitor(
                    new ForbiddenControlFlowVisitorAnalyzer(
                            config.getForbiddenControlFlow(),
                            context
                    )
            );
        }

        if (config.isForbidFieldAccess()) {
            manager.addVisitor(
                    new ForbiddenFieldAccessVisitorAnalyzer(context)
            );
        }

    /* =========================
       RUN EVERYTHING
       ========================= */

        manager.runVisitors();
    }
}
