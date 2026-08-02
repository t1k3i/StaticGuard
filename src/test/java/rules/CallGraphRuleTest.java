package rules;

import com.staticguard.analyzers.CallGraphAnalyzer;
import com.staticguard.analyzers.VisitorManager;
import com.staticguard.parser.LanguageParser;
import com.staticguard.parser.ParserFactory;
import com.staticguard.rules.CallGraphRule;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CallGraphRuleTest {

    private Map<String, Set<String>> analyze(File file) throws Exception {
        LanguageParser<?> parser = ParserFactory.createParser(file);
        Object ast = parser.parse();

        CallGraphRule<Object> rule = new CallGraphRule<>();
        CallGraphAnalyzer<Object> analyzer = new CallGraphAnalyzer<>(rule);

        VisitorManager<Object> manager = new VisitorManager<>(ast);
        manager.addVisitor(analyzer);
        manager.runVisitors();

        return rule.getCallGraph();
    }

    @Test
    void cTestCallGraph() throws Exception {
        File file = new File("src/test/resources/samples/c/CallGraph.c");
        var graph = analyze(file);

        assertTrue(graph.containsKey("root"));
        assertTrue(graph.get("root").contains("branchA"));
        assertTrue(graph.get("root").contains("branchB"));

        assertTrue(graph.get("branchA").contains("helper"));
        assertTrue(graph.get("helper").contains("leaf"));

        assertTrue(graph.get("branchB").contains("leaf"));

        assertTrue(graph.get("main").contains("root"));

        assertTrue(graph.containsKey("recursion"));
        assertTrue(graph.get("recursion").contains("recursion"));

        assertTrue(graph.containsKey("unused"));
        assertTrue(graph.get("unused").isEmpty());
    }

    @Test
    void javaTestCallGraph() throws Exception {
        File file = new File("src/test/resources/samples/java/CallGraph.java");
        var graph = analyze(file);

        assertEquals(3, graph.size());

        assertTrue(graph.containsKey("methodA"));
        assertTrue(graph.containsKey("methodB"));
        assertTrue(graph.containsKey("methodC"));

        assertTrue(graph.get("methodA").contains("methodB"));
        assertTrue(graph.get("methodB").contains("methodC"));
        assertTrue(graph.get("methodC").contains("println"));
    }
}
