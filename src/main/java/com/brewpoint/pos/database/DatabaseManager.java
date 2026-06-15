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
    private SimpleConnectionPool connectionPool;

    private DatabaseManager() {
        loadProperties();
        try {
            Class.forName(properties.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"));
            int poolSize = Integer.parseInt(properties.getProperty("db.pool.size", "5").trim());
            this.connectionPool = new SimpleConnectionPool(properties, poolSize);
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException("Không tìm thấy MySQL JDBC Driver.", ex);
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể khởi tạo Connection Pool.", ex);
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (connectionPool == null) {
            throw new SQLException("Connection Pool chưa được khởi tạo.");
        }
        return connectionPool.getConnection();
    }

    public void shutdown() {
        if (connectionPool != null) {
            try {
                connectionPool.shutdown();
            } catch (SQLException ignored) {
            }
        }
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
