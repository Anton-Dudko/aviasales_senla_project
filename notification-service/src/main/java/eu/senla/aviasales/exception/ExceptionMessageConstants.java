package eu.senla.aviasales.exception;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
public class ExceptionMessageConstants {
    public static final String NOT_VALID_EMAIL = "email format is invalid";
    public static final String NOTIFICATION_NOT_FOUND = "Notification not found.";
    public static final String EXCEPTION_FROM_USERSERVICE = "Problems with userservice.";
    public static final String SUBJECT_CANT_BE_EMPTY = "Subject custom email can't be empty.";
    public static final String BODY_INCORRECT_LENGTH = "Min subject length is 5, max is 1000. Can't start with space.";
    public static final String SUBJECT_INCORRECT_LENGTH = "Min subject length is 5, max is 100. Can't start with space.";
    public static final String BODY_CANT_BE_EMPTY = "Body custom email can't be empty.";
}
