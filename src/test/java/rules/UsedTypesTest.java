package rules;

import com.staticguard.analyzers.UsedTypesAnalyzer;
import com.staticguard.analyzers.VisitorManager;
import com.staticguard.common.RuleContext;
import com.staticguard.enums.TypeContext;
import com.staticguard.parser.ParserFactory;
import com.staticguard.rules.UsedTypesRule;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UsedTypesTest {

    @Test
    void testAllUsedTypes() throws Exception {
        File file = new File(
                "src/test/resources/samples/java/UsedTypes.java"
        );

        var parser = ParserFactory.createParser(file);
        Object ast = parser.parse();
        var context = new RuleContext(file);

        UsedTypesRule<Object> rule = new UsedTypesRule<>();
        UsedTypesAnalyzer<Object> analyzer = new UsedTypesAnalyzer<>(context, rule);

        VisitorManager<Object> manager = new VisitorManager<>(ast);
        manager.addVisitor(analyzer);
        manager.runVisitors();

        Map<String, Set<TypeContext>> usedTypes = rule.getUsedTypes();


        assertNotNull(usedTypes, "Used types should not be null");


        // ===== Fields =====

        assertContext(
                usedTypes,
                "String",
                TypeContext.FIELD
        );

        assertContext(
                usedTypes,
                "int",
                TypeContext.FIELD
        );

        assertContext(
                usedTypes,
                "List<String>",
                TypeContext.FIELD
        );

        assertContext(
                usedTypes,
                "Map<String, Integer>",
                TypeContext.FIELD
        );

        assertContext(
                usedTypes,
                "IOException",
                TypeContext.FIELD
        );


        // ===== Instantiation =====

        assertContext(
                usedTypes,
                "IOException",
                TypeContext.INSTANTIATION
        );

        assertContext(
                usedTypes,
                "ArrayList<>",
                TypeContext.INSTANTIATION
        );

        assertContext(
                usedTypes,
                "HashMap<>",
                TypeContext.INSTANTIATION
        );

        assertContext(
                usedTypes,
                "Helper",
                TypeContext.INSTANTIATION
        );


        // ===== Constructor parameters =====

        assertContext(
                usedTypes,
                "String",
                TypeContext.PARAMETER
        );

        assertContext(
                usedTypes,
                "List<String>",
                TypeContext.PARAMETER
        );


        // ===== Method return types =====

        assertContext(
                usedTypes,
                "String",
                TypeContext.RETURN_TYPE
        );

        assertContext(
                usedTypes,
                "T",
                TypeContext.RETURN_TYPE
        );

        assertContext(
                usedTypes,
                "void",
                TypeContext.RETURN_TYPE
        );


        // ===== Method parameters =====

        assertContext(
                usedTypes,
                "Integer",
                TypeContext.PARAMETER
        );

        assertContext(
                usedTypes,
                "Object",
                TypeContext.PARAMETER
        );

        assertContext(
                usedTypes,
                "String",
                TypeContext.PARAMETER
        );


        // ===== Local variables =====

        assertContext(
                usedTypes,
                "List<Integer>",
                TypeContext.LOCAL_VARIABLE
        );

        assertContext(
                usedTypes,
                "String",
                TypeContext.LOCAL_VARIABLE
        );

        assertContext(
                usedTypes,
                "Map<String, List<Integer>>",
                TypeContext.LOCAL_VARIABLE
        );

        assertContext(
                usedTypes,
                "int[]",
                TypeContext.LOCAL_VARIABLE
        );

        assertContext(
                usedTypes,
                "String[]",
                TypeContext.LOCAL_VARIABLE
        );

        assertContext(
                usedTypes,
                "Status",
                TypeContext.LOCAL_VARIABLE
        );

        assertContext(
                usedTypes,
                "Helper",
                TypeContext.LOCAL_VARIABLE
        );


        // ===== Generic arguments =====

        assertContext(
                usedTypes,
                "String",
                TypeContext.GENERIC_ARGUMENT
        );

        assertContext(
                usedTypes,
                "Integer",
                TypeContext.GENERIC_ARGUMENT
        );


        // ===== Arrays =====

        assertContext(
                usedTypes,
                "int",
                TypeContext.ARRAY_COMPONENT
        );

        assertContext(
                usedTypes,
                "String",
                TypeContext.ARRAY_COMPONENT
        );


        // ===== Cast =====

        assertContext(
                usedTypes,
                "String",
                TypeContext.CAST
        );


        // ===== instanceof =====

        assertContext(
                usedTypes,
                "String",
                TypeContext.INSTANCEOF
        );


        // ===== Throws =====

        assertContext(
                usedTypes,
                "IOException",
                TypeContext.THROWS
        );


        // ===== Inheritance =====

        assertContext(
                usedTypes,
                "ArrayList<String>",
                TypeContext.EXTENDS
        );

        assertContext(
                usedTypes,
                "Runnable",
                TypeContext.IMPLEMENTS
        );


        // ===== Generic bounds =====

        assertContext(
                usedTypes,
                "Number",
                TypeContext.GENERIC_BOUND
        );


        // ===== Record components =====

        assertContext(
                usedTypes,
                "String",
                TypeContext.RECORD_COMPONENT
        );

        assertContext(
                usedTypes,
                "int",
                TypeContext.RECORD_COMPONENT
        );
    }

    @Test
    void testAllUsedTypesC() throws Exception {
        File file = new File(
                "src/test/resources/samples/c/UsedTypes.c"
        );

        var parser = ParserFactory.createParser(file);
        Object ast = parser.parse();

        var context = new RuleContext(file);

        UsedTypesRule<Object> rule = new UsedTypesRule<>();
        UsedTypesAnalyzer<Object> analyzer =
                new UsedTypesAnalyzer<>(context, rule);

        VisitorManager<Object> manager = new VisitorManager<>(ast);
        manager.addVisitor(analyzer);
        manager.runVisitors();

        Map<String, Set<TypeContext>> usedTypes = rule.getUsedTypes();

        assertNotNull(usedTypes, "Used types should not be null");


        // ===== Typedefs =====

        assertContext(
                usedTypes,
                "unsigned long",
                TypeContext.TYPEDEF
        );

        assertContext(
                usedTypes,
                "struct Person",
                TypeContext.TYPEDEF
        );

        assertContext(
                usedTypes,
                "int",
                TypeContext.TYPEDEF
        );


        // ===== Structs =====

        assertContext(
                usedTypes,
                "struct Person",
                TypeContext.STRUCT
        );

        assertContext(
                usedTypes,
                "struct Container",
                TypeContext.STRUCT
        );


        // ===== Union =====

        assertContext(
                usedTypes,
                "union Value",
                TypeContext.UNION
        );


        // ===== Enum =====

        assertContext(
                usedTypes,
                "enum Status",
                TypeContext.ENUM
        );


        // ===== Global variables =====

        assertContext(
                usedTypes,
                "int",
                TypeContext.GLOBAL_VARIABLE
        );

        assertContext(
                usedTypes,
                "char[128]",
                TypeContext.GLOBAL_VARIABLE
        );

        assertContext(
                usedTypes,
                "Person",
                TypeContext.GLOBAL_VARIABLE
        );


        // ===== Pointer types =====

        assertContext(
                usedTypes,
                "int*",
                TypeContext.POINTER
        );

        assertContext(
                usedTypes,
                "char**",
                TypeContext.POINTER
        );


        // ===== Function return types =====

        assertContext(
                usedTypes,
                "Person",
                TypeContext.RETURN_TYPE
        );

        assertContext(
                usedTypes,
                "int*",
                TypeContext.RETURN_TYPE
        );

        assertContext(
                usedTypes,
                "void",
                TypeContext.RETURN_TYPE
        );


        // ===== Function parameters =====

        assertContext(
                usedTypes,
                "char*",
                TypeContext.PARAMETER
        );

        assertContext(
                usedTypes,
                "int",
                TypeContext.PARAMETER
        );

        assertContext(
                usedTypes,
                "size_t",
                TypeContext.PARAMETER
        );

        assertContext(
                usedTypes,
                "void*",
                TypeContext.PARAMETER
        );


        // ===== Local variables =====

        assertContext(
                usedTypes,
                "Person",
                TypeContext.LOCAL_VARIABLE
        );

        assertContext(
                usedTypes,
                "int",
                TypeContext.LOCAL_VARIABLE
        );

        assertContext(
                usedTypes,
                "size_t",
                TypeContext.LOCAL_VARIABLE
        );

        assertContext(
                usedTypes,
                "enum Status",
                TypeContext.LOCAL_VARIABLE
        );

        assertContext(
                usedTypes,
                "union Value",
                TypeContext.LOCAL_VARIABLE
        );


        // ===== Arrays =====

        assertContext(
                usedTypes,
                "int[]",
                TypeContext.ARRAY_COMPONENT
        );

        assertContext(
                usedTypes,
                "char[]",
                TypeContext.ARRAY_COMPONENT
        );


        // ===== Cast =====

        assertContext(
                usedTypes,
                "Person*",
                TypeContext.CAST
        );


        // ===== sizeof =====

        assertContext(
                usedTypes,
                "Person",
                TypeContext.SIZEOF
        );

        assertContext(
                usedTypes,
                "int",
                TypeContext.SIZEOF
        );


        // ===== Function pointer =====

        assertContext(
                usedTypes,
                "Callback",
                TypeContext.FUNCTION_POINTER
        );
    }


    private void assertContext(
            Map<String, Set<TypeContext>> usedTypes,
            String type,
            TypeContext expectedContext
    ) {

        assertTrue(
                usedTypes.containsKey(type),
                "Expected type to be detected: " + type
        );

        assertTrue(
                usedTypes.get(type).contains(expectedContext),
                "Expected context " + expectedContext +
                        " for type " + type +
                        " but found " + usedTypes.get(type)
        );
    }
}