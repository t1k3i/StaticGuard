package rules;

import com.staticguard.analyzers.VisitorManager;
import com.staticguard.analyzers.java.ClassDependencyAnalyzer;
import com.staticguard.parser.LanguageParser;
import com.staticguard.parser.ParserFactory;
import com.staticguard.common.RuleContext;
import com.staticguard.rules.java.ClassDependencyRule;
import com.staticguard.enums.TypeContext;
import org.junit.jupiter.api.Test;
import com.github.javaparser.ast.CompilationUnit;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test: Class dependency analysis for the sample ClassDependencies.java
 *
 * This test constructs the AST for the sample file, runs the
 * ClassDependencyAnalyzer and produces a human-readable report on
 * stdout and also writes the same report to
 * target/test-output/class-deps.txt so it can be copied into a thesis
 * or documentation.
 */
public class ClassDependencyRuleTest {

    private Map<String, Map<String, Set<TypeContext>>> analyze(File file) throws Exception {
        LanguageParser<?> parser = ParserFactory.createParser(file);
        CompilationUnit ast = (CompilationUnit) parser.parse();

        Set<String> projectClasses = new HashSet<>();
        // In the test we add simple class names to ensure the visitor records
        // dependencies for types referenced without package qualification.
        projectClasses.add("Helper");

        ClassDependencyRule<CompilationUnit> rule = new ClassDependencyRule<>(projectClasses);
        RuleContext context = new RuleContext(file);
        ClassDependencyAnalyzer<CompilationUnit> analyzer = new ClassDependencyAnalyzer<>(context, rule);

        VisitorManager<CompilationUnit> manager = new VisitorManager<>(ast);
        manager.addVisitor(analyzer);
        manager.runVisitors();

        return rule.getDependencies();
    }

    @Test
    void javaTestClassDependencies() throws Exception {
        File file = new File("src/test/resources/samples/java/ClassDependencies.java");
        var deps = analyze(file);

        // Basic assertions to ensure expected relationships are detected
        assertTrue(deps.containsKey("samples.ClassDependencies"));
        assertTrue(deps.get("samples.ClassDependencies").containsKey("Helper"));

        var contexts = deps.get("samples.ClassDependencies").get("Helper");
        assertTrue(contexts.contains(TypeContext.FIELD));
        assertTrue(contexts.contains(TypeContext.INSTANTIATION));

        // Build a human-readable report (also saved to target/test-output/class-deps.txt)
        List<String> report = new ArrayList<>();
        report.add("=== Class Dependency Analysis ===");
        report.add("Source file: " + file.getPath());
        report.add("");

        for (String clazz : deps.keySet().stream().sorted().collect(Collectors.toList())) {
            report.add("Class: " + clazz);
            Map<String, Set<TypeContext>> used = deps.get(clazz);
            if (used.isEmpty()) {
                report.add("  (no dependencies)");
                report.add("");
                continue;
            }

            for (Map.Entry<String, Set<TypeContext>> e : used.entrySet()) {
                String usedClass = e.getKey();
                String ctx = e.getValue().stream().map(Enum::name).sorted().collect(Collectors.joining(", "));
                report.add("  - " + usedClass + " [" + ctx + "]");
            }
            report.add("");
        }

        // Print to stdout so test output contains the description
        report.forEach(System.out::println);

        // Also write to a file for thesis copy-paste convenience.
        try {
            Path outDir = Paths.get("target", "test-output");
            Files.createDirectories(outDir);
            Path outFile = outDir.resolve("class-deps.txt");
            Files.write(outFile, report, StandardCharsets.UTF_8);
            System.out.println("Report written to: " + outFile.toAbsolutePath());
        } catch (IOException ex) {
            System.out.println("Failed to write report file: " + ex.getMessage());
        }
    }
}
