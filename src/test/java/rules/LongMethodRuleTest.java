package rules;

import com.staticguard.analyzers.GenericAnalyzer;
import com.staticguard.analyzers.VisitorManager;
import com.staticguard.common.RuleContext;
import com.staticguard.parser.LanguageParser;
import com.staticguard.parser.ParserFactory;
import com.staticguard.rules.LongMethodRule;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class LongMethodRuleTest {
    private long analyze(File file, int maxLines) throws Exception {
        LanguageParser<?> parser = ParserFactory.createParser(file);
        Object ast = parser.parse();

        RuleContext context = new RuleContext(file);

        LongMethodRule<Object> rule = new LongMethodRule<>(maxLines);
        GenericAnalyzer<Object> analyzer =
                new GenericAnalyzer<>(context, rule);

        VisitorManager<Object> manager =
                new VisitorManager<>(ast, context);

        manager.addVisitor(analyzer);
        manager.runVisitors();

        return context.getIssues().size();
    }

    @Test
    void testDetectLongMethodsJava() throws Exception {
        long issues = analyze(new File("src/test/resources/samples/java/LongMethod.java"), 10);
        assertEquals(1, issues, "Should detect long methods when max lines is 10");
    }

    @Test
    void testNoLongMethodsJavaWithHighThreshold() throws Exception {
        long issues = analyze(new File("src/test/resources/samples/java/LongMethod.java"), 100);
        assertEquals(0, issues, "Should not flag any methods when threshold is very high");
    }

    @Test
    void testDetectLongFunctionsC() throws Exception {
        long issues = analyze(new File("src/test/resources/samples/c/LongFunction.c"), 15);
        assertEquals(2, issues, "Should detect long functions when max lines is 15");
    }

    @Test
    void testNoLongFunctionsCWithHighThreshold() throws Exception {
        long issues = analyze(new File("src/test/resources/samples/c/LongFunction.c"), 100);
        assertEquals(0, issues, "Should not flag any functions when threshold is very high");
    }
}
