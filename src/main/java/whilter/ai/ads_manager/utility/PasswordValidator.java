package whilter.ai.ads_manager.utility;

import java.util.regex.Pattern;

public class PasswordValidator {
    // Password validation regex
    private static final String PASSWORD_REGEX =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$";

    public static boolean isValid(String password) {
        if (password == null) {
            return false;
        }

        Pattern pattern = Pattern.compile(PASSWORD_REGEX);
        return pattern.matcher(password).matches();
    }

    public static String getPasswordValidationMessage() {
        return "Password must be 8-20 characters long, " +
                "contain at least one digit, one lowercase, " +
                "one uppercase letter, and one special character.";
    }
}
