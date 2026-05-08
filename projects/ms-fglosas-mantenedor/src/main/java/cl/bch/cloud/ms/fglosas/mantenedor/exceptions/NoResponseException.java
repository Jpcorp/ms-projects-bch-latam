package cl.bch.cloud.ms.fglosas.mantenedor.exceptions;

import cl.bch.cloud.resterror.annotation.BchResponseStatus;
import cl.bch.cloud.resterror.exception.BchSystemException;
import org.springframework.http.HttpStatus;

/**
 * Exceptions control class
 */
@BchResponseStatus(
        title = "No Service Found",
        value = HttpStatus.FOUND,
        code = 4003
)
public class NoResponseException extends BchSystemException  {

    public NoResponseException(Throwable t) {
        this("No Service Found", t);
    }

    public NoResponseException(String message, Throwable t) {
        super(message, t);
    }
}
