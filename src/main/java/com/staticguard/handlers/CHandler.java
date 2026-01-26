package com.staticguard.handlers;

import com.staticguard.common.ProjectContext;
import com.staticguard.common.RuleContext;
import com.staticguard.cli.CLIOptionsConfig;
import com.staticguard.analyzers.VisitorManager;
import com.staticguard.analyzers.c.CNamingVisitorAnalyzer;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.File;

public class CHandler implements LanguageHandler {
    @Override
    public void handle(Object root, CLIOptionsConfig config, File sourceFile, ProjectContext projectContext) {
        ParseTree ast = (ParseTree) root;
        VisitorManager<ParseTree> manager = new VisitorManager<>(ast);

        if (config.isDevelopment()) {
            RuleContext context = new RuleContext(sourceFile);
            manager.addVisitor(new CNamingVisitorAnalyzer(context));
            manager.runVisitors();
            return;
        }

        if (config.isRunAll()) {
            RuleContext context = new RuleContext(sourceFile);
            manager.addVisitor(new CNamingVisitorAnalyzer(context));


        }

        manager.runVisitors();
    }
}
