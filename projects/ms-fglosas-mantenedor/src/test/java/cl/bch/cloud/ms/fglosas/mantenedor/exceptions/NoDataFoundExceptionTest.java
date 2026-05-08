package cl.bch.cloud.ms.fglosas.mantenedor.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NoDataFoundExceptionTest {
    @Test
    public void testConstructorWithThrowable() {
        Throwable cause = new RuntimeException("Causa original");
        NoDataFoundException exception = new NoDataFoundException(cause);

        assertEquals("No Data Found", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    public void testConstructorWithMessageAndThrowable() {
        String message = "Datos no encontrados en la base";
        Throwable cause = new NullPointerException("Null");
        NoDataFoundException exception = new NoDataFoundException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}