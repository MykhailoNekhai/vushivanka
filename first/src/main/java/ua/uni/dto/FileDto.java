package ua.uni.dto;

public class FileDto {
    private int formatVersion;
    private int rows;
    private int cols;
    private int cellSize;
    private String selectedColorHex;
    private boolean showGrid;
    private String[][] pixels;

    public FileDto() {
    }

    public FileDto(int formatVersion, int rows, int cols, int cellSize, String selectedColorHex, boolean showGrid, String[][] pixels) {
        this.formatVersion = formatVersion;
        this.rows = rows;
        this.cols = cols;
        this.cellSize = cellSize;
        this.selectedColorHex = selectedColorHex;
        this.showGrid = showGrid;
        this.pixels = pixels;
    }

    public int getFormatVersion() {
        return formatVersion;
    }

    public void setFormatVersion(int formatVersion) {
        this.formatVersion = formatVersion;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public int getCellSize() {
        return cellSize;
    }

    public void setCellSize(int cellSize) {
        this.cellSize = cellSize;
    }

    public String getSelectedColorHex() {
        return selectedColorHex;
    }

    public void setSelectedColorHex(String selectedColorHex) {
        this.selectedColorHex = selectedColorHex;
    }

    public boolean isShowGrid() {
        return showGrid;
    }

    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
    }

    public String[][] getPixels() {
        return pixels;
    }

    public void setPixels(String[][] pixels) {
        this.pixels = pixels;
    }

}
