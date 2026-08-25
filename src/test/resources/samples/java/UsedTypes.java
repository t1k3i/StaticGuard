package test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class UsedTypes extends ArrayList<String> implements Runnable {

    // Field types
    private String name;
    private int count;
    private List<String> items;
    private Map<String, Integer> mapping;

    // Static field
    private static final IOException ERROR = new IOException();

    // Constructor parameter types
    public UsedTypes(String name, List<String> items) {
        this.name = name;
        this.items = items;
        this.mapping = new HashMap<>();
    }

    @Override
    public void run() {
        System.out.println("running");
    }

    // Method parameter types and return types
    public String process(Integer value, Object input) throws IOException {
        List<Integer> numbers = new ArrayList<>();

        // Local variable types
        String result = String.valueOf(value);

        // Generic usage
        Map<String, List<Integer>> complexType = new HashMap<>();

        // Array types
        int[] primitiveArray = new int[10];
        String[] stringArray = new String[5];

        // Cast usage
        String casted = (String) input;

        // instanceof usage
        if (input instanceof String) {
            System.out.println(input);
        }

        // Enum usage
        Status status = Status.ACTIVE;

        // Inner class usage
        Helper helper = new Helper();

        int staticValue = Helper.getStaticValue();
        staticValue = Helper.staticValue;

        return result;
    }

    // Method with generic type parameters
    public <T extends Number> T genericMethod(T value) {
        return value;
    }

    // Nested class type usage
    public static class Helper {
        private String helperName;

        public static int staticValue = 42;

        public Helper() {
            this.helperName = "helper";
        }

        public static int getStaticValue() {
            return staticValue;
        }
    }

    // Enum type
    public enum Status {
        ACTIVE,
        INACTIVE
    }

    // Exception type usage
    public void exceptionMethod() throws IOException {
        throw new IOException();
    }

    // Interface type declaration
    interface Processor {
        String process(String input);
    }

    // Record type
    record User(String username, int age) {
    }
}