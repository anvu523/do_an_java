package com.brewpoint.pos.view;

import com.brewpoint.pos.controller.OrderController;
import com.brewpoint.pos.model.OrderSummary;
import com.brewpoint.pos.util.MoneyUtils;
import com.brewpoint.pos.util.UiUtils;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class OrderHistoryPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final transient OrderController controller = new OrderController();
    private final boolean admin;
    private final Integer employeeId;
    private final JTextField codeField = new JTextField(12);
    private final JTextField dateField = new JTextField(10);
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Mã hóa đơn", "Thu ngân", "Thời gian", "Thanh toán", "Tổng", "Trạng thái"}, 0) {
        private static final long serialVersionUID = 1L;

        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);
    private final List<OrderSummary> orders = new ArrayList<OrderSummary>();

    public OrderHistoryPanel(boolean admin, Integer employeeId) {
        this.admin = admin;
        this.employeeId = employeeId;
        setLayout(new BorderLayout(8, 8));
        add(buildSearch(), BorderLayout.NORTH);
        UiUtils.configureTable(table);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildBottom(), BorderLayout.SOUTH);
        loadData();
    }

    private JPanel buildSearch() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel("Mã"));
        panel.add(codeField);
        panel.add(new JLabel("Ngày yyyy-MM-dd"));
        panel.add(dateField);
        JButton searchButton = UiUtils.primaryButton("Tìm");
        searchButton.addActionListener(e -> loadData());
        panel.add(searchButton);
        return panel;
    }

    private JPanel buildBottom() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton detailButton = new JButton("Xem chi tiết");
        detailButton.addActionListener(e -> showDetail());
        panel.add(detailButton);
        return panel;
    }

    private void loadData() {
        try {
            LocalDate date = parseDate();
            Integer filterEmployeeId = admin ? null : employeeId;
            orders.clear();
            orders.addAll(controller.search(codeField.getText(), date, filterEmployeeId));
            model.setRowCount(0);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            for (OrderSummary order : orders) {
                model.addRow(new Object[]{
                        order.getOrderCode(),
                        order.getEmployeeName(),
                        order.getOrderTime().format(formatter),
                        order.getPaymentMethod().getDisplayName(),
                        MoneyUtils.formatVnd(order.getTotalAmount()),
                        order.getStatus().getDisplayName()
                });
            }
        } catch (SQLException | RuntimeException ex) {
            UiUtils.showError(this, ex);
        }
    }

    private LocalDate parseDate() {
        String value = dateField.getText() == null ? "" : dateField.getText().trim();
        if (value.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Ngày phải có dạng yyyy-MM-dd.");
        }
    }

    private void showDetail() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UiUtils.showInfo(this, "Chọn hóa đơn cần xem.");
            return;
        }
        OrderSummary order = orders.get(table.convertRowIndexToModel(row));
        OrderDetailDialog dialog = new OrderDetailDialog(javax.swing.SwingUtilities.getWindowAncestor(this), order, controller);
        dialog.setVisible(true);
    }
}
