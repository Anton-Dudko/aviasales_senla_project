package eu.senla.userservice.token;

public class PasswordCoder {

    public static String codingPassword(String password) {
        return password.hashCode() + "$" + password.toUpperCase().hashCode();
    }
}
