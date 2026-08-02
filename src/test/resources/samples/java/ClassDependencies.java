package samples;

import java.util.List;
import java.util.ArrayList;

/*
 * Sample file for class dependency analysis.
 * Purpose: demonstrate how ClassDependencies uses Helper.
 * Expected relationships (detected by the analyzer):
 *  - samples.ClassDependencies -> Helper [FIELD, INSTANTIATION]
 *
 * This file is intentionally small and self-contained so the test
 * produces a compact, thesis-friendly output.
 */
public class ClassDependencies {

    // Field dependency: Helper used as a field (should be recorded as FIELD)
    private Helper helper = new Helper();

    // Local usage and instantiation demonstrate additional dependency contexts
    public void test() {
        List<String> list = new ArrayList<>(); // external dependency on java.util.List / ArrayList
        helper.help();
    }
}

class Helper {
    void help() {}
}
