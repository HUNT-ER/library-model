package com.boldyrev.library.util.validators;

import com.boldyrev.library.exceptions.DataNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class PageValidator {

    public void validate(Page<?> page, String[] requestParameters) {
        if (page.isEmpty()) {
            StringBuilder message = new StringBuilder("Data");

            if (requestParameters != null) {
                message.append(" by parameters ").append(showNotEmptyParameters(requestParameters));
            }
            message.append(" not found");

            throw new DataNotFoundException(message.toString());
        }
    }

    public String showNotEmptyParameters(String[] requestParameters) {
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < requestParameters.length; i++) {
            if (!"".equals(requestParameters[i])) {
                builder.append(requestParameters[i])
                    .append(";");
            }
        }
        builder.append("]");
        return builder.toString();
    }
}
