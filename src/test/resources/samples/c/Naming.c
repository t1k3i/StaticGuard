#include <stdio.h>

const int MAX_COUNT = 10;
const int badConstant = 20;

struct StudentData {
    int value;
};

struct badStruct {
    int value;
};

enum ColorType {
    RED_COLOR,
    badColor
};

typedef int StudentId;
typedef int badType;

int goodFunction(int goodParameter) {
    int goodVariable = goodParameter;
    return goodVariable;
}

int BadFunction(int BadParameter) {
    int BadVariable = BadParameter;
    return BadVariable;
}

int snake_case_function(void) {
    int bad_variable = 0;
    return bad_variable;
}

int main(void) {
    goodFunction(1);
    BadFunction(2);
    snake_case_function();
    return 0;
}