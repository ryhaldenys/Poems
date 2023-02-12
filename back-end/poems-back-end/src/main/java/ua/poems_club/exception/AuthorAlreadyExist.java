package ua.poems_club.exception;

public class AuthorAlreadyExist extends RuntimeException{
    public AuthorAlreadyExist(String message) {
        super(message);
    }
}
