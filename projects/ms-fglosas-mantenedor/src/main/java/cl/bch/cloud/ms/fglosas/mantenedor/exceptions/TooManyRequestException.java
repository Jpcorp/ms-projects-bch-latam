package cl.bch.cloud.ms.fglosas.mantenedor.exceptions;

import cl.bch.cloud.resterror.annotation.BchResponseStatus;
import cl.bch.cloud.resterror.exception.BchSystemException;
import org.springframework.http.HttpStatus;

/**
 * Exceptions control class
 */
@BchResponseStatus(
        title = "Too many request",
        value = HttpStatus.TOO_MANY_REQUESTS,
        code = 3003
)
public class TooManyRequestException extends BchSystemException {


    public TooManyRequestException(Throwable t) {
        this("Too many request", t);
    }

    public TooManyRequestException(String message, Throwable t) {
        super(message, t);
    }

}
