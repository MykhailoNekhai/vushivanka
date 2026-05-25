package ua.uni.exceptions;

public class InvalidCellSizeException extends IllegalArgumentException {
    public InvalidCellSizeException(int cell) {
        super("Cell size must be positive. Provided: cell=" + cell);
    }
}
