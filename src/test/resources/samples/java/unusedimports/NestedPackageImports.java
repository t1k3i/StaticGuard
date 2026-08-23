package samples;

import java.util.concurrent.atomic.AtomicInteger; // USED
import java.util.concurrent.atomic.AtomicLong;    // UNUSED

public class NestedPackageImports {

    private final AtomicInteger counter = new AtomicInteger();

    public void test() {
        counter.incrementAndGet();
        System.out.println(counter);
    }
}
