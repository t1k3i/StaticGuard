package src.model;

import java.util.ArrayList;
import java.util.List;

import src.service.Printable;

public class Student extends Person implements Printable {

    private Address address;
    private Status status;
    private Address[] previousAddresses;
    private List<StudentRecord> records;

    public Student(
            String name,
            Address address,
            Status status
    ) {
        super(name, address);
        this.address = address;
        this.status = status;
        this.previousAddresses = new Address[0];
        this.records = new ArrayList<>();
    }

    @Override
    public void print() {
        System.out.println(getName());
    }

    public StudentRecord createRecord() {
        StudentRecord record = new StudentRecord(
                getName(),
                20,
                address
        );

        records.add(record);

        return record;
    }

    public List<StudentRecord> getRecords() {
        return records;
    }

    public Address[] getPreviousAddresses() {
        return previousAddresses;
    }

    public void updateStatus(Status newStatus) {
        this.status = newStatus;
    }

    public void process(Object value) {
        Student student = (Student) value;

        String localName = student.getName();

        System.out.println(localName);
    }

    public static class StudentHelper {

        private Address address;

        public StudentHelper(Address address) {
            this.address = address;
        }

        public Address getAddress() {
            return address;
        }
    }
}
