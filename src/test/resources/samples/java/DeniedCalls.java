package samples;

import static java.lang.Math.abs;

public class DeniedCalls {

    private void helper() {
        System.out.println("helper");
    }

    private void max(int a, int b) {
        System.out.println(a + b);
    }

    public void test() {
        System.out.println("test");
        Math.abs(-10);
        abs(-20);
        helper();
    }

    public void other() {
        System.out.println("other");
        Math.max(10, 20);
        max(10, 20);
    }

    public void recursive() {
        recursive();
    }

    public void indirect() {
        helper();
    }

    public static void main(String[] args) {
        System.out.println("main");
        Math.min(1, 2);
    }
}