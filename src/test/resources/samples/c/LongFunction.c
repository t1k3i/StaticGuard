// LongFunction.c

void very_long_function() {
    int sum = 0;
    for (int i = 0; i < 10; i++) {
        sum += i;
    }

    for (int i = 0; i < 10; i++) {
        sum += i * 2;
    }

    for (int i = 0; i < 10; i++) {
        sum += i * 3;
    }

    for (int i = 0; i < 10; i++) {
        sum += i * 4;
    }

    for (int i = 0; i < 10; i++) {
        sum += i * 5;
    }
}

void short_function() {
    int x = 5;
    int y = 10;
}

int another_long_function() {
    int result = 0;
    for (int i = 0; i < 100; i++) {
        result += i;
    }
    for (int j = 0; j < 100; j++) {
        result -= j;
    }
    for (int k = 0; k < 50; k++) {
        result *= 2;
    }
    for (int l = 0; l < 25; l++) {
        result /= 2;
    }
    return result;
}

void normal_function() {
    int a = 42;
    a = a + 1;
}