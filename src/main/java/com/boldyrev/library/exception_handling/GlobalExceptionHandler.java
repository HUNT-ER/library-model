package com.boldyrev.library.exception_handling;

import com.boldyrev.library.exceptions.DataNotFoundException;
import com.boldyrev.library.exceptions.EntityNotFoundException;
import com.boldyrev.library.exceptions.ValidationException;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({DataNotFoundException.class, EntityNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFoundException(RuntimeException e) {
        log.debug("Can't find data by request. Message: {}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), LocalDateTime.now()),
            HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ValidationException.class})
    public ResponseEntity<ErrorResponse> handleException(ValidationException e) {
        log.debug("Validation is failed, Message: {}", e.getMessage());
        return ResponseEntity.badRequest()
            .body(new ErrorResponse(e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleException(DataIntegrityViolationException e) {
        log.debug(e.getMessage());
        StringBuilder builder = new StringBuilder("Data has errors: One or more ");
        if (e.getMessage().contains("\"t_authors\"")) {
            builder.append("authors");
        } else if (e.getMessage().contains("\"t_books\"")) {
            builder.append("books");
        } else if (e.getMessage().contains("\"t_books_isbn_key\"")) {
            builder.append("ISBN");
        }

        builder.append(" is incorrect. Check id's and other values");
        return ResponseEntity.badRequest()
            .body(new ErrorResponse(builder.toString(), LocalDateTime.now()));
    }
}
