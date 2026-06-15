package com.brewpoint.pos.report.datasource;

import com.brewpoint.pos.dao.OrderDAO;
import com.brewpoint.pos.dao.ReportDAO;
import com.brewpoint.pos.model.OrderItemDetail;
import com.brewpoint.pos.model.OrderSummary;
import com.brewpoint.pos.model.ProductSalesStat;
import com.brewpoint.pos.model.ToppingSnapshot;
import com.brewpoint.pos.report.util.ReportFormatUtils;
import com.brewpoint.pos.util.ValidationException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class ReportDataSource {
    private final ReportDAO reportDAO = new ReportDAO();
    private final OrderDAO orderDAO = new OrderDAO();

    public OrderSummary loadOrderSummary(long orderId) throws SQLException {
        OrderSummary summary = reportDAO.findOrderSummary(orderId);
        if (summary == null) {
            throw new ValidationException("Không tìm thấy hóa đơn.");
        }
        return summary;
    }

    public List<ReceiptLineRow> loadReceiptLines(long orderId) throws SQLException {
        List<OrderItemDetail> details = orderDAO.findDetails(orderId);
        List<ReceiptLineRow> rows = new ArrayList<ReceiptLineRow>();
        for (OrderItemDetail detail : details) {
            rows.add(new ReceiptLineRow(
                    detail.getProductName(),
                    buildOptions(detail),
                    detail.getQuantity(),
                    ReportFormatUtils.money(detail.getUnitPrice()),
                    ReportFormatUtils.money(detail.getLineTotal())
            ));
        }
        return rows;
    }

    public DailyRevenueMetrics loadDailyRevenue(LocalDate date) throws SQLException {
        return reportDAO.dailyRevenue(date);
    }

    public List<MonthlyRevenueDayRow> loadMonthlyRevenueDays(int year, int month) throws SQLException {
        return reportDAO.monthlyRevenueByDay(year, month);
    }

    public java.math.BigDecimal loadMonthlyRevenueTotal(int year, int month) throws SQLException {
        return reportDAO.monthlyRevenueTotal(year, month);
    }

    public List<ProductSalesStat> loadBestSellingProducts(YearMonth yearMonth) throws SQLException {
        return reportDAO.bestSellingProducts(yearMonth);
    }

    public List<CashierPerformanceRow> loadCashierPerformance(YearMonth yearMonth) throws SQLException {
        return reportDAO.cashierPerformance(yearMonth);
    }

    private String buildOptions(OrderItemDetail detail) {
        StringBuilder builder = new StringBuilder(detail.getSizeName());
        for (int i = 0; i < detail.getToppings().size(); i++) {
            ToppingSnapshot topping = detail.getToppings().get(i);
            builder.append(i == 0 ? " | " : ", ");
            builder.append(topping.getName());
        }
        String note = detail.getNote();
        if (note != null && note.trim().length() > 0) {
            builder.append(" | ").append(note.trim());
        }
        return builder.toString();
    }
}
