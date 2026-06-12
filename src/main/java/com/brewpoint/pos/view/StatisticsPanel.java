package com.brewpoint.pos.view;

import com.brewpoint.pos.controller.StatisticController;
import com.brewpoint.pos.model.ProductSalesStat;
import com.brewpoint.pos.model.StatisticSummary;
import com.brewpoint.pos.util.MoneyUtils;
import com.brewpoint.pos.util.UiUtils;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class StatisticsPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final transient StatisticController controller = new StatisticController();
    private final JTextField dateField = new JTextField(LocalDate.now().toString(), 10);
    private final JLabel todayRevenueLabel = new JLabel("0 ₫");
    private final JLabel selectedRevenueLabel = new JLabel("0 ₫");
    private final JLabel orderCountLabel = new JLabel("0");
    private final DefaultTableModel model = new DefaultTableModel(new Object[]{"Sản phẩm", "Số lượng", "Doanh thu"}, 0) {
        private static final long serialVersionUID = 1L;

        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);

    public StatisticsPanel() {
        setLayout(new BorderLayout(8, 8));
        add(buildTop(), BorderLayout.NORTH);
        UiUtils.configureTable(table);
        add(new JScrollPane(table), BorderLayout.CENTER);
        loadData();
    }

    private JPanel buildTop() {
        JPanel top = new JPanel(new BorderLayout());
        JPanel filter = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filter.add(new JLabel("Ngày yyyy-MM-dd"));
        filter.add(dateField);
        JButton loadButton = UiUtils.primaryButton("Tải thống kê");
        loadButton.addActionListener(e -> loadData());
        filter.add(loadButton);
        top.add(filter, BorderLayout.NORTH);
        JPanel cards = new JPanel(new GridLayout(1, 3, 10, 0));
        cards.add(metric("Doanh thu hôm nay", todayRevenueLabel));
        cards.add(metric("Doanh thu ngày chọn", selectedRevenueLabel));
        cards.add(metric("Số hóa đơn", orderCountLabel));
        top.add(cards, BorderLayout.CENTER);
        return top;
    }

    private JPanel metric(String title, JLabel valueLabel) {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        UiUtils.panelBorder(panel, title);
        panel.add(valueLabel);
        return panel;
    }

    private void loadData() {
        LocalDate selectedDate;
        try {
            selectedDate = LocalDate.parse(dateField.getText().trim());
        } catch (DateTimeParseException ex) {
            UiUtils.showError(this, new IllegalArgumentException("Ngày phải có dạng yyyy-MM-dd."));
            return;
        }
        SwingWorker<Object[], Void> worker = new SwingWorker<Object[], Void>() {
            protected Object[] doInBackground() throws Exception {
                StatisticSummary summary = controller.summary(selectedDate);
                List<ProductSalesStat> topProducts = controller.topProducts(selectedDate);
                return new Object[]{summary, topProducts};
            }

            @SuppressWarnings("unchecked")
            protected void done() {
                try {
                    Object[] result = get();
                    fill((StatisticSummary) result[0], (List<ProductSalesStat>) result[1]);
                } catch (Exception ex) {
                    UiUtils.showError(StatisticsPanel.this, new RuntimeException("Không tải được thống kê."));
                }
            }
        };
        worker.execute();
    }

    private void fill(StatisticSummary summary, List<ProductSalesStat> topProducts) throws SQLException {
        todayRevenueLabel.setText(MoneyUtils.formatVnd(summary.getTodayRevenue()));
        selectedRevenueLabel.setText(MoneyUtils.formatVnd(summary.getSelectedRevenue()));
        orderCountLabel.setText(String.valueOf(summary.getOrderCount()));
        model.setRowCount(0);
        for (ProductSalesStat stat : topProducts) {
            model.addRow(new Object[]{
                    stat.getProductName(),
                    Integer.valueOf(stat.getQuantity()),
                    MoneyUtils.formatVnd(stat.getRevenue())
            });
        }
    }
}
