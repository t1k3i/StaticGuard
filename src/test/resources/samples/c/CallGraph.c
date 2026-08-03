#include <stdio.h>

void linear(void);
void branch(void);
void left(void);
void right(void);
void leaf(void);
void recursive(int);
void mutualA(int);
void mutualB(int);
void work(void);
void finish(void);
void unused(void);
void dead(void);

int main(void)
{
    linear();
    branch();
    recursive(2);
    mutualA(2);
    work();
    return 0;
}

void linear(void)
{
    leaf();
}

void branch(void)
{
    left();
    right();
}

void left(void) {}

void right(void) {}

void leaf(void) {}

void recursive(int n)
{
    if (n > 0)
        recursive(n - 1);
}

void mutualA(int n)
{
    if (n > 0)
        mutualB(n - 1);
}

void mutualB(int n)
{
    if (n > 0)
        mutualA(n - 1);
}

void work(void)
{
    finish();
}

void finish(void) {}

void unused(void)
{
    dead();
}

void dead(void) {}