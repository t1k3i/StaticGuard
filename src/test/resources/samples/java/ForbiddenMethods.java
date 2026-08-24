package samples;

import static java.lang.Math.abs;

public class ForbiddenMethods {

    private void helper() {
        System.out.println("helper");
    }

    public void test() {
        System.out.println("forbidden println");
        Integer.parseInt("123");
        System.exit(0);

        helper();
        String.valueOf(123);
        Math.abs(-10);
        abs(-20);
    }

    public void multipleCalls() {
        System.out.println("one");
        System.out.println("two");

        Integer.parseInt("10");
        Integer.parseInt("20");

        Math.max(10, 20);
        max(10, 20);
    }

    private static int max(int a, int b) {
        return a > b ? a : b;
    }

    public static void main(String[] args) {
        System.out.println("main");
        Math.min(1, 2);
        abs(-5);
    }
}