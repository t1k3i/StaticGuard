package samples;

public class ForbiddenControlFlow {

    public void test(int x) {

        for (int i = 0; i < 10; i++) {
            if (i == 5) break;
        }

        while (x > 0) {
            x--;
            continue;
        }

        if (x == 0) {
            return;
        }
    }
}