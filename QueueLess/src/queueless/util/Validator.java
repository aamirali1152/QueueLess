package queueless.util;

public final class Validator {
    private Validator() {}

    public static boolean validStudentId(String id) {
        return id != null && id.trim().matches("[A-Za-z0-9_-]{3,20}");
    }

    public static boolean validName(String name) {
        return name != null && name.trim().matches("[A-Za-z .'-]{2,40}");
    }
}
