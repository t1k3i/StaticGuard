package helpers;

import com.staticguard.common.Issue;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RuleTestHelper {

    public static void assertIssue(List<Issue> issues, int line, String message) {
        assertTrue(
                issues.stream().anyMatch(i -> i.getLine() == line && i.getMessage().equals(message)),
                "Expected issue at line " + line + " with message '" + message + "' but it was missing."
        );
    }
}
