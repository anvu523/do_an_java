package com.brewpoint.pos.main;

import com.brewpoint.pos.view.LoginFrame;
import com.brewpoint.pos.report.util.JasperFontBootstrap;
import com.brewpoint.pos.util.UIConstants;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public final class App {
    private App() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    UIConstants.applyGlobalLookAndFeel();
                } catch (Exception ignored) {
                    // Dùng look and feel mặc định nếu hệ điều hành không hỗ trợ.
                }
                JasperFontBootstrap.ensureInitialized();
                new LoginFrame().setVisible(true);
            }
        });
    }
}
