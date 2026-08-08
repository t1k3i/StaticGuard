package rules;

import com.staticguard.analyzers.GenericAnalyzer;
import com.staticguard.analyzers.VisitorManager;
import com.staticguard.common.RuleContext;
import com.staticguard.parser.LanguageParser;
import com.staticguard.parser.ParserFactory;
import com.staticguard.rules.LoopNestingRule;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoopNestingRuleTest {
    private int analyze(File file) throws Exception {

        LanguageParser<?> parser = ParserFactory.createParser(file);
        Object ast = parser.parse();

        RuleContext context = new RuleContext(file);

        LoopNestingRule<Object> rule = new LoopNestingRule<>();
        GenericAnalyzer<Object> analyzer =
                new GenericAnalyzer<>(context, rule);

        VisitorManager<Object> manager =
                new VisitorManager<>(ast);

        manager.addVisitor(analyzer);
        manager.runVisitors();

        return rule.getMaxDepth();
    }

    @Test
    void testDetectFourLevelsC() throws Exception {
        int depth = analyze( new File("src/test/resources/samples/c/LoopNesting.c") );
        assertEquals(4, depth);
    }

    @Test
    void testDetectThreeLevelsJava() throws Exception {
        int depth = analyze( new File("src/test/resources/samples/java/LoopNesting.java") );
        assertEquals(3, depth);
    }
}
