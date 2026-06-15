package com.brewpoint.pos.report.util;

import com.brewpoint.pos.model.OrderSummary;
import com.brewpoint.pos.model.PaymentMethod;
import com.brewpoint.pos.model.ProductSalesStat;
import com.brewpoint.pos.report.datasource.DailyRevenueMetrics;
import com.brewpoint.pos.report.datasource.ProductSalesReportRow;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

public final class ReportParameterBuilder {

    private ReportParameterBuilder() {
    }

    public static Map<String, Object> dailyRevenue(DailyRevenueMetrics metrics) {
        if (metrics == null) {
            throw new IllegalArgumentException("Dữ liệu báo cáo ngày không hợp lệ.");
        }
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("REPORT_TITLE", "Báo cáo doanh thu theo ngày");
        parameters.put("GENERATED_AT", ReportFormatUtils.generatedAt());
        parameters.put("REPORT_DATE", ReportFormatUtils.date(metrics.getReportDate()));
        parameters.put("TOTAL_REVENUE", ReportFormatUtils.money(metrics.getTotalRevenue()));
        parameters.put("ORDER_COUNT", String.valueOf(metrics.getOrderCount()));
        parameters.put("AVERAGE_ORDER", ReportFormatUtils.money(metrics.getAverageOrderValue()));
        return parameters;
    }

    public static Map<String, Object> monthlyRevenue(int year, int month, BigDecimal totalRevenue) {
        validateMonth(year, month);
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("REPORT_TITLE", "Báo cáo doanh thu theo tháng");
        parameters.put("GENERATED_AT", ReportFormatUtils.generatedAt());
        parameters.put("REPORT_MONTH", ReportFormatUtils.monthYear(year, month));
        parameters.put("TOTAL_REVENUE", ReportFormatUtils.money(totalRevenue));
        return parameters;
    }

    public static Map<String, Object> bestSellingProducts(YearMonth yearMonth) {
        if (yearMonth == null) {
            throw new IllegalArgumentException("Tháng báo cáo không hợp lệ.");
        }
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("REPORT_TITLE", "Báo cáo sản phẩm bán chạy");
        parameters.put("GENERATED_AT", ReportFormatUtils.generatedAt());
        parameters.put("REPORT_MONTH", ReportFormatUtils.monthYear(yearMonth));
        return parameters;
    }

    public static Map<String, Object> cashierPerformance(YearMonth yearMonth) {
        if (yearMonth == null) {
            throw new IllegalArgumentException("Tháng báo cáo không hợp lệ.");
        }
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("REPORT_TITLE", "Báo cáo doanh thu từng thu ngân");
        parameters.put("GENERATED_AT", ReportFormatUtils.generatedAt());
        parameters.put("REPORT_MONTH", ReportFormatUtils.monthYear(yearMonth));
        return parameters;
    }

    public static Map<String, Object> receipt(OrderSummary order) {
        if (order == null) {
            throw new IllegalArgumentException("Hóa đơn không hợp lệ.");
        }
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("SHOP_NAME", "BrewPoint POS");
        parameters.put("ORDER_CODE", order.getOrderCode());
        parameters.put("ORDER_TIME", ReportFormatUtils.dateTime(order.getOrderTime()));
        parameters.put("CASHIER_NAME", order.getEmployeeName());
        parameters.put("TOTAL_AMOUNT", ReportFormatUtils.money(order.getTotalAmount()));
        parameters.put("PAYMENT_METHOD", order.getPaymentMethod().getDisplayName());
        parameters.put("CASH_RECEIVED", formatCashReceived(order));
        parameters.put("CHANGE_AMOUNT", formatChangeAmount(order));
        return parameters;
    }

    public static ProductSalesReportRow productSalesRow(ProductSalesStat stat) {
        if (stat == null) {
            throw new IllegalArgumentException("Dòng sản phẩm không hợp lệ.");
        }
        return new ProductSalesReportRow(
                stat.getProductName(),
                stat.getQuantity(),
                ReportFormatUtils.money(stat.getRevenue())
        );
    }

    public static void validateReportDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Ngày báo cáo không hợp lệ.");
        }
    }

    public static void validateMonth(int year, int month) {
        if (year < 2000 || year > 2100 || month < 1 || month > 12) {
            throw new IllegalArgumentException("Tháng/năm báo cáo không hợp lệ.");
        }
    }

    private static String formatCashReceived(OrderSummary order) {
        if (order.getPaymentMethod() != PaymentMethod.CASH || order.getReceivedAmount() == null) {
            return "—";
        }
        return ReportFormatUtils.money(order.getReceivedAmount());
    }

    private static String formatChangeAmount(OrderSummary order) {
        if (order.getPaymentMethod() != PaymentMethod.CASH || order.getChangeAmount() == null) {
            return "—";
        }
        return ReportFormatUtils.money(order.getChangeAmount());
    }
}
