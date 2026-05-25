package ua.uni.canvas;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class CanvasPaint {
    public void drawGrid(Graphics2D g, Canvas canvas) {
        int margin = canvas.getMargin();
        int cell = canvas.getCell();
        int cols = canvas.getCols();
        int rows = canvas.getRows();

        g.setColor(Color.WHITE);
        g.fillRect(margin, margin, cols * cell, rows * cell);

        if (canvas.isShowGrid()) {
            g.setColor(new Color(205, 205, 205));
            for (int r = 0; r <= rows; r++) {
                int y = margin + r * cell;
                g.drawLine(margin, y, margin + cols * cell, y);
            }


            for (int c = 0; c <= cols; c++) {
                int x = margin + c * cell;
                g.drawLine(x, margin, x, margin + rows * cell);
            }
        }else {
            g.setColor(Color.BLACK);
            g.drawRect(margin, margin, cols * cell, rows * cell);
        }
    }

    public void drawCanvas(Graphics2D g2, Canvas canvas) {
        Color[][] pixels = canvas.getPixels();
        for (int r = 0; r < canvas.getRows(); r++) {
            for (int c = 0; c < canvas.getCols(); c++) {
                Color color = pixels[r][c];
                if (color != null) {
                    drawCell(g2, canvas, c, r, color);
                }
            }
        }
        drawSelection(g2, canvas);
    }

    private void drawSelection(Graphics2D g2, Canvas canvas) {
        Rectangle selection = canvas.getSelectionRect();
        if (selection == null) {
            return;
        }

        int x = canvas.getMargin() + selection.x * canvas.getCell();
        int y = canvas.getMargin() + selection.y * canvas.getCell();
        int w = selection.width * canvas.getCell();
        int h = selection.height * canvas.getCell();

        g2.setColor(new Color(50, 120, 220, 60));
        g2.fillRect(x, y, w, h);
        g2.setColor(new Color(35, 85, 170));
        g2.drawRect(x, y, w, h);
    }

    private void drawCell(Graphics2D g, Canvas canvas, int col, int row, Color color) {
        int x = canvas.getMargin() + col * canvas.getCell();
        int y = canvas.getMargin() + row * canvas.getCell();
        g.setColor(color);
        g.fillRect(x + 1, y + 1, canvas.getCell() - 1, canvas.getCell() - 1);
    }
}
