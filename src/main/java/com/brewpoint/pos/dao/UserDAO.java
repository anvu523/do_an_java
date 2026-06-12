package com.brewpoint.pos.dao;

import com.brewpoint.pos.database.DatabaseManager;
import com.brewpoint.pos.model.Role;
import com.brewpoint.pos.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class UserDAO {
    public Optional<User> findByUsername(String username) throws SQLException {
        String sql = "SELECT user_id, username, password_hash, role, active FROM users WHERE username = ?";
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapUser(resultSet));
                }
            }
        }
        return Optional.empty();
    }

    public User findActiveById(Connection connection, int userId) throws SQLException {
        String sql = "SELECT user_id, username, password_hash, role, active FROM users WHERE user_id = ? AND active = 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapUser(resultSet);
                }
            }
        }
        return null;
    }

    public int insert(Connection connection, User user) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, role, active) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPasswordHash());
            statement.setString(3, user.getRole().name());
            statement.setBoolean(4, user.isActive());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Không lấy được mã tài khoản vừa tạo.");
    }

    public void update(Connection connection, User user) throws SQLException {
        String sql = "UPDATE users SET username = ?, role = ?, active = ? WHERE user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getRole().name());
            statement.setBoolean(3, user.isActive());
            statement.setInt(4, user.getUserId());
            statement.executeUpdate();
        }
    }

    public void updatePassword(Connection connection, int userId, String passwordHash) throws SQLException {
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, passwordHash);
            statement.setInt(2, userId);
            statement.executeUpdate();
        }
    }

    private User mapUser(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getInt("user_id"),
                resultSet.getString("username"),
                resultSet.getString("password_hash"),
                Role.fromDatabase(resultSet.getString("role")),
                resultSet.getBoolean("active")
        );
    }
}
