package com.drinkstore.dao;

import com.drinkstore.database.DatabaseConnection;
import com.drinkstore.model.ProductSalesStat;

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
    public BigDecimal revenueByDate(LocalDate date) throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE DATE(order_date) = ?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, Date.valueOf(date));
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getBigDecimal(1) : BigDecimal.ZERO;
            }
        }
    }

    public BigDecimal revenueByMonth(int year, int month) throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE YEAR(order_date) = ? AND MONTH(order_date) = ?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, year);
            statement.setInt(2, month);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getBigDecimal(1) : BigDecimal.ZERO;
            }
        }
    }

    public int countOrders() throws SQLException {
        String sql = "SELECT COUNT(*) FROM orders";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            return resultSet.next() ? resultSet.getInt(1) : 0;
        }
    }

    public List<ProductSalesStat> topSellingProducts(int limit) throws SQLException {
        String sql = """
                SELECT p.product_id, p.name, COALESCE(SUM(od.quantity), 0) AS total_quantity,
                       COALESCE(SUM(od.line_total), 0) AS total_revenue
                FROM order_details od
                JOIN products p ON p.product_id = od.product_id
                GROUP BY p.product_id, p.name
                ORDER BY total_quantity DESC, total_revenue DESC
                LIMIT ?
                """;
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, limit);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<ProductSalesStat> stats = new ArrayList<>();
                while (resultSet.next()) {
                    stats.add(new ProductSalesStat(
                            resultSet.getInt("product_id"),
                            resultSet.getString("name"),
                            resultSet.getInt("total_quantity"),
                            resultSet.getBigDecimal("total_revenue")
                    ));
                }
                return stats;
            }
        }
    }
}
