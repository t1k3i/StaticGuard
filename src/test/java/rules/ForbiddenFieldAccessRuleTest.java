package rules;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.analyzers.GenericAnalyzer;
import com.staticguard.analyzers.VisitorManager;
import com.staticguard.common.Issue;
import com.staticguard.common.RuleContext;
import com.staticguard.parser.LanguageParser;
import com.staticguard.parser.ParserFactory;
import com.staticguard.rules.java.ForbiddenFieldAccessRule;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ForbiddenFieldAccessRuleTest {

    private List<Issue> analyze(File file) throws Exception {
        LanguageParser<?> parser = ParserFactory.createParser(file);
        CompilationUnit ast = (CompilationUnit) parser.parse();

        RuleContext context = new RuleContext(file);

        ForbiddenFieldAccessRule<CompilationUnit> rule = new ForbiddenFieldAccessRule<>();

        GenericAnalyzer<CompilationUnit> analyzer =
                new GenericAnalyzer<>(context, rule);

        VisitorManager<CompilationUnit> manager =
                new VisitorManager<>(ast, context);

        manager.addVisitor(analyzer);
        manager.runVisitors();

        return context.getIssues();
    }

    @Test
    void testDetectForbiddenFieldAccessJava() throws Exception {
        var issues = analyze(
                new File("src/test/resources/samples/java/ForbiddenFieldAccess.java")
        );

        assertEquals(6, issues.size(),
                "Should detect direct field access across object boundaries");

        assertTrue(issues.stream().anyMatch(i ->
                i.getLine() == 26 &&
                        i.getMessage().equals("Forbidden direct field access: user.name")));

        assertTrue(issues.stream().anyMatch(i ->
                i.getLine() == 27 &&
                        i.getMessage().equals("Forbidden direct field access: user.age")));

        assertTrue(issues.stream().anyMatch(i ->
                i.getLine() == 50 &&
                        i.getMessage().equals("Forbidden direct field access: person.address")));

        assertTrue(issues.stream().anyMatch(i ->
                i.getLine() == 52 &&
                        i.getMessage().equals("Forbidden direct field access: person.address.city")));

        assertTrue(issues.stream().anyMatch(i ->
                i.getLine() == 52 &&
                        i.getMessage().equals("Forbidden direct field access: person.address")));

        assertTrue(issues.stream().anyMatch(i ->
                i.getLine() == 67 &&
                        i.getMessage().equals("Forbidden direct field access: Config.VALUE")));
    }
}
