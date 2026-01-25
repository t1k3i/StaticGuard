package com.staticguard.cli;

import com.staticguard.enums.Language;
import com.staticguard.handlers.CHandler;
import com.staticguard.handlers.JavaHandler;
import com.staticguard.parser.LanguageParser;
import com.staticguard.parser.ParserFactory;
import picocli.CommandLine;

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
            names = "--allow",
            description = "Allowed calls: caller=callee1,callee2",
            converter = AllowedCallsConverter.class
    )
    private List<Map.Entry<String, Set<String>>> forbiddenCalls = new ArrayList<>();

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
        CLIOptionsConfig config = new CLIOptionsConfig(runAll, true, development, forbidden, forbiddenTypeSet, forbiddenCallsMap);

        try {
            if (file.isDirectory()) {
                handleProject(file, config);
            } else {
                handleSingleFile(file, config);
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

    private void handleSingleFile(File file, CLIOptionsConfig config) throws Exception {
        LanguageParser<?> parser = ParserFactory.createParser(file);

        System.out.println("Parsing file: " + file.getName());
        Object ast = parser.parse();
        System.out.println("Parsing succeeded.");

        switch (parser.getLanguage()) {
            case C -> new CHandler().handle(ast, config, file);
            case JAVA -> new JavaHandler().handle(ast, config, file);
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

        for (File f : sourceFiles) {
            handleSingleFile(f, config);
        }
    }
}
