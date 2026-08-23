package samples;

import java.util.AbstractList; // USED
import java.util.ArrayList;    // UNUSED

public class InheritanceImports extends AbstractList<String> {

    @Override
    public String get(int index) {
        return "Student";
    }

    @Override
    public int size() {
        return 1;
    }
}
