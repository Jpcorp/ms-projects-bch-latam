package cl.bch.cloud.ms.fglosas.mantenedor.exceptions;

public class TemplateValidationException extends RuntimeException {
    public TemplateValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}