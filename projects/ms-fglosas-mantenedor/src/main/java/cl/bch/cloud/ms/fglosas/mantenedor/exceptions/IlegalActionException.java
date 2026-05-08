package cl.bch.cloud.ms.fglosas.mantenedor.exceptions;

import cl.bch.cloud.resterror.annotation.BchResponseStatus;
import cl.bch.cloud.resterror.exception.BchException;
import org.springframework.http.HttpStatus;

@BchResponseStatus(
        title = "Ilegal action",
        value = HttpStatus.METHOD_NOT_ALLOWED,
        code = 1001
)
public class IlegalActionException extends BchException {
    private static final long serialVersionUID = 1L;

    public IlegalActionException(final String message) {
        super(message);

    }
    public IlegalActionException(String message, Throwable t) {
        super(message, t);
    }

}
