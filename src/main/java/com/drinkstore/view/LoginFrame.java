package com.drinkstore.view;

import com.drinkstore.controller.EmployeeController;
import com.drinkstore.controller.LoginController;
import com.drinkstore.model.Employee;
import com.drinkstore.model.User;
import com.drinkstore.util.UiUtil;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.Optional;

public class LoginFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final transient LoginController loginController = new LoginController();
    private final transient EmployeeController employeeController = new EmployeeController();
    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);

    public LoginFrame() {
        setTitle("Đăng nhập - Quản lý cửa hàng đồ uống");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 250);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("QUẢN LÝ CỬA HÀNG ĐỒ UỐNG", JLabel.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Tên đăng nhập"), gbc);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Mật khẩu"), gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        JButton loginButton = new JButton("Đăng nhập");
        loginButton.addActionListener(e -> login());
        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(loginButton, gbc);

        add(formPanel, BorderLayout.CENTER);
        getRootPane().setDefaultButton(loginButton);
    }

    private void login() {
        try {
            User user = loginController.login(usernameField.getText(), new String(passwordField.getPassword()));
            Optional<Employee> employee = employeeController.findByUserId(user.getUserId());
            SwingUtilities.invokeLater(() -> {
                new MainFrame(user, employee.orElse(null)).setVisible(true);
                dispose();
            });
        } catch (SQLException | RuntimeException ex) {
            UiUtil.showError(this, ex);
        }
    }
}
