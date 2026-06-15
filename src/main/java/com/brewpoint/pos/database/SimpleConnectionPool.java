package com.brewpoint.pos.database;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SimpleConnectionPool {
    private final String url;
    private final String username;
    private final String password;
    private final BlockingQueue<Connection> pool;

    public SimpleConnectionPool(Properties properties, int poolSize) throws SQLException {
        this.url = properties.getProperty("db.url");
        this.username = properties.getProperty("db.username");
        this.password = properties.getProperty("db.password");
        this.pool = new LinkedBlockingQueue<>(poolSize);

        // Khởi tạo các kết nối trước (Eager Initialization)
        for (int i = 0; i < poolSize; i++) {
            pool.offer(createRawConnection());
        }
    }

    private Connection createRawConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(url, username, password);
        try {
            conn.createStatement().execute("SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci");
        } catch (SQLException ex) {
            conn.close();
            throw ex;
        }
        return conn;
    }

    public Connection getConnection() throws SQLException {
        try {
            Connection rawConnection = pool.take();
            
            // Kiểm tra xem kết nối còn hợp lệ không trước khi giao cho client
            if (rawConnection.isClosed() || !rawConnection.isValid(2)) {
                try {
                    rawConnection.close();
                } catch (SQLException ignored) {
                }
                rawConnection = createRawConnection();
            }

            // Sử dụng Dynamic Proxy để chặn phương thức close()
            return (Connection) Proxy.newProxyInstance(
                    Connection.class.getClassLoader(),
                    new Class<?>[]{Connection.class},
                    new ConnectionProxyHandler(rawConnection)
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SQLException("Lấy kết nối từ pool bị ngắt quãng.", e);
        }
    }

    private class ConnectionProxyHandler implements InvocationHandler {
        private final Connection rawConnection;

        public ConnectionProxyHandler(Connection rawConnection) {
            this.rawConnection = rawConnection;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // Nếu gọi close(), trả kết nối lại vào pool chứ không đóng kết nối vật lý
            if ("close".equals(method.getName())) {
                if (!pool.offer(rawConnection)) {
                    // Nếu pool đầy vì lý do nào đó, thực sự đóng kết nối vật lý
                    rawConnection.close();
                }
                return null;
            }
            // Các phương thức khác thì thực hiện bình thường trên connection vật lý
            return method.invoke(rawConnection, args);
        }
    }

    public synchronized void shutdown() throws SQLException {
        for (Connection conn : pool) {
            if (!conn.isClosed()) {
                conn.close();
            }
        }
        pool.clear();
    }
}
