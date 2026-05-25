package ua.uni.canvas;

import ua.uni.dto.FileDto;
import ua.uni.exceptions.InvalidCellSizeException;
import ua.uni.exceptions.InvalidGridSizeException;

import java.awt.*;

public class Canvas {
    private int cols = 40;
    private int rows = 30;
    private int cell = 18;
    private final int margin = 20;
    private Color[][] pixels = new Color[rows][cols];
    private Color selectedColor = Color.RED;
    private SYMMETRY symmetry = SYMMETRY.NONE;
    private boolean showGrid = true;
    private boolean selectionMode = false;
    private Point selectionStart = null;
    private Point selectionEnd = null;
    private Rectangle selectionRect = null;

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    public int getCell() {
        return cell;
    }

    public int getMargin() {
        return margin;
    }

    public Color[][] getPixels() {
        return pixels;
    }

    public Color getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(Color color) {
        if (color != null) {
            selectedColor = color;
        }
    }

    public void paintAt(int col, int row, Color color) {
        if (!isInside(col, row)) {
            return;
        }
        pixels[row][col] = color;
    }

    public void clearCanvas() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                pixels[r][c] = null;
            }
        }
        clearSelection();
    }

    public void resizeGrid(int newCols, int newRows) {
        if (newCols <= 0 || newRows <= 0) {
            throw new InvalidGridSizeException(newCols, newRows);
        }
        cols = newCols;
        rows = newRows;
        pixels = new Color[newRows][newCols];
    }

    public void resizeCell(int newCellSize) {
        if (newCellSize <= 0) {
            throw new InvalidCellSizeException(newCellSize);
        }
        cell = newCellSize;
    }

    public void put(int col, int row, Color color) {
        if (isInside(col, row)) {
            pixels[row][col] = color;
        }
    }

    public void setCells(int[][] cells, Color color) {
        for (int[] p : cells) {
            put(p[0], p[1], color);
        }
    }

    public FileDto toDto() {
        String[][] dtoPixels = new String[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Color color = pixels[r][c];
                dtoPixels[r][c] = color == null ? null : toHex(color);
            }
        }
        return new FileDto(1, rows, cols, cell, toHex(selectedColor), showGrid, dtoPixels);
    }

    public void applyDto(FileDto dto) {
        if (dto == null || dto.getPixels() == null) {
            return;
        }
        resizeGrid(dto.getCols(), dto.getRows());
        resizeCell(dto.getCellSize());
        //clearCanvas();
        String[][] dtoPixels = dto.getPixels();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                pixels[r][c] = fromHex(dtoPixels[r][c]);
            }
        }
        Color loadedSelected = fromHex(dto.getSelectedColorHex());
        if (loadedSelected != null) {
            selectedColor = loadedSelected;
        }
        showGrid = dto.isShowGrid();
    }
    public void paintWithSymmetry(int col, int row, Color color) {
        if (!isInside(col, row)) {
            return;
        }
        pixels[row][col] = color;

        if (symmetry == SYMMETRY.HORIZONTAL) {
            pixels[rows - row - 1][col] = color;
        } else if (symmetry == SYMMETRY.VERTICAL) {
            pixels[row][cols - col - 1] = color;
        } else if (symmetry == SYMMETRY.BOTH) {
            int mc = cols - 1 - col;
            int mr = rows - 1 - row;

            pixels[row][col] = color;
            pixels[row][mc] = color;
            pixels[mr][col] = color;
            pixels[mr][mc] = color;
        }
    }

    private boolean isInside(int col, int row) {
        return col >= 0 && col < cols && row >= 0 && row < rows;
    }

    private String toHex(Color color) {
        return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
    }

    private Color fromHex(String hex) {
        if (hex == null || hex.isBlank()) {
            return null;
        }
        try {
            return Color.decode(hex);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
    public SYMMETRY getSymmetry() {
        return symmetry;
    }
    public void setSymmetry(SYMMETRY symmetry) {
        this.symmetry = symmetry;
    }
    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
    }
    public boolean isShowGrid() {
        return showGrid;
    }
    public boolean isSelectionMode() {
        return selectionMode;
    }
    public void enableSelectionMode() {
        selectionMode = true;
        selectionStart = null;
        selectionEnd = null;

    }
    public void disableSelectionMode() {
        selectionMode = false;
    }
    public void clearSelection() {
        selectionStart = null;
        selectionEnd = null;
        selectionRect = null;
    }
    public void setSelectionStart(Point start) {
        selectionStart = start;
    }
    public void setSelectionEnd(Point end) {
        selectionEnd = end;
    }
    public void setSelectionRect(Rectangle rect) {
        selectionRect = rect;
    }
    public Point getSelectionStart() {
        return selectionStart;
    }
    public Point getSelectionEnd() {
        return selectionEnd;
    }
    public Rectangle getSelectionRect() {
        return selectionRect;
    }

    public boolean hasSelection() {
        return selectionRect != null;
    }

    public int[] toCell(int x, int y) {
        int col = (x - margin) / cell;
        int row = (y - margin) / cell;
        if (!isInside(col, row)) {
            return null;
        }
        return new int[]{col, row};
    }

    public int[] toCellClamped(int x, int y) {
        int col = (x - margin) / cell;
        int row = (y - margin) / cell;
        col = Math.max(0, Math.min(cols - 1, col));
        row = Math.max(0, Math.min(rows - 1, row));
        return new int[]{col, row};
    }

    public Rectangle buildSelectionRect(Point start, Point end) {
        if (start == null || end == null) {
            return null;
        }
        int left = Math.min(start.x, end.x);
        int top = Math.min(start.y, end.y);
        int width = Math.abs(start.x - end.x) + 1;
        int height = Math.abs(start.y - end.y) + 1;
        return new Rectangle(left, top, width, height);
    }

    public void normalizeSelectionAfterResize() {
        if (selectionRect == null) {
            return;
        }
        int x = Math.max(0, Math.min(cols - 1, selectionRect.x));
        int y = Math.max(0, Math.min(rows - 1, selectionRect.y));
        int maxW = cols - x;
        int maxH = rows - y;
        int w = Math.max(1, Math.min(selectionRect.width, maxW));
        int h = Math.max(1, Math.min(selectionRect.height, maxH));
        selectionRect = new Rectangle(x, y, w, h);

        selectionStart = new Point(selectionRect.x, selectionRect.y);
        selectionEnd = new Point(selectionRect.x + selectionRect.width - 1, selectionRect.y + selectionRect.height - 1);
    }
    public boolean applyDuplicationFromSelection(SYMMETRY mode) {
        if (!hasSelection()) {
            return false;
        }
        Rectangle rect = selectionRect;
        if (rect.width <= 0 || rect.height <= 0) {
            return false;
        }

        Color[][] fragment = extractFragment(rect);
        duplicateToWholeGrid(fragment, rect.width, rect.height, mode == null ? SYMMETRY.NONE : mode);
        return true;
    }

    private Color[][] extractFragment(Rectangle rect) {
        Color[][] fragment = new Color[rect.height][rect.width];
        for (int r = 0; r < rect.height; r++) {
            for (int c = 0; c < rect.width; c++) {
                fragment[r][c] = pixels[rect.y + r][rect.x + c];
            }
        }
        return fragment;
    }

    private void duplicateToWholeGrid(Color[][] fragment, int fragmentWidth, int fragmentHeight, SYMMETRY mode) {
        for (int targetRow = 0; targetRow < rows; targetRow++) {
            for (int targetCol = 0; targetCol < cols; targetCol++) {
                int blockX = targetCol / fragmentWidth;
                int blockY = targetRow / fragmentHeight;

                int localCol = targetCol % fragmentWidth;
                int localRow = targetRow % fragmentHeight;

                int[] mapped = mapTargetToFragmentCell(localCol, localRow, blockX, blockY, fragmentWidth, fragmentHeight, mode);
                pixels[targetRow][targetCol] = fragment[mapped[1]][mapped[0]];
            }
        }
    }

    private int[] mapTargetToFragmentCell(int localCol, int localRow, int blockX, int blockY,
                                          int fragmentWidth, int fragmentHeight, SYMMETRY mode) {
        int mappedCol = localCol;
        int mappedRow = localRow;

        if (mode == SYMMETRY.HORIZONTAL || mode == SYMMETRY.BOTH) {
            if (blockY % 2 == 1) {
                mappedRow = fragmentHeight - 1 - mappedRow;
            }
        }
        if (mode == SYMMETRY.VERTICAL || mode == SYMMETRY.BOTH) {
            if (blockX % 2 == 1) {
                mappedCol = fragmentWidth - 1 - mappedCol;
            }
        }
        return new int[]{mappedCol, mappedRow};
    }

}
