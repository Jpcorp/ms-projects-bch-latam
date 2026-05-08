package cl.bch.cloud.ms.fglosas.mantenedor.utils;

import cl.bch.cloud.ms.fglosas.mantenedor.entities.CampoEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.exceptions.InvalidTemplateFieldsException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TemplateValidatorTest {

    @Test
    void testValidateTemplateFieldsOrThrow_validFields() {
        TemplateValidator validator = new TemplateValidator();
        String htmlContent = "<html><body>${data.nombre} ${data.apellido}</body></html>";
        List<CampoEntity> fields = Arrays.asList(
                new CampoEntity("nombre", ""),
                new CampoEntity("apellido", "")
        );

        assertThatCode(() -> validator.validateTemplateFieldsOrThrow(htmlContent, fields))
                .doesNotThrowAnyException();
    }

    @Test
    void testValidateTemplateFieldsOrThrow_invalidFields() {
        TemplateValidator validator = new TemplateValidator();
        String htmlContent = "<html><body>${data.nombre} ${data.edad}</body></html>";
        List<CampoEntity> fields = Arrays.asList(
                new CampoEntity("apellido", "")
        );

        assertThrows(InvalidTemplateFieldsException.class, () -> {
            validator.validateTemplateFieldsOrThrow(htmlContent, fields);
        });
    }

    @Test
    void testValidateJsonTemplateFieldsOrThrow_allFieldsPresent_shouldPass() {
        TemplateValidator validator = new TemplateValidator();
        String htmlContent = """
            <html>
                <body>
                    <p>${data.comuna}</p>
                    <p>${data.region}</p>
                    <p>${data.monto}</p>
                    <p>${data.firma}</p> <!-- Este campo debe ser ignorado -->
                </body>
            </html>
        """;

        String json = """
            {
              "atributos": {
                "tipo": "HTML"
              },
              "data": {
                "comuna": "Santiago",
                "region": "Metropolitana",
                "monto": "1.000.000"
              }
            }
        """;

        assertThatCode(() -> validator.validateJsonTemplateFieldsOrThrow(htmlContent, json))
                .doesNotThrowAnyException();
    }

    @Test
    void testValidateJsonTemplateFieldsOrThrow_onlyFirmaMissing_shouldPass() {
        TemplateValidator validator = new TemplateValidator();
        String htmlContent = """
            <html>
                <body>
                    <p>${data.firma}</p>
                </body>
            </html>
        """;

        String json = """
            {
              "atributos": {
                "tipo": "HTML"
              },
              "data": {
                "comuna": "Santiago"
              }
            }
        """;

        assertThatCode(() -> validator.validateJsonTemplateFieldsOrThrow(htmlContent, json))
                .doesNotThrowAnyException();
    }
}