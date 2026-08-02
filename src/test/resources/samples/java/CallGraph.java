package samples;

public class CallGraph {

    public void methodA() {
        methodB();
    }

    public void methodB() {
        methodC();
    }

    public void methodC() {
        System.out.println("End");
    }
}