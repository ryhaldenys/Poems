package ua.poems_club.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ua.poems_club.dto.ErrorResponse;
import ua.poems_club.exception.NotFoundException;

import static org.springframework.http.HttpStatus.*;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(NotFoundException e){
        return ResponseEntity.status(NOT_FOUND)
                .body(new ErrorResponse(e.getMessage()));
    }
}
