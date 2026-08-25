package samples;

import java.util.List;

public class ClassDependencies extends Base implements Printable {

    private Helper helper;
    private Helper[] helpers;
    private Status status;

    public ClassDependencies(Helper helper, Status status) {
        this.helper = helper;
        this.status = status;
    }

    public Helper getHelper() {
        return helper;
    }

    public List<Helper> getHelpers() throws CustomException {
        Helper local = new Helper();
        Helper value = (Helper) local;

        Helper.staticMethod();
        int x = Helper.STATIC_VALUE;

        try {
            throw new CustomException();
        } catch (CustomException e) {
            throw e;
        }
    }

    public void use(Helper parameter) {
        parameter.help();
        Status current = Status.ACTIVE;
    }

    static class Helper {
        static int STATIC_VALUE = 10;

        static void staticMethod() {
        }

        void help() {
        }
    }

    enum Status {
        ACTIVE,
        INACTIVE
    }
}

class Base {
}

interface Printable {
}

class CustomException extends Exception {
}