package rules;

import com.staticguard.analyzers.GenericAnalyzer;
import com.staticguard.analyzers.VisitorManager;
import com.staticguard.common.Issue;
import com.staticguard.common.RuleContext;
import com.staticguard.parser.LanguageParser;
import com.staticguard.parser.ParserFactory;
import com.staticguard.rules.ForbiddenTypesRule;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.Set;

import static helpers.RuleTestHelper.assertIssue;
import static org.junit.jupiter.api.Assertions.*;

class ForbiddenTypesRuleTest {

    private List<Issue> analyze(File file, Set<String> forbiddenTypes) throws Exception {
        LanguageParser<?> parser = ParserFactory.createParser(file);
        Object ast = parser.parse();

        RuleContext context = new RuleContext(file);

        ForbiddenTypesRule<Object> rule =
                new ForbiddenTypesRule<>(forbiddenTypes, null);

        GenericAnalyzer<Object> analyzer = new GenericAnalyzer<>(context, rule);
        VisitorManager<Object> manager = new VisitorManager<>(ast, context);

        manager.addVisitor(analyzer);
        manager.runVisitors();

        return context.getIssues();
    }

    @Test
    void testForbiddenTypesInAllContexts() throws Exception {
        File file = new File(
                "src/test/resources/samples/c/ForbiddenTypes.c"
        );

        var issues = analyze(
                file,
                Set.of(
                        "Node",
                        "struct ForbiddenStruct",
                        "signed long",
                        "int",
                        "char",
                        "bool",
                        "long long"
                )
        );

        assertEquals(21, issues.size(), "Should detect all forbidden type usages");

        assertIssue(issues, 4, "Forbidden type usage: int in context FIELD");
        assertIssue(issues, 8, "Forbidden type usage: int in context FIELD");
        assertIssue(issues, 17, "Forbidden type usage: int in context FIELD");
        assertIssue(issues, 23, "Forbidden type usage: int in context GLOBAL_VARIABLE");
        assertIssue(issues, 25, "Forbidden type usage: signed long in context GLOBAL_VARIABLE");
        assertIssue(issues, 26, "Forbidden type usage: long long in context GLOBAL_VARIABLE");
        assertIssue(issues, 28, "Forbidden type usage: Node in context GLOBAL_VARIABLE");
        assertIssue(issues, 29, "Forbidden type usage: struct ForbiddenStruct in context GLOBAL_VARIABLE");
        assertIssue(issues, 35, "Forbidden type usage: Node in context RETURN_TYPE");
        assertIssue(issues, 36, "Forbidden type usage: Node in context PARAMETER");
        assertIssue(issues, 41, "Forbidden type usage: Node in context LOCAL_VARIABLE");
        assertIssue(issues, 42, "Forbidden type usage: struct ForbiddenStruct in context LOCAL_VARIABLE");
        assertIssue(issues, 46, "Forbidden type usage: int in context LOCAL_VARIABLE");
        assertIssue(issues, 50, "Forbidden type usage: Node in context LOCAL_VARIABLE");
        assertIssue(issues, 51, "Forbidden type usage: struct ForbiddenStruct in context LOCAL_VARIABLE");
        assertIssue(issues, 57, "Forbidden type usage: Node in context PARAMETER");
        assertIssue(issues, 58, "Forbidden type usage: struct ForbiddenStruct in context PARAMETER");
        assertIssue(issues, 61, "Forbidden type usage: Node in context LOCAL_VARIABLE");
        assertIssue(issues, 62, "Forbidden type usage: struct ForbiddenStruct in context LOCAL_VARIABLE");
        assertIssue(issues, 68, "Forbidden type usage: int in context LOCAL_VARIABLE");
        assertIssue(issues, 68, "Forbidden type usage: int in context CAST");
    }

