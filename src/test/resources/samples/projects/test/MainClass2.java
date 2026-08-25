import pkg2.Beta;

public class MainClass2 {
    public static void main(String[] args) {
        System.out.println("Alpha -> Beta inter-package call test:");
        System.out.println(Beta.reply());
    }
}
