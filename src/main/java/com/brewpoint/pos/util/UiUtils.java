package com.brewpoint.pos.util;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

public final class UiUtils {
    public static final Color BACKGROUND = new Color(246, 247, 249);
    public static final Color PRIMARY = new Color(36, 99, 71);
    public static final Color BORDER = new Color(210, 214, 220);

    private UiUtils() {
    }

    public static void showError(Component parent, Exception exception) {
        String message = exception.getMessage();
        if (message == null || message.trim().isEmpty()) {
            message = "Đã xảy ra lỗi. Vui lòng thử lại.";
        }
        JOptionPane.showMessageDialog(parent, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    public static JButton primaryButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 14f));
        return button;
    }

    public static void configureTable(JTable table) {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30);
        table.getTableHeader().setReorderingAllowed(false);
    }

    public static void panelBorder(JComponent component, String title) {
        component.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(BORDER), title));
    }
}
