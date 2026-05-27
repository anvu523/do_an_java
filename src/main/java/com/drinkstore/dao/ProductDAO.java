package com.drinkstore.dao;

import com.drinkstore.database.DatabaseConnection;
import com.drinkstore.model.Product;
import com.drinkstore.model.ProductStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductDAO {
    public List<Product> findAll() throws SQLException {
        return search("", null, false);
    }

    public List<Product> findActive() throws SQLException {
        return search("", null, true);
    }

    public Optional<Product> findById(int productId) throws SQLException {
        String sql = """
                SELECT p.product_id, p.category_id, c.name AS category_name, p.name, p.price, p.stock_quantity, p.status
                FROM products p
                JOIN categories c ON c.category_id = p.category_id
                WHERE p.product_id = ?
                """;
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, productId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapProduct(resultSet));
                }
            }
        }
        return Optional.empty();
    }

    public List<Product> search(String keyword, Integer categoryId, boolean activeOnly) throws SQLException {
        String sql = """
                SELECT p.product_id, p.category_id, c.name AS category_name, p.name, p.price, p.stock_quantity, p.status
                FROM products p
                JOIN categories c ON c.category_id = p.category_id
                WHERE p.name LIKE ?
                  AND (? IS NULL OR p.category_id = ?)
                  AND (? = 0 OR p.status = 'ACTIVE')
                ORDER BY p.product_id
                """;
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + (keyword == null ? "" : keyword) + "%");
            if (categoryId == null) {
                statement.setNull(2, Types.INTEGER);
                statement.setNull(3, Types.INTEGER);
            } else {
                statement.setInt(2, categoryId);
                statement.setInt(3, categoryId);
            }
            statement.setInt(4, activeOnly ? 1 : 0);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Product> products = new ArrayList<>();
                while (resultSet.next()) {
                    products.add(mapProduct(resultSet));
                }
                return products;
            }
        }
    }

    public int insert(Product product) throws SQLException {
        String sql = "INSERT INTO products (category_id, name, price, stock_quantity, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            fillProductStatement(statement, product);
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
        String sql = "UPDATE products SET category_id = ?, name = ?, price = ?, stock_quantity = ?, status = ? WHERE product_id = ?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            fillProductStatement(statement, product);
            statement.setInt(6, product.getProductId());
            statement.executeUpdate();
        }
    }

    public void delete(int productId) throws SQLException {
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM products WHERE product_id = ?")) {
            statement.setInt(1, productId);
            statement.executeUpdate();
        }
    }

    public boolean decreaseStock(Connection connection, int productId, int quantity) throws SQLException {
        String sql = """
                UPDATE products
                SET stock_quantity = stock_quantity - ?
                WHERE product_id = ?
                  AND stock_quantity >= ?
                  AND status = 'ACTIVE'
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, quantity);
            statement.setInt(2, productId);
            statement.setInt(3, quantity);
            return statement.executeUpdate() == 1;
        }
    }

    private void fillProductStatement(PreparedStatement statement, Product product) throws SQLException {
        statement.setInt(1, product.getCategoryId());
        statement.setString(2, product.getName());
        statement.setBigDecimal(3, product.getPrice());
        statement.setInt(4, product.getStockQuantity());
        statement.setString(5, product.getStatus().name());
    }

    private Product mapProduct(ResultSet resultSet) throws SQLException {
        return new Product(
                resultSet.getInt("product_id"),
                resultSet.getInt("category_id"),
                resultSet.getString("category_name"),
                resultSet.getString("name"),
                resultSet.getBigDecimal("price"),
                resultSet.getInt("stock_quantity"),
                ProductStatus.fromDatabase(resultSet.getString("status"))
        );
    }
}
