package cl.bch.cloud.ms.fglosas.mantenedor.utils.validators;

import cl.bch.cloud.ms.fglosas.mantenedor.utils.validators.impl.RutValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;


@Documented
@Constraint(validatedBy = RutValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRut {
    String message() default "RUT inválido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}