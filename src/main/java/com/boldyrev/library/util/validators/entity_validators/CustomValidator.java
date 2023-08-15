package com.boldyrev.library.util.validators.entity_validators;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public abstract class CustomValidator implements Validator {

    protected final String entityClass;

    protected CustomValidator(String entityClass) {
        this.entityClass = entityClass;
    }

    public String getErrors(Errors errors) {
        StringBuilder builder = new StringBuilder();

        builder.append(entityClass)
            .append(" not saved: ");

        if (errors.hasFieldErrors()) {
            errors.getFieldErrors().forEach(e ->
                builder.append(e.getField())
                    .append(" - ")
                    .append(e.getDefaultMessage())
                    .append("; "));
        } else {
            builder.append(errors.getGlobalError().getDefaultMessage());
        }

        return builder.toString();
    }
}
