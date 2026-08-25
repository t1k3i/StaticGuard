package src.model;

public class Person {
    private String name;
    private Address address;

    public Person(String name, Address address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public Address getAddress() {
        return address;
    }

    public void move(Address newAddress) {
        this.address = newAddress;
    }

    public static void printPerson(Person person) {
        System.out.println("Name: " + person.getName() + ", Country: " + Address.country);
        Address.printAddress(person.getAddress());
    }
}
