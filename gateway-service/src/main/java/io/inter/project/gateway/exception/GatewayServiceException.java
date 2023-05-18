package io.inter.project.gateway.exception;

public class GatewayServiceException extends Exception{

    private static final long serialVersionUID = 1L;

    public GatewayServiceException() {
        super();
    }

    public GatewayServiceException(String message) {
        super(message);
    }

    public GatewayServiceException(Exception e) {
        super(e);
    }

    public GatewayServiceException(String message, Exception e) {
        super(message, e);
    }
}
