package com.boldyrev.library.util.validators.entity_validators;

import com.boldyrev.library.dto.BookDTO;
import com.boldyrev.library.exceptions.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class BookValidator extends CustomValidator {

    public BookValidator() {
        super("Book");
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(BookDTO.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

        //todo add isbn validation

        if (errors.hasErrors()) {
            throw new ValidationException(getErrors(errors));
        }
    }
}
