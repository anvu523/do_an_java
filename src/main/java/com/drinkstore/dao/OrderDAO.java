package com.drinkstore.dao;

import com.drinkstore.database.DatabaseConnection;
import com.drinkstore.model.Order;
import com.drinkstore.model.OrderDetail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    private final ProductDAO productDAO = new ProductDAO();

    public int insert(Order order) throws SQLException {
        Connection connection = DatabaseConnection.getInstance().getConnection();
        boolean oldAutoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);
            int orderId = insertOrderHeader(order, connection);
            for (OrderDetail detail : order.getDetails()) {
                boolean stockUpdated = productDAO.decreaseStock(connection, detail.getProductId(), detail.getQuantity());
                if (!stockUpdated) {
                    throw new SQLException("Sản phẩm mã " + detail.getProductId() + " không đủ tồn kho hoặc đã ngừng bán.");
                }
                detail.setOrderId(orderId);
                insertOrderDetail(detail, connection);
            }
            connection.commit();
            return orderId;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(oldAutoCommit);
            connection.close();
        }
    }

    public List<Order> findOrders(Integer orderId, LocalDate date, Integer employeeId) throws SQLException {
        String sql = """
                SELECT o.order_id, o.employee_id, e.full_name AS employee_name, o.order_date, o.total_amount
                FROM orders o
                JOIN employees e ON e.employee_id = o.employee_id
                WHERE (? IS NULL OR o.order_id = ?)
                  AND (? IS NULL OR (o.order_date >= ? AND o.order_date < ?))
                  AND (? IS NULL OR o.employee_id = ?)
                ORDER BY o.order_date DESC, o.order_id DESC
                """;
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (orderId == null) {
                statement.setNull(1, Types.INTEGER);
                statement.setNull(2, Types.INTEGER);
            } else {
                statement.setInt(1, orderId);
                statement.setInt(2, orderId);
            }
            if (date == null) {
                statement.setNull(3, Types.TIMESTAMP);
                statement.setNull(4, Types.TIMESTAMP);
                statement.setNull(5, Types.TIMESTAMP);
            } else {
                statement.setTimestamp(3, Timestamp.valueOf(date.atStartOfDay()));
                statement.setTimestamp(4, Timestamp.valueOf(date.atStartOfDay()));
                statement.setTimestamp(5, Timestamp.valueOf(date.plusDays(1).atStartOfDay()));
            }
            if (employeeId == null) {
                statement.setNull(6, Types.INTEGER);
                statement.setNull(7, Types.INTEGER);
            } else {
                statement.setInt(6, employeeId);
                statement.setInt(7, employeeId);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Order> orders = new ArrayList<>();
                while (resultSet.next()) {
                    orders.add(mapOrder(resultSet));
                }
                return orders;
            }
        }
    }

    public List<OrderDetail> findDetails(int orderId) throws SQLException {
        String sql = """
                SELECT od.order_detail_id, od.order_id, od.product_id, p.name AS product_name,
                       od.quantity, od.unit_price, od.line_total
                FROM order_details od
                JOIN products p ON p.product_id = od.product_id
                WHERE od.order_id = ?
                ORDER BY od.order_detail_id
                """;
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, orderId);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<OrderDetail> details = new ArrayList<>();
                while (resultSet.next()) {
                    details.add(new OrderDetail(
                            resultSet.getInt("order_detail_id"),
                            resultSet.getInt("order_id"),
                            resultSet.getInt("product_id"),
                            resultSet.getString("product_name"),
                            resultSet.getInt("quantity"),
                            resultSet.getBigDecimal("unit_price"),
                            resultSet.getBigDecimal("line_total")
                    ));
                }
                return details;
            }
        }
    }

    private int insertOrderHeader(Order order, Connection connection) throws SQLException {
        String sql = "INSERT INTO orders (employee_id, order_date, total_amount) VALUES (?, NOW(), ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, order.getEmployeeId());
            statement.setBigDecimal(2, order.getTotalAmount());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Không lấy được mã hóa đơn vừa tạo.");
    }

    private void insertOrderDetail(OrderDetail detail, Connection connection) throws SQLException {
        String sql = "INSERT INTO order_details (order_id, product_id, quantity, unit_price, line_total) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, detail.getOrderId());
            statement.setInt(2, detail.getProductId());
            statement.setInt(3, detail.getQuantity());
            statement.setBigDecimal(4, detail.getUnitPrice());
            statement.setBigDecimal(5, detail.getLineTotal());
            statement.executeUpdate();
        }
    }

    private Order mapOrder(ResultSet resultSet) throws SQLException {
        return new Order(
                resultSet.getInt("order_id"),
                resultSet.getInt("employee_id"),
                resultSet.getString("employee_name"),
                resultSet.getTimestamp("order_date").toLocalDateTime(),
                resultSet.getBigDecimal("total_amount")
        );
    }
}
