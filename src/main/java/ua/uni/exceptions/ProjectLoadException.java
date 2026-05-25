package ua.uni.exceptions;

import java.nio.file.Path;

public class ProjectLoadException extends RuntimeException {
    public ProjectLoadException(Path path, Throwable cause) {
        super("Failed to load project from: " + path, cause);
    }
}
