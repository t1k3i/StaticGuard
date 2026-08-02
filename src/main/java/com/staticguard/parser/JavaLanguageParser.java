package com.staticguard.parser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.cli.CLIOptionsConfig;
import com.staticguard.common.ProjectContext;
import com.staticguard.common.RuleContext;
import com.staticguard.enums.Language;
import com.staticguard.handlers.JavaHandler;

import java.io.File;
import java.io.FileNotFoundException;

public class JavaLanguageParser extends LanguageParser<CompilationUnit> {

    public JavaLanguageParser(File file) {
        super(file, Language.JAVA);
    }

    @Override
    public CompilationUnit parse() throws FileNotFoundException {
        return StaticJavaParser.parse(file);
    }

    @Override
    public void handle(CLIOptionsConfig config, RuleContext context, ProjectContext projectContext) throws Exception {
        System.out.println("Parsing file: " + file.getName());
        var cu = parse();
        System.out.println("Parsing succeeded.");
        new JavaHandler().handle(cu, config, context, projectContext);
    }
}
