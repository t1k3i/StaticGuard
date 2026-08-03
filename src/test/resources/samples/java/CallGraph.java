public class CallGraph {

    public static void main(String[] args) {
        linear();
        branch();
        recursive(2);
        mutualA(2);
        Util.work();
    }

    static void linear() {
        leaf();
    }

    static void branch() {
        left();
        right();
    }

    static void left() {}

    static void right() {}

    static void leaf() {}

    static int recursive(int n) {
        if (n == 0) return 0;
        return recursive(n - 1);
    }

    static void mutualA(int n) {
        if (n > 0) mutualB(n - 1);
    }

    static void mutualB(int n) {
        if (n > 0) mutualA(n - 1);
    }

    static void unused() {
        dead();
    }

    static void dead() {}

    static class Util {
        static void work() {
            finish();
        }

        static void finish() {}
    }
}