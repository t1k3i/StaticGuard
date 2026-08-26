#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <stdbool.h>

// Typedef type usage
typedef unsigned long Size;
typedef struct Person Person;
typedef int (*Callback)(int);

// Enum type
enum Status {
    ACTIVE,
    INACTIVE
};

// Struct type
struct Person {
    char *name;
    int age;
    Size size;
};

// Union type
union Value {
    int integer;
    float floating;
    char character;
};

// Global variables
int globalCount;
static char globalBuffer[128];
Person globalPerson;

// Pointer types
int *pointerToInt;
char **pointerToString;

// Function declaration with parameter and return types
Person createPerson(char *name, int age);

// Function returning pointer type
int *getValue(void);

// Function pointer
Callback callbackFunction;

// Function definition
Person createPerson(char *name, int age)
{
    Person person;

    person.name = name;
    person.age = age;
    person.size = sizeof(Person);

    return person;
}

// Function with arrays and pointer parameters
void processData(
    int numbers[],
    size_t length,
    const char *message,
    void *context
) {
    int localValue = 10;

    // Array type
    int values[5];

    // Pointer type
    int *ptr = &localValue;

    // Double pointer
    char **args = NULL;

    // Cast type usage
    Person *person = (Person *) context;

    // sizeof type usage
    size_t sizeOfInt = sizeof(int);

    // enum variable
    enum Status status = ACTIVE;

    // union variable
    union Value value = {0};
    value.integer = 42;

    printf("%s %d\n", message, *ptr);

    (void)numbers;
    (void)length;
    (void)args;
    (void)person;
    (void)status;
}

// Function returning pointer
int *getValue(void)
{
    static int value = 100;
    return &value;
}

// Callback implementation
int callback(int input)
{
    return input * 2;
}

// Struct pointer usage
void updatePerson(struct Person *person)
{
    person->age++;
}

// Const and volatile qualified types
void qualifiers(
    const int value,
    volatile int *counter,
    const char buffer[]
) {
    (void)value;
    (void)counter;
    (void)buffer;
}

// Nested struct type
struct Container {
    struct Person owner;
    union Value data;
    enum Status status;
};

// Function pointer parameter
void execute(Callback callback, int value)
{
    callback(value);
}

// Main
int main(int argc, char **argv)
{
    Person person;
    int numbers[10];

    Callback cb = callback;

    execute(cb, argc);

    updatePerson(&person);

    processData(
        numbers,
        10,
        "test",
        &person
    );

    (void)argv;

    return EXIT_SUCCESS;
}