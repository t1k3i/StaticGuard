package src.model;

import java.util.*;

public record NotUsedRecord(
        String name,
        int age
) {
    private void test() {
        save();
    }

    private void save() {}
}
