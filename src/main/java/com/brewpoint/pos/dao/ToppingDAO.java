package com.brewpoint.pos.dao;

import com.brewpoint.pos.database.DatabaseManager;
import com.brewpoint.pos.model.Topping;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ToppingDAO {
    public List<Topping> findAll(boolean activeOnly) throws SQLException {
        String sql = "SELECT topping_id, topping_code, name, extra_price, active FROM toppings "
                + "WHERE (? = 0 OR active = 1) ORDER BY name";
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, activeOnly ? 1 : 0);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Topping> toppings = new ArrayList<Topping>();
                while (resultSet.next()) {
                    toppings.add(mapTopping(resultSet));
                }
                return toppings;
            }
        }
    }

    public List<Topping> findByIds(Connection connection, List<Integer> toppingIds, boolean activeOnly) throws SQLException {
        List<Topping> toppings = new ArrayList<Topping>();
        if (toppingIds == null || toppingIds.isEmpty()) {
            return toppings;
        }
        StringBuilder sql = new StringBuilder("SELECT topping_id, topping_code, name, extra_price, active FROM toppings WHERE topping_id IN (");
        for (int i = 0; i < toppingIds.size(); i++) {
            if (i > 0) {
                sql.append(",");
            }
            sql.append("?");
        }
        sql.append(") AND (? = 0 OR active = 1) ORDER BY topping_id");
        try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < toppingIds.size(); i++) {
                statement.setInt(i + 1, toppingIds.get(i).intValue());
            }
            statement.setInt(toppingIds.size() + 1, activeOnly ? 1 : 0);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    toppings.add(mapTopping(resultSet));
                }
            }
        }
        return toppings;
    }

    public int insert(Topping topping) throws SQLException {
        String sql = "INSERT INTO toppings (topping_code, name, extra_price, active) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            fill(statement, topping);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Không lấy được mã topping vừa tạo.");
    }

    public void update(Topping topping) throws SQLException {
        String sql = "UPDATE toppings SET topping_code = ?, name = ?, extra_price = ?, active = ? WHERE topping_id = ?";
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            fill(statement, topping);
            statement.setInt(5, topping.getToppingId());
            statement.executeUpdate();
        }
    }

    public void deactivate(int toppingId) throws SQLException {
        String sql = "UPDATE toppings SET active = 0 WHERE topping_id = ?";
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, toppingId);
            statement.executeUpdate();
        }
    }

    private void fill(PreparedStatement statement, Topping topping) throws SQLException {
        statement.setString(1, topping.getToppingCode());
        statement.setString(2, topping.getName());
        statement.setBigDecimal(3, topping.getExtraPrice());
        statement.setBoolean(4, topping.isActive());
    }

    private Topping mapTopping(ResultSet resultSet) throws SQLException {
        return new Topping(
                resultSet.getInt("topping_id"),
                resultSet.getString("topping_code"),
                resultSet.getString("name"),
                resultSet.getBigDecimal("extra_price"),
                resultSet.getBoolean("active")
        );
    }
}
