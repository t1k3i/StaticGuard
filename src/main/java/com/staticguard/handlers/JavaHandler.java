package com.staticguard.handlers;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.analyzers.*;
import com.staticguard.analyzers.java.*;
import com.staticguard.common.ProjectContext;
import com.staticguard.common.RuleContext;
import com.staticguard.cli.CLIOptionsConfig;
import com.staticguard.common.RuleVisitor;
import com.staticguard.rules.*;
import com.staticguard.rules.java.ClassDependencyRule;
import com.staticguard.rules.java.ForbiddenFieldAccessRule;
import com.staticguard.rules.java.PrimitiveTypeRule;
import com.staticguard.rules.java.UnusedImportsRule;

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
            manager.runVisitors();
            return;
        }

        /* =========================
           INFO VISITORS
           ========================= */

        if (runInfo || config.isCallGraph()) {
            manager.addVisitor(new CallGraphAnalyzer<>(new CallGraphRule<>()));
        }

        if (runInfo || config.isClassDependencies()) {
            var classDependencyRule = new ClassDependencyRule<CompilationUnit>(projectContext.projectClasses);
            manager.addVisitor(
                    new ClassDependencyAnalyzer<>(context, classDependencyRule)
            );
        }

        if (runInfo || config.isUsedTypes()) {
            var usedTypesRule = new UsedTypesRule<CompilationUnit>();
            manager.addVisitor(new UsedTypesAnalyzer<>(context, usedTypesRule));
        }

        if (runInfo || config.isLoopNesting()) {
            var loopNestingRule = new LoopNestingRule<CompilationUnit>();
            manager.addVisitor(new LoopNestingAnalyzer<>(context, loopNestingRule));
        }

         /* =========================
           GOOD PRACTICES
           ========================= */

        if (runGood || config.isNaming()) {
            RuleVisitor<CompilationUnit> namingRule = new NamingRule<>();
            manager.addVisitor(new GenericAnalyzer<>(context, namingRule));
        }

        if (runGood || config.getLongMethodsMaxLines() != null) {
            int maxLines = config.getLongMethodsMaxLines() != null
                    ? config.getLongMethodsMaxLines()
                    : 30;

            manager.addVisitor(
                    new GenericAnalyzer<>(
                            context,
                            new LongMethodRule<>(maxLines)
                    )
            );
        }

        if (runGood || config.isUnusedLocals()) {
            var unusedLocalVariableRule = new UnusedLocalVariablesRule<CompilationUnit>();
            manager.addVisitor(new GenericAnalyzer<>(context, unusedLocalVariableRule));
        }

        if (runGood || config.isUnusedImports()) {
            var unusedImportRule = new UnusedImportsRule<CompilationUnit>();
            manager.addVisitor(new GenericAnalyzer<>(context, unusedImportRule));
        }

        /* =========================
           FORBIDDEN RULES
           ========================= */

        if (!config.getForbiddenMethods().isEmpty()) {
            var forbiddenMethodRule = new ForbiddenFunctionRule<CompilationUnit>(config.getForbiddenMethods());
            manager.addVisitor(
                    new GenericAnalyzer<>(
                            context,
                            forbiddenMethodRule
                    )
            );
        }

        if (!config.getForbiddenTypes().isEmpty()) {
            var forbiddenTypesRule = new ForbiddenTypesRule<CompilationUnit>(config.getForbiddenTypes(), null);
            manager.addVisitor(
                    new GenericAnalyzer<>(context, forbiddenTypesRule)
            );
        }

        if (!config.getForbiddenCalls().isEmpty()) {
            var deniedCallsRule = new DeniedCallsRule<CompilationUnit>(config.getForbiddenCalls());
            manager.addVisitor(
                    new GenericAnalyzer<>(
                            context,
                            deniedCallsRule
                    )
            );
        }

        if (config.getPrimitiveMode() != null) {
            var primitiveTypeRule = new PrimitiveTypeRule<CompilationUnit>(config.getPrimitiveMode());
            manager.addVisitor(
                    new GenericAnalyzer<>(
                            context,
                            primitiveTypeRule
                    )
            );
        }

        if (!config.getForbiddenControlFlow().isEmpty()) {
            var forbiddenControlFlowRule = new ForbiddenControlFlowRule<CompilationUnit>(config.getForbiddenControlFlow());
            manager.addVisitor(
                    new GenericAnalyzer<>(
                            context,
                            forbiddenControlFlowRule
                    )
            );
        }

        if (config.isForbidFieldAccess()) {
            var forbiddenFieldAccessRule = new ForbiddenFieldAccessRule<CompilationUnit>();
            manager.addVisitor(
                    new GenericAnalyzer<>(context, forbiddenFieldAccessRule)
            );
        }

    /* =========================
       RUN EVERYTHING
       ========================= */

        manager.runVisitors();
    }
}
