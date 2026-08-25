package src.repository;

import java.util.ArrayList;
import java.util.List;

import src.model.Student;

public class StudentRepository implements Repository<Student> {

    private List<Student> students = new ArrayList<>();

    @Override
    public Student findById(int id) {
        for (Student student : students) {
            if (id == students.indexOf(student)) {
                return student;
            }
        }

        return null;
    }

    @Override
    public List<Student> findAll() {
        return students;
    }

    @Override
    public void save(Student student) {
        students.add(student);
    }
}
