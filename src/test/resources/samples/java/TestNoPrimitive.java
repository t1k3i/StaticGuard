/*Class/Interface name should be UpperCamelCase: testNoPrimitive*/
public class TestNoPrimitive {

    public static void main(String[] args) {
        String Text = "Hello, world!";
        System.out.println(Text);
        int Number = 42;
        double pi = 3.14;
        for (int i = 0; i < 10; i++) {
            while (true) {
                isPositive(2);
            }
        }
        for (int i = 0; i < 10; i++) {
            while (true) {
                do {
                } while (true);
            }
        }
    }

    public static boolean isPositive(int value) {
        return value > 0;
    }
}
