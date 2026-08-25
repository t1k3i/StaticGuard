package src.model;

public class Address {
    private String street;
    private String city;

    public static String country = "USA";

    public Address(String street, String city) {
        this.street = street;
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public static void printAddress(Address address) {
        System.out.println("Street: " + address.getStreet());
        System.out.println("City: " + address.getCity());
    }
}
