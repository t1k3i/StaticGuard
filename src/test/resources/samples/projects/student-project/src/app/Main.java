package src.app;

import src.exception.StudentNotFoundException;
import src.model.Address;
import src.model.Status;
import src.model.Student;
import src.model.StudentRecord;
import src.repository.StudentRepository;
import src.service.PersonService;
import src.service.StudentService;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        Address address = new Address(
                "Main Street 1",
                "Nova Gorica"
        );

        Student student = new Student(
                "Tine",
                address,
                Status.ACTIVE
        );

        StudentRepository repository =
                new StudentRepository();

        repository.save(student);

        PersonService personService =
                new PersonService(student);

        StudentService studentService =
                new StudentService(
                        repository,
                        personService
                );

        try {
            Student found =
                    studentService.findStudent(0);

            List<StudentRecord> records =
                    studentService.getRecords(0);

            System.out.println(found);
            System.out.println(records);

        } catch (StudentNotFoundException exception) {
            studentService.handle(exception);
        }
    }
}
