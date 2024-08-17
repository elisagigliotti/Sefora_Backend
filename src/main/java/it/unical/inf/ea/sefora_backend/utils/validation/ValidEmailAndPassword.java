package it.unical.inf.ea.sefora_backend.utils.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {EmailAndPasswordValidator.class})
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEmailAndPassword {

    String message() default "Invalid email or password";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean validateEmail() default true;

    boolean validatePassword() default true;
}