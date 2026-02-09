// ForbiddenFunction.c

#include <stdio.h>

void forbidden() {
    puts("Forbidden function call");
    printf("Another forbidden call\n");
}

int main() {
    forbidden();
    return 0;
}