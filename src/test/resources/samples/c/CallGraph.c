// CallGraph.c

void leaf() {}

void helper() {
    leaf();
}

void branchA() {
    helper();
}

void branchB() {
    leaf();
}

void root() {
    branchA();
    branchB();
}

void unused() {}

int recursion(int a) {
    return recursion(a + 1);
}

int main() {
    root();
    return 0;
}