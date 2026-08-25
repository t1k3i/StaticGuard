#include <stdio.h>
#include "student_list.h"

static void print_menu(void)
{
    printf("Student Manager\n");
    printf("---------------\n");
}

int main(void)
{
    StudentList list;

    student_list_init(&list);

    Student alice = {
        1,
        "Alice",
        9.5f
    };

    Student bob = {
        2,
        "Bob",
        8.0f
    };

    student_list_add(&list, alice);
    student_list_add(&list, bob);

    print_menu();
    student_list_print(&list);

    Student *found = student_list_find(&list, 1);

    if (found != NULL) {
        printf("Found: ");
        student_print(found);
    }

    return 0;
}