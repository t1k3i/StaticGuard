package samples;

class GoodClassName {

    int goodField;
    int BAD_field;

    static final int MAX_SIZE = 100;
    static final int badConstant = 50;

    void goodMethod(int goodParameter) {
        int goodVariable = goodParameter;
    }

    void BADMethod(int BadParameter) {
        int BadVariable = BadParameter;
    }

    enum ColorType {
        RED_COLOR,
        badColor
    }
}

class badClassName {

    int goodField;

    void goodMethod() {
        int goodVariable = 0;
    }
}