package com.drinkstore.main;

import com.drinkstore.view.LoginFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Default Swing look and feel remains usable.
            }
            new LoginFrame().setVisible(true);
        });
    }
}
