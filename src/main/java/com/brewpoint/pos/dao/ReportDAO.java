package com.brewpoint.pos.dao;

import com.brewpoint.pos.database.DatabaseManager;
import com.brewpoint.pos.model.OrderStatus;
import com.brewpoint.pos.model.OrderSummary;
import com.brewpoint.pos.model.PaymentMethod;
import com.brewpoint.pos.model.ProductSalesStat;
import com.brewpoint.pos.report.datasource.CashierPerformanceRow;
import com.brewpoint.pos.report.datasource.DailyRevenueMetrics;
import com.brewpoint.pos.report.datasource.MonthlyRevenueDayRow;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

    public OrderSummary findOrderSummary(long orderId) throws SQLException {
        String sql = "SELECT o.order_id, o.order_code, o.employee_id, e.full_name AS employee_name, o.order_time, "
                + "o.status, o.payment_method, o.total_amount, o.received_amount, o.change_amount "
                + "FROM orders o JOIN employees e ON e.employee_id = o.employee_id WHERE o.order_id = ?";
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, orderId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapSummary(resultSet);
                }
            }
        }
        return null;
    }

    public DailyRevenueMetrics dailyRevenue(LocalDate date) throws SQLException {
        LocalDate safeDate = date == null ? LocalDate.now() : date;
        try (Connection connection = DatabaseManager.getInstance().getConnection()) {
            BigDecimal revenue = revenueForDate(connection, safeDate);
            int orderCount = orderCountForDate(connection, safeDate);
            BigDecimal average = BigDecimal.ZERO;
            if (orderCount > 0) {
                average = revenue.divide(BigDecimal.valueOf(orderCount), 0, RoundingMode.HALF_UP);
            }
            return new DailyRevenueMetrics(safeDate, revenue, orderCount, average);
        }
    }

    public List<MonthlyRevenueDayRow> monthlyRevenueByDay(int year, int month) throws SQLException {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.plusMonths(1).atDay(1);
        String sql = "SELECT DATE(order_time) AS sale_date, COALESCE(SUM(total_amount), 0) AS revenue, COUNT(*) AS orders "
                + "FROM orders WHERE status = 'COMPLETED' AND order_time >= ? AND order_time < ? "
                + "GROUP BY DATE(order_time) ORDER BY sale_date";
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, Date.valueOf(start));
            statement.setDate(2, Date.valueOf(end));
            try (ResultSet resultSet = statement.executeQuery()) {
                List<MonthlyRevenueDayRow> rows = new ArrayList<MonthlyRevenueDayRow>();
                while (resultSet.next()) {
                    LocalDate saleDate = resultSet.getDate("sale_date").toLocalDate();
                    rows.add(new MonthlyRevenueDayRow(
                            saleDate,
                            resultSet.getBigDecimal("revenue"),
                            resultSet.getInt("orders")
                    ));
                }
                return rows;
            }
        }
    }

    public BigDecimal monthlyRevenueTotal(int year, int month) throws SQLException {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.plusMonths(1).atDay(1);
        String sql = "SELECT COALESCE(SUM(total_amount), 0) AS revenue FROM orders "
                + "WHERE status = 'COMPLETED' AND order_time >= ? AND order_time < ?";
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, Date.valueOf(start));
            statement.setDate(2, Date.valueOf(end));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBigDecimal("revenue");
                }
            }
        }
        return BigDecimal.ZERO;
    }

    public List<ProductSalesStat> bestSellingProducts(YearMonth yearMonth) throws SQLException {
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.plusMonths(1).atDay(1);
        String sql = "SELECT oi.product_name_snapshot, SUM(oi.quantity) AS qty, SUM(oi.line_total) AS revenue "
                + "FROM order_items oi JOIN orders o ON o.order_id = oi.order_id "
                + "WHERE o.status = 'COMPLETED' AND o.order_time >= ? AND o.order_time < ? "
                + "GROUP BY oi.product_name_snapshot ORDER BY qty DESC, revenue DESC";
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, Date.valueOf(start));
            statement.setDate(2, Date.valueOf(end));
            try (ResultSet resultSet = statement.executeQuery()) {
                List<ProductSalesStat> stats = new ArrayList<ProductSalesStat>();
                while (resultSet.next()) {
                    stats.add(new ProductSalesStat(
                            resultSet.getString("product_name_snapshot"),
                            resultSet.getInt("qty"),
                            resultSet.getBigDecimal("revenue")
                    ));
                }
                return stats;
            }
        }
    }

    public List<CashierPerformanceRow> cashierPerformance(YearMonth yearMonth) throws SQLException {
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.plusMonths(1).atDay(1);
        String sql = "SELECT e.full_name, COUNT(o.order_id) AS order_count, COALESCE(SUM(o.total_amount), 0) AS revenue "
                + "FROM orders o JOIN employees e ON e.employee_id = o.employee_id "
                + "WHERE o.status = 'COMPLETED' AND o.order_time >= ? AND o.order_time < ? "
                + "GROUP BY e.employee_id, e.full_name ORDER BY revenue DESC, order_count DESC";
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, Date.valueOf(start));
            statement.setDate(2, Date.valueOf(end));
            try (ResultSet resultSet = statement.executeQuery()) {
                List<CashierPerformanceRow> rows = new ArrayList<CashierPerformanceRow>();
                while (resultSet.next()) {
                    rows.add(new CashierPerformanceRow(
                            resultSet.getString("full_name"),
                            resultSet.getInt("order_count"),
                            resultSet.getBigDecimal("revenue")
                    ));
                }
                return rows;
            }
        }
    }

    private BigDecimal revenueForDate(Connection connection, LocalDate date) throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) AS revenue FROM orders "
                + "WHERE status = 'COMPLETED' AND DATE(order_time) = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, Date.valueOf(date));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBigDecimal("revenue");
                }
            }
        }
        return BigDecimal.ZERO;
    }

    private int orderCountForDate(Connection connection, LocalDate date) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM orders WHERE status = 'COMPLETED' AND DATE(order_time) = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, Date.valueOf(date));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("total");
                }
            }
        }
        return 0;
    }

    private OrderSummary mapSummary(ResultSet resultSet) throws SQLException {
        OrderSummary summary = new OrderSummary();
        summary.setOrderId(resultSet.getLong("order_id"));
        summary.setOrderCode(resultSet.getString("order_code"));
        summary.setEmployeeId(resultSet.getInt("employee_id"));
        summary.setEmployeeName(resultSet.getString("employee_name"));
        summary.setOrderTime(resultSet.getTimestamp("order_time").toLocalDateTime());
        summary.setStatus(OrderStatus.fromDatabase(resultSet.getString("status")));
        summary.setPaymentMethod(PaymentMethod.valueOf(resultSet.getString("payment_method")));
        summary.setTotalAmount(resultSet.getBigDecimal("total_amount"));
        summary.setReceivedAmount(resultSet.getBigDecimal("received_amount"));
        summary.setChangeAmount(resultSet.getBigDecimal("change_amount"));
        return summary;
    }
}
