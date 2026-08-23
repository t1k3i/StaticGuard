package samples;

import java.util.*; // USED
import java.io.*;    // UNUSED

public class WildcardImports {

    public void test() {
        List<String> names = new ArrayList<>();
        Map<String, Integer> scores = new HashMap<>();
        Set<String> uniqueNames = new HashSet<>();
        Queue<String> queue = new LinkedList<>();

        System.out.println(names);
        System.out.println(scores);
        System.out.println(uniqueNames);
        System.out.println(queue);
    }
}
