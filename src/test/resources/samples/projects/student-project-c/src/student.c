#include <stdio.h>
#include "student.h"

void student_print(const Student *student)
{
    if (student == NULL) {
        return;
    }

    printf("ID: %d, Name: %s, Grade: %.2f\n",
           student->id,
           student->name,
           student->grade);
}

float student_average(const Student *student)
{
    if (student == NULL) {
        return 0.0f;
    }

    return student->grade;
}