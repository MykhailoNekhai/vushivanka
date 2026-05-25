package ua.uni.visual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.uni.canvas.SYMMETRY;
import ua.uni.canvas.ThemeOrnament;
import ua.uni.exceptions.InvalidCellSizeException;
import ua.uni.exceptions.InvalidGridSizeException;
import ua.uni.logging.SwingLogAppender;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Panels {
    private static final Logger LOG = LoggerFactory.getLogger(Panels.class);
    private static final Font UI_FONT = UIManager.getFont("Label.font");
    private static final Font UI_FONT_BOLD = UI_FONT.deriveFont(Font.BOLD);
    private static final Font LOG_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 11);
    private static final int S4 = 4;
    private static final int S8 = 8;
    private static final int S12 = 12;
    private static final int S16 = 16;
    private static final Color PANEL_BORDER = new Color(184, 186, 190);
    private static final Color PRIMARY_BUTTON_BG = new Color(250, 250, 250);
    private static final Color PRIMARY_BUTTON_HOVER = new Color(238, 238, 238);
    private static final Color PRIMARY_BUTTON_FG = new Color(35, 35, 35);
    private static final Color SECONDARY_BUTTON_BG = new Color(240, 240, 240);
    private static final Color SECONDARY_BUTTON_HOVER = new Color(225, 225, 225);
    private static final Color SECONDARY_BUTTON_FG = new Color(30, 30, 30);
    private static final Color INPUT_FOCUS_BORDER = new Color(32, 120, 220);
    private static final LineBorder ACTIVE_BORDER = new LineBorder(Color.BLUE, 3);
    private static final LineBorder INACTIVE_BORDER = new LineBorder(Color.GRAY, 1);
    private final ThemeOrnament canvas = new ThemeOrnament();
    private final Map<Color, String> colorNames = createColorNames();
    private final JLabel colorInfoLabel = new JLabel();
    private final JLabel sizeInfoLabel = new JLabel("Size: 40 x 30");
    private final JLabel cellInfoLabel = new JLabel("Cell: 20px");
    private final JLabel symmetryStatusLabel = new JLabel("Symmetry: none");
    private final JLabel selectionInfoLabel = new JLabel("Selection: none");
    private JToggleButton selectFragmentToggleButton;
    private JSpinner widthSpinner;
    private JSpinner heightSpinner;
    private JSpinner cellSizeSpinner;
    private JCheckBox showGridCheckBox;
    private final JTextPane logPane = new JTextPane();
    private final StyledDocument logDocument = logPane.getStyledDocument();
    private final Consumer<String> uiLogListener = message ->
            SwingUtilities.invokeLater(() -> appendToLogArea(message));
    private boolean selectionUiTrackingInstalled = false;

    public JPanel central() {
        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(new EmptyBorder(S12, S12, S12, S12));
        center.setFont(UI_FONT);

        JScrollPane scroll = new JScrollPane(canvas);
        scroll.setBorder(new LineBorder(PANEL_BORDER));

        center.add(scroll, BorderLayout.CENTER);
        return center;
    }

    public JPanel right() {
        JPanel right = new JPanel();
        right.setPreferredSize(new Dimension(230, 0));
        right.setBorder(new EmptyBorder(S12, 10, S12, S12));
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setOpaque(false);

        right.add(buildLogPanel());
        right.add(Box.createVerticalGlue());
        right.add(createThinSeparator());
        right.add(Box.createVerticalStrut(S8));
        right.add(buildSymmetryPanel());
        right.add(Box.createVerticalStrut(8));
        right.add(buildAutoOrnamentPanel());
        right.add(Box.createVerticalStrut(8));
        right.add(buildCurrentDataPanel());
        installSelectionUiTracking();

        return right;
    }

    private void installSelectionUiTracking() {
        if (selectionUiTrackingInstalled) {
            return;
        }
        MouseAdapter sync = new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (selectFragmentToggleButton != null && !canvas.isSelectionMode()) {
                    selectFragmentToggleButton.setSelected(false);
                    canvas.setCursor(Cursor.getDefaultCursor());
                }
                updateSelectionInfoLabel();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                updateSelectionInfoLabel();
            }
        };
        canvas.addMouseListener(sync);
        canvas.addMouseMotionListener(sync);
        selectionUiTrackingInstalled = true;
    }

    private void styleSymmetryOption(JRadioButton option) {
        option.setOpaque(false);
        option.setBorder(BorderFactory.createEmptyBorder(0, S8, 0, 0));
        option.setAlignmentX(Component.LEFT_ALIGNMENT);
        option.setHorizontalAlignment(SwingConstants.LEFT);
        option.setFont(UI_FONT);
    }

    public JPanel left() {
        JPanel left = new JPanel();
        left.setPreferredSize(new Dimension(230, 0));
        left.setBorder(new EmptyBorder(S12, S12, S12, S12));
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        left.add(buildColorPalettePanel(left));
        left.add(Box.createVerticalStrut(S16));
        left.add(buildGridSettingsPanel());
        left.add(Box.createVerticalGlue());

        return left;
    }

    private JPanel buildSymmetryPanel() {
        JPanel symmetry = new JPanel();
        symmetry.setBorder(createSectionBorder("Symmetry Mode"));
        symmetry.setLayout(new BoxLayout(symmetry, BoxLayout.Y_AXIS));
        symmetry.setAlignmentX(Component.LEFT_ALIGNMENT);

        JRadioButton none = createSymmetryOption("No Symmetry", true);
        JRadioButton horizontal = createSymmetryOption("Horizontal", false);
        JRadioButton vertical = createSymmetryOption("Vertical", false);
        JRadioButton both = createSymmetryOption("Horizontal + Vertical", false);

        ButtonGroup group = new ButtonGroup();
        group.add(none);
        group.add(horizontal);
        group.add(vertical);
        group.add(both);

        none.addActionListener(e -> updateSymmetryMode(SYMMETRY.NONE, "none"));
        horizontal.addActionListener(e -> updateSymmetryMode(SYMMETRY.HORIZONTAL, "horizontal"));
        vertical.addActionListener(e -> updateSymmetryMode(SYMMETRY.VERTICAL, "vertical"));
        both.addActionListener(e -> updateSymmetryMode(SYMMETRY.BOTH, "horizontal + vertical"));

        symmetry.add(Box.createVerticalStrut(S4));
        symmetry.add(none);
        symmetry.add(Box.createVerticalStrut(S8));
        symmetry.add(horizontal);
        symmetry.add(Box.createVerticalStrut(S8));
        symmetry.add(vertical);
        symmetry.add(Box.createVerticalStrut(S8));
        symmetry.add(both);
        symmetry.add(Box.createVerticalStrut(S4));
        return symmetry;
    }

    private JRadioButton createSymmetryOption(String text, boolean selected) {
        JRadioButton option = new JRadioButton(text, selected);
        styleSymmetryOption(option);
        return option;
    }

    private JPanel buildAutoOrnamentPanel() {
        JPanel duplicate = new JPanel(new GridBagLayout());
        duplicate.setBorder(createSectionBorder("Auto Build Ornament"));
        duplicate.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3, 5, 3, 5);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;
        c.gridx = 0;
        c.gridwidth = 2;
        c.weightx = 1;
        duplicate.add(createBuildOrnamentButton(), c);

        c.gridy = 1;
        c.gridx = 0;
        c.gridwidth = 2;
        duplicate.add(createSelectFragmentToggleButton(), c);

        c.gridy = 2;
        c.gridx = 0;
        c.gridwidth = 2;
        duplicate.add(createClearSelectionButton(), c);

        c.gridy = 3;
        c.gridx = 0;
        c.gridwidth = 2;
        selectionInfoLabel.setFont(UI_FONT);
        duplicate.add(selectionInfoLabel, c);
        lockPanelHeightToPreferred(duplicate);
        return duplicate;
    }

    private void lockPanelHeightToPreferred(JPanel panel) {
        Dimension preferred = panel.getPreferredSize();
        int adjustedHeight = (int) Math.ceil(preferred.height * 1.25);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, adjustedHeight));
    }

    private JButton createBuildOrnamentButton() {
        JButton button = new JButton("Build Ornament");
        stylePrimaryButton(button);
        button.addActionListener(e -> {
            if (!canvas.hasSelection()) {
                LOG.warn("Build Ornament requested without selection");
                JOptionPane.showMessageDialog(
                        canvas,
                        "Спочатку виділи фрагмент через Select Fragment",
                        "Немає виділення",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            boolean applied = canvas.applyDuplicationFromSelection();
            if (applied) {
                LOG.info("Ornament duplicated from selected fragment with symmetry {}", canvas.getSymmetryMode());
            } else {
                LOG.warn("Build Ornament failed: invalid selected fragment");
            }
            updateSelectionInfoLabel();
        });
        return button;
    }

    private JToggleButton createSelectFragmentToggleButton() {
        JToggleButton button = new JToggleButton("Select Fragment");
        selectFragmentToggleButton = button;
        stylePrimaryButton(button);
        button.addActionListener(e -> {
            if (button.isSelected()) {
                canvas.enableSelectionMode();
                canvas.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                LOG.info("Fragment selection mode enabled");
            } else {
                canvas.disableSelectionMode();
                canvas.setCursor(Cursor.getDefaultCursor());
                LOG.info("Fragment selection mode disabled");
            }
            updateSelectionInfoLabel();
        });
        return button;
    }

    private JButton createClearSelectionButton() {
        JButton button = new JButton("Clear Selection");
        styleSecondaryButton(button);
        button.addActionListener(e -> {
            canvas.clearSelection();
            updateSelectionInfoLabel();
            LOG.info("Selection cleared");
        });
        return button;
    }

    private JPanel buildCurrentDataPanel() {
        JPanel info = new JPanel();
        info.setBorder(createSectionBorder("Current Data"));
        info.setLayout(new GridLayout(4, 1, 4, 4));
        info.setAlignmentX(Component.LEFT_ALIGNMENT);
        sizeInfoLabel.setFont(UI_FONT);
        colorInfoLabel.setFont(UI_FONT);
        cellInfoLabel.setFont(UI_FONT);
        info.add(sizeInfoLabel);
        info.add(cellInfoLabel);
        updateColorInfoLabel(canvas.getSelectedColor());
        info.add(colorInfoLabel);
        symmetryStatusLabel.setFont(UI_FONT);
        symmetryStatusLabel.setForeground(new Color(35, 35, 35));
        info.add(symmetryStatusLabel);
        return info;
    }

    private JPanel buildLogPanel() {
        JPanel logs = new JPanel(new BorderLayout(0, 6));
        logs.setBorder(createSectionBorder("Logs"));
        logs.setAlignmentX(Component.LEFT_ALIGNMENT);

        logPane.setEditable(false);
        logPane.setFont(LOG_FONT);
        logPane.setBackground(Color.WHITE);

        JScrollPane logScroll = new JScrollPane(logPane);
        logScroll.getViewport().setBackground(Color.WHITE);
        logScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        logScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        logs.add(logScroll, BorderLayout.CENTER);

        SwingLogAppender.addListener(uiLogListener);
        logs.addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                // no-op
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
                SwingLogAppender.removeListener(uiLogListener);
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
                // no-op
            }
        });

        LOG.info("UI initialized");
        LOG.debug("Canvas 40x30 is ready");
        LOG.warn("Autosave is off");
        return logs;
    }

    private JPanel buildColorPalettePanel(JPanel leftParent) {
        JPanel paletteBox = new JPanel(new BorderLayout());
        paletteBox.setBorder(createSectionBorder("Color Palette"));

        JPanel colors = new JPanel(new GridLayout(4, 3, 8, 8));
        colors.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton[] activeButton = new JButton[1];
        JButton otherColorButton = createOtherColorButton(leftParent, activeButton);
        Color[] palette = createPaletteColors();
        for (int i = 0; i < palette.length; i++) {
            JButton colorButton = createColorButton(palette[i], i == 0, activeButton, otherColorButton);
            if (i == 0) {
                activeButton[0] = colorButton;
            }
            colors.add(colorButton);
        }

        paletteBox.add(colors, BorderLayout.CENTER);
        paletteBox.add(otherColorButton, BorderLayout.SOUTH);
        return paletteBox;
    }

    private Color[] createPaletteColors() {
        return new Color[]{
                Color.RED, Color.BLACK, Color.WHITE, Color.YELLOW,
                new Color(0, 70, 180), new Color(0, 140, 60),
                new Color(235, 205, 165), new Color(140, 25, 25), Color.LIGHT_GRAY,
                new Color(255, 140, 0), new Color(128, 0, 128), new Color(32, 178, 170)
        };
    }

    private JButton createColorButton(Color color, boolean isActive, JButton[] activeButtonRef, JButton otherColorButton) {
        JButton colorButton = new JButton();
        colorButton.setBackground(color);
        colorButton.setOpaque(true);
        colorButton.setPreferredSize(new Dimension(46, 46));
        colorButton.setBorder(isActive ? ACTIVE_BORDER : INACTIVE_BORDER);
        colorButton.setFocusPainted(false);
        colorButton.addActionListener(e -> {
            setActiveColorButton(activeButtonRef, colorButton);
            otherColorButton.setBorder(INACTIVE_BORDER);
            canvas.setSelectedColor(colorButton.getBackground());
            updateColorInfoLabel(canvas.getSelectedColor());
            LOG.info("Color changed to {}", toHex(colorButton.getBackground()));
        });
        return colorButton;
    }

    private JButton createOtherColorButton(JPanel leftParent, JButton[] activeButtonRef) {
        JButton otherColor = new JButton("Other Color...");
        otherColor.setBorder(INACTIVE_BORDER);
        styleSecondaryButton(otherColor);
        otherColor.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(leftParent, "Choose Color", Color.RED);
            if (chosen != null) {
                if (activeButtonRef[0] != null) {
                    activeButtonRef[0].setBorder(INACTIVE_BORDER);
                }
                activeButtonRef[0] = null;
                otherColor.setBorder(ACTIVE_BORDER);
                canvas.setSelectedColor(chosen);
                updateColorInfoLabel(canvas.getSelectedColor());
                LOG.info("Custom color selected {}", toHex(chosen));
            }
        });
        return otherColor;
    }

    private JPanel buildGridSettingsPanel() {
        JPanel settings = new JPanel(new GridBagLayout());
        settings.setBorder(createSectionBorder("Grid Settings"));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;

        widthSpinner = createWidthSpinner();
        heightSpinner = createHeightSpinner();
        cellSizeSpinner = createCellSizeSpinner();
        addSetting(settings, c, 0, "Width:", widthSpinner);
        addSetting(settings, c, 1, "Height:", heightSpinner);
        addSetting(settings, c, 2, "Cell:", cellSizeSpinner);

        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        settings.add(createShowGridCheckbox(), c);

        c.gridy = 4;
        settings.add(createApplySizeButton(settings, widthSpinner, heightSpinner,cellSizeSpinner), c);
        settings.setMaximumSize(new Dimension(Integer.MAX_VALUE, settings.getPreferredSize().height));
        return settings;
    }

    private JSpinner createWidthSpinner() {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(40, 10, 100, 1));
        spinner.setPreferredSize(new Dimension(78, 26));
        return spinner;
    }
    private JSpinner createCellSizeSpinner() {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(20, 10, 100, 1));
        spinner.setPreferredSize(new Dimension(78, 26));
        return spinner;
    }

    private JSpinner createHeightSpinner() {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(30, 10, 100, 1));
        spinner.setPreferredSize(new Dimension(78, 26));
        return spinner;
    }

    private JComboBox<String> createCellSizeCombo() {
        JComboBox<String> combo = new JComboBox<>(new String[]{"10", "15", "20", "25", "30"});
        styleInputComponent(combo);
        return combo;
    }

    private JCheckBox createShowGridCheckbox() {
        showGridCheckBox = new JCheckBox("Show Grid", true);
        showGridCheckBox.setFont(UI_FONT);
        showGridCheckBox.addActionListener(e -> {
            canvas.setShowGrid(showGridCheckBox.isSelected());
            canvas.repaint();
            LOG.info("Show Grid set to {}", showGridCheckBox.isSelected());
        });
        return showGridCheckBox;
    }
    public JButton createApplySizeButton(JPanel settings, JSpinner widthSpinner, JSpinner heightSpinner,JSpinner cellSizeSpinner) {
        JButton applySize = new JButton("Apply Size");
        stylePrimaryButton(applySize);
        applySize.addActionListener(e -> {
            try {
                int newCols = (Integer) widthSpinner.getValue();
                int newRows = (Integer) heightSpinner.getValue();
                int newCellSize = (Integer) cellSizeSpinner.getValue();
                canvas.resizeGrid(newCols, newRows);
                canvas.resizeCell(newCellSize);
                sizeInfoLabel.setText("Size: " + newCols + " x " + newRows);
                cellInfoLabel.setText("Cell: " + newCellSize + "px");
                canvas.revalidate();
                canvas.repaint();
                LOG.info("Grid resized to {}x{}, cell={}px", newCols, newRows, newCellSize);
            } catch (InvalidGridSizeException | InvalidCellSizeException ex) {
                LOG.error("Invalid size settings: cols={}, rows={}, cell={}",
                        widthSpinner.getValue(), heightSpinner.getValue(), cellSizeSpinner.getValue());
                JOptionPane.showMessageDialog(
                        settings,
                        ex.getMessage(),
                        "Помилка",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
        return applySize;
    }
    public void applySize(int newCols, int newRows, int newCellSize) {
        canvas.resizeGrid(newCols, newRows);
        canvas.resizeCell(newCellSize);
        syncUiSizeControls(newCols, newRows, newCellSize);
        updateSelectionInfoLabel();
        canvas.revalidate();
        canvas.repaint();
        LOG.info("Grid resized to {}x{}, cell={}px", newCols, newRows, newCellSize);
    }

    public void syncUiSizeControls(int newCols, int newRows, int newCellSize) {
        sizeInfoLabel.setText("Size: " + newCols + " x " + newRows);
        cellInfoLabel.setText("Cell: " + newCellSize + "px");
        if (widthSpinner != null) {
            widthSpinner.setValue(newCols);
        }
        if (heightSpinner != null) {
            heightSpinner.setValue(newRows);
        }
        if (cellSizeSpinner != null) {
            cellSizeSpinner.setValue(newCellSize);
        }
    }

    public void syncShowGridControl(boolean showGrid) {
        if (showGridCheckBox != null) {
            showGridCheckBox.setSelected(showGrid);
        }
    }

    public void refreshSelectionInfo() {
        updateSelectionInfoLabel();
    }

    private void addSetting(JPanel panel, GridBagConstraints c, int row, String label, JComponent component) {
        c.gridy = row;
        c.gridwidth = 1;

        c.gridx = 0;
        c.weightx = 0.4;
        JLabel title = new JLabel(label);
        title.setFont(UI_FONT);
        panel.add(title, c);

        c.gridx = 1;
        c.weightx = 0.6;
        component.setFont(UI_FONT);
        styleInputComponent(component);
        panel.add(component, c);
    }

    private void setActiveColorButton(JButton[] activeButtonRef, JButton nextButton) {
        if (activeButtonRef[0] != null) {
            activeButtonRef[0].setBorder(INACTIVE_BORDER);
        }
        activeButtonRef[0] = nextButton;
        activeButtonRef[0].setBorder(ACTIVE_BORDER);
    }

    private Map<Color, String> createColorNames() {
        Map<Color, String> names = new HashMap<>();
        names.put(Color.RED, "Red");
        names.put(Color.BLACK, "Black");
        names.put(Color.WHITE, "White");
        names.put(Color.YELLOW, "Yellow");
        names.put(new Color(0, 70, 180), "Blue");
        names.put(new Color(0, 140, 60), "Green");
        names.put(new Color(235, 205, 165), "Beige");
        names.put(new Color(140, 25, 25), "Maroon");
        names.put(Color.LIGHT_GRAY, "Light Gray");
        names.put(new Color(255, 140, 0), "Orange");
        names.put(new Color(128, 0, 128), "Purple");
        names.put(new Color(32, 178, 170), "Turquoise");
        return names;
    }

    private void updateColorInfoLabel(Color color) {
        String displayName = colorNames.get(color);
        if (displayName == null) {
            displayName = String.format("Custom (#%02X%02X%02X)", color.getRed(), color.getGreen(), color.getBlue());
        }
        colorInfoLabel.setText("Color: " + displayName);
    }

    public ThemeOrnament getCanvas() {
        return canvas;
    }

    private void updateSymmetryMode(SYMMETRY mode, String labelText) {
        canvas.setSymmetryMode(mode);
        symmetryStatusLabel.setText("Symmetry: " + labelText);
        LOG.info("Symmetry mode changed to {}", labelText);
    }

    private void updateSelectionInfoLabel() {
        Rectangle rect = canvas.getSelectionRect();
        if (rect == null) {
            selectionInfoLabel.setText("Selection: none");
            return;
        }
        selectionInfoLabel.setText(
                String.format("Selection: %dx%d @ (%d,%d)", rect.width, rect.height, rect.x, rect.y)
        );
    }

    private void appendToLogArea(String message) {
        try {
            String line = message == null ? "" : message;
            String trimmed = line.stripTrailing();
            String[] parts = trimmed.split("\\s+", 3);

            if (parts.length >= 2) {
                String prefix = parts[0] + " ";
                String level = parts[1];
                String suffix = parts.length == 3 ? " " + parts[2] : "";
                String lineEnd = line.endsWith("\n") ? "\n" : "";

                logDocument.insertString(logDocument.getLength(), prefix, normalLogStyle());
                logDocument.insertString(logDocument.getLength(), level, levelStyle(level));
                logDocument.insertString(logDocument.getLength(), suffix + lineEnd, normalLogStyle());
            } else {
                logDocument.insertString(logDocument.getLength(), line, normalLogStyle());
            }
            logPane.setCaretPosition(logDocument.getLength());
        } catch (BadLocationException ex) {
            LOG.error("Failed to append log line to UI", ex);
        }
    }

    private String toHex(Color color) {
        return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
    }

    private SimpleAttributeSet levelStyle(String level) {
        Color color = switch (level) {
            case "TRACE" -> new Color(123, 66, 246);
            case "DEBUG" -> new Color(25, 118, 210);
            case "INFO" -> new Color(46, 125, 50);
            case "WARN" -> new Color(237, 108, 2);
            case "ERROR" -> new Color(198, 40, 40);
            default -> Color.WHITE;
        };

        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setForeground(attrs, color);
        StyleConstants.setBold(attrs, true);
        return attrs;
    }

    private SimpleAttributeSet normalLogStyle() {
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setForeground(attrs, Color.BLACK);
        StyleConstants.setBold(attrs, false);
        return attrs;
    }

    private TitledBorder createSectionBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(new LineBorder(PANEL_BORDER), title);
        border.setTitleFont(UI_FONT_BOLD);
        border.setTitleColor(new Color(55, 55, 55));
        return border;
    }

    private void stylePrimaryButton(AbstractButton button) {
        styleButtonBase(button, PRIMARY_BUTTON_BG, PRIMARY_BUTTON_FG, PRIMARY_BUTTON_HOVER);
    }

    private void styleSecondaryButton(AbstractButton button) {
        styleButtonBase(button, SECONDARY_BUTTON_BG, SECONDARY_BUTTON_FG, SECONDARY_BUTTON_HOVER);
    }

    private void styleButtonBase(AbstractButton button, Color baseBg, Color fg, Color hoverBg) {
        button.setFont(UI_FONT_BOLD);
        button.setForeground(fg);
        button.setBackground(baseBg);
        button.setOpaque(true);
        button.setBorder(new LineBorder(PANEL_BORDER));
        button.setFocusPainted(false);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverBg);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(baseBg);
            }
        });
    }

    private void styleInputComponent(JComponent component) {
        component.setFont(UI_FONT);
        if (!(component instanceof JCheckBox) && !(component instanceof JRadioButton)) {
            component.setBorder(new LineBorder(new Color(200, 200, 200)));
        }
        component.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (!(component instanceof JCheckBox) && !(component instanceof JRadioButton)) {
                    component.setBorder(new LineBorder(INPUT_FOCUS_BORDER, 2));
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (!(component instanceof JCheckBox) && !(component instanceof JRadioButton)) {
                    component.setBorder(new LineBorder(new Color(200, 200, 200)));
                }
            }
        });
    }

    private JSeparator createThinSeparator() {
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        separator.setForeground(new Color(210, 210, 210));
        return separator;
    }

}
