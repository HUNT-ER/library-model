package com.boldyrev.library.exception_handling;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class ErrorResponse {

    private String message;

    private LocalDateTime timestamp;
}
