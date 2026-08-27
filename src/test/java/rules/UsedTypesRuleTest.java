package rules;

import com.staticguard.analyzers.UsedTypesAnalyzer;
import com.staticguard.analyzers.VisitorManager;
import com.staticguard.common.RuleContext;
import com.staticguard.enums.TypeContext;
import com.staticguard.parser.LanguageParser;
import com.staticguard.parser.ParserFactory;
import com.staticguard.rules.UsedTypesRule;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UsedTypesRuleTest {

    private Map<String, Set<TypeContext>> analyze(File file)
            throws Exception {

        LanguageParser<?> parser = ParserFactory.createParser(file);
        Object ast = parser.parse();

        RuleContext context = new RuleContext(file);

        UsedTypesRule<Object> rule =
                new UsedTypesRule<>();

        UsedTypesAnalyzer<Object> analyzer =
                new UsedTypesAnalyzer<>(context, rule);

        VisitorManager<Object> manager =
                new VisitorManager<>(ast, context);

        manager.addVisitor(analyzer);
        manager.runVisitors();

        return rule.getUsedTypes();
    }

    @Test
    void cUsedTypes() throws Exception {
        File file = new File(
                "src/test/resources/samples/c/UsedTypes.c"
        );

        var usedTypes = analyze(file);

        assertEquals(Set.of(
                "Callback",
                "Person",
                "Size",
                "char",
                "enum Status",
                "float",
                "int",
                "size_t",
                "struct Person",
                "union Value",
                "unsigned long",
                "void"
        ), usedTypes.keySet());

        assertEquals(Set.of(
                TypeContext.LOCAL_VARIABLE,
                TypeContext.PARAMETER
        ), usedTypes.get("Callback"));

        assertEquals(Set.of(
                TypeContext.LOCAL_VARIABLE,
                TypeContext.RETURN_TYPE,
                TypeContext.CAST,
                TypeContext.GLOBAL_VARIABLE
        ), usedTypes.get("Person"));

        assertEquals(Set.of(
                TypeContext.FIELD
        ), usedTypes.get("Size"));

        assertEquals(Set.of(
                TypeContext.FIELD,
                TypeContext.LOCAL_VARIABLE,
                TypeContext.PARAMETER,
                TypeContext.GLOBAL_VARIABLE
        ), usedTypes.get("char"));

        assertEquals(Set.of(
                TypeContext.FIELD,
                TypeContext.LOCAL_VARIABLE
        ), usedTypes.get("enum Status"));

        assertEquals(Set.of(
                TypeContext.FIELD
        ), usedTypes.get("float"));

        assertEquals(Set.of(
                TypeContext.FIELD,
                TypeContext.LOCAL_VARIABLE,
                TypeContext.PARAMETER,
                TypeContext.RETURN_TYPE,
                TypeContext.TYPEDEF,
                TypeContext.GLOBAL_VARIABLE
        ), usedTypes.get("int"));

        assertEquals(Set.of(
                TypeContext.LOCAL_VARIABLE,
                TypeContext.PARAMETER
        ), usedTypes.get("size_t"));

        assertEquals(Set.of(
                TypeContext.FIELD,
                TypeContext.PARAMETER,
                TypeContext.TYPEDEF
        ), usedTypes.get("struct Person"));

        assertEquals(Set.of(
                TypeContext.FIELD,
                TypeContext.LOCAL_VARIABLE
        ), usedTypes.get("union Value"));

        assertEquals(Set.of(
                TypeContext.TYPEDEF
        ), usedTypes.get("unsigned long"));

        assertEquals(Set.of(
                TypeContext.PARAMETER,
                TypeContext.RETURN_TYPE,
                TypeContext.CAST
        ), usedTypes.get("void"));
    }

    @Test
    void javaUsedTypes() throws Exception {
        File file = new File(
                "src/test/resources/samples/java/UsedTypes.java"
        );

        var usedTypes = analyze(file);

        // Make sure there are no unexpected types.
        assertEquals(Set.of(
                "int",
                "int[]",
                "java.io.IOException",
                "java.lang.Integer",
                "java.lang.Object",
                "java.lang.Runnable",
                "java.lang.String",
                "java.lang.String[]",
                "java.lang.System",
                "java.util.ArrayList",
                "java.util.HashMap",
                "java.util.List",
                "java.util.Map",
                "test.UsedTypes.Helper",
                "test.UsedTypes.Status",
                "void"
        ), usedTypes.keySet());

        assertEquals(Set.of(
                TypeContext.FIELD,
                TypeContext.LOCAL_VARIABLE,
                TypeContext.PARAMETER,
                TypeContext.RETURN_TYPE,
                TypeContext.ARRAY_COMPONENT,
                TypeContext.RECORD_COMPONENT
        ), usedTypes.get("int"));

        assertEquals(Set.of(
                TypeContext.LOCAL_VARIABLE
        ), usedTypes.get("int[]"));

        assertEquals(Set.of(
                TypeContext.FIELD,
                TypeContext.INSTANTIATION,
                TypeContext.THROWS
        ), usedTypes.get("java.io.IOException"));

        assertEquals(Set.of(
                TypeContext.PARAMETER,
                TypeContext.GENERIC_ARGUMENT
        ), usedTypes.get("java.lang.Integer"));

        assertEquals(Set.of(
                TypeContext.PARAMETER
        ), usedTypes.get("java.lang.Object"));

        assertEquals(Set.of(
                TypeContext.IMPLEMENTS
        ), usedTypes.get("java.lang.Runnable"));

        assertEquals(Set.of(
                TypeContext.FIELD,
                TypeContext.LOCAL_VARIABLE,
                TypeContext.PARAMETER,
                TypeContext.RETURN_TYPE,
                TypeContext.GENERIC_ARGUMENT,
                TypeContext.ARRAY_COMPONENT,
                TypeContext.CAST,
                TypeContext.RECORD_COMPONENT,
                TypeContext.STATIC_METHOD_CALL
        ), usedTypes.get("java.lang.String"));

        assertEquals(Set.of(
                TypeContext.LOCAL_VARIABLE
        ), usedTypes.get("java.lang.String[]"));

        assertEquals(Set.of(
                TypeContext.STATIC_FIELD_ACCESS
        ), usedTypes.get("java.lang.System"));

        assertEquals(Set.of(
                TypeContext.INSTANTIATION,
                TypeContext.EXTENDS
        ), usedTypes.get("java.util.ArrayList"));

        assertEquals(Set.of(
                TypeContext.INSTANTIATION
        ), usedTypes.get("java.util.HashMap"));

        assertEquals(Set.of(
                TypeContext.FIELD,
                TypeContext.LOCAL_VARIABLE,
                TypeContext.PARAMETER,
                TypeContext.GENERIC_ARGUMENT
        ), usedTypes.get("java.util.List"));

        assertEquals(Set.of(
                TypeContext.FIELD,
                TypeContext.LOCAL_VARIABLE
        ), usedTypes.get("java.util.Map"));

        assertEquals(Set.of(
                TypeContext.LOCAL_VARIABLE,
                TypeContext.INSTANTIATION,
                TypeContext.STATIC_METHOD_CALL,
                TypeContext.STATIC_FIELD_ACCESS
        ), usedTypes.get("test.UsedTypes.Helper"));

        assertEquals(Set.of(
                TypeContext.LOCAL_VARIABLE,
                TypeContext.STATIC_FIELD_ACCESS
        ), usedTypes.get("test.UsedTypes.Status"));

        assertEquals(Set.of(
                TypeContext.RETURN_TYPE
        ), usedTypes.get("void"));
    }
}