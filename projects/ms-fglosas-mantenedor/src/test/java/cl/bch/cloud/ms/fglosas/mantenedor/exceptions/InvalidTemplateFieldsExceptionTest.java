package cl.bch.cloud.ms.fglosas.mantenedor.exceptions;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InvalidTemplateFieldsExceptionTest {

    @Test
    void testExceptionMessage() {
        List<String> invalidFields = Arrays.asList("fecha", "monto", "rut_proveedor");
        InvalidTemplateFieldsException exception = new InvalidTemplateFieldsException(invalidFields);

        String expectedMessage = "Campos inválidos en el template: *fecha, monto, rut_proveedor*";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testExceptionIsRuntimeException() {
        InvalidTemplateFieldsException exception = new InvalidTemplateFieldsException(List.of("campoX"));
        assertTrue(exception instanceof RuntimeException);
    }
}
