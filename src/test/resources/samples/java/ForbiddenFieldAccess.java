package samples;

public class ForbiddenFieldAccess {

    public int value = 10;

    public void test() {
        int x = value; // direct field access
    }
}