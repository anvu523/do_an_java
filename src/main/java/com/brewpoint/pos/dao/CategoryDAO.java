package com.brewpoint.pos.dao;

import com.brewpoint.pos.database.DatabaseManager;
import com.brewpoint.pos.model.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    public List<Category> findAll(boolean activeOnly) throws SQLException {
        String sql = "SELECT category_id, name, display_order, active FROM categories "
                + "WHERE (? = 0 OR active = 1) ORDER BY display_order, name";
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, activeOnly ? 1 : 0);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Category> categories = new ArrayList<Category>();
                while (resultSet.next()) {
                    categories.add(mapCategory(resultSet));
                }
                return categories;
            }
        }
    }

    public int insert(Category category) throws SQLException {
        String sql = "INSERT INTO categories (name, display_order, active) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, category.getName());
            statement.setInt(2, category.getDisplayOrder());
            statement.setBoolean(3, category.isActive());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Không lấy được mã danh mục vừa tạo.");
    }

    public void update(Category category) throws SQLException {
        String sql = "UPDATE categories SET name = ?, display_order = ?, active = ? WHERE category_id = ?";
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, category.getName());
            statement.setInt(2, category.getDisplayOrder());
            statement.setBoolean(3, category.isActive());
            statement.setInt(4, category.getCategoryId());
            statement.executeUpdate();
        }
    }

    public void deactivate(int categoryId) throws SQLException {
        String sql = "UPDATE categories SET active = 0 WHERE category_id = ?";
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, categoryId);
            statement.executeUpdate();
        }
    }

    private Category mapCategory(ResultSet resultSet) throws SQLException {
        return new Category(
                resultSet.getInt("category_id"),
                resultSet.getString("name"),
                resultSet.getInt("display_order"),
                resultSet.getBoolean("active")
        );
    }
}
