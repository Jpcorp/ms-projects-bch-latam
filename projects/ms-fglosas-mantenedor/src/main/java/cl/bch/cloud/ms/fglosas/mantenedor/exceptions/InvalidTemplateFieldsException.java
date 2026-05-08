package cl.bch.cloud.ms.fglosas.mantenedor.exceptions;

import java.util.List;

public class InvalidTemplateFieldsException extends RuntimeException  {
    public InvalidTemplateFieldsException(List<String> invalidFields) {
        super("Campos inválidos en el template: *" + String.join(", ", invalidFields) + "*");
    }
}
