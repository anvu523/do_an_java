package com.brewpoint.pos.dao;

import com.brewpoint.pos.database.DatabaseManager;
import com.brewpoint.pos.model.Employee;
import com.brewpoint.pos.model.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {
    public Employee findByUserId(int userId) throws SQLException {
        String sql = "SELECT e.employee_id, e.user_id, u.username, u.role, e.full_name, e.phone, e.email, e.active "
                + "FROM employees e JOIN users u ON u.user_id = e.user_id WHERE e.user_id = ?";
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapEmployee(resultSet);
                }
            }
        }
        return null;
    }

    public Employee findActiveById(Connection connection, int employeeId) throws SQLException {
        String sql = "SELECT e.employee_id, e.user_id, u.username, u.role, e.full_name, e.phone, e.email, e.active "
                + "FROM employees e JOIN users u ON u.user_id = e.user_id "
                + "WHERE e.employee_id = ? AND e.active = 1 AND u.active = 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, employeeId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapEmployee(resultSet);
                }
            }
        }
        return null;
    }

    public List<Employee> findAll() throws SQLException {
        String sql = "SELECT e.employee_id, e.user_id, u.username, u.role, e.full_name, e.phone, e.email, e.active "
                + "FROM employees e JOIN users u ON u.user_id = e.user_id ORDER BY e.employee_id";
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<Employee> employees = new ArrayList<Employee>();
            while (resultSet.next()) {
                employees.add(mapEmployee(resultSet));
            }
            return employees;
        }
    }

    public int insert(Connection connection, Employee employee) throws SQLException {
        String sql = "INSERT INTO employees (user_id, full_name, phone, email, active) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            fill(statement, employee);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Không lấy được mã nhân viên vừa tạo.");
    }

    public void update(Connection connection, Employee employee) throws SQLException {
        String sql = "UPDATE employees SET full_name = ?, phone = ?, email = ?, active = ? WHERE employee_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, employee.getFullName());
            statement.setString(2, emptyToNull(employee.getPhone()));
            statement.setString(3, emptyToNull(employee.getEmail()));
            statement.setBoolean(4, employee.isActive());
            statement.setInt(5, employee.getEmployeeId());
            statement.executeUpdate();
        }
    }

    private void fill(PreparedStatement statement, Employee employee) throws SQLException {
        statement.setInt(1, employee.getUserId());
        statement.setString(2, employee.getFullName());
        statement.setString(3, emptyToNull(employee.getPhone()));
        statement.setString(4, emptyToNull(employee.getEmail()));
        statement.setBoolean(5, employee.isActive());
    }

    private String emptyToNull(String value) {
        String clean = value == null ? "" : value.trim();
        return clean.isEmpty() ? null : clean;
    }

    private Employee mapEmployee(ResultSet resultSet) throws SQLException {
        return new Employee(
                resultSet.getInt("employee_id"),
                resultSet.getInt("user_id"),
                resultSet.getString("username"),
                Role.fromDatabase(resultSet.getString("role")),
                resultSet.getString("full_name"),
                resultSet.getString("phone"),
                resultSet.getString("email"),
                resultSet.getBoolean("active")
        );
    }
}
