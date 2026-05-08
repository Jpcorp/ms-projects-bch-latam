package cl.bch.cloud.ms.fglosas.mantenedor.exceptions;

import java.util.List;

public class FieldsNotFoundException extends RuntimeException {

    public FieldsNotFoundException(List<String> fieldsNotFound) {
        super("Hay campos faltantes en la data de prueba: *" + String.join(", ", fieldsNotFound) + "*");
    }

}
