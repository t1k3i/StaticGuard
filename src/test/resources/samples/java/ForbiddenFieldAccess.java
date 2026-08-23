package samples;

public class ForbiddenFieldAccess {

    static class User {
        private String name;
        private int age;

        void internal() {
            name = "John";       // SHOULD NOT FLAG
            this.name = "Jane";  // SHOULD NOT FLAG
            age = 20;            // SHOULD NOT FLAG
            this.age = 30;       // SHOULD NOT FLAG
        }

        String getName() {
            return name;        // SHOULD NOT FLAG
        }

        void setName(String name) {
            this.name = name;   // SHOULD NOT FLAG
        }
    }

    static void external(User user) {
        user.name = "John";     // SHOULD FLAG
        user.age = 42;          // SHOULD FLAG

        user.getName();         // SHOULD NOT FLAG
        user.setName("Bob");    // SHOULD NOT FLAG
    }

    static class Address {
        private String city;

        String getCity() {
            return city;        // SHOULD NOT FLAG
        }
    }

    static class Person {
        private Address address;

        Address getAddress() {
            return address;     // SHOULD NOT FLAG
        }
    }

    static void chained(Person person) {
        person.address = null;          // SHOULD FLAG

        person.address.city = "London";  // SHOULD FLAG

        person.getAddress();            // SHOULD NOT FLAG
        person.getAddress().getCity();  // SHOULD NOT FLAG
    }

    static class Config {
        static String VALUE = "test";

        static String getValue() {
            return VALUE;       // SHOULD NOT FLAG
        }
    }

    static void staticAccess() {
        Config.VALUE = "changed";   // SHOULD FLAG
        Config.getValue();          // SHOULD NOT FLAG
    }

    static void locals(String name) {
        int value = 10;

        value = 20;             // SHOULD NOT FLAG
        name = "Bob";           // SHOULD NOT FLAG
    }

    public static void main(String[] args) {
        User user = new User();
        external(user);

        Person person = new Person();
        chained(person);

        staticAccess();

        locals("John");
    }
}