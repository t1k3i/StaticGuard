// UnusedLocalVariables.c

void unused_locals() {
    int unused1;
    int unused2 = 5;

    int used = 10;
    used++;

    {
        int inner_unused;
    }
}