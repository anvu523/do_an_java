package com.brewpoint.pos.dao;

import com.brewpoint.pos.database.DatabaseManager;
import com.brewpoint.pos.model.CartLine;
import com.brewpoint.pos.model.OrderItemDetail;
import com.brewpoint.pos.model.OrderStatus;
import com.brewpoint.pos.model.OrderSummary;
import com.brewpoint.pos.model.PaymentMethod;
import com.brewpoint.pos.model.ToppingSnapshot;

import java.math.BigDecimal;
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
    public long insertHeader(Connection connection, String orderCode, int employeeId, PaymentMethod paymentMethod,
                             BigDecimal subtotal, BigDecimal totalAmount, BigDecimal receivedAmount,
                             BigDecimal changeAmount) throws SQLException {
        String sql = "INSERT INTO orders (order_code, employee_id, order_time, status, payment_method, subtotal, "
                + "discount_amount, total_amount, received_amount, change_amount) VALUES (?, ?, NOW(), ?, ?, ?, 0, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, orderCode);
            statement.setInt(2, employeeId);
            statement.setString(3, OrderStatus.COMPLETED.name());
            statement.setString(4, paymentMethod.name());
            statement.setBigDecimal(5, subtotal);
            statement.setBigDecimal(6, totalAmount);
            if (receivedAmount == null) {
                statement.setNull(7, Types.DECIMAL);
            } else {
                statement.setBigDecimal(7, receivedAmount);
            }
            if (changeAmount == null) {
                statement.setNull(8, Types.DECIMAL);
            } else {
                statement.setBigDecimal(8, changeAmount);
            }
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }
        throw new SQLException("Không lấy được mã hóa đơn vừa tạo.");
    }

    public long insertItem(Connection connection, long orderId, CartLine line) throws SQLException {
        String sql = "INSERT INTO order_items (order_id, product_id, product_code_snapshot, product_name_snapshot, "
                + "size_code_snapshot, size_name_snapshot, base_price_snapshot, unit_price, quantity, line_total, note) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, orderId);
            statement.setInt(2, line.getRequest().getProductId());
            statement.setString(3, line.getProductCode());
            statement.setString(4, line.getProductName());
            statement.setString(5, line.getSizeCode());
            statement.setString(6, line.getSizeName());
            statement.setBigDecimal(7, line.getBasePrice());
            statement.setBigDecimal(8, line.getUnitPrice());
            statement.setInt(9, line.getRequest().getQuantity());
            statement.setBigDecimal(10, line.getLineTotal());
            statement.setString(11, emptyToNull(line.getRequest().getNote()));
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }
        throw new SQLException("Không lấy được mã dòng hóa đơn.");
    }

    public void insertTopping(Connection connection, long orderItemId, ToppingSnapshot topping) throws SQLException {
        String sql = "INSERT INTO order_item_toppings (order_item_id, topping_id, topping_code_snapshot, "
                + "topping_name_snapshot, extra_price_snapshot) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, orderItemId);
            statement.setInt(2, topping.getToppingId());
            statement.setString(3, topping.getToppingCode());
            statement.setString(4, topping.getName());
            statement.setBigDecimal(5, topping.getExtraPrice());
            statement.executeUpdate();
        }
    }

    public List<OrderSummary> search(String orderCode, LocalDate date, Integer employeeId) throws SQLException {
        String sql = "SELECT o.order_id, o.order_code, o.employee_id, e.full_name AS employee_name, o.order_time, "
                + "o.status, o.payment_method, o.total_amount, o.received_amount, o.change_amount "
                + "FROM orders o JOIN employees e ON e.employee_id = o.employee_id "
                + "WHERE (? = '' OR o.order_code LIKE ?) "
                + "AND (? IS NULL OR (o.order_time >= ? AND o.order_time < ?)) "
                + "AND (? IS NULL OR o.employee_id = ?) "
                + "ORDER BY o.order_time DESC, o.order_id DESC";
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            String cleanCode = orderCode == null ? "" : orderCode.trim();
            statement.setString(1, cleanCode);
            statement.setString(2, "%" + cleanCode + "%");
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
                statement.setInt(6, employeeId.intValue());
                statement.setInt(7, employeeId.intValue());
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                List<OrderSummary> orders = new ArrayList<OrderSummary>();
                while (resultSet.next()) {
                    orders.add(mapSummary(resultSet));
                }
                return orders;
            }
        }
    }

    public List<OrderItemDetail> findDetails(long orderId) throws SQLException {
        String sql = "SELECT order_item_id, product_name_snapshot, size_name_snapshot, base_price_snapshot, "
                + "unit_price, quantity, line_total, note FROM order_items WHERE order_id = ? ORDER BY order_item_id";
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, orderId);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<OrderItemDetail> details = new ArrayList<OrderItemDetail>();
                while (resultSet.next()) {
                    OrderItemDetail detail = new OrderItemDetail();
                    detail.setOrderItemId(resultSet.getLong("order_item_id"));
                    detail.setProductName(resultSet.getString("product_name_snapshot"));
                    detail.setSizeName(resultSet.getString("size_name_snapshot"));
                    detail.setBasePrice(resultSet.getBigDecimal("base_price_snapshot"));
                    detail.setUnitPrice(resultSet.getBigDecimal("unit_price"));
                    detail.setQuantity(resultSet.getInt("quantity"));
                    detail.setLineTotal(resultSet.getBigDecimal("line_total"));
                    detail.setNote(resultSet.getString("note"));
                    detail.getToppings().addAll(findToppings(connection, detail.getOrderItemId()));
                    details.add(detail);
                }
                return details;
            }
        }
    }

    private List<ToppingSnapshot> findToppings(Connection connection, long orderItemId) throws SQLException {
        String sql = "SELECT topping_id, topping_code_snapshot, topping_name_snapshot, extra_price_snapshot "
                + "FROM order_item_toppings WHERE order_item_id = ? ORDER BY order_item_topping_id";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, orderItemId);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<ToppingSnapshot> toppings = new ArrayList<ToppingSnapshot>();
                while (resultSet.next()) {
                    toppings.add(new ToppingSnapshot(
                            resultSet.getInt("topping_id"),
                            resultSet.getString("topping_code_snapshot"),
                            resultSet.getString("topping_name_snapshot"),
                            resultSet.getBigDecimal("extra_price_snapshot")
                    ));
                }
                return toppings;
            }
        }
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

    private String emptyToNull(String value) {
        String clean = value == null ? "" : value.trim();
        return clean.isEmpty() ? null : clean;
    }
}
