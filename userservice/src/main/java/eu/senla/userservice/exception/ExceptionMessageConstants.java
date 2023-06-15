package eu.senla.userservice.exception;

public final class ExceptionMessageConstants {

    public static final String USER_NOT_FOUND = "User not found.";
    public static final String INVALID_TOKEN = "Invalid token.";
    public static final String INVALID_PASSWORD = "Invalid password.";
    public static final String USER_WITH_SUCH_EMAIL_EXIST = "User with such email exist.";
    public static final String USER_WITH_SUCH_USERNAME_EXIST = "User with such username exist.";
    public static final String USER_NOT_REGISTRATE = "User with such email hasn't registration in system.";
    public static final String EMAIL_DONT_MATCH_ID = "User email dont match id.";
    public static final String NOT_VALID_LANGUAGE = "The language should be RU or EN or not to be at all (default is EN)";
    public static final String NOT_VALID_ROLE = "The role should be ROLE_USER or ROLE_ADMIN or not to be at all (default is ROLE_USER). The ROLE_ADMIN shouldn't be able in future";
    public static final String ERROR_USERNAME_PATTERN = "Should be min 3 symbols not start with space";
    public static final String ERROR_PASSWORD_PATTERN = "Should be min 3 symbols without space";
}
