package rules;

import com.staticguard.analyzers.GenericAnalyzer;
import com.staticguard.analyzers.VisitorManager;
import com.staticguard.common.Issue;
import com.staticguard.common.RuleContext;
import com.staticguard.parser.LanguageParser;
import com.staticguard.parser.ParserFactory;
import com.staticguard.rules.NamingRule;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static helpers.RuleTestHelper.assertIssue;
import static org.junit.jupiter.api.Assertions.*;

class NamingRuleTest {

    private List<Issue> analyze(File file) throws Exception {
        LanguageParser<?> parser = ParserFactory.createParser(file);
        Object ast = parser.parse();

        RuleContext context = new RuleContext(file);

        NamingRule<Object> rule = new NamingRule<>();

        GenericAnalyzer<Object> analyzer =
                new GenericAnalyzer<>(context, rule);

        VisitorManager<Object> manager =
                new VisitorManager<>(ast, context);

        manager.addVisitor(analyzer);
        manager.runVisitors();

        return context.getIssues();
    }

    @Test
    void testDetectBadNamingJava() throws Exception {
        var issues = analyze(
                new File("src/test/resources/samples/java/NamingTest.java")
        );

        assertEquals(7, issues.size(),
                "Should detect incorrectly named Java constructs");

        assertIssue(issues, 6, "Field name should be camelCase: BAD_field");
        assertIssue(issues, 9, "Constant name should be UPPER_SNAKE_CASE: badConstant");
        assertIssue(issues, 15, "Method name should be camelCase: BADMethod");
        assertIssue(issues, 15, "Parameter name should be camelCase: BadParameter");
        assertIssue(issues, 16, "Variable name should be camelCase: BadVariable");
        assertIssue(issues, 21, "Enum constant should be UPPER_SNAKE_CASE: badColor");
        assertIssue(issues, 25, "Class/Interface name should be PascalCase: badClassName");
    }

    @Test
    void testDetectBadNamingC() throws Exception {
        var issues = analyze(
                new File("src/test/resources/samples/c/Naming.c")
        );

        assertEquals(9, issues.size(),
                "Should detect incorrectly named C constructs");

        assertIssue(issues, 4, "Constant name should be UPPER_SNAKE_CASE: badConstant");
        assertIssue(issues, 10, "Struct/Union name should be PascalCase: badStruct");
        assertIssue(issues, 16, "Enum constant name should be UPPER_SNAKE_CASE: badColor");
        assertIssue(issues, 20, "Typedef name should be PascalCase: badType");
        assertIssue(issues, 27, "Function name should be camelCase: BadFunction");
        assertIssue(issues, 27, "Parameter name should be camelCase: BadParameter");
        assertIssue(issues, 28, "Variable name should be camelCase: BadVariable");
        assertIssue(issues, 32, "Function name should be camelCase: snake_case_function");
        assertIssue(issues, 33, "Variable name should be camelCase: bad_variable");
    }
}