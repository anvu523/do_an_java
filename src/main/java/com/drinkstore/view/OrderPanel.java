package com.drinkstore.view;

import com.drinkstore.controller.EmployeeController;
import com.drinkstore.controller.OrderController;
import com.drinkstore.model.Employee;
import com.drinkstore.model.Order;
import com.drinkstore.model.OrderDetail;
import com.drinkstore.util.UiUtil;
import com.drinkstore.util.ValidationException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class OrderPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final transient OrderController orderController = new OrderController();
    private final transient EmployeeController employeeController = new EmployeeController();
    private final boolean adminMode;
    private final Integer fixedEmployeeId;
    private final JTextField orderIdField = new JTextField(8);
    private final JTextField dateField = new JTextField(10);
    private final JComboBox<Employee> employeeCombo = new JComboBox<>();
    private final DefaultTableModel orderTableModel = new DefaultTableModel(
            new Object[]{"Mã HĐ", "Nhân viên", "Ngày lập", "Tổng tiền"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final DefaultTableModel detailTableModel = new DefaultTableModel(
            new Object[]{"Mã SP", "Tên sản phẩm", "Số lượng", "Đơn giá", "Thành tiền"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable orderTable = new JTable(orderTableModel);
    private final JTable detailTable = new JTable(detailTableModel);

    public OrderPanel(boolean adminMode, Integer fixedEmployeeId) {
        this.adminMode = adminMode;
        this.fixedEmployeeId = fixedEmployeeId;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(buildFilterPanel(), BorderLayout.NORTH);
        add(buildContentPanel(), BorderLayout.CENTER);
        loadEmployees();
        loadOrders();
    }

    private JPanel buildFilterPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Lọc hóa đơn"));
        panel.add(new JLabel("Mã HĐ"));
        panel.add(orderIdField);
        panel.add(new JLabel("Ngày yyyy-MM-dd"));
        panel.add(dateField);
        if (adminMode) {
            panel.add(new JLabel("Nhân viên"));
            panel.add(employeeCombo);
        }
        JButton searchButton = new JButton("Tìm");
        searchButton.addActionListener(e -> loadOrders());
        JButton clearButton = new JButton("Xóa lọc");
        clearButton.addActionListener(e -> {
            orderIdField.setText("");
            dateField.setText("");
            if (employeeCombo.getItemCount() > 0) {
                employeeCombo.setSelectedIndex(0);
            }
            loadOrders();
        });
        panel.add(searchButton);
        panel.add(clearButton);
        return panel;
    }

    private JPanel buildContentPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JPanel orderPanel = new JPanel(new BorderLayout());
        orderPanel.setBorder(BorderFactory.createTitledBorder("Danh sách hóa đơn"));
        UiUtil.configureTable(orderTable);
        orderTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && orderTable.getSelectedRow() >= 0) {
                int modelRow = orderTable.convertRowIndexToModel(orderTable.getSelectedRow());
                int orderId = (int) orderTableModel.getValueAt(modelRow, 0);
                loadDetails(orderId);
            }
        });
        orderPanel.add(new JScrollPane(orderTable), BorderLayout.CENTER);

        JPanel detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBorder(BorderFactory.createTitledBorder("Chi tiết hóa đơn"));
        UiUtil.configureTable(detailTable);
        detailPanel.add(new JScrollPane(detailTable), BorderLayout.CENTER);

        panel.add(orderPanel, BorderLayout.CENTER);
        panel.add(detailPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void loadEmployees() {
        if (!adminMode) {
            return;
        }
        try {
            employeeCombo.removeAllItems();
            employeeCombo.addItem(new Employee(0, null, "Tất cả", "", "", "", true));
            for (Employee employee : employeeController.findAll()) {
                employeeCombo.addItem(employee);
            }
        } catch (SQLException ex) {
            UiUtil.showError(this, ex);
        }
    }

    private void loadOrders() {
        try {
            Integer orderId = parseOrderId();
            LocalDate date = parseDate();
            Integer employeeId = adminMode ? selectedEmployeeId() : fixedEmployeeId;
            List<Order> orders = orderController.findOrders(orderId, date, employeeId);
            orderTableModel.setRowCount(0);
            detailTableModel.setRowCount(0);
            for (Order order : orders) {
                orderTableModel.addRow(new Object[]{
                        order.getOrderId(),
                        order.getEmployeeName(),
                        order.getOrderDate(),
                        UiUtil.money(order.getTotalAmount())
                });
            }
        } catch (SQLException | RuntimeException ex) {
            UiUtil.showError(this, ex);
        }
    }

    private void loadDetails(int orderId) {
        try {
            List<OrderDetail> details = orderController.findDetails(orderId);
            detailTableModel.setRowCount(0);
            for (OrderDetail detail : details) {
                detailTableModel.addRow(new Object[]{
                        detail.getProductId(),
                        detail.getProductName(),
                        detail.getQuantity(),
                        UiUtil.money(detail.getUnitPrice()),
                        UiUtil.money(detail.getLineTotal())
                });
            }
        } catch (SQLException ex) {
            UiUtil.showError(this, ex);
        }
    }

    private Integer selectedEmployeeId() {
        Employee employee = (Employee) employeeCombo.getSelectedItem();
        return employee == null || employee.getEmployeeId() == 0 ? null : employee.getEmployeeId();
    }

    private Integer parseOrderId() {
        String text = orderIdField.getText().trim();
        if (text.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new ValidationException("Mã hóa đơn phải là số nguyên.");
        }
    }

    private LocalDate parseDate() {
        String text = dateField.getText().trim();
        if (text.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            throw new ValidationException("Ngày lọc phải có dạng yyyy-MM-dd.");
        }
    }
}
