package rules;

import com.github.javaparser.ast.CompilationUnit;
import com.staticguard.analyzers.VisitorManager;
import com.staticguard.analyzers.java.ClassDependencyAnalyzer;
import com.staticguard.analyzers.java.ProjectClassCollectorAnalyzer;
import com.staticguard.common.ProjectContext;
import com.staticguard.common.RuleContext;
import com.staticguard.parser.LanguageParser;
import com.staticguard.parser.ParserFactory;
import com.staticguard.enums.TypeContext;
import com.staticguard.rules.java.ClassDependencyRule;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ClassDependencyRuleTest {

    private Map<String, Map<String, Set<TypeContext>>> analyze(File file)
            throws Exception {

        LanguageParser<?> parser = ParserFactory.createParser(file);
        CompilationUnit ast = (CompilationUnit) parser.parse();

        ProjectContext projectContext = new ProjectContext();

        new ProjectClassCollectorAnalyzer(projectContext)
                .runVisitor(ast);

        RuleContext context = new RuleContext(file);

        ClassDependencyRule<CompilationUnit> rule =
                new ClassDependencyRule<>(projectContext.projectClasses);

        ClassDependencyAnalyzer<CompilationUnit> analyzer =
                new ClassDependencyAnalyzer<>(context, rule);

        VisitorManager<CompilationUnit> manager =
                new VisitorManager<>(ast);

        manager.addVisitor(analyzer);
        manager.runVisitors();

        return rule.getDependencies();
    }

    @Test
    void javaClassDependencies() throws Exception {
        File file =
                new File("src/test/resources/samples/java/ClassDependencies.java");

        var graph = analyze(file);

        var dependencies =
                graph.get("samples.ClassDependencies");

        assertTrue(dependencies.containsKey("samples.Base"));
        assertTrue(dependencies.get("samples.Base")
                .contains(TypeContext.EXTENDS));

        assertTrue(dependencies.containsKey("samples.Printable"));
        assertTrue(dependencies.get("samples.Printable")
                .contains(TypeContext.IMPLEMENTS));

        assertTrue(dependencies.containsKey("samples.ClassDependencies.Helper"));
        var helper = dependencies.get(
                "samples.ClassDependencies.Helper"
        );

        assertTrue(helper.contains(TypeContext.FIELD));
        assertTrue(helper.contains(TypeContext.PARAMETER));
        assertTrue(helper.contains(TypeContext.LOCAL_VARIABLE));
        assertTrue(helper.contains(TypeContext.RETURN_TYPE));
        assertTrue(helper.contains(TypeContext.INSTANTIATION));
        assertTrue(helper.contains(TypeContext.CAST));
        assertTrue(helper.contains(TypeContext.GENERIC_ARGUMENT));
        assertTrue(helper.contains(TypeContext.STATIC_METHOD_CALL));
        assertTrue(helper.contains(TypeContext.STATIC_FIELD_ACCESS));
        assertTrue(helper.contains(TypeContext.ARRAY_COMPONENT));

        assertTrue(dependencies.containsKey(
                "samples.ClassDependencies.Status"
        ));

        var status = dependencies.get(
                "samples.ClassDependencies.Status"
        );

        assertTrue(status.contains(TypeContext.FIELD));
        assertTrue(status.contains(TypeContext.PARAMETER));
        assertTrue(status.contains(TypeContext.STATIC_FIELD_ACCESS));
        assertTrue(status.contains(TypeContext.LOCAL_VARIABLE));

        assertTrue(dependencies.containsKey(
                "samples.CustomException"
        ));

        var exception = dependencies.get(
                "samples.CustomException"
        );

        assertTrue(exception.contains(TypeContext.THROWS));
        assertTrue(exception.contains(TypeContext.CATCH));
        assertTrue(exception.contains(TypeContext.PARAMETER));
        assertTrue(exception.contains(TypeContext.INSTANTIATION));
    }
}