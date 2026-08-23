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
                new VisitorManager<>(ast);

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

        assertTrue(issues.stream().anyMatch(i ->
                i.getLine() == 6 &&
                        i.getMessage().equals("Field name should be camelCase: BAD_field")));

        assertTrue(issues.stream().anyMatch(i ->
                i.getLine() == 9 &&
                        i.getMessage().equals("Constant name should be UPPER_SNAKE_CASE: badConstant")));

        assertTrue(issues.stream().anyMatch(i ->
                i.getLine() == 15 &&
                        i.getMessage().equals("Method name should be camelCase: BADMethod")));

        assertTrue(issues.stream().anyMatch(i ->
                i.getLine() == 15 &&
                        i.getMessage().equals("Parameter name should be camelCase: BadParameter")));

        assertTrue(issues.stream().anyMatch(i ->
                i.getLine() == 16 &&
                        i.getMessage().equals("Variable name should be camelCase: BadVariable")));

        assertTrue(issues.stream().anyMatch(i ->
                i.getLine() == 21 &&
                        i.getMessage().equals("Enum constant should be UPPER_SNAKE_CASE: badColor")));

        assertTrue(issues.stream().anyMatch(i ->
                i.getLine() == 25 &&
                        i.getMessage().equals("Class/Interface name should be PascalCase: badClassName")));
    }

    @Test
    void testDetectBadNamingC() throws Exception {
        var issues = analyze(
                new File("src/test/resources/samples/c/Naming.c")
        );

        assertEquals(9, issues.size(),
                "Should detect incorrectly named C constructs");

        assertTrue(issues.stream().anyMatch(i ->
                i.getLine() == 4 &&
                        i.getMessage().equals("Constant name should be UPPER_SNAKE_CASE: badConstant")));

        assertTrue(issues.stream().anyMatch(i ->
                i.getLine() == 10 &&
                        i.getMessage().equals("Struct/Union name should be PascalCase: badStruct")));

        assertTrue(issues.stream().anyMatch(i ->
                i.getLine() == 16 &&
                        i.getMessage().equals("Enum constant name should be UPPER_SNAKE_CASE: badColor")));

        assertTrue(issues.stream().anyMatch(i ->
                i.getLine() == 20 &&
                        i.getMessage().equals("Typedef name should be PascalCase: badType")));

        assertTrue(issues.stream().anyMatch(i ->
                i.getLine() == 27 &&
                        i.getMessage().equals("Function name should be camelCase: BadFunction")));

        assertTrue(issues.stream().anyMatch(i ->
                i.getLine() == 27 &&
                        i.getMessage().equals("Parameter name should be camelCase: BadParameter")));

        assertTrue(issues.stream().anyMatch(i ->
                i.getLine() == 28 &&
                        i.getMessage().equals("Variable name should be camelCase: BadVariable")));

        assertTrue(issues.stream().anyMatch(i ->
                i.getLine() == 32 &&
                        i.getMessage().equals("Function name should be camelCase: snake_case_function")));

        assertTrue(issues.stream().anyMatch(i ->
                i.getLine() == 33 &&
                        i.getMessage().equals("Variable name should be camelCase: bad_variable")));
    }
}