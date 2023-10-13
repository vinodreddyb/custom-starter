package com.schneiderelectric.dces.semtech.security.exception;

public class UmsServiceException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    private String message;
    private Throwable exception;

    public UmsServiceException(String message, Throwable exception) {
        super(message,exception);
        this.message = message;
        this.exception = exception;
    }

    public UmsServiceException(Throwable exception) {
        super(exception);
        this.exception = exception;
    }

    public UmsServiceException(String message) {
        super(message);
        this.message = message;
    }
}
