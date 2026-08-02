package samples;

public class LoopNesting {

    public void nestedLoops() {

        for (int i = 0; i < 10; i++) {
            while (i > 0) {
                for (int j = 0; j < 5; j++) {
                    System.out.println(j);
                }
            }
        }
    }
}