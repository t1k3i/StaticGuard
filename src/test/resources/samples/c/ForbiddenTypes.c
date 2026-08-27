#include <stdbool.h>

typedef struct {
    int value;
} Node;

struct ForbiddenStruct {
    int data;
};

enum Status {
    OK,
    ERROR
};

union ForbiddenUnion {
    int integer;
    double decimal;
};

/* Global */

int globalInt = 0;
unsigned int globalUnsignedInt = 0;
signed long globalSignedLong = 0;
long long globalLongLong = 0;

Node globalNode = {0};
struct ForbiddenStruct globalStruct = {0};
enum Status globalStatus;
union ForbiddenUnion globalUnion = {0};

/* Function */

Node createNode(
        Node node,
        unsigned int unsignedValue,
        enum Status status,
        union ForbiddenUnion data) {

    Node localNode = {0};
    struct ForbiddenStruct localStruct = {0};
    enum Status localStatus;
    union ForbiddenUnion localUnion = {0};

    int localInt = 0;
    unsigned long localUnsignedLong = 0;
    signed char localSignedChar = 0;

    Node *nodePointer = NULL;
    struct ForbiddenStruct *structPointer = NULL;

    return localNode;
}

void process(
        Node *node,
        struct ForbiddenStruct *forbiddenStruct,
        unsigned long value) {

    Node nodes[10];
    struct ForbiddenStruct structs[10];

    enum Status status;
    union ForbiddenUnion data;

    double b = 0.0;
    int a = (int)b;
}