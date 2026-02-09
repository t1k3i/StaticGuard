// DeniedCalls.c

#include <stdio.h>
#include <stdlib.h>

void allowed() {}

void forbidden_calls() {
    printf("This should be flagged\n");
    system("ls");
    allowed();
}

int main() {
    forbidden_calls();
    return 0;
}