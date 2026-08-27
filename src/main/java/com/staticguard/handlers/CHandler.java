package com.staticguard.handlers;

import com.staticguard.analyzers.*;
import com.staticguard.common.ProjectContext;
import com.staticguard.common.RuleContext;
import com.staticguard.cli.CLIOptionsConfig;
import com.staticguard.rules.*;
import org.antlr.v4.runtime.tree.ParseTree;

public class CHandler implements LanguageHandler<ParseTree> {
    @Override
    public void handle(ParseTree root, CLIOptionsConfig config, RuleContext context, ProjectContext projectContext) {
        VisitorManager<ParseTree> manager = new VisitorManager<>(root, context);

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

        if (!config.getForbiddenControlFlow().isEmpty()) {
            var forbiddenControlFlowRule = new ForbiddenControlFlowRule<ParseTree>(config.getForbiddenControlFlow());
            manager.addVisitor(
                    new GenericAnalyzer<>(
                            context,
                            forbiddenControlFlowRule
                    )
            );
        }

        /* =========================
           RUN EVERYTHING
           ========================= */

        manager.runVisitors();
    }
}
