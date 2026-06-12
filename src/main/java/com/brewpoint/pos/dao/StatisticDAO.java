package com.brewpoint.pos.dao;

import com.brewpoint.pos.database.DatabaseManager;
import com.brewpoint.pos.model.ProductSalesStat;
import com.brewpoint.pos.model.StatisticSummary;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StatisticDAO {
    public StatisticSummary summary(LocalDate selectedDate) throws SQLException {
        LocalDate safeDate = selectedDate == null ? LocalDate.now() : selectedDate;
        try (Connection connection = DatabaseManager.getInstance().getConnection()) {
            BigDecimal todayRevenue = revenueForDate(connection, LocalDate.now());
            BigDecimal selectedRevenue = revenueForDate(connection, safeDate);
            int orderCount = orderCountForDate(connection, safeDate);
            return new StatisticSummary(todayRevenue, selectedRevenue, orderCount);
        }
    }

    public List<ProductSalesStat> topProducts(LocalDate selectedDate) throws SQLException {
        LocalDate safeDate = selectedDate == null ? LocalDate.now() : selectedDate;
        String sql = "SELECT oi.product_name_snapshot, SUM(oi.quantity) AS qty, SUM(oi.line_total) AS revenue "
                + "FROM order_items oi JOIN orders o ON o.order_id = oi.order_id "
                + "WHERE o.status = 'COMPLETED' AND DATE(o.order_time) = ? "
                + "GROUP BY oi.product_name_snapshot ORDER BY qty DESC, revenue DESC LIMIT 10";
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, Date.valueOf(safeDate));
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

    private BigDecimal revenueForDate(Connection connection, LocalDate date) throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) AS revenue FROM orders WHERE status = 'COMPLETED' AND DATE(order_time) = ?";
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
}
