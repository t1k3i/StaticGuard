package rules;

import com.staticguard.cli.CLIOptionsConfig;
import com.staticguard.common.ProjectContext;
import com.staticguard.common.RuleContext;
import com.staticguard.parser.ParserFactory;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NamingRuleTest {

    private void runNamingRuleAnalysis(File file,  RuleContext context) throws Exception {

        var parser = ParserFactory.createParser(file);

        CLIOptionsConfig config = CLIOptionsConfig.builder()
                .naming(true)
                .build();

        var projectContext = new ProjectContext();

        parser.handle(config, context, projectContext);
    }

    @Test
    void cTestNaming() throws Exception {

        File file = new File("src/test/resources/samples/c/Naming.c");
        var context = new RuleContext(file);

        runNamingRuleAnalysis(file, context);

        var issues = context.getIssues();

        assertEquals(2, issues.size());

        assertTrue(
                issues.stream()
                        .anyMatch(i -> i.getMessage().contains("BADFunctionName"))
        );

        assertTrue(
                issues.stream()
                        .anyMatch(i -> i.getMessage().contains("good_function_name"))
        );
    }

    @Test
    void javaTestNaming() throws Exception {
        File file = new File("src/test/resources/samples/java/badClassName.java");
        var context = new RuleContext(file);

        runNamingRuleAnalysis(file, context);

        var issues = context.getIssues();

        assertEquals(3, issues.size());

        assertTrue(
                issues.stream()
                        .anyMatch(i -> i.getMessage().contains("badClassName"))
        );

        assertTrue(
                issues.stream()
                        .anyMatch(i -> i.getMessage().contains("BADMethodName"))
        );

        assertTrue(
                issues.stream()
                        .anyMatch(i -> i.getMessage().contains("BAD_field"))
        );
    }
}
