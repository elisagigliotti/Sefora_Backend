package it.unical.inf.ea.sefora_backend.utils.validation;

import it.unical.inf.ea.sefora_backend.utils.auth.RegisterRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmailAndPasswordValidator implements ConstraintValidator<ValidEmailAndPassword, RegisterRequest> {

    private boolean checkEmail;
    private boolean checkPassword;

    @Override
    public void initialize(ValidEmailAndPassword constraintAnnotation) {
        this.checkEmail = constraintAnnotation.checkEmail();
        this.checkPassword = constraintAnnotation.checkPassword();
    }

    @Override
    public boolean isValid(RegisterRequest registerRequest, ConstraintValidatorContext context) {
        boolean valid = true;

        if (checkEmail) {
            if (!isValidEmail(registerRequest.getEmail())) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Invalid email format")
                        .addPropertyNode("email")
                        .addConstraintViolation();
                valid = false;
            }
        }

        if (checkPassword) {
            if (!isValidPassword(registerRequest.getPassword())) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Invalid password format")
                        .addPropertyNode("password")
                        .addConstraintViolation();
                valid = false;
            }
        }

        return valid;
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 8 && password.length() <= 50
                && password.matches(".*[A-Z].*") // at least one uppercase letter
                && password.matches(".*[a-z].*") // at least one lowercase letter
                && password.matches(".*\\d.*")   // at least one digit
                && password.matches(".*[@#$%^&+=!].*"); // at least one special character
    }
}
