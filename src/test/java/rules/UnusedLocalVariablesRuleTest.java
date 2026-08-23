package rules;

import com.staticguard.analyzers.GenericAnalyzer;
import com.staticguard.analyzers.VisitorManager;
import com.staticguard.common.Issue;
import com.staticguard.common.RuleContext;
import com.staticguard.parser.LanguageParser;
import com.staticguard.parser.ParserFactory;
import com.staticguard.rules.UnusedLocalVariablesRule;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UnusedLocalVariablesRuleTest {

    private List<Issue> analyze(File file) throws Exception {
        LanguageParser<?> parser = ParserFactory.createParser(file);
        Object ast = parser.parse();

        RuleContext context = new RuleContext(file);
        UnusedLocalVariablesRule<Object> rule = new UnusedLocalVariablesRule<>();

        GenericAnalyzer<Object> analyzer = new GenericAnalyzer<>(context, rule);
        VisitorManager<Object> manager = new VisitorManager<>(ast);

        manager.addVisitor(analyzer);
        manager.runVisitors();

        return context.getIssues();
    }

    private static void assertIssue(List<Issue> issues, int line, String message) {
        assertTrue(
                issues.stream().anyMatch(i -> i.getLine() == line && i.getMessage().equals(message)),
                "Expected issue at line " + line + " with message '" + message + "' but it was missing."
        );
    }

    @Test
    void testDetectUnusedLocalVariablesJava() throws Exception {
        var issues = analyze(
                new File("src/test/resources/samples/java/UnusedLocalsTest.java")
        );

        assertEquals(16, issues.size(), "Should detect all unused Java local variables");

        assertIssue(issues, 6, "Unused local variable: unused");
        assertIssue(issues, 10, "Unused local variable: unusedString");
        assertIssue(issues, 22, "Unused local variable: unused");
        assertIssue(issues, 25, "Unused local variable: innerUnused");
        assertIssue(issues, 35, "Unused local variable: unused");
        assertIssue(issues, 40, "Unused local variable: unusedArray");
        assertIssue(issues, 61, "Unused local variable: unused");
        assertIssue(issues, 71, "Unused local variable: unused");
        assertIssue(issues, 74, "Unused local variable: unused");
        assertIssue(issues, 82, "Unused local variable: unused");
        assertIssue(issues, 90, "Unused local variable: unused");
        assertIssue(issues, 95, "Unused local variable: ignored");
        assertIssue(issues, 96, "Unused local variable: text");
        assertIssue(issues, 102, "Unused local variable: exception");
        assertIssue(issues, 109, "Unused local variable: e");
        assertIssue(issues, 114, "Unused local variable: args");
    }

    @Test
    void testDetectUnusedLocalVariablesC() throws Exception {
        var issues = analyze(
                new File("src/test/resources/samples/c/UnusedLocalVariables.c")
        );

        assertEquals(12, issues.size(), "Should detect all unused C local variables");

        assertIssue(issues, 5, "Unused local variable: unused");
        assertIssue(issues, 19, "Unused local variable: unused");
        assertIssue(issues, 22, "Unused local variable: inner_unused");
        assertIssue(issues, 30, "Unused local variable: value");
        assertIssue(issues, 45, "Unused local variable: unused");
        assertIssue(issues, 54, "Unused local variable: unused");
        assertIssue(issues, 69, "Unused local variable: unused");
        assertIssue(issues, 83, "Unused local variable: unused");
        assertIssue(issues, 86, "Unused local variable: unused");
        assertIssue(issues, 110, "Unused local variable: unused_struct");
        assertIssue(issues, 122, "Unused local variable: unused_union");
        assertIssue(issues, 130, "Unused local variable: p2");
    }
}
