package com.brewpoint.pos.database;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseManager {
    private static DatabaseManager instance;

    private final Properties properties = new Properties();

    private DatabaseManager() {
        loadProperties();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        try {
            Class.forName(properties.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"));
        } catch (ClassNotFoundException ex) {
            throw new SQLException("Không tìm thấy MySQL JDBC Driver.", ex);
        }
        Connection connection = DriverManager.getConnection(
                properties.getProperty("db.url"),
                properties.getProperty("db.username"),
                properties.getProperty("db.password")
        );
        try {
            connection.createStatement().execute("SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci");
        } catch (SQLException ex) {
            connection.close();
            throw ex;
        }
        return connection;
    }

    public String getUrl() {
        return properties.getProperty("db.url");
    }

    private void loadProperties() {
        Path configPath = Paths.get("config", "database.properties");
        InputStream input = null;
        try {
            if (Files.exists(configPath)) {
                input = Files.newInputStream(configPath);
            } else {
                input = DatabaseManager.class.getClassLoader().getResourceAsStream("database.properties");
            }
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Không đọc được cấu hình database.", ex);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ignored) {
                    // Không cần xử lý khi đóng stream cấu hình.
                }
            }
        }
        properties.putIfAbsent("db.driver", "com.mysql.cj.jdbc.Driver");
        properties.putIfAbsent("db.url",
                "jdbc:mysql://localhost:3306/brewpoint_pos?useSSL=false&serverTimezone=Asia/Ho_Chi_Minh"
                        + "&useUnicode=true&characterEncoding=UTF-8&connectionCollation=utf8mb4_unicode_ci"
                        + "&characterSetResults=utf8mb4");
        properties.putIfAbsent("db.username", "root");
        properties.putIfAbsent("db.password", "");
    }
}
