#ifndef STUDENT_LIST_H
#define STUDENT_LIST_H

#include "student.h"

#define MAX_STUDENTS 100

typedef struct {
    Student students[MAX_STUDENTS];
    int count;
} StudentList;

void student_list_init(StudentList *list);
int student_list_add(StudentList *list, Student student);
Student *student_list_find(StudentList *list, int id);
void student_list_print(const StudentList *list);

#endif