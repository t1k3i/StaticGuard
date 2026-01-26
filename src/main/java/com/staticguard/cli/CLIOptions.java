package com.staticguard.cli;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.analyzers.java.ProjectClassCollectorAnalyzer;
import com.staticguard.common.ProjectContext;
import com.staticguard.enums.Language;
import com.staticguard.handlers.CHandler;
import com.staticguard.handlers.JavaHandler;
import com.staticguard.parser.LanguageParser;
import com.staticguard.parser.ParserFactory;
import com.staticguard.visitors.java.PrimitiveTypeVisitor;
import picocli.CommandLine;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "staticguard",
        mixinStandardHelpOptions = true,
        version = "1.0",
        description = "Static analysis for Java and C source files."
)
public class CLIOptions implements Callable<Integer> {
    @CommandLine.Parameters(index = "0", description = "The source file to analyze")
    private File file;

    @CommandLine.Option(
            names = {"--lang"},
            description = "Project language: java or c (required for directories)"
    )
    private String language;

    @CommandLine.Option(names = "--all", description = "Run all visitors")
    private boolean runAll;

    @CommandLine.Option(names = "--dev", description = "Run just one test visitor you are developing")
    private boolean development;

    @CommandLine.Option(
            names = "--forbid-methods",
            description = "Forbidden method calls (e.g. System.out.println)",
            split = ","
    )
    List<String> forbiddenMethods = new ArrayList<>();;

    @CommandLine.Option(
            names = "--forbid-types",
            description = "Forbidden types (e.g. int)",
            split = ","
    )
    List<String> forbiddenTypes = new ArrayList<>();;

    @CommandLine.Option(
            names = "--deny",
            description = "Forbidden calls: caller=callee1,callee2",
            converter = DeniedCallsConverter.class
    )
    private List<Map.Entry<String, Set<String>>> forbiddenCalls = new ArrayList<>();

    @CommandLine.Option(
            names = "--primitive-mode",
            description = "Check primitive types: ${COMPLETION-CANDIDATES}",
            arity = "1"
    )
    private PrimitiveTypeVisitor.Mode primitiveMode;

    @Override
    public Integer call() {
        Map<String, Set<String>> forbiddenCallsMap = new HashMap<>();
        for (Map.Entry<String, Set<String>> e : forbiddenCalls) {
            forbiddenCallsMap
                    .computeIfAbsent(e.getKey(), k -> new HashSet<>())
                    .addAll(e.getValue());
        }

        Set<String> forbidden = new HashSet<>(forbiddenMethods);
        Set<String> forbiddenTypeSet = new HashSet<>(forbiddenTypes);
        CLIOptionsConfig config = new CLIOptionsConfig(runAll, true, development, forbidden, forbiddenTypeSet, forbiddenCallsMap, primitiveMode);

        try {
            if (file.isDirectory()) {
                handleProject(file, config);
            } else {
                ProjectContext projectContext = new ProjectContext();
                List<File> sourceFiles = List.of(file);
                handlePreProject(sourceFiles, projectContext);
                handleSingleFile(file, config, projectContext);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }

        return 0;
    }

    private Language parseLanguage(String lang) {
        return switch (lang.toLowerCase()) {
            case "java" -> Language.JAVA;
            case "c" -> Language.C;
            default -> throw new IllegalArgumentException("Unknown language: " + lang);
        };
    }

    private List<File> collectSourceFiles(File root, Language lang) throws IOException {
        try (var paths = java.nio.file.Files.walk(root.toPath())) {
            return paths
                    .filter(java.nio.file.Files::isRegularFile)
                    .map(java.nio.file.Path::toFile)
                    .filter(f -> matchesLanguage(f, lang))
                    .toList();
        }
    }

    private boolean matchesLanguage(File f, Language lang) {
        return switch (lang) {
            case JAVA -> f.getName().endsWith(".java");
            case C -> f.getName().endsWith(".c") || f.getName().endsWith(".h");
        };
    }

    private void handleSingleFile(File file, CLIOptionsConfig config, ProjectContext projectContext) throws Exception {
        LanguageParser<?> parser = ParserFactory.createParser(file);

        System.out.println("Parsing file: " + file.getName());
        Object ast = parser.parse();
        System.out.println("Parsing succeeded.");

        switch (parser.getLanguage()) {
            case C -> new CHandler().handle(ast, config, file, projectContext);
            case JAVA -> new JavaHandler().handle(ast, config, file, projectContext);
        }
    }

    private void handleProject(File dir, CLIOptionsConfig config) throws Exception {
        if (language == null) {
            throw new IllegalArgumentException(
                    "Project directory requires --lang java or --lang c"
            );
        }

        var projectLang = parseLanguage(language);

        List<File> sourceFiles = collectSourceFiles(dir, projectLang);

        ProjectContext projectContext = new ProjectContext();
        handlePreProject(sourceFiles, projectContext);

        for (File f : sourceFiles) {
            handleSingleFile(f, config, projectContext);
        }
    }

    private void handlePreProject(List<File> sourceFiles, ProjectContext projectContext) throws Exception {
        for (File f : sourceFiles) {
            LanguageParser<?> parser = ParserFactory.createParser(f);
            Object ast = parser.parse();

            if (parser.getLanguage() == Language.JAVA) {
                new ProjectClassCollectorAnalyzer(projectContext)
                        .runVisitor((CompilationUnit) ast);
            }
        }
    }
}
