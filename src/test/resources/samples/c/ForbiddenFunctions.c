#include <stdio.h>
#include <stdlib.h>
#include <string.h>

void helper(void) {
    printf("helper\n");
}

void test(void) {
    printf("forbidden printf\n");
    scanf("%d", NULL);
    malloc(100);

    helper();
    strlen("hello");
    strcmp("a", "b");
}

void multipleCalls(void) {
    printf("one\n");
    printf("two\n");

    scanf("%d", NULL);
    scanf("%d", NULL);

    strlen("test");
}

int main(void) {
    printf("main\n");
    strlen("hello");

    return 0;
}

void test_variable(void)
{
    int strcpy = 10;

    strcpy = 20;
    int x = strcpy;
    strcpy++;
}

void test_call(void)
{
    char a[10];
    char b[10];

    strcpy(a, b);
}