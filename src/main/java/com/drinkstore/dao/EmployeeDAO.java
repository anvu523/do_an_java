package com.drinkstore.dao;

import com.drinkstore.database.DatabaseConnection;
import com.drinkstore.model.Employee;
import com.drinkstore.model.Role;
import com.drinkstore.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeDAO {
    private final UserDAO userDAO = new UserDAO();

    public List<Employee> findAll() throws SQLException {
        return search("");
    }

    public List<Employee> search(String keyword) throws SQLException {
        String sql = """
                SELECT e.employee_id, e.full_name, e.phone, e.email, e.address, e.active AS employee_active,
                       u.user_id, u.username, u.password_hash, u.role, u.active AS user_active
                FROM employees e
                JOIN users u ON u.user_id = e.user_id
                WHERE e.full_name LIKE ? OR u.username LIKE ? OR e.phone LIKE ? OR e.email LIKE ?
                ORDER BY e.employee_id
                """;
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            String pattern = "%" + (keyword == null ? "" : keyword) + "%";
            for (int i = 1; i <= 4; i++) {
                statement.setString(i, pattern);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Employee> employees = new ArrayList<>();
                while (resultSet.next()) {
                    employees.add(mapEmployee(resultSet));
                }
                return employees;
            }
        }
    }

    public Optional<Employee> findById(int employeeId) throws SQLException {
        String sql = """
                SELECT e.employee_id, e.full_name, e.phone, e.email, e.address, e.active AS employee_active,
                       u.user_id, u.username, u.password_hash, u.role, u.active AS user_active
                FROM employees e
                JOIN users u ON u.user_id = e.user_id
                WHERE e.employee_id = ?
                """;
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, employeeId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapEmployee(resultSet));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Employee> findByUserId(int userId) throws SQLException {
        String sql = """
                SELECT e.employee_id, e.full_name, e.phone, e.email, e.address, e.active AS employee_active,
                       u.user_id, u.username, u.password_hash, u.role, u.active AS user_active
                FROM employees e
                JOIN users u ON u.user_id = e.user_id
                WHERE u.user_id = ?
                """;
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapEmployee(resultSet));
                }
            }
        }
        return Optional.empty();
    }

    public int insert(Employee employee) throws SQLException {
        Connection connection = DatabaseConnection.getInstance().getConnection();
        boolean oldAutoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);
            int userId = userDAO.insert(employee.getUser(), connection);
            employee.getUser().setUserId(userId);
            int employeeId = insertEmployeeOnly(employee, connection);
            connection.commit();
            return employeeId;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(oldAutoCommit);
            connection.close();
        }
    }

    public void update(Employee employee, boolean updatePassword) throws SQLException {
        Connection connection = DatabaseConnection.getInstance().getConnection();
        boolean oldAutoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);
            userDAO.update(employee.getUser(), updatePassword, connection);
            updateEmployeeOnly(employee, connection);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(oldAutoCommit);
            connection.close();
        }
    }

    public void delete(int employeeId) throws SQLException {
        Optional<Employee> employee = findById(employeeId);
        if (employee.isEmpty()) {
            return;
        }
        Connection connection = DatabaseConnection.getInstance().getConnection();
        boolean oldAutoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM employees WHERE employee_id = ?")) {
                statement.setInt(1, employeeId);
                statement.executeUpdate();
            }
            userDAO.delete(employee.get().getUser().getUserId(), connection);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(oldAutoCommit);
            connection.close();
        }
    }

    private int insertEmployeeOnly(Employee employee, Connection connection) throws SQLException {
        String sql = "INSERT INTO employees (user_id, full_name, phone, email, address, active) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, employee.getUser().getUserId());
            statement.setString(2, employee.getFullName());
            statement.setString(3, emptyToNull(employee.getPhone()));
            statement.setString(4, emptyToNull(employee.getEmail()));
            statement.setString(5, employee.getAddress());
            statement.setBoolean(6, employee.isActive());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Không lấy được mã nhân viên vừa tạo.");
    }

    private void updateEmployeeOnly(Employee employee, Connection connection) throws SQLException {
        String sql = "UPDATE employees SET full_name = ?, phone = ?, email = ?, address = ?, active = ? WHERE employee_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, employee.getFullName());
            statement.setString(2, emptyToNull(employee.getPhone()));
            statement.setString(3, emptyToNull(employee.getEmail()));
            statement.setString(4, employee.getAddress());
            statement.setBoolean(5, employee.isActive());
            statement.setInt(6, employee.getEmployeeId());
            statement.executeUpdate();
        }
    }

    private Employee mapEmployee(ResultSet resultSet) throws SQLException {
        User user = new User(
                resultSet.getInt("user_id"),
                resultSet.getString("username"),
                resultSet.getString("password_hash"),
                Role.fromDatabase(resultSet.getString("role")),
                resultSet.getBoolean("user_active")
        );
        return new Employee(
                resultSet.getInt("employee_id"),
                user,
                resultSet.getString("full_name"),
                resultSet.getString("phone"),
                resultSet.getString("email"),
                resultSet.getString("address"),
                resultSet.getBoolean("employee_active")
        );
    }

    private String emptyToNull(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }
}
