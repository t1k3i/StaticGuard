public class MainApp {

    public static void main(String[] args) {
        int result = addNumbers(3, 4);
        System.out.println(result);
        Util util1 = new Util();
    }

    public static int addNumbers(int a, int b) {
        return a + b;
    }
}