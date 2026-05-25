package ua.uni.controlers;

import ua.uni.canvas.Canvas;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class Mouse {
    public MouseAdapter createPainter(Canvas canvas, Consumer<int[]> onPaintCell) {
        return new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (canvas.isSelectionMode()) {
                    handleSelectionPressed(canvas, e);
                    return;
                }
                int[] coords = coordinatesForMouse(e, canvas);
                if (coords != null) {
                    onPaintCell.accept(coords);
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (canvas.isSelectionMode()) {
                    handleSelectionDragged(canvas, e);
                    return;
                }
                int[] coords = coordinatesForMouse(e, canvas);
                if (coords != null) {
                    onPaintCell.accept(coords);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (canvas.isSelectionMode()) {
                    handleSelectionReleased(canvas, e);
                }
            }
        };
    }

    private void handleSelectionPressed(Canvas canvas, MouseEvent e) {
        int[] cell = canvas.toCell(e.getX(), e.getY());
        if (cell == null) {
            canvas.disableSelectionMode();
            return;
        }
        Point start = new Point(cell[0], cell[1]);
        canvas.setSelectionStart(start);
        canvas.setSelectionEnd(start);
        canvas.setSelectionRect(new Rectangle(start.x, start.y, 1, 1));
        e.getComponent().repaint();
    }

    private void handleSelectionDragged(Canvas canvas, MouseEvent e) {
        if (canvas.getSelectionStart() == null) {
            return;
        }
        int[] cell = canvas.toCellClamped(e.getX(), e.getY());
        Point end = new Point(cell[0], cell[1]);
        canvas.setSelectionEnd(end);
        canvas.setSelectionRect(canvas.buildSelectionRect(canvas.getSelectionStart(), end));
        e.getComponent().repaint();
    }

    private void handleSelectionReleased(Canvas canvas, MouseEvent e) {
        if (canvas.getSelectionStart() == null) {
            return;
        }
        int[] cell = canvas.toCellClamped(e.getX(), e.getY());
        Point end = new Point(cell[0], cell[1]);
        canvas.setSelectionEnd(end);
        canvas.setSelectionRect(canvas.buildSelectionRect(canvas.getSelectionStart(), end));
        canvas.disableSelectionMode();
        e.getComponent().repaint();
    }

    private int[] coordinatesForMouse(MouseEvent e, Canvas canvas) {
        return canvas.toCell(e.getX(), e.getY());
    }
}
