package com.brewpoint.pos.view;

import com.brewpoint.pos.controller.ReportController;
import com.brewpoint.pos.util.DateUtils;
import com.brewpoint.pos.util.FormLayout;
import com.brewpoint.pos.util.UIConstants;
import com.brewpoint.pos.util.UiUtils;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.time.LocalDate;

public class ReportsPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final transient ReportController controller = new ReportController();
    private final JComboBox<String> reportTypeCombo = new JComboBox<String>(new String[]{
            "Doanh thu theo ngày",
            "Doanh thu theo tháng",
            "Sản phẩm bán chạy",
            "Doanh thu từng thu ngân"
    });
    private final JTextField dateField = new JTextField(DateUtils.format(LocalDate.now()), 12);
    private final JTextField monthField = new JTextField(String.valueOf(LocalDate.now().getMonthValue()), 4);
    private final JTextField yearField = new JTextField(String.valueOf(LocalDate.now().getYear()), 6);
    private final JPanel parameterPanel = new JPanel(new CardLayout());
    private final JPanel dailyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, UIConstants.SPACING_SM, 0));
    private final JPanel monthlyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, UIConstants.SPACING_SM, 0));

    public ReportsPanel() {
        UiUtils.styleContentPanel(this);
        setLayout(new BorderLayout(UIConstants.SPACING_MD, UIConstants.SPACING_MD));
        add(buildForm(), BorderLayout.NORTH);
        add(buildHint(), BorderLayout.CENTER);
    }

    private JPanel buildForm() {
        reportTypeCombo.setFont(UIConstants.font(UIConstants.FONT_INPUT));
        reportTypeCombo.addActionListener(e -> showParameterCard());
        UiUtils.styleField(dateField);
        UiUtils.styleField(monthField);
        UiUtils.styleField(yearField);

        dailyPanel.setOpaque(false);
        JLabel dateLabel = new JLabel("Ngày (dd/MM/yyyy)");
        UiUtils.styleLabel(dateLabel);
        dailyPanel.add(dateLabel);
        dailyPanel.add(dateField);

        monthlyPanel.setOpaque(false);
        JLabel monthLabel = new JLabel("Tháng");
        UiUtils.styleLabel(monthLabel);
        monthlyPanel.add(monthLabel);
        monthlyPanel.add(monthField);
        JLabel yearLabel = new JLabel("Năm");
        UiUtils.styleLabel(yearLabel);
        monthlyPanel.add(yearLabel);
        monthlyPanel.add(yearField);

        parameterPanel.setOpaque(false);
        parameterPanel.add(dailyPanel, "daily");
        parameterPanel.add(monthlyPanel, "monthly");

        JButton previewButton = UiUtils.secondaryButton("Xem báo cáo");
        previewButton.addActionListener(e -> preview());
        JButton printButton = UiUtils.secondaryButton("In");
        printButton.addActionListener(e -> print());
        JButton exportButton = UiUtils.primaryButton("Xuất PDF");
        exportButton.addActionListener(e -> exportPdf());

        JPanel body = new FormLayout()
                .addRow("Chọn báo cáo", reportTypeCombo)
                .addFullWidth(parameterPanel)
                .build();
        showParameterCard();
        return UiUtils.wrapFormCard(body, previewButton, printButton, exportButton);
    }

    private JPanel buildHint() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.add(hintLine("Chọn báo cáo, nhập ngày hoặc tháng, rồi bấm Xem báo cáo, In hoặc Xuất PDF."));
        panel.add(Box.createVerticalStrut(UIConstants.SPACING_SM));
        panel.add(hintLine("In hóa đơn từng đơn tại màn Lịch sử hóa đơn."));
        return panel;
    }

    private JLabel hintLine(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.font(UIConstants.FONT_LABEL));
        label.setForeground(UIConstants.TEXT_MUTED);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void showParameterCard() {
        CardLayout layout = (CardLayout) parameterPanel.getLayout();
        if (reportTypeCombo.getSelectedIndex() == 0) {
            layout.show(parameterPanel, "daily");
        } else {
            layout.show(parameterPanel, "monthly");
        }
    }

    private void preview() {
        int type = reportTypeCombo.getSelectedIndex();
        if (type == 0) {
            controller.previewDailyRevenue(parseDate(), this);
        } else {
            int year = parseYear();
            int month = parseMonth();
            if (type == 1) {
                controller.previewMonthlyRevenue(year, month, this);
            } else if (type == 2) {
                controller.previewBestSellingProducts(year, month, this);
            } else {
                controller.previewCashierPerformance(year, month, this);
            }
        }
    }

    private void print() {
        int type = reportTypeCombo.getSelectedIndex();
        if (type == 0) {
            controller.printDailyRevenue(parseDate(), this);
        } else {
            int year = parseYear();
            int month = parseMonth();
            if (type == 1) {
                controller.printMonthlyRevenue(year, month, this);
            } else if (type == 2) {
                controller.printBestSellingProducts(year, month, this);
            } else {
                controller.printCashierPerformance(year, month, this);
            }
        }
    }

    private void exportPdf() {
        int type = reportTypeCombo.getSelectedIndex();
        if (type == 0) {
            controller.exportDailyRevenuePdf(parseDate(), this);
        } else {
            int year = parseYear();
            int month = parseMonth();
            if (type == 1) {
                controller.exportMonthlyRevenuePdf(year, month, this);
            } else if (type == 2) {
                controller.exportBestSellingProductsPdf(year, month, this);
            } else {
                controller.exportCashierPerformancePdf(year, month, this);
            }
        }
    }

    private LocalDate parseDate() {
        return DateUtils.parseRequired(dateField.getText());
    }

    private int parseMonth() {
        return Integer.parseInt(monthField.getText().trim());
    }

    private int parseYear() {
        return Integer.parseInt(yearField.getText().trim());
    }
}
