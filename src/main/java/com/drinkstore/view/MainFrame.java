package com.drinkstore.view;

import com.drinkstore.model.Employee;
import com.drinkstore.model.Role;
import com.drinkstore.model.User;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final transient User currentUser;
    private final transient Employee currentEmployee;

    public MainFrame(User currentUser, Employee currentEmployee) {
        this.currentUser = currentUser;
        this.currentEmployee = currentEmployee;
        setTitle("Quản lý cửa hàng bán đồ uống");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 720);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildMenu(), BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
        buildContent();
    }

    private JPanel buildHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        JLabel title = new JLabel("Phần mềm quản lý cửa hàng bán đồ uống");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        JLabel userLabel = new JLabel(currentUser.getUsername() + " - " + currentUser.getRole(), SwingConstants.RIGHT);
        panel.add(title, BorderLayout.WEST);
        panel.add(userLabel, BorderLayout.EAST);
        return panel;
    }

    private JPanel buildMenu() {
        JPanel menu = new JPanel(new GridLayout(10, 1, 0, 6));
        menu.setPreferredSize(new Dimension(190, 0));
        menu.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        menu.setBackground(new Color(245, 245, 245));

        addMenuButton(menu, "Sản phẩm", "products", true);
        addMenuButton(menu, "Loại sản phẩm", "categories", isAdmin());
        addMenuButton(menu, "Nhân viên", "employees", isAdmin());
        addMenuButton(menu, "Bán hàng", "sales", true);
        addMenuButton(menu, "Hóa đơn", "orders", true);
        addMenuButton(menu, "Thống kê", "statistics", isAdmin());

        JButton logoutButton = new JButton("Đăng xuất");
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        menu.add(logoutButton);
        return menu;
    }

    private void addMenuButton(JPanel menu, String text, String cardName, boolean enabled) {
        JButton button = new JButton(text);
        button.setEnabled(enabled);
        button.addActionListener(e -> cardLayout.show(contentPanel, cardName));
        menu.add(button);
    }

    private void buildContent() {
        boolean employeeMode = !isAdmin();
        Integer fixedEmployeeId = currentEmployee == null ? null : currentEmployee.getEmployeeId();
        contentPanel.add(new ProductPanel(employeeMode), "products");
        contentPanel.add(new CategoryPanel(), "categories");
        contentPanel.add(new EmployeePanel(), "employees");
        contentPanel.add(new SalePanel(fixedEmployeeId), "sales");
        contentPanel.add(new OrderPanel(isAdmin(), fixedEmployeeId), "orders");
        contentPanel.add(new StatisticPanel(), "statistics");
        cardLayout.show(contentPanel, "products");
    }

    private boolean isAdmin() {
        return currentUser.getRole() == Role.ADMIN;
    }
}
