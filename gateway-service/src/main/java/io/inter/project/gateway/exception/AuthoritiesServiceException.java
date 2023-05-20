package io.inter.project.gateway.exception;

public class AuthoritiesServiceException extends Exception{

    private static final long serialVersionUID = 1L;

    public AuthoritiesServiceException() {
        super();
    }

    public AuthoritiesServiceException(String message) {
        super(message);
    }

    public AuthoritiesServiceException(Exception e) {
        super(e);
    }

    public AuthoritiesServiceException(String message, Exception e) {
        super(message, e);
    }
}
