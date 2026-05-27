package com.drinkstore.view;

import com.drinkstore.controller.StatisticController;
import com.drinkstore.model.ProductSalesStat;
import com.drinkstore.util.UiUtil;
import com.drinkstore.util.ValidationException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;

public class StatisticPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final transient StatisticController controller = new StatisticController();
    private final JTextField dateField = new JTextField(LocalDate.now().toString(), 10);
    private final JTextField monthField = new JTextField(YearMonth.now().toString(), 8);
    private final JLabel dailyRevenueLabel = new JLabel("Doanh thu ngày: 0 đ");
    private final JLabel monthlyRevenueLabel = new JLabel("Doanh thu tháng: 0 đ");
    private final JLabel orderCountLabel = new JLabel("Số lượng hóa đơn: 0");
    private final DefaultTableModel topProductModel = new DefaultTableModel(
            new Object[]{"Mã SP", "Tên sản phẩm", "Số lượng bán", "Doanh thu"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable topProductTable = new JTable(topProductModel);

    public StatisticPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(buildFilterPanel(), BorderLayout.NORTH);
        add(buildSummaryPanel(), BorderLayout.WEST);
        add(buildTopProductPanel(), BorderLayout.CENTER);
        loadStatistics();
    }

    private JPanel buildFilterPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Bộ lọc thống kê"));
        panel.add(new JLabel("Ngày yyyy-MM-dd"));
        panel.add(dateField);
        panel.add(new JLabel("Tháng yyyy-MM"));
        panel.add(monthField);
        JButton loadButton = new JButton("Tải thống kê");
        loadButton.addActionListener(e -> loadStatistics());
        panel.add(loadButton);
        return panel;
    }

    private JPanel buildSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 1, 8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Tổng quan"));
        panel.add(dailyRevenueLabel);
        panel.add(monthlyRevenueLabel);
        panel.add(orderCountLabel);
        return panel;
    }

    private JPanel buildTopProductPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Sản phẩm bán chạy"));
        UiUtil.configureTable(topProductTable);
        panel.add(new JScrollPane(topProductTable), BorderLayout.CENTER);
        return panel;
    }

    private void loadStatistics() {
        try {
            LocalDate date = parseDate();
            YearMonth month = parseMonth();
            dailyRevenueLabel.setText("Doanh thu ngày: " + UiUtil.money(controller.revenueByDate(date)));
            monthlyRevenueLabel.setText("Doanh thu tháng: " + UiUtil.money(controller.revenueByMonth(month.getYear(), month.getMonthValue())));
            orderCountLabel.setText("Số lượng hóa đơn: " + controller.countOrders());
            fillTopProducts(controller.topSellingProducts(10));
        } catch (SQLException | RuntimeException ex) {
            UiUtil.showError(this, ex);
        }
    }

    private void fillTopProducts(List<ProductSalesStat> stats) {
        topProductModel.setRowCount(0);
        for (ProductSalesStat stat : stats) {
            topProductModel.addRow(new Object[]{
                    stat.getProductId(),
                    stat.getProductName(),
                    stat.getTotalQuantity(),
                    UiUtil.money(stat.getTotalRevenue())
            });
        }
    }

    private LocalDate parseDate() {
        try {
            return LocalDate.parse(dateField.getText().trim());
        } catch (DateTimeParseException e) {
            throw new ValidationException("Ngày thống kê phải có dạng yyyy-MM-dd.");
        }
    }

    private YearMonth parseMonth() {
        try {
            return YearMonth.parse(monthField.getText().trim());
        } catch (DateTimeParseException e) {
            throw new ValidationException("Tháng thống kê phải có dạng yyyy-MM.");
        }
    }
}
