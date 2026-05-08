package cl.bch.cloud.ms.fglosas.mantenedor.exceptions;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FieldsNotFoundExceptionTest {

    @Test
    void testExceptionMessage() {
        List<String> missingFields = Arrays.asList("nombre", "rut", "monto");
        FieldsNotFoundException exception = new FieldsNotFoundException(missingFields);

        String expectedMessage = "Hay campos faltantes en la data de prueba: *nombre, rut, monto*";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testExceptionIsRuntimeException() {
        FieldsNotFoundException exception = new FieldsNotFoundException(List.of("campo1"));
        assertTrue(exception instanceof RuntimeException);
    }
}
