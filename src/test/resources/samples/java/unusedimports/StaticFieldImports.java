package samples;

import java.util.concurrent.TimeUnit;          // USED
import java.util.concurrent.atomic.AtomicInteger; // UNUSED

public class StaticFieldImports {

    public void test() {
        long millis = TimeUnit.SECONDS.toMillis(10);
        System.out.println(millis);
    }
}
