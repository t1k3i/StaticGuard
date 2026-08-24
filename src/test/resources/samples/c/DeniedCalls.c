#include <stdio.h>
#include <stdlib.h>
#include <string.h>

void helper() {
    printf("helper");
}

void test() {
    printf("test");
    strlen("hello");
    helper();
}

void other() {
    printf("other");
    malloc(10);
}

void recursive() {
    recursive();
}

void main() {
    printf("main");
}