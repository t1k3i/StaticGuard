package samples;

import java.util.Scanner;

public class PrimitiveSample {

    // Primitive types
    private int primitiveField = 10;
    private double primitiveDoubleField = 3.14;
    private boolean primitiveBooleanField = true;

    // Reference / wrapper types
    private Integer integerField = 10;
    private Double doubleField = 3.14;
    private String stringField = "test";
    private Scanner scannerField = new Scanner(System.in);
    int[] array = {1, 2, 3};

    // Primitive parameter
    public void primitiveParameter(int number) {
        System.out.println(number);
    }

    // Reference / wrapper parameters
    public void wrapperParameter(Integer number) {
        System.out.println(number);
    }

    public void referenceParameter(String text) {
        System.out.println(text);
    }

    // Primitive return types
    public int primitiveReturn() {
        return 10;
    }

    public double primitiveDoubleReturn() {
        return 3.14;
    }

    // Reference / wrapper return types
    public Integer wrapperReturn() {
        return 10;
    }

    public String referenceReturn() {
        return "test";
    }

    public void localVariables() {

        // Primitive local variables
        int number = 10;
        double decimal = 5.5;
        boolean flag = true;
        char character = 'A';

        // Reference / wrapper local variables
        Integer integer = 10;
        Double doubleValue = 5.5;
        String text = "hello";
        Scanner scanner = new Scanner(System.in);

        System.out.println(number);
        System.out.println(decimal);
        System.out.println(flag);
        System.out.println(character);

        System.out.println(integer);
        System.out.println(doubleValue);
        System.out.println(text);
        System.out.println(scanner);
    }

    public static void main(String[] args) {

        int primitive = 100;
        Integer wrapper = 200;

        String text = "Hello";
        Scanner scanner = new Scanner(System.in);

        System.out.println(primitive);
        System.out.println(wrapper);
        System.out.println(text);
        System.out.println(scanner);
    }
}