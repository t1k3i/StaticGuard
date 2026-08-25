#ifndef STUDENT_H
#define STUDENT_H

typedef struct {
    int id;
    char name[50];
    float grade;
} Student;

void student_print(const Student *student);
float student_average(const Student *student);

#endif