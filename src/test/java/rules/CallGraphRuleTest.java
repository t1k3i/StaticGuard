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

import static org.junit.jupiter.api.Assertions.assertTrue;

class CallGraphRuleTest {

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

        // main
        assertTrue(graph.get("main").contains("linear"));
        assertTrue(graph.get("main").contains("branch"));
        assertTrue(graph.get("main").contains("recursive"));
        assertTrue(graph.get("main").contains("mutualA"));
        assertTrue(graph.get("main").contains("work"));

        // linear
        assertTrue(graph.get("linear").contains("leaf"));

        // branching
        assertTrue(graph.get("branch").contains("left"));
        assertTrue(graph.get("branch").contains("right"));

        // empty
        assertTrue(graph.get("left").isEmpty());
        assertTrue(graph.get("right").isEmpty());
        assertTrue(graph.get("leaf").isEmpty());

        // recursion
        assertTrue(graph.get("recursive").contains("recursive"));

        // mutual recursion
        assertTrue(graph.get("mutualA").contains("mutualB"));
        assertTrue(graph.get("mutualB").contains("mutualA"));

        // helper chain
        assertTrue(graph.get("work").contains("finish"));

        // unused
        assertTrue(graph.get("unused").contains("dead"));
        assertTrue(graph.get("dead").isEmpty());
    }

    @Test
    void javaTestCallGraph() throws Exception {
        File file = new File("src/test/resources/samples/java/CallGraph.java");
        var graph = analyze(file);

        // main
        assertTrue(graph.get("main").contains("linear"));
        assertTrue(graph.get("main").contains("branch"));
        assertTrue(graph.get("main").contains("recursive"));
        assertTrue(graph.get("main").contains("mutualA"));
        assertTrue(graph.get("main").contains("work"));
        assertTrue(graph.get("main").contains("anonymousClassExample"));

        // linear
        assertTrue(graph.get("linear").contains("leaf"));

        // branching
        assertTrue(graph.get("branch").contains("left"));
        assertTrue(graph.get("branch").contains("right"));

        // empty
        assertTrue(graph.get("left").isEmpty());
        assertTrue(graph.get("right").isEmpty());
        assertTrue(graph.get("leaf").isEmpty());
        assertTrue(graph.get("finish").isEmpty());

        // recursion
        assertTrue(graph.get("recursive").contains("recursive"));

        // mutual recursion
        assertTrue(graph.get("mutualA").contains("mutualB"));
        assertTrue(graph.get("mutualB").contains("mutualA"));

        // utility class
        assertTrue(graph.get("work").contains("finish"));

        // unused
        assertTrue(graph.get("unused").contains("dead"));
        assertTrue(graph.get("dead").isEmpty());

        // anonymous class
        assertTrue(graph.get("anonymousClassExample").contains("run"));
        assertTrue(graph.get("run").contains("anonymousLeaf"));
        assertTrue(graph.get("anonymousLeaf").isEmpty());
    }
}