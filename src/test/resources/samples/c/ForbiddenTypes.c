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

int globalInt;
unsigned int globalUnsignedInt;
signed long globalSignedLong;
long long globalLongLong;

Node globalNode;
struct ForbiddenStruct globalStruct;
enum Status globalStatus;
union ForbiddenUnion globalUnion;

/* Function */

Node createNode(
        Node node,
        unsigned int unsignedValue,
        enum Status status,
        union ForbiddenUnion data) {

    Node localNode;
    struct ForbiddenStruct localStruct;
    enum Status localStatus;
    union ForbiddenUnion localUnion;

    int localInt;
    unsigned long localUnsignedLong;
    signed char localSignedChar;

    Node *nodePointer;
    struct ForbiddenStruct *structPointer;

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
}