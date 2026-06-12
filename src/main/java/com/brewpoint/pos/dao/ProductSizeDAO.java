package com.brewpoint.pos.dao;

import com.brewpoint.pos.database.DatabaseManager;
import com.brewpoint.pos.model.ProductSize;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductSizeDAO {
    public List<ProductSize> findByProductId(int productId, boolean activeOnly) throws SQLException {
        String sql = "SELECT product_size_id, product_id, size_code, size_name, sale_price, active "
                + "FROM product_sizes WHERE product_id = ? AND (? = 0 OR active = 1) ORDER BY sale_price";
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, productId);
            statement.setInt(2, activeOnly ? 1 : 0);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<ProductSize> sizes = new ArrayList<ProductSize>();
                while (resultSet.next()) {
                    sizes.add(mapSize(resultSet));
                }
                return sizes;
            }
        }
    }

    public ProductSize findById(Connection connection, int productSizeId, boolean activeOnly) throws SQLException {
        String sql = "SELECT product_size_id, product_id, size_code, size_name, sale_price, active "
                + "FROM product_sizes WHERE product_size_id = ? AND (? = 0 OR active = 1)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, productSizeId);
            statement.setInt(2, activeOnly ? 1 : 0);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapSize(resultSet);
                }
            }
        }
        return null;
    }

    public int insert(ProductSize size) throws SQLException {
        String sql = "INSERT INTO product_sizes (product_id, size_code, size_name, sale_price, active) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            fill(statement, size);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Không lấy được mã size vừa tạo.");
    }

    public void update(ProductSize size) throws SQLException {
        String sql = "UPDATE product_sizes SET size_code = ?, size_name = ?, sale_price = ?, active = ? WHERE product_size_id = ?";
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, size.getSizeCode());
            statement.setString(2, size.getSizeName());
            statement.setBigDecimal(3, size.getSalePrice());
            statement.setBoolean(4, size.isActive());
            statement.setInt(5, size.getProductSizeId());
            statement.executeUpdate();
        }
    }

    public void deactivate(int productSizeId) throws SQLException {
        String sql = "UPDATE product_sizes SET active = 0 WHERE product_size_id = ?";
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, productSizeId);
            statement.executeUpdate();
        }
    }

    private void fill(PreparedStatement statement, ProductSize size) throws SQLException {
        statement.setInt(1, size.getProductId());
        statement.setString(2, size.getSizeCode());
        statement.setString(3, size.getSizeName());
        statement.setBigDecimal(4, size.getSalePrice());
        statement.setBoolean(5, size.isActive());
    }

    private ProductSize mapSize(ResultSet resultSet) throws SQLException {
        return new ProductSize(
                resultSet.getInt("product_size_id"),
                resultSet.getInt("product_id"),
                resultSet.getString("size_code"),
                resultSet.getString("size_name"),
                resultSet.getBigDecimal("sale_price"),
                resultSet.getBoolean("active")
        );
    }
}
