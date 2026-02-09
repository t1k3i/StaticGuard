// ForbiddenControlFlow.c

int control_flow(int x) {
    for (int i = 0; i < 10; i++) {
        if (i == x) {
            break;
        }
        if (i % 2 == 0) {
            continue;
        }
        if (i == 7) {
            return i;
        }
    }
    return -1;
}