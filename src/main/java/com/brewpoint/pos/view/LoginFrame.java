package com.brewpoint.pos.view;

import com.brewpoint.pos.controller.AuthController;
import com.brewpoint.pos.model.Employee;
import com.brewpoint.pos.model.User;
import com.brewpoint.pos.util.UiUtils;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final transient AuthController authController = new AuthController();
    private final JTextField usernameField = new JTextField("admin", 18);
    private final JPasswordField passwordField = new JPasswordField("admin123", 18);

    public LoginFrame() {
        setTitle("BrewPoint POS - Đăng nhập");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(430, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(buildContent(), BorderLayout.CENTER);
    }

    private JPanel buildContent() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(22, 28, 22, 28));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("BrewPoint POS");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 26f));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Tên đăng nhập"), gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Mật khẩu"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        JButton loginButton = UiUtils.primaryButton("Đăng nhập");
        loginButton.addActionListener(e -> login());
        getRootPane().setDefaultButton(loginButton);
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(loginButton, gbc);
        return panel;
    }

    private void login() {
        try {
            User user = authController.login(usernameField.getText(), new String(passwordField.getPassword()));
            Employee employee = authController.findEmployee(user);
            dispose();
            new MainFrame(user, employee).setVisible(true);
        } catch (SQLException | RuntimeException ex) {
            UiUtils.showError(this, ex);
        }
    }
}
