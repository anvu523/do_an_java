package com.brewpoint.pos.view;

import com.brewpoint.pos.controller.ReportController;
import com.brewpoint.pos.report.exporter.ReportExportStrategy;
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
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;

public class ReportsPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final transient ReportController controller = com.brewpoint.pos.DependencyContainer.getInstance().getReportController();
    private final JComboBox<String> reportTypeCombo = new JComboBox<String>(new String[]{
            "Doanh thu",
            "Sản phẩm bán chạy",
            "Doanh thu từng thu ngân"
    });
    private final JComboBox<String> periodCombo = new JComboBox<String>(new String[]{
            "Ngày",
            "Tuần",
            "Tháng",
            "Quý",
            "Khoảng ngày"
    });
    private final JLabel dateLabel = new JLabel("Chọn ngày (dd/MM/yyyy)");
    private final JTextField dateField = new JTextField(DateUtils.format(LocalDate.now()), 12);
    private final JTextField startDateField = new JTextField(DateUtils.format(LocalDate.now()), 10);
    private final JTextField endDateField = new JTextField(DateUtils.format(LocalDate.now()), 10);
    private final JTextField monthField = new JTextField(String.valueOf(LocalDate.now().getMonthValue()), 4);
    private final JTextField yearField = new JTextField(String.valueOf(LocalDate.now().getYear()), 6);
    private final JComboBox<Integer> quarterCombo = new JComboBox<Integer>(new Integer[]{1, 2, 3, 4});
    private final JTextField quarterYearField = new JTextField(String.valueOf(LocalDate.now().getYear()), 6);
    private final JPanel parameterPanel = new JPanel(new CardLayout());
    private final JPanel dailyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, UIConstants.SPACING_SM, 0));
    private final JPanel rangePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, UIConstants.SPACING_SM, 0));
    private final JPanel monthlyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, UIConstants.SPACING_SM, 0));
    private final JPanel quarterlyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, UIConstants.SPACING_SM, 0));

    public ReportsPanel() {
        UiUtils.styleContentPanel(this);
        setLayout(new BorderLayout(UIConstants.SPACING_MD, UIConstants.SPACING_MD));
        add(buildForm(), BorderLayout.NORTH);
        add(buildHint(), BorderLayout.CENTER);
    }

    private JPanel buildForm() {
        reportTypeCombo.setFont(UIConstants.font(UIConstants.FONT_INPUT));
        
        periodCombo.setFont(UIConstants.font(UIConstants.FONT_INPUT));
        periodCombo.addActionListener(e -> showParameterCard());

        UiUtils.styleField(dateField);
        UiUtils.styleField(startDateField);
        UiUtils.styleField(endDateField);
        UiUtils.styleField(monthField);
        UiUtils.styleField(yearField);
        quarterCombo.setFont(UIConstants.font(UIConstants.FONT_INPUT));
        UiUtils.styleField(quarterYearField);

        dailyPanel.setOpaque(false);
        UiUtils.styleLabel(dateLabel);
        dailyPanel.add(dateLabel);
        dailyPanel.add(dateField);

        rangePanel.setOpaque(false);
        JLabel fromLabel = new JLabel("Từ ngày");
        UiUtils.styleLabel(fromLabel);
        rangePanel.add(fromLabel);
        rangePanel.add(startDateField);
        JLabel toLabel = new JLabel("Đến ngày");
        UiUtils.styleLabel(toLabel);
        rangePanel.add(toLabel);
        rangePanel.add(endDateField);

        monthlyPanel.setOpaque(false);
        JLabel monthLabel = new JLabel("Tháng");
        UiUtils.styleLabel(monthLabel);
        monthlyPanel.add(monthLabel);
        monthlyPanel.add(monthField);
        JLabel yearLabel = new JLabel("Năm");
        UiUtils.styleLabel(yearLabel);
        monthlyPanel.add(yearLabel);
        monthlyPanel.add(yearField);

        quarterlyPanel.setOpaque(false);
        JLabel quarterLabel = new JLabel("Quý");
        UiUtils.styleLabel(quarterLabel);
        quarterlyPanel.add(quarterLabel);
        quarterlyPanel.add(quarterCombo);
        JLabel qYearLabel = new JLabel("Năm");
        UiUtils.styleLabel(qYearLabel);
        quarterlyPanel.add(qYearLabel);
        quarterlyPanel.add(quarterYearField);

        parameterPanel.setOpaque(false);
        parameterPanel.add(dailyPanel, "daily");
        parameterPanel.add(rangePanel, "range");
        parameterPanel.add(monthlyPanel, "monthly");
        parameterPanel.add(quarterlyPanel, "quarterly");

        JButton previewButton = UiUtils.secondaryButton("Xem báo cáo");
        previewButton.addActionListener(e -> preview());
        JButton printButton = UiUtils.secondaryButton("In");
        printButton.addActionListener(e -> print());
        JButton exportPdfButton = UiUtils.secondaryButton("Xuất PDF");
        exportPdfButton.addActionListener(e -> exportReport(controller.getPdfStrategy()));
        
        JButton exportXlsxButton = UiUtils.secondaryButton("Xuất Excel");
        exportXlsxButton.addActionListener(e -> exportReport(controller.getXlsxStrategy()));
        
        JButton exportDocxButton = UiUtils.secondaryButton("Xuất Word");
        exportDocxButton.addActionListener(e -> exportReport(controller.getDocxStrategy()));

        JPanel body = new FormLayout()
                .addRow("Chọn báo cáo", reportTypeCombo)
                .addRow("Thời gian", periodCombo)
                .addFullWidth(parameterPanel)
                .build();
        showParameterCard();
        return UiUtils.wrapFormCard(body, previewButton, printButton, exportPdfButton, exportXlsxButton, exportDocxButton);
    }

    private JPanel buildHint() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.add(hintLine("Chọn báo cáo, nhập ngày hoặc tháng, rồi bấm Xem báo cáo, In, Xuất PDF, Xuất Excel hoặc Xuất Word."));
        panel.add(Box.createVerticalStrut(UIConstants.SPACING_SM));
        panel.add(hintLine("In hóa đơn từng đơn tại màn Hóa đơn."));
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
        int idx = periodCombo.getSelectedIndex();
        if (idx == 0) {
            dateLabel.setText("Chọn ngày (dd/MM/yyyy)");
            layout.show(parameterPanel, "daily");
        } else if (idx == 1) {
            dateLabel.setText("Chọn ngày trong tuần (dd/MM/yyyy)");
            layout.show(parameterPanel, "daily");
        } else if (idx == 2) {
            layout.show(parameterPanel, "monthly");
        } else if (idx == 3) {
            layout.show(parameterPanel, "quarterly");
        } else {
            layout.show(parameterPanel, "range");
        }
    }

    private void preview() {
        int reportType = reportTypeCombo.getSelectedIndex();
        PeriodRange range = calculatePeriodRangeAndDesc();
        if (reportType == 0) { // Doanh thu
            int periodIdx = periodCombo.getSelectedIndex();
            if (periodIdx == 2) { // Tháng
                int month = parseMonth();
                int year = parseYear();
                controller.previewMonthlyRevenue(year, month, this);
            } else { // Ngày, Tuần, Quý, Khoảng ngày
                int idx = periodCombo.getSelectedIndex();
                if (idx == 0) {
                    controller.previewDailyRevenue(range.startDate, this);
                } else {
                    controller.previewDailyRevenue(range.startDate, range.endDate, this);
                }
            }
        } else if (reportType == 1) { // Sản phẩm bán chạy
            controller.previewBestSellingProducts(range.startDate, range.endDate, range.periodDesc, this);
        } else if (reportType == 2) { // Doanh thu từng thu ngân
            controller.previewCashierPerformance(range.startDate, range.endDate, range.periodDesc, this);
        }
    }

    private void print() {
        int reportType = reportTypeCombo.getSelectedIndex();
        PeriodRange range = calculatePeriodRangeAndDesc();
        if (reportType == 0) { // Doanh thu
            int periodIdx = periodCombo.getSelectedIndex();
            if (periodIdx == 2) { // Tháng
                int month = parseMonth();
                int year = parseYear();
                controller.printMonthlyRevenue(year, month, this);
            } else { // Ngày, Tuần, Quý, Khoảng ngày
                int idx = periodCombo.getSelectedIndex();
                if (idx == 0) {
                    controller.printDailyRevenue(range.startDate, this);
                } else {
                    controller.printDailyRevenue(range.startDate, range.endDate, this);
                }
            }
        } else if (reportType == 1) { // Sản phẩm bán chạy
            controller.printBestSellingProducts(range.startDate, range.endDate, range.periodDesc, this);
        } else if (reportType == 2) { // Doanh thu từng thu ngân
            controller.printCashierPerformance(range.startDate, range.endDate, range.periodDesc, this);
        }
    }

    private void exportReport(ReportExportStrategy strategy) {
        int reportType = reportTypeCombo.getSelectedIndex();
        PeriodRange range = calculatePeriodRangeAndDesc();
        if (reportType == 0) { // Doanh thu
            int periodIdx = periodCombo.getSelectedIndex();
            if (periodIdx == 2) { // Tháng
                int month = parseMonth();
                int year = parseYear();
                controller.exportMonthlyRevenue(year, month, strategy, this);
            } else { // Ngày, Tuần, Quý, Khoảng ngày
                int idx = periodCombo.getSelectedIndex();
                if (idx == 0) {
                    controller.exportDailyRevenue(range.startDate, strategy, this);
                } else {
                    controller.exportDailyRevenue(range.startDate, range.endDate, strategy, this);
                }
            }
        } else if (reportType == 1) { // Sản phẩm bán chạy
            controller.exportBestSellingProducts(range.startDate, range.endDate, range.periodDesc, strategy, this);
        } else if (reportType == 2) { // Doanh thu từng thu ngân
            controller.exportCashierPerformance(range.startDate, range.endDate, range.periodDesc, strategy, this);
        }
    }

    private PeriodRange calculatePeriodRangeAndDesc() {
        int idx = periodCombo.getSelectedIndex();
        if (idx == 0) { // Ngày
            LocalDate date = parseDate();
            return new PeriodRange(date, date, "Ngày " + DateUtils.format(date));
        } else if (idx == 1) { // Tuần
            LocalDate date = parseDate();
            LocalDate start = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate end = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            return new PeriodRange(start, end, "Tuần từ " + DateUtils.format(start) + " đến " + DateUtils.format(end));
        } else if (idx == 2) { // Tháng
            int month = parseMonth();
            int year = parseYear();
            LocalDate start = LocalDate.of(year, month, 1);
            LocalDate end = start.with(TemporalAdjusters.lastDayOfMonth());
            return new PeriodRange(start, end, "Tháng " + month + "/" + year);
        } else if (idx == 3) { // Quý
            int quarter = (Integer) quarterCombo.getSelectedItem();
            int year = parseQuarterYear();
            int startMonth = (quarter - 1) * 3 + 1;
            int endMonth = quarter * 3;
            LocalDate start = LocalDate.of(year, startMonth, 1);
            LocalDate end = LocalDate.of(year, endMonth, 1).with(TemporalAdjusters.lastDayOfMonth());
            return new PeriodRange(start, end, "Quý " + quarter + " năm " + year);
        } else { // Khoảng ngày
            LocalDate start = parseStartDate();
            LocalDate end = parseEndDate();
            return new PeriodRange(start, end, "Từ ngày " + DateUtils.format(start) + " đến " + DateUtils.format(end));
        }
    }

    private LocalDate parseDate() {
        return DateUtils.parseRequired(dateField.getText());
    }

    private LocalDate parseStartDate() {
        return DateUtils.parseRequired(startDateField.getText());
    }

    private LocalDate parseEndDate() {
        return DateUtils.parseRequired(endDateField.getText());
    }

    private int parseMonth() {
        return Integer.parseInt(monthField.getText().trim());
    }

    private int parseYear() {
        return Integer.parseInt(yearField.getText().trim());
    }

    private int parseQuarterYear() {
        return Integer.parseInt(quarterYearField.getText().trim());
    }

    private static class PeriodRange {
        final LocalDate startDate;
        final LocalDate endDate;
        final String periodDesc;

        PeriodRange(LocalDate startDate, LocalDate endDate, String periodDesc) {
            this.startDate = startDate;
            this.endDate = endDate;
            this.periodDesc = periodDesc;
        }
    }
}
