package cl.bch.cloud.ms.fglosas.mantenedor.utils.validators.impl;

import cl.bch.cloud.ms.fglosas.mantenedor.utils.validators.ValidRut;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;
@Component
@NoArgsConstructor
public class RutValidator implements ConstraintValidator<ValidRut, String> {

    @Value("${validations.rut.pattern.regexp}")
    private  String rutRegex;

    @Override
    public boolean isValid(String rut, ConstraintValidatorContext context) {
        final Pattern rutPattern = Pattern.compile(rutRegex);
        return !(StringUtils.isEmpty(rut) || !rutPattern.matcher(rut).matches());
    }
}
