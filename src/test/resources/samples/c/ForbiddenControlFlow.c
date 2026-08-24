#include <stdio.h>

void test_return(void)
{
    int condition = 1;

    if (condition) {
        return;
    }

    printf("This line will not be executed.\n");
}

int main(void)
{
    int i;

    for (i = 0; i < 10; i++) {
        if (i == 5) {
            break;
        }

        printf("%d\n", i);
    }

    for (i = 0; i < 10; i++) {
        if (i % 2 == 0) {
            continue;
        }

        printf("Odd: %d\n", i);
    }

    test_return();

    if (i == 10) {
        goto end;
    }

    printf("This line will be executed.\n");

end:
    printf("End of program.\n");

    return 0;
}