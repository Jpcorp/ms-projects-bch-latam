package cl.bch.cloud.ms.fglosas.mantenedor.utils.validators.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RutValidatorTest {

    private RutValidator validator;

    @BeforeEach
    public void setUp() {
        validator = new RutValidator();

        String testRegex = "^(\\d{1,9}-[\\dkK])$"; //^(\\d{1,9}-[\\dkK])$  ||  ^\d{7,8}-[\dkK]$

        try {
            var field = RutValidator.class.getDeclaredField("rutRegex");
            field.setAccessible(true);
            field.set(validator, testRegex);
        } catch (Exception e) {
            fail("No se pudo configurar el campo rutRegex: " + e.getMessage());
        }
    }

    @Test
    public void testValidRut() {
        assertTrue(validator.isValid("12345678-9", null));
        assertTrue(validator.isValid("16743635-8", null));
    }

    @Test
    public void testInvalidRut() {
        assertFalse(validator.isValid("123456789", null)); // sin guion
        assertFalse(validator.isValid("abc-def", null));   // caracteres inválidos
        assertFalse(validator.isValid("", null));          // vacío
        assertFalse(validator.isValid(null, null));        // nulo
    }
}