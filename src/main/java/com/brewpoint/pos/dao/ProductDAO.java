package com.brewpoint.pos.dao;

import com.brewpoint.pos.database.DatabaseManager;
import com.brewpoint.pos.model.Product;
import com.brewpoint.pos.model.ProductStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    public List<Product> search(String keyword, Integer categoryId, boolean activeOnly) throws SQLException {
        String sql = "SELECT p.product_id, p.category_id, c.name AS category_name, p.product_code, p.name, "
                + "p.image_path, p.stock_quantity, p.active, "
                + "(SELECT MIN(ps.sale_price) FROM product_sizes ps WHERE ps.product_id = p.product_id AND ps.active = 1) AS from_price "
                + "FROM products p JOIN categories c ON c.category_id = p.category_id "
                + "WHERE p.name LIKE ? AND (? IS NULL OR p.category_id = ?) AND (? = 0 OR p.active = 1) "
                + "ORDER BY p.product_id";
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + safe(keyword) + "%");
            if (categoryId == null) {
                statement.setNull(2, Types.INTEGER);
                statement.setNull(3, Types.INTEGER);
            } else {
                statement.setInt(2, categoryId.intValue());
                statement.setInt(3, categoryId.intValue());
            }
            statement.setInt(4, activeOnly ? 1 : 0);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Product> products = new ArrayList<Product>();
                while (resultSet.next()) {
                    products.add(mapProduct(resultSet));
                }
                return products;
            }
        }
    }

    public Product findById(int productId) throws SQLException {
        try (Connection connection = DatabaseManager.getInstance().getConnection()) {
            return findById(connection, productId, false);
        }
    }

    public Product findById(Connection connection, int productId, boolean activeOnly) throws SQLException {
        String sql = "SELECT p.product_id, p.category_id, c.name AS category_name, p.product_code, p.name, "
                + "p.image_path, p.stock_quantity, p.active, "
                + "(SELECT MIN(ps.sale_price) FROM product_sizes ps WHERE ps.product_id = p.product_id AND ps.active = 1) AS from_price "
                + "FROM products p JOIN categories c ON c.category_id = p.category_id "
                + "WHERE p.product_id = ? AND (? = 0 OR p.active = 1)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, productId);
            statement.setInt(2, activeOnly ? 1 : 0);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapProduct(resultSet);
                }
            }
        }
        return null;
    }

    public int insert(Product product) throws SQLException {
        String sql = "INSERT INTO products (category_id, product_code, name, image_path, stock_quantity, active) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            fill(statement, product);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Không lấy được mã sản phẩm vừa tạo.");
    }

    public void update(Product product) throws SQLException {
        String sql = "UPDATE products SET category_id = ?, product_code = ?, name = ?, image_path = ?, "
                + "stock_quantity = ?, active = ? WHERE product_id = ?";
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            fill(statement, product);
            statement.setInt(7, product.getProductId());
            statement.executeUpdate();
        }
    }

    public void deactivate(int productId) throws SQLException {
        String sql = "UPDATE products SET active = 0 WHERE product_id = ?";
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, productId);
            statement.executeUpdate();
        }
    }

    public boolean decreaseStock(Connection connection, int productId, int quantity) throws SQLException {
        String sql = "UPDATE products SET stock_quantity = stock_quantity - ? "
                + "WHERE product_id = ? AND active = 1 AND stock_quantity >= ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, quantity);
            statement.setInt(2, productId);
            statement.setInt(3, quantity);
            return statement.executeUpdate() == 1;
        }
    }

    private void fill(PreparedStatement statement, Product product) throws SQLException {
        statement.setInt(1, product.getCategoryId());
        statement.setString(2, product.getProductCode());
        statement.setString(3, product.getName());
        statement.setString(4, emptyToNull(product.getImagePath()));
        statement.setInt(5, product.getStockQuantity());
        statement.setBoolean(6, product.getStatus() == null || product.getStatus().isActive());
    }

    private Product mapProduct(ResultSet resultSet) throws SQLException {
        return new Product(
                resultSet.getInt("product_id"),
                resultSet.getInt("category_id"),
                resultSet.getString("category_name"),
                resultSet.getString("product_code"),
                resultSet.getString("name"),
                resultSet.getString("image_path"),
                resultSet.getInt("stock_quantity"),
                ProductStatus.fromActive(resultSet.getBoolean("active")),
                resultSet.getBigDecimal("from_price")
        );
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private String emptyToNull(String value) {
        String clean = value == null ? "" : value.trim();
        return clean.isEmpty() ? null : clean;
    }
}
