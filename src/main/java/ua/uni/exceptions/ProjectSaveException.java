package ua.uni.exceptions;

import java.nio.file.Path;

public class ProjectSaveException extends RuntimeException {
    public ProjectSaveException(Path path, Throwable cause) {
        super("Failed to save project to: " + path, cause);
    }
}
