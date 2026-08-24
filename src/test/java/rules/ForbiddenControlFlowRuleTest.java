package rules;

import com.staticguard.analyzers.GenericAnalyzer;
import com.staticguard.analyzers.VisitorManager;
import com.staticguard.common.Issue;
import com.staticguard.common.RuleContext;
import com.staticguard.parser.LanguageParser;
import com.staticguard.parser.ParserFactory;
import com.staticguard.rules.ForbiddenControlFlowRule;
import com.staticguard.enums.ControlFlowRule;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.Set;

import static helpers.RuleTestHelper.assertIssue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ForbiddenControlFlowRuleTest {

    private List<Issue> analyze(File file, Set<ControlFlowRule> forbidden) throws Exception {
        LanguageParser<?> parser = ParserFactory.createParser(file);
        Object ast = parser.parse();

        RuleContext context = new RuleContext(file);
        ForbiddenControlFlowRule<Object> rule = new ForbiddenControlFlowRule<>(forbidden);

        GenericAnalyzer<Object> analyzer = new GenericAnalyzer<>(context, rule);
        VisitorManager<Object> manager = new VisitorManager<>(ast);

        manager.addVisitor(analyzer);
        manager.runVisitors();

        return context.getIssues();
    }

    @Test
    void testDetectForbiddenControlFlowJava_break() throws Exception {
        var issues = analyze(
                new File("src/test/resources/samples/java/ForbiddenControlFlow.java"),
                Set.of(ControlFlowRule.BREAK)
        );

        assertEquals(1, issues.size(), "Should detect forbidden Java break");
        assertIssue(issues, 9, "Forbidden control flow statement: break");
    }

    @Test
    void testDetectForbiddenControlFlowJava_continue() throws Exception {
        var issues = analyze(
                new File("src/test/resources/samples/java/ForbiddenControlFlow.java"),
                Set.of(ControlFlowRule.CONTINUE)
        );

        assertEquals(1, issues.size(), "Should detect forbidden Java continue");
        assertIssue(issues, 16, "Forbidden control flow statement: continue");
    }

    @Test
    void testDetectForbiddenControlFlowJava_return() throws Exception {
        var issues = analyze(
                new File("src/test/resources/samples/java/ForbiddenControlFlow.java"),
                Set.of(ControlFlowRule.RETURN)
        );

        assertEquals(1, issues.size(), "Should detect forbidden Java return");
        assertIssue(issues, 35, "Forbidden control flow statement: return");
    }

    @Test
    void testDetectForbiddenControlFlowJava_instanceof() throws Exception {
        var issues = analyze(
                new File("src/test/resources/samples/java/ForbiddenControlFlow.java"),
                Set.of(ControlFlowRule.INSTANCEOF)
        );

        assertEquals(1, issues.size(), "Should detect forbidden Java instanceof");
        assertIssue(issues, 23, "Forbidden language construct: instanceof");
    }

    @Test
    void testDetectForbiddenControlFlowJava_all() throws Exception {
        var issues = analyze(
                new File("src/test/resources/samples/java/ForbiddenControlFlow.java"),
                Set.of(ControlFlowRule.BREAK, ControlFlowRule.CONTINUE, ControlFlowRule.RETURN, ControlFlowRule.INSTANCEOF)
        );

        assertEquals(4, issues.size(), "Should detect all forbidden Java control flow occurrences");

        assertIssue(issues, 9, "Forbidden control flow statement: break");
        assertIssue(issues, 16, "Forbidden control flow statement: continue");
        assertIssue(issues, 23, "Forbidden language construct: instanceof");
        assertIssue(issues, 35, "Forbidden control flow statement: return");
    }

    @Test
    void testDetectForbiddenControlFlowC_break() throws Exception {
        var issues = analyze(
                new File("src/test/resources/samples/c/ForbiddenControlFlow.c"),
                Set.of(ControlFlowRule.BREAK)
        );

        assertEquals(1, issues.size(), "Should detect forbidden C break");
        assertIssue(issues, 20, "Forbidden control flow statement: break");
    }

    @Test
    void testDetectForbiddenControlFlowC_continue() throws Exception {
        var issues = analyze(
                new File("src/test/resources/samples/c/ForbiddenControlFlow.c"),
                Set.of(ControlFlowRule.CONTINUE)
        );

        assertEquals(1, issues.size(), "Should detect forbidden C continue");
        assertIssue(issues, 28, "Forbidden control flow statement: continue");
    }

    @Test
    void testDetectForbiddenControlFlowC_return() throws Exception {
        var issues = analyze(
                new File("src/test/resources/samples/c/ForbiddenControlFlow.c"),
                Set.of(ControlFlowRule.RETURN)
        );

        assertEquals(2, issues.size(), "Should detect all forbidden C returns");
        assertIssue(issues, 8, "Forbidden control flow statement: return");
        assertIssue(issues, 45, "Forbidden control flow statement: return");
    }

    @Test
    void testDetectForbiddenControlFlowC_goto() throws Exception {
        var issues = analyze(
                new File("src/test/resources/samples/c/ForbiddenControlFlow.c"),
                Set.of(ControlFlowRule.GOTO)
        );

        assertEquals(1, issues.size(), "Should detect forbidden C goto");
        assertIssue(issues, 37, "Forbidden control flow statement: goto");
    }

    @Test
    void testDetectForbiddenControlFlowC_all() throws Exception {
        var issues = analyze(
                new File("src/test/resources/samples/c/ForbiddenControlFlow.c"),
                Set.of(ControlFlowRule.BREAK, ControlFlowRule.CONTINUE, ControlFlowRule.RETURN, ControlFlowRule.GOTO)
        );

        assertEquals(5, issues.size(), "Should detect all forbidden C control flow occurrences");

        assertIssue(issues, 20, "Forbidden control flow statement: break");
        assertIssue(issues, 28, "Forbidden control flow statement: continue");
        assertIssue(issues, 8, "Forbidden control flow statement: return");
        assertIssue(issues, 45, "Forbidden control flow statement: return");
        assertIssue(issues, 37, "Forbidden control flow statement: goto");
    }
}
