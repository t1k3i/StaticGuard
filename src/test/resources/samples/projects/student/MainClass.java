import java.time.LocalDate;            // USED
import java.util.ArrayList;            // USED
import java.util.List;                 // USED

import services.Student;                // USED
import services.StudentService;         // USED
import utils.StudentUtils;              // UNUSED

public class MainClass {

    public static void main(String[] args) {

        List<Student> students = new ArrayList<>();

        students.add(new Student("Alice", 20));
        students.add(new Student("Bob", 22));

        StudentService service = new StudentService();

        for (Student student : students) {
            service.printStudent(student);
            System.out.println(student.getName());
        }

        LocalDate today = LocalDate.now();

        System.out.println("Today: " + today);
    }
}