package rules;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.analyzers.GenericAnalyzer;
import com.staticguard.analyzers.VisitorManager;
import com.staticguard.common.Issue;
import com.staticguard.common.RuleContext;
import com.staticguard.parser.LanguageParser;
import com.staticguard.parser.ParserFactory;
import com.staticguard.rules.java.UnusedImportsRule;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UnusedImportsRuleTest {

    private List<Issue> analyze(File file) throws Exception {
        LanguageParser<?> parser = ParserFactory.createParser(file);
        CompilationUnit ast = (CompilationUnit) parser.parse();

        RuleContext context = new RuleContext(file);

        UnusedImportsRule<CompilationUnit> rule =
                new UnusedImportsRule<>();

        GenericAnalyzer<CompilationUnit> analyzer =
                new GenericAnalyzer<>(context, rule);

        VisitorManager<CompilationUnit> manager =
                new VisitorManager<>(ast);

        manager.addVisitor(analyzer);
        manager.runVisitors();

        return context.getIssues();
    }

    private void assertUnusedImports(
            File file,
            String... expectedMessages
    ) throws Exception {

        List<Issue> issues = analyze(file);

        assertEquals(
                expectedMessages.length,
                issues.size(),
                "Unexpected number of unused imports in "
                        + file.getName()
        );

        for (String expectedMessage : expectedMessages) {
            assertTrue(
                    issues.stream()
                            .anyMatch(i ->
                                    i.getMessage().equals(expectedMessage)
                            ),
                    "Missing expected issue: "
                            + expectedMessage
                            + " in "
                            + file.getName()
            );
        }
    }

    @Test
    void testWildcardImports() throws Exception {
        assertUnusedImports(
                new File(
                        "src/test/resources/samples/java/unusedimports/WildcardImports.java"
                ),
                "Unused import: java.io.*"
        );
    }

    @Test
    void testExplicitImports() throws Exception {
        assertUnusedImports(
                new File(
                        "src/test/resources/samples/java/unusedimports/ExplicitImports.java"
                ),
                "Unused import: java.time.LocalDateTime",
                "Unused import: java.nio.file.Files"
        );
    }

    @Test
    void testNestedPackageImports() throws Exception {
        assertUnusedImports(
                new File(
                        "src/test/resources/samples/java/unusedimports/NestedPackageImports.java"
                ),
                "Unused import: java.util.concurrent.atomic.AtomicLong"
        );
    }

    @Test
    void testQualifiedAccessImports() throws Exception {
        assertUnusedImports(
                new File(
                        "src/test/resources/samples/java/unusedimports/QualifiedAccessImports.java"
                ),
                "Unused import: java.util.Arrays"
        );
    }

    @Test
    void testInheritanceImports() throws Exception {
        assertUnusedImports(
                new File(
                        "src/test/resources/samples/java/unusedimports/InheritanceImports.java"
                ),
                "Unused import: java.util.ArrayList"
        );
    }

    @Test
    void testAnnotationImports() throws Exception {
        assertUnusedImports(
                new File(
                        "src/test/resources/samples/java/unusedimports/AnnotationImports.java"
                ),
                "Unused import: java.lang.annotation.Documented"
        );
    }

    @Test
    void testStaticFieldImports() throws Exception {
        assertUnusedImports(
                new File(
                        "src/test/resources/samples/java/unusedimports/StaticFieldImports.java"
                ),
                "Unused import: java.util.concurrent.atomic.AtomicInteger"
        );
    }
}