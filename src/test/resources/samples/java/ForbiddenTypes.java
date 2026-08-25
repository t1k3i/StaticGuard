package samples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ForbiddenTypes extends ArrayList<String>
        implements List<String> {

    private ArrayList<String> field;
    private int number;
    private boolean flag;
    private double value;

    public ArrayList<String> method(
            ArrayList<String> parameter,
            int count,
            boolean enabled) throws IOException {

        ArrayList<String> local = new ArrayList<>();
        List<ArrayList<String>> generic = new ArrayList<>();

        int localNumber = 10;
        boolean localFlag = true;
        double localValue = 10.5;

        try {
            local.add("test");
            throw new IOException("Test exception");
        } catch (IOException e) {
            System.out.println(e);
        }

        return local;
    }

    public void multiple() {
        LinkedList<String> list = new LinkedList<>();
        ArrayList<Integer> numbers = new ArrayList<>();
    }

    public void arrays(ArrayList<String>[] array) {
        ArrayList<String>[] local = array;
        int[] primitiveArray = new int[10];
    }

    public void test() {
        java.util.HashMap<String, Integer> map1 = new java.util.HashMap<>();
        HashMap map = new HashMap();
    }

    private static class HashMap {
    }

    public void test2() {
        System.out.println(Helper.value);
        System.out.println(Helper.getValue());
    }

    private static class Helper {
        public static double value = 42;

        public static double getValue() {
            return value;
        }
    }
}