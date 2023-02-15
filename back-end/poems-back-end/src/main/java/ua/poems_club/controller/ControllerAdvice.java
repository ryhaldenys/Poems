package ua.poems_club.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ua.poems_club.dto.exception.ErrorResponse;
import ua.poems_club.exception.AuthorAlreadyExist;
import ua.poems_club.exception.IncorrectAuthorDetailsException;
import ua.poems_club.exception.NotFoundException;

import static org.springframework.http.HttpStatus.*;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(NotFoundException e){
        return ResponseEntity.status(NOT_FOUND)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(AuthorAlreadyExist.class)
    public ResponseEntity<?> handleAuthorAlreadyExist(AuthorAlreadyExist e){
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(IncorrectAuthorDetailsException.class)
    public ResponseEntity<?> handleIncorrectAuthorDetails(IncorrectAuthorDetailsException e){
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
    }
}
