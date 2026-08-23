package services;

import utils.StudentUtils;      // USED
import utils2.*;                // UNUSED

public class StudentService {

    public void printStudent(Student student) {
        System.out.println(
                StudentUtils.formatName(student.getName()) + " - " + student.getAge()
        );
    }
}