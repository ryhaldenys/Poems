package ua.poems_club.exception;

public class IncorrectAuthorDetailsException extends RuntimeException {
    public IncorrectAuthorDetailsException(String message) {
        super(message);
    }
}
