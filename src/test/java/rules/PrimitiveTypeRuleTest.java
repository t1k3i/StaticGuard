package rules;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.analyzers.GenericAnalyzer;
import com.staticguard.analyzers.VisitorManager;
import com.staticguard.common.Issue;
import com.staticguard.common.RuleContext;
import com.staticguard.parser.LanguageParser;
import com.staticguard.parser.ParserFactory;
import com.staticguard.rules.java.PrimitiveTypeRule;
import com.staticguard.visitors.java.PrimitiveTypeVisitor;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.Set;

import static helpers.RuleTestHelper.assertIssue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PrimitiveTypeRuleTest {

    private List<Issue> analyze(
            File file,
            PrimitiveTypeVisitor.Mode mode,
            Set<String> exceptions
    ) throws Exception {
        LanguageParser<?> parser = ParserFactory.createParser(file);
        CompilationUnit ast = (CompilationUnit) parser.parse();

        RuleContext context = new RuleContext(file);

        PrimitiveTypeRule<CompilationUnit> rule =
                (exceptions == null || exceptions.isEmpty())
                        ? new PrimitiveTypeRule<>(mode)
                        : new PrimitiveTypeRule<>(mode, exceptions);

        GenericAnalyzer<CompilationUnit> analyzer =
                new GenericAnalyzer<>(context, rule);

        VisitorManager<CompilationUnit> manager =
                new VisitorManager<>(ast);

        manager.addVisitor(analyzer);
        manager.runVisitors();

        return context.getIssues();
    }

    @Test
    void testOnlyPrimitiveModeWithoutExceptions() throws Exception {
        File file = new File(
                "src/test/resources/samples/java/PrimitiveSample.java"
        );

        var issues = analyze(
                file,
                PrimitiveTypeVisitor.Mode.ONLY_PRIMITIVE,
                null
        );

        assertEquals(
                20,
                issues.size(),
                "Should detect all non-primitive type usages in ONLY_PRIMITIVE mode"
        );

        assertIssue(issues, 13, "Only primitive types are allowed, found: Integer");
        assertIssue(issues, 14, "Only primitive types are allowed, found: Double");
        assertIssue(issues, 15, "Only primitive types are allowed, found: String");
        assertIssue(issues, 16, "Only primitive types are allowed, found: Scanner");
        assertIssue(issues, 16, "Only primitive types are allowed, found: Scanner");
        assertIssue(issues, 17, "Only primitive types are allowed, found: int[]");
        assertIssue(issues, 25, "Only primitive types are allowed, found: Integer");
        assertIssue(issues, 29, "Only primitive types are allowed, found: String");
        assertIssue(issues, 43, "Only primitive types are allowed, found: Integer");
        assertIssue(issues, 47, "Only primitive types are allowed, found: String");
        assertIssue(issues, 60, "Only primitive types are allowed, found: Integer");
        assertIssue(issues, 61, "Only primitive types are allowed, found: Double");
        assertIssue(issues, 62, "Only primitive types are allowed, found: String");
        assertIssue(issues, 63, "Only primitive types are allowed, found: Scanner");
        assertIssue(issues, 63, "Only primitive types are allowed, found: Scanner");
        assertIssue(issues, 79, "Only primitive types are allowed, found: Integer");
        assertIssue(issues, 81, "Only primitive types are allowed, found: String");
        assertIssue(issues, 82, "Only primitive types are allowed, found: Scanner");
        assertIssue(issues, 82, "Only primitive types are allowed, found: Scanner");
        assertIssue(issues, 76, "Only primitive types are allowed, found: String[]");
    }

    @Test
    void testOnlyPrimitiveModeWithExceptions() throws Exception {
        File file = new File(
                "src/test/resources/samples/java/PrimitiveSample.java"
        );

        var issues = analyze(
                file,
                PrimitiveTypeVisitor.Mode.ONLY_PRIMITIVE,
                Set.of("String[]", "Scanner")
        );

        assertEquals(13, issues.size(),
                "Should detect all non-primitive type usages in ONLY_PRIMITIVE mode");

        assertIssue(issues, 13, "Only primitive types are allowed, found: Integer");
        assertIssue(issues, 14, "Only primitive types are allowed, found: Double");
        assertIssue(issues, 15, "Only primitive types are allowed, found: String");
        assertIssue(issues, 17, "Only primitive types are allowed, found: int[]");
        assertIssue(issues, 25, "Only primitive types are allowed, found: Integer");
        assertIssue(issues, 29, "Only primitive types are allowed, found: String");
        assertIssue(issues, 43, "Only primitive types are allowed, found: Integer");
        assertIssue(issues, 47, "Only primitive types are allowed, found: String");
        assertIssue(issues, 60, "Only primitive types are allowed, found: Integer");
        assertIssue(issues, 61, "Only primitive types are allowed, found: Double");
        assertIssue(issues, 62, "Only primitive types are allowed, found: String");
        assertIssue(issues, 79, "Only primitive types are allowed, found: Integer");
        assertIssue(issues, 81, "Only primitive types are allowed, found: String");
    }

    @Test
    void testNoPrimitiveModeWithoutExceptions() throws Exception {
        File file = new File(
                "src/test/resources/samples/java/PrimitiveSample.java"
        );

        var issues = analyze(
                file,
                PrimitiveTypeVisitor.Mode.NO_PRIMITIVE,
                null
        );

        assertEquals(11, issues.size(),
                "Should detect all primitive type usages in NO_PRIMITIVE mode");

        assertIssue(issues, 8, "Primitive types are not allowed, found: int");
        assertIssue(issues, 9, "Primitive types are not allowed, found: double");
        assertIssue(issues, 10, "Primitive types are not allowed, found: boolean");
        assertIssue(issues, 20, "Primitive types are not allowed, found: int");
        assertIssue(issues, 34, "Primitive types are not allowed, found: int");
        assertIssue(issues, 38, "Primitive types are not allowed, found: double");
        assertIssue(issues, 54, "Primitive types are not allowed, found: int");
        assertIssue(issues, 55, "Primitive types are not allowed, found: double");
        assertIssue(issues, 56, "Primitive types are not allowed, found: boolean");
        assertIssue(issues, 57, "Primitive types are not allowed, found: char");
        assertIssue(issues, 78, "Primitive types are not allowed, found: int");
    }

    @Test
    void testNoPrimitiveModeWithExceptions() throws Exception {
        File file = new File(
                "src/test/resources/samples/java/PrimitiveSample.java"
        );

        var issues = analyze(
                file,
                PrimitiveTypeVisitor.Mode.NO_PRIMITIVE,
                Set.of("int")
        );

        assertEquals(6, issues.size(),
                "Should detect all non-exempt primitive type usages in NO_PRIMITIVE mode");

        assertIssue(issues, 9, "Primitive types are not allowed, found: double");
        assertIssue(issues, 10, "Primitive types are not allowed, found: boolean");
        assertIssue(issues, 38, "Primitive types are not allowed, found: double");
        assertIssue(issues, 55, "Primitive types are not allowed, found: double");
        assertIssue(issues, 56, "Primitive types are not allowed, found: boolean");
        assertIssue(issues, 57, "Primitive types are not allowed, found: char");
    }
}