package samples;

public class DeniedCalls {

    public void test() {
        System.out.println("Forbidden call");
        Math.abs(-5);
    }

    public void test2() throws InterruptedException {
        System.out.println("Forbidden call");
        Thread.sleep(10);
    }
}