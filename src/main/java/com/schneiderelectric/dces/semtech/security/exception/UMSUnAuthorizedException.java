package com.schneiderelectric.dces.semtech.security.exception;

public class UMSUnAuthorizedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private String message;
    private Throwable exception;

    public UMSUnAuthorizedException(String message, Throwable exception) {
        super(message, exception);
        this.message = message;
        this.exception = exception;
    }

    public UMSUnAuthorizedException(Throwable exception) {
        super(exception);
        this.exception = exception;
    }

    public UMSUnAuthorizedException(String message) {
        super(message);
        this.message = message;
    }
}
