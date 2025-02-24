package com.omdb.exception;

import com.omdb.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    /** Handle ResourceNotFoundException */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundRecord(ResourceNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /** Handle DuplicatedRecordException */
    @ExceptionHandler(DaplicateRecoredException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateRecord(DaplicateRecoredException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
}
