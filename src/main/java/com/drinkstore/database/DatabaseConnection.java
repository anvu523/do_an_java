package com.drinkstore.database;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseConnection {
    private static DatabaseConnection instance;

    private final Properties properties = new Properties();
    private Connection connection;

    private DatabaseConnection() {
        loadProperties();
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName(properties.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"));
            } catch (ClassNotFoundException e) {
                throw new SQLException("Không tìm thấy MySQL JDBC Driver. Kiểm tra dependency mysql-connector-j.", e);
            }
            connection = DriverManager.getConnection(
                    properties.getProperty("db.url"),
                    properties.getProperty("db.username"),
                    properties.getProperty("db.password")
            );
        }
        return connection;
    }

    public String getUrl() {
        return properties.getProperty("db.url");
    }

    public synchronized void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ignored) {
                // No recovery needed during shutdown.
            }
        }
    }

    private void loadProperties() {
        Path configPath = Path.of("config", "database.properties");
        try (InputStream input = Files.exists(configPath)
                ? Files.newInputStream(configPath)
                : DatabaseConnection.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Không đọc được cấu hình database.", e);
        }
        properties.putIfAbsent("db.driver", "com.mysql.cj.jdbc.Driver");
        properties.putIfAbsent("db.url", "jdbc:mysql://localhost:3306/drink_store?useSSL=false&serverTimezone=Asia/Ho_Chi_Minh");
        properties.putIfAbsent("db.username", "root");
        properties.putIfAbsent("db.password", "");
    }
}
