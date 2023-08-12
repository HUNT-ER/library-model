package com.boldyrev.library.util.validators;

import com.boldyrev.library.exceptions.DataNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class PageValidator {

    public void validate(Page<?> page, String requestParameter) {
        if (page.isEmpty()) {
            StringBuilder message = new StringBuilder("Data");

            if (requestParameter != null) {
                message.append(" by parameter '").append(requestParameter).append("'");
            }
            message.append(" not found");

            throw new DataNotFoundException(message.toString());
        }
    }
}
