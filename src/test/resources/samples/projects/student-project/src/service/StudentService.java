package src.service;

import src.exception.StudentNotFoundException;
import src.model.Address;
import src.model.Status;
import src.model.Student;
import src.model.StudentRecord;
import src.repository.StudentRepository;

import java.util.List;

public class StudentService {

    private StudentRepository repository;
    private PersonService personService;

    public StudentService(
            StudentRepository repository,
            PersonService personService
    ) {
        this.repository = repository;
        this.personService = personService;
    }

    public Student findStudent(int id)
            throws StudentNotFoundException {

        Student student = repository.findById(id);

        if (student == null) {
            throw new StudentNotFoundException(
                    "Student not found"
            );
        }

        return student;
    }

    public List<StudentRecord> getRecords(int id)
            throws StudentNotFoundException {

        Student student = findStudent(id);

        return student.getRecords();
    }

    public void changeAddress(
            int id,
            Address address
    ) throws StudentNotFoundException {

        Student student = findStudent(id);

        personService.updateAddress(address);

        student.move(address);
    }

    public void changeStatus(
            Student student,
            Status status
    ) {
        student.updateStatus(status);
    }

    public void process(Object object) {
        Student student = (Student) object;

        StudentRecord record =
                student.createRecord();

        System.out.println(record);
    }

    public void handle(StudentNotFoundException exception) {
        System.out.println(
                exception.getMessage()
        );
    }
}