package com.staticguard.parser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.staticguard.cli.CLIOptionsConfig;
import com.staticguard.common.ProjectContext;
import com.staticguard.common.RuleContext;
import com.staticguard.enums.Language;
import com.staticguard.handlers.JavaHandler;

import java.io.File;
import java.io.FileNotFoundException;

public class JavaLanguageParser extends LanguageParser<CompilationUnit> {

    private final File sourceRoot;

    public JavaLanguageParser(File file, File sourceRoot) {
        super(file, Language.JAVA);
        this.sourceRoot = sourceRoot;
    }

    public JavaLanguageParser(File file) {
        super(file, Language.JAVA);
        this.sourceRoot = null;
    }

    @Override
    public CompilationUnit parse() throws FileNotFoundException {

        CombinedTypeSolver typeSolver = new CombinedTypeSolver();
        typeSolver.add(new ReflectionTypeSolver());
        if (sourceRoot != null) {
            typeSolver.add(
                    new JavaParserTypeSolver(sourceRoot)
            );
        }

        JavaSymbolSolver symbolSolver =
                new JavaSymbolSolver(typeSolver);

        ParserConfiguration config = new ParserConfiguration()
                .setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_21)
                .setSymbolResolver(symbolSolver);

        JavaParser parser = new JavaParser(config);

        return parser.parse(file)
                .getResult()
                .orElseThrow();
    }

    @Override
    public void handle(CLIOptionsConfig config, RuleContext context, ProjectContext projectContext) throws Exception {
        System.out.println("Parsing file: " + file.getName());
        var cu = parse();
        System.out.println("Parsing succeeded.");
        new JavaHandler().handle(cu, config, context, projectContext);
    }
}
