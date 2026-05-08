package cl.bch.cloud.ms.fglosas.mantenedor.exceptions;

import cl.bch.cloud.resterror.annotation.BchResponseStatus;
import cl.bch.cloud.resterror.exception.BchSystemException;
import org.springframework.http.HttpStatus;

/**
 * Exceptions control class
 */
@BchResponseStatus(
        title = "No Data Found",
        value = HttpStatus.FOUND,
        code = 4000
)
public class NoDataFoundException extends BchSystemException {


    public NoDataFoundException(Throwable t) {
        this("No Data Found", t);
    }

    public NoDataFoundException(String message) {
        super(message, null);
    }

    public NoDataFoundException(String message, Throwable t) {
        super(message, t);
    }

}
