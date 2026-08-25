package com.staticguard.cli;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.analyzers.java.ProjectClassCollectorAnalyzer;
import com.staticguard.common.ProjectContext;
import com.staticguard.common.RuleContext;
import com.staticguard.enums.ControlFlowRule;
import com.staticguard.enums.Language;
import com.staticguard.parser.LanguageParser;
import com.staticguard.parser.ParserFactory;
import com.staticguard.visitors.java.PrimitiveTypeVisitor;
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
    protected File file;

    @CommandLine.Option(
            names = {"--lang"},
            description = "Project language: java or c (required for directories)"
    )
    protected String language;

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
            description = "Detect the max depth of nested loops"
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
            description = "Check naming conventions"
    )
    protected boolean naming;

    protected Integer longMethodsMaxLines;
    protected boolean longMethodsEnabled;

    @CommandLine.Option(
            names = "--long-methods",
            description = "Detect long methods (default: 30 lines; e.g. --long-methods 50)",
            arity = "0..1"
    )
    public void setLongMethodsMaxLines(String value) {
        longMethodsEnabled = true;
        longMethodsMaxLines = value == null || value.isBlank()
                ? 30
                : Integer.parseInt(value);
    }

    /* FORBIDDEN */

    @CommandLine.Option(
            names = "--forbid-methods",
            description = "Forbid method calls by name or fully qualified name, comma-separated (e.g. println,java.lang.Math.abs). A simple name such as 'abs' forbids all methods with that name.",
            split = ","
    )
    List<String> forbiddenMethods = new ArrayList<>();

    @CommandLine.Option(
            names = "--forbid-types",
            description = "Forbid types by simple or fully qualified name, comma-separated (e.g. String,java.lang.String,int).",
            split = ","
    )
    List<String> forbiddenTypes = new ArrayList<>();

    @CommandLine.Option(
            names = "--deny",
            description = "Forbidden calls: caller=callee1,callee2. Callees support simple or fully qualified names.",
            converter = DeniedCallsConverter.class
    )
    protected List<Map.Entry<String, Set<String>>> forbiddenCalls = new ArrayList<>();

    @CommandLine.Option(
            names = "--primitive-mode",
            description = "Control primitive type usage: ${COMPLETION-CANDIDATES}",
            arity = "1"
    )
    protected PrimitiveTypeVisitor.Mode primitiveMode;

    @CommandLine.Option(
            names = {"--primitive-exceptions"},
            description = "Allowed type exceptions for primitive mode (e.g. String[],Scanner)",
            split = ","
    )
    protected List<String> primitiveExceptions = new ArrayList<>();


    @CommandLine.Option(
            names = "--forbid-control-flow",
            description = "Forbidden control flow constructs, comma-separated. Supported values: BREAK,CONTINUE,RETURN,INSTANCEOF (e.g. BREAK,RETURN). Values must be uppercase.",
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
        try {
            CLIOptionsConfig config = CLIOptionsConfig.fromCLI(this);

            if (file.isDirectory()) {
                handleProject(file, config);
            } else {
                ProjectContext projectContext = new ProjectContext();
                List<File> sourceFiles = List.of(file);
                handlePreProject(sourceFiles, config, projectContext);
                handleSingleFile(file, config, projectContext, null);
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid input: " + e.getMessage());
            return 2;
        } catch (IOException e) {
            System.err.println("Could not read file: " + e.getMessage());
            return 3;
        } catch (IllegalStateException e) {
            System.err.println("Internal parser error: " + e.getMessage());
            return 4;
        } catch (Exception e) {
            System.err.println("Unexpected error.");
            return 1;
        }

        return 0;
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

    private void handleSingleFile(
            File file,
            CLIOptionsConfig config,
            ProjectContext projectContext,
            File sourceRoot
    ) throws Exception {

        LanguageParser<?> parser =
                ParserFactory.createParser(file, sourceRoot, config);

        var context = new RuleContext(file);

        parser.handle(
                config,
                context,
                projectContext
        );
    }

    private void handleProject(File dir, CLIOptionsConfig config) throws Exception {
        var projectLang = config.getLang();

        List<File> sourceFiles = collectSourceFiles(dir, projectLang);

        ProjectContext projectContext = new ProjectContext();
        handlePreProject(sourceFiles, config, projectContext);

        for (File f : sourceFiles) {
            handleSingleFile(f, config, projectContext, dir);
        }
    }

    private void handlePreProject(List<File> sourceFiles, CLIOptionsConfig config, ProjectContext projectContext) throws Exception {
        for (File f : sourceFiles) {
            LanguageParser<?> parser = ParserFactory.createParser(f, config);
            Object ast = parser.parse();

            if (config.getLang() == Language.JAVA) {
                new ProjectClassCollectorAnalyzer(projectContext)
                        .runVisitor((CompilationUnit) ast);
            }
        }
    }
}
