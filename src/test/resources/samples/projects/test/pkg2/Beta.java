package pkg2;

import pkg1.Alpha;

public class Beta {
    public static String reply() {
        // Call function from another package
        System.out.println("before");
        return Alpha.sayHello() + " and hello from Beta";
    }

    private static String sayHello() {
        return "Hi";
    }
}
