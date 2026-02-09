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