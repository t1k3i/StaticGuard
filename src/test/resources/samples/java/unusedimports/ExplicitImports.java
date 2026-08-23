package samples;

import java.time.LocalDate;      // USED
import java.time.LocalDateTime;  // UNUSED
import java.nio.file.Path;        // USED
import java.nio.file.Files;       // UNUSED

public class ExplicitImports {

    public void test() {
        LocalDate today = LocalDate.now();
        Path path = Path.of("students.txt");

        System.out.println(today);
        System.out.println(path);
    }
}
