package com.brewpoint.pos.view;

import com.brewpoint.pos.controller.OrderController;
import com.brewpoint.pos.model.OrderSummary;
import com.brewpoint.pos.util.DateUtils;
import com.brewpoint.pos.util.MoneyUtils;
import com.brewpoint.pos.util.UIConstants;
import com.brewpoint.pos.util.UiUtils;

import com.brewpoint.pos.controller.EmployeeController;
import com.brewpoint.pos.model.Employee;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import java.util.ArrayList;
import java.util.List;

public class OrderHistoryPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final transient OrderController controller = com.brewpoint.pos.DependencyContainer.getInstance().getOrderController();
    private final transient EmployeeController employeeController = com.brewpoint.pos.DependencyContainer.getInstance().getEmployeeController();
    private final boolean admin;
    private final Integer employeeId;
    private final JTextField codeField = new JTextField(14);
    private final JTextField dateField = new JTextField(10);
    private final JComboBox<Object> cashierCombo = new JComboBox<Object>();
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
        UiUtils.styleContentPanel(this);
        setLayout(new BorderLayout(UIConstants.SPACING_MD, UIConstants.SPACING_MD));
        add(buildSearch(), BorderLayout.NORTH);
        UiUtils.configureTable(table);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildBottom(), BorderLayout.SOUTH);
        loadData();
    }

    private JPanel buildSearch() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, UIConstants.SPACING_MD, UIConstants.SPACING_SM));
        row.setOpaque(false);

        row.add(UiUtils.formLabel("Mã hóa đơn"));
        UiUtils.styleCompactField(codeField);
        row.add(codeField);

        row.add(UiUtils.formLabel("Ngày lập hóa đơn (dd/MM/yyyy)"));
        dateField.setToolTipText("Để trống nếu không lọc theo ngày");
        UiUtils.styleCompactField(dateField);
        row.add(dateField);

        if (admin) {
            row.add(UiUtils.formLabel("Thu ngân"));
            cashierCombo.setFont(UIConstants.font(UIConstants.FONT_INPUT));
            cashierCombo.setPreferredSize(new java.awt.Dimension(180, java.lang.Math.max(UIConstants.FORM_FIELD_HEIGHT, 30)));
            row.add(cashierCombo);
            loadCashiers();
        }

        JButton searchButton = UiUtils.primaryButton("Tìm kiếm");
        searchButton.addActionListener(e -> loadData());
        row.add(searchButton);

        return UiUtils.wrapFormCard(row);
    }

    private void loadCashiers() {
        try {
            cashierCombo.removeAllItems();
            cashierCombo.addItem("Tất cả thu ngân");
            List<Employee> list = employeeController.findAll();
            for (Employee emp : list) {
                cashierCombo.addItem(emp);
            }
        } catch (SQLException ex) {
            UiUtils.showError(this, ex);
        }
    }

    private JPanel buildBottom() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIConstants.SPACING_SM, UIConstants.SPACING_SM));
        JButton detailButton = UiUtils.secondaryButton("Xem chi tiết");
        detailButton.addActionListener(e -> showDetail());
        panel.add(detailButton);
        return panel;
    }

    private void loadData() {
        try {
            LocalDate date = parseDate();
            Integer filterEmployeeId = null;
            if (admin) {
                Object selected = cashierCombo.getSelectedItem();
                if (selected instanceof Employee) {
                    filterEmployeeId = ((Employee) selected).getEmployeeId();
                }
            } else {
                filterEmployeeId = employeeId;
            }
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
        return DateUtils.parseOptional(dateField.getText());
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
