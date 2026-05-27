package com.drinkstore.view;

import com.drinkstore.controller.EmployeeController;
import com.drinkstore.model.Employee;
import com.drinkstore.model.Role;
import com.drinkstore.model.User;
import com.drinkstore.util.UiUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeePanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final transient EmployeeController controller = new EmployeeController();
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Mã NV", "Họ tên", "Điện thoại", "Email", "Vai trò", "Tên đăng nhập", "Hoạt động"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);
    private final JTextField fullNameField = new JTextField(22);
    private final JTextField phoneField = new JTextField(22);
    private final JTextField emailField = new JTextField(22);
    private final JTextArea addressArea = new JTextArea(3, 22);
    private final JTextField usernameField = new JTextField(22);
    private final JPasswordField passwordField = new JPasswordField(22);
    private final JComboBox<Role> roleCombo = new JComboBox<>(Role.values());
    private final JCheckBox activeCheckBox = new JCheckBox("Đang hoạt động", true);
    private final JTextField searchField = new JTextField(22);
    private final transient List<Employee> currentEmployees = new ArrayList<>();
    private transient Employee selectedEmployee;

    public EmployeePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(buildForm(), BorderLayout.WEST);
        add(buildTable(), BorderLayout.CENTER);
        loadData();
    }

    private JPanel buildForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin nhân viên"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addFormRow(panel, gbc, 0, "Họ tên", fullNameField);
        addFormRow(panel, gbc, 1, "Số điện thoại", phoneField);
        addFormRow(panel, gbc, 2, "Email", emailField);
        addFormRow(panel, gbc, 3, "Địa chỉ", new JScrollPane(addressArea));
        addFormRow(panel, gbc, 4, "Vai trò", roleCombo);
        addFormRow(panel, gbc, 5, "Tên đăng nhập", usernameField);
        addFormRow(panel, gbc, 6, "Mật khẩu", passwordField);
        gbc.gridx = 1;
        gbc.gridy = 7;
        panel.add(activeCheckBox, gbc);

        JButton addButton = new JButton("Thêm");
        addButton.addActionListener(e -> create());
        JButton updateButton = new JButton("Sửa");
        updateButton.addActionListener(e -> update());
        JButton deleteButton = new JButton("Xóa");
        deleteButton.addActionListener(e -> delete());
        JButton clearButton = new JButton("Làm mới");
        clearButton.addActionListener(e -> clearForm());

        JPanel buttons = new JPanel();
        buttons.add(addButton);
        buttons.add(updateButton);
        buttons.add(deleteButton);
        buttons.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        panel.add(buttons, gbc);
        return panel;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, java.awt.Component field) {
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private JPanel buildTable() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Tìm nhân viên"));
        searchPanel.add(searchField);
        JButton searchButton = new JButton("Tìm");
        searchButton.addActionListener(e -> search());
        JButton reloadButton = new JButton("Tải lại");
        reloadButton.addActionListener(e -> loadData());
        searchPanel.add(searchButton);
        searchPanel.add(reloadButton);
        panel.add(searchPanel, BorderLayout.NORTH);

        UiUtil.configureTable(table);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                selectEmployee(table.convertRowIndexToModel(table.getSelectedRow()));
            }
        });
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void loadData() {
        try {
            fillTable(controller.findAll());
        } catch (SQLException ex) {
            UiUtil.showError(this, ex);
        }
    }

    private void search() {
        try {
            fillTable(controller.search(searchField.getText()));
        } catch (SQLException ex) {
            UiUtil.showError(this, ex);
        }
    }

    private void create() {
        try {
            Employee employee = readEmployeeForm();
            controller.create(employee, usernameField.getText(), new String(passwordField.getPassword()), (Role) roleCombo.getSelectedItem());
            UiUtil.showInfo(this, "Đã thêm nhân viên.");
            clearForm();
            loadData();
        } catch (SQLException | RuntimeException ex) {
            UiUtil.showError(this, ex);
        }
    }

    private void update() {
        if (selectedEmployee == null) {
            UiUtil.showInfo(this, "Chọn nhân viên cần sửa.");
            return;
        }
        try {
            Employee employee = readEmployeeForm();
            employee.setEmployeeId(selectedEmployee.getEmployeeId());
            employee.setUser(selectedEmployee.getUser());
            controller.update(employee, usernameField.getText(), new String(passwordField.getPassword()), (Role) roleCombo.getSelectedItem());
            UiUtil.showInfo(this, "Đã cập nhật nhân viên.");
            clearForm();
            loadData();
        } catch (SQLException | RuntimeException ex) {
            UiUtil.showError(this, ex);
        }
    }

    private void delete() {
        if (selectedEmployee == null) {
            UiUtil.showInfo(this, "Chọn nhân viên cần xóa.");
            return;
        }
        if (!UiUtil.confirm(this, "Xóa nhân viên đã chọn?")) {
            return;
        }
        try {
            controller.delete(selectedEmployee.getEmployeeId());
            UiUtil.showInfo(this, "Đã xóa nhân viên.");
            clearForm();
            loadData();
        } catch (SQLException | RuntimeException ex) {
            UiUtil.showError(this, ex);
        }
    }

    private Employee readEmployeeForm() {
        Employee employee = new Employee();
        employee.setFullName(fullNameField.getText());
        employee.setPhone(phoneField.getText());
        employee.setEmail(emailField.getText());
        employee.setAddress(addressArea.getText());
        employee.setActive(activeCheckBox.isSelected());
        User user = new User();
        user.setActive(activeCheckBox.isSelected());
        user.setRole((Role) roleCombo.getSelectedItem());
        user.setUsername(usernameField.getText());
        employee.setUser(user);
        return employee;
    }

    private void fillTable(List<Employee> employees) {
        currentEmployees.clear();
        currentEmployees.addAll(employees);
        tableModel.setRowCount(0);
        for (Employee employee : employees) {
            tableModel.addRow(new Object[]{
                    employee.getEmployeeId(),
                    employee.getFullName(),
                    employee.getPhone(),
                    employee.getEmail(),
                    employee.getUser().getRole(),
                    employee.getUser().getUsername(),
                    employee.isActive() ? "Có" : "Không"
            });
        }
    }

    private void selectEmployee(int row) {
        selectedEmployee = currentEmployees.get(row);
        fullNameField.setText(selectedEmployee.getFullName());
        phoneField.setText(nullToEmpty(selectedEmployee.getPhone()));
        emailField.setText(nullToEmpty(selectedEmployee.getEmail()));
        addressArea.setText(nullToEmpty(selectedEmployee.getAddress()));
        usernameField.setText(selectedEmployee.getUser().getUsername());
        passwordField.setText("");
        roleCombo.setSelectedItem(selectedEmployee.getUser().getRole());
        activeCheckBox.setSelected(selectedEmployee.isActive());
    }

    private void clearForm() {
        selectedEmployee = null;
        fullNameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addressArea.setText("");
        usernameField.setText("");
        passwordField.setText("");
        roleCombo.setSelectedItem(Role.EMPLOYEE);
        activeCheckBox.setSelected(true);
        table.clearSelection();
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
