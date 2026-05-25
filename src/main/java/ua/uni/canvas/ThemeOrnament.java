package ua.uni.canvas;

import ua.uni.controlers.Mouse;
import ua.uni.dto.FileDto;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;

public final class ThemeOrnament extends JPanel {
    private final Canvas canvas = new Canvas();
    private final CanvasPaint painter = new CanvasPaint();

    public ThemeOrnament() {
        setBackground(Color.WHITE);
        updatePreferredSize();
        installMouseHandlers();
        loadDefaultPattern();
    }

    private void installMouseHandlers() {
        Mouse mouse = new Mouse();
        MouseAdapter adapter = mouse.createPainter(canvas, coords -> {
            canvas.paintWithSymmetry(coords[0], coords[1], canvas.getSelectedColor());
            repaint();
        });
        addMouseListener(adapter);
        addMouseMotionListener(adapter);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        painter.drawGrid(g2, canvas);
        painter.drawCanvas(g2, canvas);
    }

    public void paintAt(int col, int row, Color color) {
        canvas.paintAt(col, row, color);
        repaint();
    }

    public void resizeGrid(int newCols, int newRows) {
        canvas.resizeGrid(newCols, newRows);
        canvas.normalizeSelectionAfterResize();
        updatePreferredSize();
        revalidate();
        repaint();
    }

    private void updatePreferredSize() {
        int w = canvas.getCols() * canvas.getCell() + canvas.getMargin() * 2;
        int h = canvas.getRows() * canvas.getCell() + canvas.getMargin() * 2;
        setPreferredSize(new Dimension(w, h));
    }

    public void clearCanvas() {
        canvas.clearCanvas();
    }

    public Color getSelectedColor() {
        return canvas.getSelectedColor();
    }

    public void setSelectedColor(Color color) {
        canvas.setSelectedColor(color);
    }

    public FileDto toDto() {
        return canvas.toDto();
    }

    public void applyDto(FileDto dto) {
        canvas.applyDto(dto);
        canvas.normalizeSelectionAfterResize();
        updatePreferredSize();
        revalidate();
        repaint();
    }

    public void loadDefaultPattern() {
        clearCanvas();
        loadHardcodedPattern();
        repaint();
    }

    private void loadHardcodedPattern() {
        for (int c = 0; c < canvas.getCols(); c++) {
            canvas.put(c, 4, Color.BLACK);
            canvas.put(c, 25, Color.BLACK);
        }
        for (int c = 2; c < canvas.getCols() - 2; c += 4) {
            canvas.put(c, 2, Color.RED);
            canvas.put(c + 1, 2, Color.RED);
            canvas.put(c, 27, Color.RED);
            canvas.put(c + 1, 27, Color.RED);
        }
        for (int c = 1; c < canvas.getCols() - 1; c += 2) {
            canvas.put(c, 3, Color.BLACK);
            canvas.put(c, 26, Color.BLACK);
        }

        int[][] redCells = {
                {19, 13}, {20, 13},
                {18, 14}, {19, 14}, {20, 14}, {21, 14},
                {17, 15}, {18, 15}, {19, 15}, {20, 15}, {21, 15}, {22, 15},
                {16, 16}, {17, 16}, {18, 16}, {19, 16}, {20, 16}, {21, 16}, {22, 16}, {23, 16},
                {17, 17}, {18, 17}, {19, 17}, {20, 17}, {21, 17}, {22, 17},
                {18, 18}, {19, 18}, {20, 18}, {21, 18},
                {19, 19}, {20, 19},
                {17, 9}, {18, 9}, {19, 9}, {20, 9}, {21, 9}, {22, 9},
                {18, 10}, {19, 10}, {20, 10}, {21, 10},
                {12, 16}, {13, 16}, {14, 16},
                {13, 17}, {14, 17}, {15, 17},
                {14, 18}, {15, 18},
                {25, 16}, {26, 16}, {27, 16},
                {24, 17}, {25, 17}, {26, 17},
                {24, 18}, {25, 18},
                {13, 21}, {14, 21}, {15, 21},
                {14, 22}, {15, 22},
                {25, 21}, {26, 21}, {27, 21},
                {24, 22}, {25, 22}
        };
        canvas.setCells(redCells, Color.RED);

        for (int i = 0; i < 8; i++) {
            canvas.put(16 + i, 11 + i, Color.BLACK);
            canvas.put(23 - i, 11 + i, Color.BLACK);
        }
        canvas.put(19, 16, Color.BLACK);
        canvas.put(20, 16, Color.BLACK);
        canvas.put(19, 17, Color.BLACK);
        canvas.put(20, 17, Color.BLACK);

        drawStar(19, 6);
        drawStar(19, 23);
        drawStar(7, 15);
        drawStar(32, 15);

        drawSmallFlower(5, 8);
        drawSmallFlower(32, 8);
        drawSmallFlower(5, 20);
        drawSmallFlower(32, 20);
    }

    private void drawSmallFlower(int x, int y) {
        canvas.put(x, y, Color.RED);
        canvas.put(x + 1, y, Color.RED);
        canvas.put(x, y + 1, Color.RED);
        canvas.put(x + 1, y + 1, Color.RED);
        canvas.put(x - 1, y + 2, Color.RED);
        canvas.put(x + 2, y + 2, Color.RED);
        canvas.put(x, y + 3, Color.RED);
        canvas.put(x + 1, y + 3, Color.RED);
        canvas.put(x - 1, y + 1, Color.BLACK);
        canvas.put(x + 2, y + 1, Color.BLACK);
        canvas.put(x, y + 4, Color.BLACK);
        canvas.put(x + 1, y + 4, Color.BLACK);
    }

    private void drawStar(int x, int y) {
        canvas.put(x, y, Color.BLACK);
        canvas.put(x + 1, y, Color.BLACK);
        canvas.put(x, y + 1, Color.BLACK);
        canvas.put(x + 1, y + 1, Color.BLACK);
        canvas.put(x - 1, y + 1, Color.BLACK);
        canvas.put(x + 2, y + 1, Color.BLACK);
        canvas.put(x, y - 1, Color.BLACK);
        canvas.put(x + 1, y - 1, Color.BLACK);
        canvas.put(x, y + 2, Color.BLACK);
        canvas.put(x + 1, y + 2, Color.BLACK);
    }

    public void resizeCell(int newCellSize) {
        canvas.resizeCell(newCellSize);
        updatePreferredSize();
        revalidate();
        repaint();
    }

    public SYMMETRY getSymmetryMode() {
        return canvas.getSymmetry();
    }

    public void setSymmetryMode(SYMMETRY symmetry) {
        canvas.setSymmetry(symmetry);
    }

    public void setShowGrid(boolean showGrid) {
        canvas.setShowGrid(showGrid);
    }

    public void toggleShowGrid() {
        canvas.setShowGrid(!canvas.isShowGrid());
    }

    public boolean isSelectionMode() {
        return canvas.isSelectionMode();
    }

    public void enableSelectionMode() {
        canvas.enableSelectionMode();
        repaint();
    }

    public void disableSelectionMode() {
        canvas.disableSelectionMode();
        repaint();
    }

    public void clearSelection() {
        canvas.clearSelection();
        repaint();
    }

    public Rectangle getSelectionRect() {
        return canvas.getSelectionRect();
    }

    public boolean hasSelection() {
        return canvas.hasSelection();
    }

    public boolean applyDuplicationFromSelection() {
        boolean applied = canvas.applyDuplicationFromSelection(canvas.getSymmetry());
        if (applied) {
            repaint();
        }
        return applied;
    }
}
