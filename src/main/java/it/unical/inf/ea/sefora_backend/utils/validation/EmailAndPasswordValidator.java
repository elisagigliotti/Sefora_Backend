package it.unical.inf.ea.sefora_backend.utils.validation;


import it.unical.inf.ea.sefora_backend.dto.UserDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmailAndPasswordValidator implements ConstraintValidator<ValidEmailAndPassword, UserDto> {

    private boolean validateEmail;
    private boolean validatePassword;

    @Override
    public void initialize(ValidEmailAndPassword constraintAnnotation) {
        this.validateEmail = constraintAnnotation.validateEmail();
        this.validatePassword = constraintAnnotation.validatePassword();
    }

    @Override
    public boolean isValid(UserDto userDto, ConstraintValidatorContext context) {
        boolean valid = true;

        if (validateEmail) {
            valid = isValidEmail(userDto.getEmail());
            if (!valid) {
                context.buildConstraintViolationWithTemplate("Invalid email format")
                        .addPropertyNode("email")
                        .addConstraintViolation();
            }
        }

        if (validatePassword) {
            boolean passwordValid = isValidPassword(userDto.getPassword());
            valid = valid && passwordValid;
            if (!passwordValid) {
                context.buildConstraintViolationWithTemplate("Invalid password format")
                        .addPropertyNode("password")
                        .addConstraintViolation();
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