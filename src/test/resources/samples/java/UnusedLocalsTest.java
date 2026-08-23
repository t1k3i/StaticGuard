package samples;

public class UnusedLocalsTest {

    static void basics() {
        int unused = 1;                 // SHOULD FLAG
        int used = 2;                   // SHOULD NOT FLAG
        System.out.println(used);

        String unusedString = "x";      // SHOULD FLAG
        String usedString = "y";        // SHOULD NOT FLAG
        System.out.println(usedString);
    }

    static void writes() {
        int y = 0;                      // SHOULD NOT FLAG
        y = 10;
        System.out.println(y);
    }

    static void scopesAndShadowing() {
        int unused = 1;                 // SHOULD FLAG

        {
            int innerUnused = 2;        // SHOULD FLAG
            int inner = 3;              // SHOULD NOT FLAG
            System.out.println(inner);
        }
    }

    static void references() {
        Object object = new Object();   // SHOULD NOT FLAG
        System.out.println(object);

        String unused = "unused";       // SHOULD FLAG

        int[] array = {1, 2, 3};        // SHOULD NOT FLAG
        System.out.println(array[0]);

        int[] unusedArray = {4, 5};     // SHOULD FLAG
    }

    static void objects() {
        StringBuilder builder = new StringBuilder();
        builder.append("hello");        // SHOULD NOT FLAG

        Integer number = 42;             // SHOULD NOT FLAG
        System.out.println(number);
    }

    static void controlFlow(boolean condition) {
        int value;                      // SHOULD NOT FLAG
        if (condition)
            value = 1;
        else
            value = 2;

        System.out.println(value);

        for (int i = 0; i < 3; i++) {
            int unused = i;             // SHOULD FLAG
            System.out.println(i);      // i SHOULD NOT FLAG
        }
    }

    static void dependencies() {
        int a = 10;                     // SHOULD NOT FLAG: used by b
        int b = a + 1;                  // SHOULD NOT FLAG: printed
        System.out.println(b);

        double unused = 3.14;           // SHOULD FLAG
    }

    static void parameters(int used, int unused, String text) {
        System.out.println(used);       // used SHOULD NOT FLAG
        // unused SHOULD FLAG
        System.out.println(text);       // text SHOULD NOT FLAG
    }

    static void specialCases(int value) {
        int captured = value;           // SHOULD NOT FLAG
        int unused = 99;                // SHOULD FLAG

        Runnable r = () ->             // captured is used by lambda
                System.out.println(captured);
        r.run();
    }

    static void nullAndCasts() {
        Object unused = null;           // SHOULD FLAG

        Object value = null;            // SHOULD NOT FLAG
        System.out.println(value);

        int ignored = 10;               // SHOULD FLAG
        String text = (String) null;     // SHOULD FLAG
    }

    static void tryCatch() {
        try {
            System.out.println("test");
        } catch (RuntimeException exception) {
            // exception SHOULD FLAG: catch parameter is never read
        }

        try (java.io.StringReader reader =
                     new java.io.StringReader("test")) {
            reader.ready();             // reader SHOULD NOT FLAG
        } catch (java.io.IOException e) {
            // e SHOULD FLAG if catch parameters are checked
        }
    }

    public static void main(String[] args) {
        // args SHOULD FLAG if method parameters are checked

        basics();
        writes();
        scopesAndShadowing();
        references();
        objects();
        controlFlow(true);
        dependencies();
        parameters(1, 2, "hello");
        specialCases(10);
        nullAndCasts();
        tryCatch();
    }
}