package samples;

public class ForbiddenControlFlow {

    public static void main(String[] args) {

        for (int i = 0; i < 10; i++) {
            if (i == 5) {
                break;
            }
            System.out.println(i);
        }

        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                continue;
            }
            System.out.println("Odd: " + i);
        }

        Object obj = "Test";

        if (obj instanceof String) {
            System.out.println("Object is String.");
        }

        testReturn();
    }

    public static void testReturn() {

        boolean condition = true;

        if (condition) {
            return;
        }

        System.out.println("This line will not be executed.");
    }
}