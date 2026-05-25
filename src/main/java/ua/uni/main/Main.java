package ua.uni.main;

import ua.uni.visual.Menu;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
       SwingUtilities.invokeLater(() -> new Menu().setVisible(true));
    }
}
