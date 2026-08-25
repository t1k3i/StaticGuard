package src.service;

import src.model.Address;
import src.model.Person;

public class PersonService {

    private Person person;

    public PersonService(Person person) {
        this.person = person;
    }

    public Address getAddress() {
        return person.getAddress();
    }

    public void updateAddress(Address address) {
        person.move(address);
    }
}
