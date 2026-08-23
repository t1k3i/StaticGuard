#include <stdio.h>

void basics(void)
{
    int unused = 1;             // SHOULD FLAG
    int used = 2;               // SHOULD NOT FLAG
    printf("%d\n", used);
}

void writes(void)
{
    int y = 0;                  // SHOULD NOT FLAG: value read
    y = 10;
    printf("%d\n", y);
}

void scopes(void)
{
    int unused = 1;             // SHOULD FLAG

    {
        int inner_unused = 2;   // SHOULD FLAG
        int inner = 3;          // SHOULD NOT FLAG
        printf("%d\n", inner);
    }
}

void shadowing(void)
{
    int value = 1;              // SHOULD FLAG: shadowed by inner value

    {
        int value = 2;          // SHOULD NOT FLAG
        printf("%d\n", value);
    }
}

void pointers(void)
{
    int value = 10;             // SHOULD NOT FLAG: address/value used
    int *ptr = &value;
    *ptr += 1;
    printf("%d\n", value);

    int unused = 20;            // SHOULD FLAG
}

void dependencies(void)
{
    int a = 10;                 // SHOULD NOT FLAG: used by b
    int b = a + 1;              // SHOULD NOT FLAG: printed
    printf("%d\n", b);

    int unused = 30;            // SHOULD FLAG
}

void control_flow(int condition)
{
    int value;                  // SHOULD NOT FLAG: used after both paths

    if (condition)
        value = 1;
    else
        value = 2;

    printf("%d\n", value);

    for (int i = 0; i < 3; i++) {
        int unused = i;         // SHOULD FLAG
        printf("%d\n", i);
    }
}

void special_cases(void)
{
    int ignored = 1;
    (void)ignored;               // SHOULD NOT FLAG: explicit C suppression

    int x = 10;
    size_t size = sizeof(x);     // x: SHOULD NOT FLAG
    printf("%zu\n", size);

    int unused = 20;             // SHOULD FLAG
}

void parameters(int used, int unused)
{
    printf("%d\n", used);        // used SHOULD NOT FLAG
                                  // unused SHOULD FLAG
}

void parameter_in_expression(int n)
{
    int array[n];                // n SHOULD NOT FLAG: used here
    array[0] = 42;
    printf("%d\n", array[0]);
}

void aggregates(void)
{
    struct {
        int x;
        int y;
    } used = {1, 2};             // SHOULD NOT FLAG: used.x is read

    printf("%d\n", used.x);

    struct {
        int value;
    } unused_struct = {10};      // SHOULD FLAG: variable itself is never read

    union {
        int value;
        float decimal;
    } used_union;                 // SHOULD NOT FLAG: used below

    used_union.value = 42;
    printf("%d\n", used_union.value);

    union {
        int value;
    } unused_union = {10};               // SHOULD FLAG: variable itself is never read
}

typedef struct {
    int x;
    int y;
} Point;

int unused_struct_member(Point p1, Point p2)
{
    return p1.x + p1.y;         // p1 SHOULD NOT FLAG
                                  // p2 SHOULD FLAG
}

int main(void)
{
    basics();
    writes();
    scopes();
    shadowing();
    pointers();
    dependencies();
    control_flow(1);
    special_cases();
    parameters(1, 2);
    parameter_in_expression(5);
    aggregates();

    return 0;
}