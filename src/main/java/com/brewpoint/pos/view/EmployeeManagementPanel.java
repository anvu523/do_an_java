package com.brewpoint.pos.view;

import com.brewpoint.pos.controller.EmployeeController;
import com.brewpoint.pos.model.Employee;
import com.brewpoint.pos.model.Role;
import com.brewpoint.pos.util.FormLayout;
import com.brewpoint.pos.util.UIConstants;
import com.brewpoint.pos.util.UiUtils;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeManagementPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final transient EmployeeController controller = new EmployeeController();
    private final int currentUserId;
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Mã NV", "Tên đăng nhập", "Họ tên", "Vai trò", "Điện thoại", "Email", "Trạng thái"}, 0) {
        private static final long serialVersionUID = 1L;

        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);
    private final JTextField usernameField = new JTextField(12);
    private final JTextField passwordField = new JTextField(12);
    private final JTextField fullNameField = new JTextField(18);
    private final JTextField phoneField = new JTextField(10);
    private final JTextField emailField = new JTextField(18);
    private final JComboBox<Role> roleCombo = new JComboBox<Role>(Role.values());
    private final JCheckBox activeBox = new JCheckBox("Đang làm việc", true);
    private final List<Employee> employees = new ArrayList<Employee>();
    private int selectedEmployeeId;
    private int selectedUserId;

    public EmployeeManagementPanel(int currentUserId) {
        this.currentUserId = currentUserId;
        UiUtils.styleContentPanel(this);
        setLayout(new BorderLayout(UIConstants.SPACING_MD, UIConstants.SPACING_MD));
        add(buildForm(), BorderLayout.NORTH);
        UiUtils.configureTable(table);
        table.getSelectionModel().addListSelectionListener(e -> selectRow());
        add(new JScrollPane(table), BorderLayout.CENTER);
        loadData();
    }

    private JPanel buildForm() {
        UiUtils.styleField(usernameField);
        passwordField.setFont(UIConstants.font(UIConstants.FONT_INPUT));
        passwordField.setPreferredSize(new java.awt.Dimension(160, UIConstants.FORM_FIELD_HEIGHT));
        UiUtils.styleField(fullNameField);
        UiUtils.styleField(phoneField);
        UiUtils.styleField(emailField);
        roleCombo.setFont(UIConstants.font(UIConstants.FONT_INPUT));
        roleCombo.setPreferredSize(new java.awt.Dimension(160, UIConstants.FORM_FIELD_HEIGHT));
        activeBox.setFont(UIConstants.font(UIConstants.FONT_LABEL));
        JButton saveButton = UiUtils.primaryButton("Lưu");
        saveButton.addActionListener(e -> save());
        JButton resetButton = UiUtils.secondaryButton("Đặt lại mật khẩu");
        resetButton.addActionListener(e -> resetPassword());
        JButton clearButton = UiUtils.secondaryButton("Nhập mới");
        clearButton.addActionListener(e -> clearForm());
        return new FormLayout()
                .addRow("Tên đăng nhập", usernameField)
                .addRow("Mật khẩu", passwordField)
                .addRow("Họ tên", fullNameField)
                .addRow("SĐT", phoneField)
                .addRow("Email", emailField)
                .addRow("Vai trò", roleCombo)
                .addFullWidth(activeBox)
                .buildCard(saveButton, resetButton, clearButton);
    }

    private void loadData() {
        try {
            employees.clear();
            employees.addAll(controller.findAll());
            model.setRowCount(0);
            for (Employee employee : employees) {
                model.addRow(new Object[]{
                        Integer.valueOf(employee.getEmployeeId()),
                        employee.getUsername(),
                        employee.getFullName(),
                        employee.getRole().getDisplayName(),
                        employee.getPhone(),
                        employee.getEmail(),
                        employee.isActive() ? "Đang làm việc" : "Tạm khóa"
                });
            }
        } catch (SQLException ex) {
            UiUtils.showError(this, ex);
        }
    }

    private void selectRow() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        Employee employee = employees.get(table.convertRowIndexToModel(row));
        selectedEmployeeId = employee.getEmployeeId();
        selectedUserId = employee.getUserId();
        usernameField.setText(employee.getUsername());
        passwordField.setText("");
        fullNameField.setText(employee.getFullName());
        phoneField.setText(employee.getPhone());
        emailField.setText(employee.getEmail());
        roleCombo.setSelectedItem(employee.getRole());
        activeBox.setSelected(employee.isActive());
    }

    private void save() {
        try {
            Employee employee = new Employee();
            employee.setEmployeeId(selectedEmployeeId);
            employee.setUserId(selectedUserId);
            employee.setUsername(usernameField.getText());
            employee.setFullName(fullNameField.getText());
            employee.setPhone(phoneField.getText());
            employee.setEmail(emailField.getText());
            employee.setRole((Role) roleCombo.getSelectedItem());
            employee.setActive(activeBox.isSelected());
            if (selectedEmployeeId > 0) {
                controller.update(employee, currentUserId);
            } else {
                controller.create(employee, usernameField.getText(), passwordField.getText());
            }
            clearForm();
            loadData();
        } catch (SQLException | RuntimeException ex) {
            UiUtils.showError(this, ex);
        }
    }

    private void resetPassword() {
        if (selectedUserId <= 0) {
            UiUtils.showInfo(this, "Chọn tài khoản cần đặt lại mật khẩu.");
            return;
        }
        try {
            controller.resetPassword(selectedUserId, passwordField.getText());
            UiUtils.showInfo(this, "Đã đặt lại mật khẩu thành công.");
        } catch (SQLException | RuntimeException ex) {
            UiUtils.showError(this, ex);
        }
    }

    private void clearForm() {
        selectedEmployeeId = 0;
        selectedUserId = 0;
        usernameField.setText("");
        passwordField.setText("");
        fullNameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        roleCombo.setSelectedItem(Role.CASHIER);
        activeBox.setSelected(true);
        table.clearSelection();
    }
}
