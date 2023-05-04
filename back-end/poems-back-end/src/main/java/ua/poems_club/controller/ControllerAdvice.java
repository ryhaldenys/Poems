package ua.poems_club.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ua.poems_club.dto.exception.ErrorResponse;
import ua.poems_club.exception.*;
import ua.poems_club.security.exception.JwtAuthenticationException;

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

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException e){
        return ResponseEntity.status(FORBIDDEN)
                .body(new ErrorResponse("Invalid email/password combination"));
    }

    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<?> handleJwtAuthenticationException(JwtAuthenticationException e){
        return ResponseEntity.status(FORBIDDEN)
                .body(new ErrorResponse("JWT token is expired or invalid"));
    }

    @ExceptionHandler(ImageNotFoundException.class)
    public ResponseEntity<?> handleImageNotFoundException(ImageNotFoundException e){
        return ResponseEntity.status(NOT_FOUND)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(InvalidImageException.class)
    public ResponseEntity<?> handleInvalidImageException(InvalidImageException e){
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
    }
}
