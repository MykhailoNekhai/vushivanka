package ua.uni.exceptions;

public class InvalidGridSizeException extends IllegalArgumentException {
    public InvalidGridSizeException(int cols, int rows) {
        super("Grid size must be positive. Provided: cols=" + cols + ", rows=" + rows);
    }
}
