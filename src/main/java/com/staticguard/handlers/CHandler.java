package com.staticguard.handlers;

import com.staticguard.analyzers.GenericAnalyzer;
import com.staticguard.common.ProjectContext;
import com.staticguard.common.RuleContext;
import com.staticguard.cli.CLIOptionsConfig;
import com.staticguard.analyzers.VisitorManager;
import com.staticguard.common.RuleVisitor;
import com.staticguard.rules.c.CNamingRule;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.File;

public class CHandler implements LanguageHandler {
    @Override
    public void handle(Object root, CLIOptionsConfig config, File sourceFile, ProjectContext projectContext) {
        ParseTree ast = (ParseTree) root;
        VisitorManager<ParseTree> manager = new VisitorManager<>(ast);

        if (config.isDevelopment()) {
            RuleContext context = new RuleContext(sourceFile);
            RuleVisitor<ParseTree> cNamingRule = new CNamingRule();
            manager.addVisitor(new GenericAnalyzer<ParseTree>(context, cNamingRule));
            manager.runVisitors();
            return;
        }

        if (config.isRunAll()) {
            RuleContext context = new RuleContext(sourceFile);
            RuleVisitor<ParseTree> cNamingRule = new CNamingRule();
            manager.addVisitor(new GenericAnalyzer<ParseTree>(context, cNamingRule));


        }

        manager.runVisitors();
    }
}
