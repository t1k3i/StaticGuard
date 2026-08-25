#include <stdio.h>
#include "student_list.h"

void student_list_init(StudentList *list)
{
    list->count = 0;
}

int student_list_add(StudentList *list, Student student)
{
    if (list->count >= MAX_STUDENTS) {
        return 0;
    }

    list->students[list->count] = student;
    list->count++;

    return 1;
}

Student *student_list_find(StudentList *list, int id)
{
    for (int i = 0; i < list->count; i++) {
        if (list->students[i].id == id) {
            return &list->students[i];
        }
    }

    return NULL;
}

void student_list_print(const StudentList *list)
{
    for (int i = 0; i < list->count; i++) {
        student_print(&list->students[i]);
    }
}