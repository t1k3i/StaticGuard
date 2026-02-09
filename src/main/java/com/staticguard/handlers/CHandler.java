package com.staticguard.handlers;

import com.staticguard.analyzers.*;
import com.staticguard.common.ProjectContext;
import com.staticguard.common.RuleContext;
import com.staticguard.cli.CLIOptionsConfig;
import com.staticguard.rules.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.File;

public class CHandler implements LanguageHandler {
    @Override
    public void handle(Object root, CLIOptionsConfig config, File sourceFile, ProjectContext projectContext) {
        ParseTree ast = (ParseTree) root;
        VisitorManager<ParseTree> manager = new VisitorManager<>(ast);
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
            var callGraphRule = new CallGraphRule<ParseTree>();
            manager.addVisitor(new CallGraphAnalyzer<>(callGraphRule));
        }

        if (runInfo || config.isClassDependencies()) {
            warnIf(config.isClassDependencies(), "--class-deps");
        }

        if (runInfo || config.isUsedTypes()) {
            var usedTypesRule = new UsedTypesRule<ParseTree>();
            manager.addVisitor(new UsedTypesAnalyzer<>(context, usedTypesRule));
        }

        if (runInfo || config.isLoopNesting()) {
            var loopNestingRule = new LoopNestingRule<ParseTree>();
            manager.addVisitor(new LoopNestingAnalyzer<>(context, loopNestingRule));
        }

         /* =========================
           GOOD PRACTICES
           ========================= */

        if (runGood || config.isNaming()) {
            var namingRule = new NamingRule<ParseTree>();
            manager.addVisitor(new GenericAnalyzer<>(context, namingRule));
        }

        if (runGood || config.getLongMethodsMaxLines() != null) {
            int maxLines = config.getLongMethodsMaxLines() != null
                    ? config.getLongMethodsMaxLines()
                    : 30;

            var longMethodRule = new LongMethodRule<ParseTree>(maxLines);
            manager.addVisitor(
                    new GenericAnalyzer<>(
                            context,
                            longMethodRule
                    )
            );
        }

        if (runGood || config.isUnusedLocals()) {
            var unusedLocalVariablesRule = new UnusedLocalVariablesRule<ParseTree>();
            manager.addVisitor(new GenericAnalyzer<>(context, unusedLocalVariablesRule));
        }

        if (runGood || config.isUnusedImports()) {
            warnIf(config.isUnusedImports(), "--unused-imports");
        }

        /* =========================
           FORBIDDEN RULES
           ========================= */

        if (!config.getForbiddenMethods().isEmpty()) {
            var forbiddenMethodRule = new ForbiddenFunctionRule<ParseTree>(config.getForbiddenMethods());
            manager.addVisitor(
                    new GenericAnalyzer<>(
                            context,
                            forbiddenMethodRule
                    )
            );
        }

        if (!config.getForbiddenTypes().isEmpty()) {
            var forbiddenTypesRule = new ForbiddenTypesRule<ParseTree>(config.getForbiddenTypes(), null);
            manager.addVisitor(
                    new GenericAnalyzer<>(context, forbiddenTypesRule)
            );
        }

        if (!config.getForbiddenCalls().isEmpty()) {
            var deniedCallsRule = new DeniedCallsRule<ParseTree>(config.getForbiddenCalls());
            manager.addVisitor(
                    new GenericAnalyzer<>(
                            context,
                            deniedCallsRule
                    )
            );
        }

        warnIf(config.getPrimitiveMode() != null, "--primitive-mode");

        if (!config.getForbiddenControlFlow().isEmpty()) {
            var forbiddenControlFlowRule = new ForbiddenControlFlowRule<ParseTree>(config.getForbiddenControlFlow());
            manager.addVisitor(
                    new GenericAnalyzer<>(
                            context,
                            forbiddenControlFlowRule
                    )
            );
        }

        warnIf(config.isForbidFieldAccess(), "--forbid-field-access");

        /* =========================
           RUN EVERYTHING
           ========================= */

        manager.runVisitors();
    }

    private void warnIf(boolean condition, String flag) {
        if (condition) {
            System.err.println(
                    "[WARN] Flag " + flag + " is not supported for C — skipping"
            );
        }
    }
}
