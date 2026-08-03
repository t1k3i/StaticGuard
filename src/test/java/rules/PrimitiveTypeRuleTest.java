package rules;

import com.staticguard.cli.CLIOptionsConfig;
import com.staticguard.common.ProjectContext;
import com.staticguard.common.RuleContext;
import com.staticguard.parser.ParserFactory;
import com.staticguard.visitors.java.PrimitiveTypeVisitor;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PrimitiveTypeRuleTest {

    private RuleContext runAnalysis(File file, CLIOptionsConfig config) throws Exception {
        var parser = ParserFactory.createParser(file);
        var context = new RuleContext(file);
        var projectContext = new ProjectContext();
        parser.handle(config, context, projectContext);
        return context;
    }

    @Test
    void testOnlyPrimitiveModeWithoutExceptions() throws Exception {
        File file = new File("src/test/resources/samples/java/PrimitiveSample.java");

        CLIOptionsConfig config = CLIOptionsConfig.builder()
                .primitiveMode(PrimitiveTypeVisitor.Mode.ONLY_PRIMITIVE)
                .build();

        RuleContext context = runAnalysis(file, config);
        var issues = context.getIssues();

        assertFalse(issues.isEmpty(), "Should report violations when no exceptions are configured");

        assertTrue(issues.stream().anyMatch(i -> i.getMessage().contains("String[]")),
                "Should report String[] parameter");
        assertTrue(issues.stream().anyMatch(i -> i.getMessage().contains("Scanner")),
                "Should report Scanner variable/instantiation");
        assertTrue(issues.stream().anyMatch(i -> i.getMessage().contains("String")),
                "Should report String variable");
        assertTrue(issues.stream().anyMatch(i -> i.getMessage().contains("Boolean")),
                "Should report Boolean variable");
    }

    @Test
    void testOnlyPrimitiveModeWithExceptions() throws Exception {
        File file = new File("src/test/resources/samples/java/PrimitiveSample.java");

        CLIOptionsConfig config = CLIOptionsConfig.builder()
                .primitiveMode(PrimitiveTypeVisitor.Mode.ONLY_PRIMITIVE)
                .primitiveExceptions(Set.of("String[]", "Scanner"))
                .build();

        RuleContext context = runAnalysis(file, config);
        var issues = context.getIssues();

        assertFalse(issues.stream().anyMatch(i -> i.getMessage().contains("found: String[]")),
                "String[] should be allowed as exception");
        assertFalse(issues.stream().anyMatch(i -> i.getMessage().contains("found: Scanner")),
                "Scanner should be allowed as exception");

        assertTrue(issues.stream().anyMatch(i -> i.getMessage().contains("found: String")),
                "String should still be flagged as violation");
        assertTrue(issues.stream().anyMatch(i -> i.getMessage().contains("Boolean")),
                "Boolean should still be flagged as violation");
    }

    @Test
    void testNoPrimitiveModeWithExceptions() throws Exception {
        File file = new File("src/test/resources/samples/java/PrimitiveSample.java");

        CLIOptionsConfig config = CLIOptionsConfig.builder()
                .primitiveMode(PrimitiveTypeVisitor.Mode.NO_PRIMITIVE)
                .primitiveExceptions(Set.of("int"))
                .build();

        RuleContext context = runAnalysis(file, config);
        var issues = context.getIssues();

        assertFalse(issues.stream().anyMatch(i -> i.getMessage().contains("found: int")),
                "int should be allowed in NO_PRIMITIVE mode when in exceptions");

        assertTrue(issues.stream().anyMatch(i -> i.getMessage().contains("found: double")),
                "double should be flagged as primitive violation");
        assertTrue(issues.stream().anyMatch(i -> i.getMessage().contains("found: boolean")),
                "boolean should be flagged as primitive violation");
    }
}
