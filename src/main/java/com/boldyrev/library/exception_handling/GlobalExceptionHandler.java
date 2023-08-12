package com.boldyrev.library.exception_handling;

import com.boldyrev.library.exceptions.DataNotFoundException;
import com.boldyrev.library.exceptions.EntityNotFoundException;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
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
}
