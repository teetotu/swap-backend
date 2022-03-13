package ru.swap.exceptions;

public class SwapApplicationException extends RuntimeException {
    public SwapApplicationException(String exMessage, Exception exception) {
        super(exMessage, exception);
    }

    public SwapApplicationException(String exMessage) {
        super(exMessage);
    }
}
