package rules;

import com.staticguard.analyzers.GenericAnalyzer;
import com.staticguard.analyzers.VisitorManager;
import com.staticguard.common.Issue;
import com.staticguard.common.RuleContext;
import com.staticguard.parser.LanguageParser;
import com.staticguard.parser.ParserFactory;
import com.staticguard.rules.DeniedCallsRule;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static helpers.RuleTestHelper.assertIssue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DeniedCallsRuleTest {
    private List<Issue> analyze(
            File file,
            Map<String, Set<String>> forbidden
    ) throws Exception {
        LanguageParser<?> parser = ParserFactory.createParser(file);
        Object ast = parser.parse();

        RuleContext context = new RuleContext(file);
        DeniedCallsRule<Object> rule = new DeniedCallsRule<>(forbidden);

        GenericAnalyzer<Object> analyzer = new GenericAnalyzer<>(context, rule);
        VisitorManager<Object> manager = new VisitorManager<>(ast, context);

        manager.addVisitor(analyzer);
        manager.runVisitors();

        return context.getIssues();
    }

    @Test
    void testDeniedCalls() throws Exception {
        File file = new File(
                "src/test/resources/samples/c/DeniedCalls.c"
        );

        var issues = analyze(
                file,
                Map.of(
                        "test", Set.of("printf", "strlen"),
                        "other", Set.of("malloc"),
                        "recursive", Set.of("recursive")
                )
        );

        assertEquals(4, issues.size(), "Should detect all denied function calls");

        assertIssue(issues, 10, "Function 'test' is not allowed to call 'printf'");
        assertIssue(issues, 11, "Function 'test' is not allowed to call 'strlen'");
        assertIssue(issues, 17, "Function 'other' is not allowed to call 'malloc'");
        assertIssue(issues, 21, "Function 'recursive' is not allowed to call 'recursive'");
    }

    @Test
    void testDeniedSimpleMethodNames() throws Exception {
        File file = new File(
                "src/test/resources/samples/java/DeniedCalls.java"
        );

        var issues = analyze(
                file,
                Map.of(
                        "test", Set.of("helper"),
                        "indirect", Set.of("helper")
                )
        );

        assertEquals(2, issues.size(), "Should detect denied simple method calls");
        assertIssue(issues, 19, "Method 'test' is not allowed to call 'helper'");
        assertIssue(issues, 33, "Method 'indirect' is not allowed to call 'helper'");
    }

    @Test
    void testDeniedQualifiedMethodNames() throws Exception {
        File file = new File(
                "src/test/resources/samples/java/DeniedCalls.java"
        );

        var issues = analyze(
                file,
                Map.of(
                        "test", Set.of("java.lang.Math.abs"),
                        "other", Set.of("java.lang.Math.max"),
                        "main", Set.of("java.lang.Math.min")
                )
        );

        assertEquals(4, issues.size(), "Should detect denied qualified method calls");
        assertIssue(issues, 17, "Method 'test' is not allowed to call 'java.lang.Math.abs'");
        assertIssue(issues, 18, "Method 'test' is not allowed to call 'java.lang.Math.abs'");
        assertIssue(issues, 24, "Method 'other' is not allowed to call 'java.lang.Math.max'");
        assertIssue(issues, 38, "Method 'main' is not allowed to call 'java.lang.Math.min'");
    }

    @Test
    void testDeniedSimpleNameAlsoMatchesStaticImport() throws Exception {
        File file = new File(
                "src/test/resources/samples/java/DeniedCalls.java"
        );

        var issues = analyze(
                file,
                Map.of(
                        "test", Set.of("abs")
                )
        );

        assertEquals(2, issues.size(), "Should detect both Math.abs and statically imported abs");
        assertIssue(issues, 17, "Method 'test' is not allowed to call 'abs'");
        assertIssue(issues, 18, "Method 'test' is not allowed to call 'abs'");
    }

    @Test
    void testDirectRecursion() throws Exception {
        File file = new File(
                "src/test/resources/samples/java/DeniedCalls.java"
        );

        var issues = analyze(
                file,
                Map.of(
                        "recursive", Set.of("recursive")
                )
        );

        assertEquals(1, issues.size(), "Should detect direct recursion");
        assertIssue(issues, 29, "Method 'recursive' is not allowed to call 'recursive'");
    }
}
