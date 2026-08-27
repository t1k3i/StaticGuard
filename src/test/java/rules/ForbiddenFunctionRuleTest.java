package rules;

import com.staticguard.analyzers.GenericAnalyzer;
import com.staticguard.analyzers.VisitorManager;
import com.staticguard.common.Issue;
import com.staticguard.common.RuleContext;
import com.staticguard.parser.LanguageParser;
import com.staticguard.parser.ParserFactory;
import com.staticguard.rules.ForbiddenFunctionRule;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.Set;

import static helpers.RuleTestHelper.assertIssue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ForbiddenFunctionRuleTest {

    private List<Issue> analyze(File file, Set<String> forbidden) throws Exception {
        LanguageParser<?> parser = ParserFactory.createParser(file);
        Object ast = parser.parse();

        RuleContext context = new RuleContext(file);
        ForbiddenFunctionRule<Object> rule = new ForbiddenFunctionRule<>(forbidden);

        GenericAnalyzer<Object> analyzer = new GenericAnalyzer<>(context, rule);
        VisitorManager<Object> manager = new VisitorManager<>(ast, context);

        manager.addVisitor(analyzer);
        manager.runVisitors();

        return context.getIssues();
    }

    @Test
    void testDetectForbiddenFunctions() throws Exception {
        File file = new File(
                "src/test/resources/samples/c/ForbiddenFunctions.c"
        );

        var issues = analyze(
                file,
                Set.of("printf", "scanf", "malloc", "strcpy")
        );

        assertEquals(10, issues.size(),
                "Should detect all forbidden function calls");

        assertIssue(issues, 6, "Forbidden function call: printf");
        assertIssue(issues, 10, "Forbidden function call: printf");
        assertIssue(issues, 11, "Forbidden function call: scanf");
        assertIssue(issues, 12, "Forbidden function call: malloc");
        assertIssue(issues, 20, "Forbidden function call: printf");
        assertIssue(issues, 21, "Forbidden function call: printf");
        assertIssue(issues, 23, "Forbidden function call: scanf");
        assertIssue(issues, 24, "Forbidden function call: scanf");
        assertIssue(issues, 30, "Forbidden function call: printf");
        assertIssue(issues, 50, "Forbidden function call: strcpy");
    }

    @Test
    void testOnlySelectedFunctionsAreForbidden() throws Exception {
        File file = new File(
                "src/test/resources/samples/c/ForbiddenFunctions.c"
        );

        var issues = analyze(
                file,
                Set.of("strlen")
        );

        assertEquals(3, issues.size(),
                "Should detect only strlen calls");

        assertIssue(issues, 15, "Forbidden function call: strlen");
        assertIssue(issues, 26, "Forbidden function call: strlen");
        assertIssue(issues, 31, "Forbidden function call: strlen");
    }

    @Test
    void testDetectForbiddenMethodBySimpleName() throws Exception {
        File file = new File(
                "src/test/resources/samples/java/ForbiddenMethods.java"
        );

        var issues = analyze(
                file,
                Set.of("println")
        );

        assertEquals(5, issues.size(),
                "Should detect all println calls by simple method name");

        assertIssue(issues, 8, "Forbidden method call: java.io.PrintStream.println");
        assertIssue(issues, 12, "Forbidden method call: java.io.PrintStream.println");
        assertIssue(issues, 23, "Forbidden method call: java.io.PrintStream.println");
        assertIssue(issues, 24, "Forbidden method call: java.io.PrintStream.println");
        assertIssue(issues, 38, "Forbidden method call: java.io.PrintStream.println");
    }

    @Test
    void testDetectForbiddenMethodBySimpleNameMax() throws Exception {
        File file = new File(
                "src/test/resources/samples/java/ForbiddenMethods.java"
        );

        var issues = analyze(
                file,
                Set.of("max")
        );

        assertEquals(2, issues.size(),
                "Should detect both max method calls");

        assertIssue(issues, 29, "Forbidden method call: java.lang.Math.max");
        assertIssue(issues, 30, "Forbidden method call: samples.ForbiddenMethods.max");
    }

    @Test
    void testDetectForbiddenMethodByQualifiedName() throws Exception {
        File file = new File(
                "src/test/resources/samples/java/ForbiddenMethods.java"
        );

        var issues = analyze(
                file,
                Set.of("java.lang.Math.abs")
        );

        assertEquals(3, issues.size(),
                "Should detect Math.abs calls by qualified method name");

        assertIssue(issues, 18, "Forbidden method call: java.lang.Math.abs");
        assertIssue(issues, 19, "Forbidden method call: java.lang.Math.abs");
        assertIssue(issues, 40, "Forbidden method call: java.lang.Math.abs");
    }

    @Test
    void testQualifiedNameDoesNotMatchSameMethodNameOnOtherClass() throws Exception {
        File file = new File(
                "src/test/resources/samples/java/ForbiddenMethods.java"
        );

        var issues = analyze(
                file,
                Set.of("java.lang.Math.max")
        );

        assertEquals(1, issues.size(),
                "Should detect only the specifically qualified forbidden method");

        assertIssue(issues, 29, "Forbidden method call: java.lang.Math.max");
    }

    @Test
    void testDetectMultipleForbiddenMethods() throws Exception {
        File file = new File(
                "src/test/resources/samples/java/ForbiddenMethods.java"
        );

        var issues = analyze(
                file,
                Set.of(
                        "java.lang.Integer.parseInt",
                        "java.lang.System.exit",
                        "java.lang.Math.abs"
                )
        );

        assertEquals(7, issues.size(),
                "Should detect all configured forbidden methods");

        assertIssue(issues, 13, "Forbidden method call: java.lang.Integer.parseInt");
        assertIssue(issues, 14, "Forbidden method call: java.lang.System.exit");
        assertIssue(issues, 18, "Forbidden method call: java.lang.Math.abs");
        assertIssue(issues, 19, "Forbidden method call: java.lang.Math.abs");
        assertIssue(issues, 26, "Forbidden method call: java.lang.Integer.parseInt");
        assertIssue(issues, 27, "Forbidden method call: java.lang.Integer.parseInt");
        assertIssue(issues, 40, "Forbidden method call: java.lang.Math.abs");
    }
}