package util;

public class ValidationUtil {
    public static boolean isValidUsername(String username) {
        return username != null && username.length() >= 3 && username.length() <= 30;
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }
}
