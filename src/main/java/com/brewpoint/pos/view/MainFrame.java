package com.brewpoint.pos.view;

import com.brewpoint.pos.model.Employee;
import com.brewpoint.pos.model.Role;
import com.brewpoint.pos.model.User;
import com.brewpoint.pos.util.UIConstants;
import com.brewpoint.pos.util.UiUtils;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final transient User currentUser;
    private final transient Employee currentEmployee;
    private final JLabel clockLabel = new JLabel();
    private final JLabel sessionLabel = new JLabel();
    private final Map<String, JButton> menuButtons = new LinkedHashMap<String, JButton>();

    public MainFrame(User currentUser, Employee currentEmployee) {
        this.currentUser = currentUser;
        this.currentEmployee = currentEmployee;
        setTitle("BrewPoint POS");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1280, 720));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        getContentPane().setBackground(UIConstants.BG_APP);
        setLayout(new BorderLayout());
        add(buildHeader(), BorderLayout.NORTH);
        add(buildMenu(), BorderLayout.WEST);
        contentPanel.setBackground(UIConstants.BG_APP);
        add(contentPanel, BorderLayout.CENTER);
        buildContent();
        selectMenu("pos");
        startClock();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(UIConstants.SPACING_MD, UIConstants.SPACING_LG,
                UIConstants.SPACING_MD, UIConstants.SPACING_LG));
        header.setBackground(UIConstants.BG_PANEL);

        JLabel title = new JLabel("BrewPoint POS");
        title.setFont(UIConstants.fontBold(UIConstants.FONT_MAIN_TITLE));
        title.setForeground(UIConstants.PRIMARY);

        String employeeName = currentEmployee == null ? currentUser.getUsername() : currentEmployee.getFullName();
        sessionLabel.setText(employeeName + " - " + currentUser.getRole().getDisplayName());
        sessionLabel.setFont(UIConstants.font(UIConstants.FONT_LABEL));
        sessionLabel.setForeground(UIConstants.TEXT_PRIMARY);
        clockLabel.setFont(UIConstants.font(UIConstants.FONT_LABEL));
        clockLabel.setForeground(UIConstants.TEXT_MUTED);

        JPanel right = new JPanel(new GridLayout(2, 1, 0, 4));
        right.setOpaque(false);
        right.add(sessionLabel);
        right.add(clockLabel);
        header.add(title, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JPanel buildMenu() {
        JPanel menu = new JPanel(new GridLayout(13, 1, 0, UIConstants.SPACING_SM));
        menu.setPreferredSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 0));
        menu.setBorder(BorderFactory.createEmptyBorder(UIConstants.SPACING_MD, UIConstants.SPACING_SM,
                UIConstants.SPACING_MD, UIConstants.SPACING_SM));
        menu.setBackground(UIConstants.BG_SIDEBAR);
        addMenuButton(menu, "Bán hàng", "pos", true);
        addMenuButton(menu, "Lịch sử hóa đơn", "orders", true);
        addMenuButton(menu, "Danh mục", "categories", isAdmin());
        addMenuButton(menu, "Sản phẩm", "products", isAdmin());
        addMenuButton(menu, "Topping bổ sung", "toppings", isAdmin());
        addMenuButton(menu, "Nhân viên", "employees", isAdmin());
        addMenuButton(menu, "Báo cáo in/PDF", "reports", isAdmin());
        addMenuButton(menu, "Tổng quan ngày", "statistics", isAdmin());
        JButton logoutButton = UiUtils.dangerButton("Đăng xuất");
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        menu.add(logoutButton);
        return menu;
    }

    private void addMenuButton(JPanel menu, String text, String cardName, boolean enabled) {
        JButton button = UiUtils.secondaryButton(text);
        button.setEnabled(enabled);
        button.addActionListener(e -> selectMenu(cardName));
        menuButtons.put(cardName, button);
        menu.add(button);
    }

    private void selectMenu(String cardName) {
        JButton button = menuButtons.get(cardName);
        if (button == null || !button.isEnabled()) {
            return;
        }
        cardLayout.show(contentPanel, cardName);
        for (Map.Entry<String, JButton> entry : menuButtons.entrySet()) {
            UiUtils.setNavButtonSelected(entry.getValue(), cardName.equals(entry.getKey()));
        }
    }

    private void buildContent() {
        Integer employeeId = currentEmployee == null ? null : Integer.valueOf(currentEmployee.getEmployeeId());
        contentPanel.add(new PosPanel(employeeId), "pos");
        contentPanel.add(new OrderHistoryPanel(isAdmin(), employeeId), "orders");
        contentPanel.add(new CategoryManagementPanel(), "categories");
        contentPanel.add(new ProductManagementPanel(), "products");
        contentPanel.add(new ToppingManagementPanel(), "toppings");
        contentPanel.add(new EmployeeManagementPanel(currentUser.getUserId()), "employees");
        contentPanel.add(new ReportsPanel(), "reports");
        contentPanel.add(new StatisticsPanel(), "statistics");
    }

    private boolean isAdmin() {
        return currentUser.getRole() == Role.ADMIN;
    }

    private void startClock() {
        Timer timer = new Timer(1000, e -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            clockLabel.setText(LocalDateTime.now().format(formatter));
        });
        timer.setRepeats(true);
        timer.start();
    }
}
