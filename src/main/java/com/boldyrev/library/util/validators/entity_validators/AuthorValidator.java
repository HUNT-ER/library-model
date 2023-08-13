package com.boldyrev.library.util.validators.entity_validators;

import com.boldyrev.library.dto.AuthorDTO;
import com.boldyrev.library.exceptions.ValidationException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class AuthorValidator extends CustomValidator {

    public AuthorValidator() {
        super("Author");
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(AuthorDTO.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

        if (errors.hasErrors()) {
            throw new ValidationException(getErrors(errors));
        }
    }
}
