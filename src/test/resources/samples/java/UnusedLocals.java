package samples;

public class UnusedLocals {

    public void test() {
        int used = 5;
        int unused = 10;

        System.out.println(used);
    }
}