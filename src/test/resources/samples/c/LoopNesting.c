// LoopNesting.c

void nested_loops() {
    for (int i = 0; i < 3; i++) {
        while (i < 2) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 2; k++) {
                    int x = i + j + k;
                }
            }
            break;
        }
    }
}