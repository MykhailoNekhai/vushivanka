package ua.uni.visual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.uni.io.InputOutput;
import ua.uni.dto.FileDto;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class Menu extends JFrame {
    private static final Logger LOG = LoggerFactory.getLogger(Menu.class);
    private final InputOutput inputOutput = new InputOutput();
    private final Panels panels = new Panels();
    private JCheckBoxMenuItem showGridMenuItem;

    public Menu() {
        setTitle("Pixel Embroidery. Ornament Editor");
        setSize(1120, 740);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(ThemeColors.APP_BG);

        setJMenuBar(topBar());
        add(createPanels(), BorderLayout.CENTER);
        add(downBar(), BorderLayout.SOUTH);
        shortcuts();
    }

    private JMenuBar topBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setOpaque(true);
        menuBar.setBackground(ThemeColors.MENU_BG);
        menuBar.setBorder(new EmptyBorder(4, 8, 4, 8));

        menuBar.add(file());
        menuBar.add(edit());
        menuBar.add(help());
        return menuBar;
    }

    private JMenu file() {
        JMenu file = new JMenu("File");
        styleMenu(file);
        file.add(newFile());
        file.add(openJSON());
        file.add(saveJSON());
        file.addSeparator();
        file.add(exit());
        return file;
    }

    private JMenu edit() {
        JMenu edit = new JMenu("Edit");
        styleMenu(edit);
        edit.add(clearCanvas());
        edit.add(showGrid());
        return edit;
    }

    private JMenu help() {
        JMenu help = new JMenu("Help");
        styleMenu(help);
        help.add(about());
        return help;
    }

    private JMenuItem newFile() {
        JMenuItem newItem = inputChoose("New", KeyEvent.VK_N, true);
        newItem.addActionListener(e -> {
            panels.getCanvas().clearCanvas();
            panels.getCanvas().repaint();
            LOG.info("Canvas cleared via File -> New");
        });
        return newItem;
    }

    private JMenuItem openJSON() {
        JMenuItem openItem = inputChoose("Open JSON", KeyEvent.VK_O, true);
        openItem.addActionListener(e -> {
            FileDto dto = inputOutput.chooseAndLoad(this);
            if (dto != null) {
                panels.getCanvas().applyDto(dto);
                panels.syncUiSizeControls(dto.getCols(), dto.getRows(), dto.getCellSize());
                panels.syncShowGridControl(dto.isShowGrid());
                panels.refreshSelectionInfo();
                if (showGridMenuItem != null) {
                    showGridMenuItem.setSelected(dto.isShowGrid());
                }
                LOG.info("Project loaded from JSON");
            } else {
                LOG.debug("Open JSON canceled");
            }
        });
        return openItem;
    }

    private JMenuItem saveJSON() {
        JMenuItem saveItem = inputChoose("Save JSON", KeyEvent.VK_S, true);
        saveItem.addActionListener(e -> {
            inputOutput.chooseAndSave(this, panels.getCanvas().toDto());
            LOG.info("Save JSON action executed");
        });
        return saveItem;
    }

    private JMenuItem exit() {
        JMenuItem exitItem = inputChoose("Exit", KeyEvent.VK_Q, true);
        exitItem.addActionListener(e -> {
            LOG.info("Exit requested");
            dispose();
        });
        return exitItem;
    }

    private JMenuItem clearCanvas() {
        JMenuItem clearItem = inputChoose("Clear Canvas", KeyEvent.VK_DELETE, false);
        clearItem.addActionListener(e -> {
            panels.getCanvas().clearCanvas();
            panels.getCanvas().repaint();
            LOG.info("Canvas cleared via Edit -> Clear Canvas");
        });
        return clearItem;
    }

    private JMenuItem showGrid() {
        showGridMenuItem = new JCheckBoxMenuItem("Show Grid", true);
        showGridMenuItem.setForeground(ThemeColors.MENU_BG);
        showGridMenuItem.setBackground(ThemeColors.APP_BG);
        showGridMenuItem.setOpaque(true);
        int modifiers = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
        showGridMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, modifiers));
        showGridMenuItem.addActionListener(e -> {
            boolean show = showGridMenuItem.isSelected();
            panels.getCanvas().setShowGrid(show);
            panels.getCanvas().repaint();
            panels.syncShowGridControl(show);
            LOG.info("Show Grid set to {} via menu", show);
        });
        return showGridMenuItem;
    }

    private JMenuItem about() {
        JMenuItem aboutItem = inputChoose("About", KeyEvent.VK_F1, false);
        aboutItem.addActionListener(e -> {
            LOG.debug("About dialog opened");
            showAboutDialog();
        });
        return aboutItem;
    }

    private JMenuItem inputChoose(String label, int keyCode, boolean useShortcutMask) {
        JMenuItem item = new JMenuItem(label);
        item.setForeground(ThemeColors.MENU_BG);
        item.setBackground(ThemeColors.APP_BG);
        item.setOpaque(true);

        int modifiers = useShortcutMask ? Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx() : 0;
        item.setAccelerator(KeyStroke.getKeyStroke(keyCode, modifiers));

        item.addChangeListener(e -> {
            ButtonModel model = item.getModel();
            if (model.isArmed() || model.isRollover()) {
                item.setBackground(ThemeColors.MENU_HOVER);
                item.setForeground(Color.WHITE);
            } else {
                item.setBackground(ThemeColors.APP_BG);
                item.setForeground(ThemeColors.MENU_BG);
            }
        });

        return item;
    }

    private void styleMenu(JMenu menu) {
        menu.setOpaque(true);
        menu.setBackground(ThemeColors.MENU_BG);
        menu.setForeground(ThemeColors.MENU_FG);

        JPopupMenu popup = menu.getPopupMenu();
        popup.setOpaque(true);
        popup.setBackground(ThemeColors.APP_BG);
        popup.setBorder(new LineBorder(ThemeColors.MENU_BG.darker(), 1));
    }

    private JPanel createPanels() {
        JPanel main = new JPanel(new BorderLayout());
        main.add(panels.left(), BorderLayout.WEST);
        main.add(panels.central(), BorderLayout.CENTER);
        main.add(panels.right(), BorderLayout.EAST);
        return main;
    }
     public JPanel downBar() {
        JPanel status = new JPanel(new BorderLayout());
        status.setPreferredSize(new Dimension(0, 28));
        status.setBorder(new BevelBorder(BevelBorder.LOWERED));

        status.add(new JLabel(" Author: Nekhai Mykhailo"), BorderLayout.WEST);
        status.add(new JLabel(" Ready"), BorderLayout.EAST);
        return status;
    }

    private void shortcuts() {
        JRootPane root = getRootPane();
        InputMap inputMap = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = root.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "clearCanvas");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "clearCanvas");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "showAbout");

        actionMap.put("clearCanvas", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panels.getCanvas().clearCanvas();
                panels.getCanvas().repaint();
            }
        });
        actionMap.put("showAbout", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAboutDialog();
            }
        });
    }

    private void showAboutDialog() {
        String aboutText =
                "Pixel Embroidery. Ornament Editor\n\n" +
                "This program is designed to build Ukrainian embroidery patterns on a pixel grid.\n" +
                "You can draw directly on the canvas using the mouse (click and drag), choose colors from the palette, and create your own ornament combinations.\n" +
                "The editor supports a default startup pattern, quick canvas reset, and JSON project save/load to continue work later.\n" +
                "Current project data includes grid size, selected color, and all painted cells.\n" +
                "Use the File menu to create a new empty canvas, open a saved project, or save the current one.\n" +
                "Use the Edit menu to clear the canvas instantly.\n" +
                "Keyboard shortcuts: Ctrl/Cmd+N (New), Ctrl/Cmd+O (Open JSON), Ctrl/Cmd+S (Save JSON), Delete/Backspace (Clear Canvas), Ctrl/Cmd+Q (Exit), F1 (About).\n" +
                "On macOS, F-keys may be mapped to system controls, so use Fn+F1 to open About if plain F1 does not trigger.\n" +
                "Author: Nekhai Mykhailo.";
        JTextArea textArea = new JTextArea(aboutText);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setOpaque(false);
        textArea.setFont(UIManager.getFont("Label.font"));
        textArea.setBorder(null);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(560, 300));
        scrollPane.setBorder(null);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);

        JOptionPane.showMessageDialog(this, scrollPane, "About", JOptionPane.INFORMATION_MESSAGE);
    }

    static final class ThemeColors {
        static final Color APP_BG = new Color(246, 243, 236);
        static final Color MENU_BG = new Color(32, 38, 46);
        static final Color MENU_FG = new Color(244, 238, 224);
        static final Color MENU_HOVER = new Color(196, 64, 44);
    }

}
