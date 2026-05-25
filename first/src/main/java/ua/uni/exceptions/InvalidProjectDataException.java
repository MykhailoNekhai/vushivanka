package ua.uni.exceptions;

public class InvalidProjectDataException extends IllegalArgumentException {
    public InvalidProjectDataException(String message) {
        super(message);
    }
}
