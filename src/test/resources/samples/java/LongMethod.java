package samples;

public class LongMethod {

    public void veryLongMethod() {

        int a=1; int b=2; int c=3; int d=4; int e=5;
        int f=6; int g=7; int h=8; int i=9; int j=10;
        int k=11; int l=12; int m=13; int n=14; int o=15;
        int p=16; int q=17; int r=18; int s=19; int t=20;
        int u=21; int v=22; int w=23; int x=24; int y=25;
        int z=26; int aa=27; int bb=28; int cc=29; int dd=30;
        int ee=31;
    }

    public void shortMethod() {
        int x = 5;
        int y = 10;
    }

    public int anotherLongMethod() {
        int result = 0;
        for (int i = 0; i < 100; i++) {
            result += i;
        }
        for (int j = 0; j < 100; j++) {
            result -= j;
        }
        for (int k = 0; k < 50; k++) {
            result *= 2;
        }
        for (int l = 0; l < 25; l++) {
            result /= 2;
        }
        return result;
    }

    public void normalMethod() {
        String message = "Hello";
        System.out.println(message);
    }
}