    @Test
    void detectsAllForbiddenTypes() throws Exception {
        File file = new File("src/test/resources/samples/java/ForbiddenTypes.java");

        var issues = analyze(
                file,
                Set.of("ArrayList", "LinkedList", "List", "IOException", "int", "boolean", "String")
        );

        assertEquals(38, issues.size(), "Should detect all forbidden type usages");

        assertIssue(issues, 9, "Forbidden type usage: ArrayList in context EXTENDS");
        assertIssue(issues, 9, "Forbidden type usage: List in context IMPLEMENTS");
        assertIssue(issues, 9, "Forbidden type usage: String in context GENERIC_ARGUMENT");
        assertIssue(issues, 10, "Forbidden type usage: String in context GENERIC_ARGUMENT");
        assertIssue(issues, 12, "Forbidden type usage: ArrayList in context FIELD");
        assertIssue(issues, 12, "Forbidden type usage: String in context GENERIC_ARGUMENT");
        assertIssue(issues, 13, "Forbidden type usage: int in context FIELD");
        assertIssue(issues, 14, "Forbidden type usage: boolean in context FIELD");
        assertIssue(issues, 17, "Forbidden type usage: ArrayList in context RETURN_TYPE");
        assertIssue(issues, 17, "Forbidden type usage: IOException in context THROWS");
        assertIssue(issues, 22, "Forbidden type usage: ArrayList in context LOCAL_VARIABLE");
        assertIssue(issues, 22, "Forbidden type usage: ArrayList in context INSTANTIATION");
        assertIssue(issues, 22, "Forbidden type usage: String in context GENERIC_ARGUMENT");
        assertIssue(issues, 23, "Forbidden type usage: List in context LOCAL_VARIABLE");
        assertIssue(issues, 23, "Forbidden type usage: ArrayList in context INSTANTIATION");
        assertIssue(issues, 23, "Forbidden type usage: ArrayList in context GENERIC_ARGUMENT");
        assertIssue(issues, 23, "Forbidden type usage: String in context GENERIC_ARGUMENT");
        assertIssue(issues, 25, "Forbidden type usage: int in context LOCAL_VARIABLE");
        assertIssue(issues, 26, "Forbidden type usage: boolean in context LOCAL_VARIABLE");
        assertIssue(issues, 32, "Forbidden type usage: IOException in context CATCH");
        assertIssue(issues, 32, "Forbidden type usage: IOException in context PARAMETER");
        assertIssue(issues, 31, "Forbidden type usage: IOException in context INSTANTIATION");
        assertIssue(issues, 17, "Forbidden type usage: String in context GENERIC_ARGUMENT");
        assertIssue(issues, 18, "Forbidden type usage: ArrayList in context PARAMETER");
        assertIssue(issues, 18, "Forbidden type usage: String in context GENERIC_ARGUMENT");
        assertIssue(issues, 19, "Forbidden type usage: int in context PARAMETER");
        assertIssue(issues, 20, "Forbidden type usage: boolean in context PARAMETER");
        assertIssue(issues, 40, "Forbidden type usage: LinkedList in context LOCAL_VARIABLE");
        assertIssue(issues, 40, "Forbidden type usage: LinkedList in context INSTANTIATION");
        assertIssue(issues, 40, "Forbidden type usage: String in context GENERIC_ARGUMENT");
        assertIssue(issues, 41, "Forbidden type usage: ArrayList in context LOCAL_VARIABLE");
        assertIssue(issues, 41, "Forbidden type usage: ArrayList in context INSTANTIATION");
        assertIssue(issues, 45, "Forbidden type usage: ArrayList in context ARRAY_COMPONENT");
        assertIssue(issues, 45, "Forbidden type usage: String in context GENERIC_ARGUMENT");
        assertIssue(issues, 46, "Forbidden type usage: int in context ARRAY_COMPONENT");
        assertIssue(issues, 44, "Forbidden type usage: ArrayList in context ARRAY_COMPONENT");
        assertIssue(issues, 44, "Forbidden type usage: String in context GENERIC_ARGUMENT");
        assertIssue(issues, 50, "Forbidden type usage: String in context GENERIC_ARGUMENT");
    }

    @Test
    void detectsNestedHashMapBySimpleName() throws Exception {
        File file = new File("src/test/resources/samples/java/ForbiddenTypes.java");

        var issues = analyze(file, Set.of("HashMap"));

        assertEquals(4, issues.size(), "Should detect all HashMap usages");
        assertIssue(issues, 50, "Forbidden type usage: HashMap in context LOCAL_VARIABLE");
        assertIssue(issues, 50, "Forbidden type usage: HashMap in context INSTANTIATION");
        assertIssue(issues, 51, "Forbidden type usage: HashMap in context LOCAL_VARIABLE");
        assertIssue(issues, 51, "Forbidden type usage: HashMap in context INSTANTIATION");
    }

    @Test
    void detectsFullyQualifiedHashMap() throws Exception {
        File file = new File("src/test/resources/samples/java/ForbiddenTypes.java");

        var issues = analyze(file, Set.of("java.util.HashMap"));

        assertEquals(2, issues.size(), "Should detect both fully qualified HashMap usages");
        assertIssue(issues, 50, "Forbidden type usage: java.util.HashMap in context LOCAL_VARIABLE");
        assertIssue(issues, 50, "Forbidden type usage: java.util.HashMap in context INSTANTIATION");
    }

    @Test
    void detectsForbiddenAccessToStaticMembers() throws Exception {
        File file = new File("src/test/resources/samples/java/ForbiddenTypes.java");

        var issues = analyze(file, Set.of("Helper"));

        assertEquals(2, issues.size(), "Should detect both Helper usages");
        assertIssue(issues, 58, "Forbidden type usage: Helper in context STATIC_FIELD_ACCESS");
        assertIssue(issues, 59, "Forbidden type usage: Helper in context STATIC_METHOD_CALL");
    }

}