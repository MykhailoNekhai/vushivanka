package ua.uni.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.uni.dto.FileDto;
import ua.uni.exceptions.InvalidProjectDataException;
import ua.uni.exceptions.ProjectLoadException;
import ua.uni.exceptions.ProjectSaveException;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Component;
import java.io.IOException;
import java.nio.file.Path;

public class InputOutput {
    private static final Logger LOG = LoggerFactory.getLogger(InputOutput.class);
    private final JFileChooser fileChooser = new JFileChooser();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public InputOutput() {
        fileChooser.setFileFilter(new FileNameExtensionFilter("JSON file (*.json)", "json"));
    }

    public void saveFile(Path path, FileDto fileDto) {
        validate(fileDto);
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), fileDto);
        } catch (IOException ex) {
            throw new ProjectSaveException(path, ex);
        }
    }

    public FileDto loadFile(Path path) {
        try {
            FileDto dto = objectMapper.readValue(path.toFile(), FileDto.class);
            validate(dto);
            return dto;
        } catch (InvalidProjectDataException ex) {
            throw ex;
        } catch (IOException ex) {
            throw new ProjectLoadException(path, ex);
        }
    }

    public FileDto chooseAndLoad(Component parent) {
        int result = fileChooser.showOpenDialog(parent);
        if (result != JFileChooser.APPROVE_OPTION) {
            LOG.debug("Open project canceled by user");
            return null;
        }
        Path path = fileChooser.getSelectedFile().toPath();
        try {
            FileDto dto = loadFile(path);
            JOptionPane.showMessageDialog(parent, "File opened successfully.");
            LOG.info("Project opened: {}", path);
            return dto;
        } catch (InvalidProjectDataException | ProjectLoadException ex) {
            LOG.error("Open failed for {}: {}", path, ex.getMessage());
            JOptionPane.showMessageDialog(parent, "Open failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public void chooseAndSave(Component parent, FileDto dto) {
        int result = fileChooser.showSaveDialog(parent);
        if (result != JFileChooser.APPROVE_OPTION) {
            LOG.debug("Save project canceled by user");
            return;
        }
        Path path = fileChooser.getSelectedFile().toPath();
        String filename = path.toString().toLowerCase();
        if (!filename.endsWith(".json")) {
            path = Path.of(path.toString() + ".json");
        }
        try {
            saveFile(path, dto);
            JOptionPane.showMessageDialog(parent, "File saved successfully.");
            LOG.info("Project saved: {}", path);
        } catch (InvalidProjectDataException | ProjectSaveException ex) {
            LOG.error("Save failed for {}: {}", path, ex.getMessage());
            JOptionPane.showMessageDialog(parent, "Save failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void validate(FileDto dto) {
        if (dto == null) {
            throw new InvalidProjectDataException("FileDto must not be null");
        }
        if (dto.getFormatVersion() <= 0) {
            throw new InvalidProjectDataException("formatVersion must be > 0");
        }
        if (dto.getRows() <= 0 || dto.getCols() <= 0) {
            throw new InvalidProjectDataException("rows and cols must be > 0");
        }
        if (dto.getCellSize() <= 0) {
            throw new InvalidProjectDataException("cellSize must be > 0");
        }

        String[][] pixels = dto.getPixels();
        if (pixels == null) {
            throw new InvalidProjectDataException("pixels must not be null");
        }
        if (pixels.length != dto.getRows()) {
            throw new InvalidProjectDataException("pixels row count must match rows");
        }
        for (int r = 0; r < pixels.length; r++) {
            if (pixels[r] == null || pixels[r].length != dto.getCols()) {
                throw new InvalidProjectDataException("pixels column count must match cols for each row");
            }
        }
    }
}
