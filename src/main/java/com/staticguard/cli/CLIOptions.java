package com.staticguard.cli;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.analyzers.java.ProjectClassCollectorAnalyzer;
import com.staticguard.common.ProjectContext;
import com.staticguard.common.RuleContext;
import com.staticguard.enums.ControlFlowRule;
import com.staticguard.enums.Language;
import com.staticguard.handlers.CHandler;
import com.staticguard.handlers.JavaHandler;
import com.staticguard.parser.LanguageParser;
import com.staticguard.parser.ParserFactory;
import com.staticguard.visitors.java.PrimitiveTypeVisitor;
import org.antlr.v4.runtime.tree.ParseTree;
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
    protected String language;

    @CommandLine.Option(names = "--all", description = "Run all visitors")
    protected boolean runAll;

    @CommandLine.Option(names = "--dev", description = "Run just one test visitor you are developing")
    protected boolean development;

    /*  INFO  */

    @CommandLine.Option(
            names = "--info",
            description = "Run all informational visitors"
    )
    protected boolean runInfo;

    @CommandLine.Option(
            names = "--call-graph",
            description = "Analyze method call graph"
    )
    protected boolean callGraph;

    @CommandLine.Option(
            names = "--class-deps",
            description = "Analyze class dependencies"
    )
    protected boolean classDependencies;

    @CommandLine.Option(
            names = "--used-types",
            description = "Report used types"
    )
    protected boolean usedTypes;

    @CommandLine.Option(
            names = "--loop-nesting",
            description = "Detect deeply nested loops"
    )
    protected boolean loopNesting;

    /* GOOD PRACTICES */

    @CommandLine.Option(
            names = "--good-practices",
            description = "Run all good-practice visitors"
    )
    protected boolean runGoodPractices;

    @CommandLine.Option(
            names = "--unused-imports",
            description = "Detect unused imports"
    )
    protected boolean unusedImports;

    @CommandLine.Option(
            names = "--unused-locals",
            description = "Detect unused local variables"
    )
    protected boolean unusedLocals;

    @CommandLine.Option(
            names = "--naming",
            description = "Check Java naming conventions"
    )
    protected boolean naming;

    @CommandLine.Option(
            names = "--long-methods",
            description = "Detect long methods (default: ${DEFAULT-VALUE} lines)",
            defaultValue = "30",
            arity = "0..1"
    )
    protected Integer longMethodsMaxLines;

    /* FORBIDDEN */

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
    protected List<Map.Entry<String, Set<String>>> forbiddenCalls = new ArrayList<>();

    @CommandLine.Option(
            names = "--primitive-mode",
            description = "Check primitive types: ${COMPLETION-CANDIDATES}",
            arity = "1"
    )
    protected PrimitiveTypeVisitor.Mode primitiveMode;

    @CommandLine.Option(
            names = "--forbid-control-flow",
            description = "Forbidden control flow constructs (break,continue,return,instanceof)",
            split = ","
    )
    protected Set<ControlFlowRule> forbiddenControlFlow = EnumSet.noneOf(ControlFlowRule.class);

    @CommandLine.Option(
            names = "--forbid-field-access",
            description = "Forbid direct field access"
    )
    protected boolean forbidFieldAccess;

    @Override
    public Integer call() {
        CLIOptionsConfig config = CLIOptionsConfig.fromCLI(this);

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
        var context = new RuleContext(file);
        parser.handle(config, context, projectContext);
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
