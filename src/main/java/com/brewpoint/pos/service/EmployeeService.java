package com.brewpoint.pos.service;

import com.brewpoint.pos.dao.EmployeeDAO;
import com.brewpoint.pos.dao.UserDAO;
import com.brewpoint.pos.database.DatabaseManager;
import com.brewpoint.pos.model.Employee;
import com.brewpoint.pos.model.Role;
import com.brewpoint.pos.model.User;
import com.brewpoint.pos.util.PasswordUtils;
import com.brewpoint.pos.util.ValidationException;
import com.brewpoint.pos.util.ValidationUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class EmployeeService {
    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final UserDAO userDAO = new UserDAO();

    public List<Employee> findAll() throws SQLException {
        return employeeDAO.findAll();
    }

    public int create(Employee employee, String username, String password) throws SQLException {
        validate(employee);
        User user = new User();
        user.setUsername(ValidationUtils.requireText(username, "Tên đăng nhập"));
        user.setPasswordHash(PasswordUtils.sha256(ValidationUtils.requireText(password, "Mật khẩu")));
        user.setRole(employee.getRole() == null ? Role.CASHIER : employee.getRole());
        user.setActive(employee.isActive());
        try (Connection connection = DatabaseManager.getInstance().getConnection()) {
            boolean oldAutoCommit = connection.getAutoCommit();
            try {
                connection.setAutoCommit(false);
                int userId = userDAO.insert(connection, user);
                employee.setUserId(userId);
                int employeeId = employeeDAO.insert(connection, employee);
                connection.commit();
                return employeeId;
            } catch (SQLException | RuntimeException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(oldAutoCommit);
            }
        }
    }

    public void update(Employee employee, int currentUserId) throws SQLException {
        if (employee.getEmployeeId() <= 0 || employee.getUserId() <= 0) {
            throw new ValidationException("Chọn nhân viên cần sửa.");
        }
        validate(employee);
        if (employee.getUserId() == currentUserId && !employee.isActive()) {
            throw new ValidationException("Admin không được tự khóa tài khoản của chính mình.");
        }
        try (Connection connection = DatabaseManager.getInstance().getConnection()) {
            boolean oldAutoCommit = connection.getAutoCommit();
            try {
                connection.setAutoCommit(false);
                User user = new User();
                user.setUserId(employee.getUserId());
                user.setUsername(ValidationUtils.requireText(employee.getUsername(), "Tên đăng nhập"));
                user.setRole(employee.getRole() == null ? Role.CASHIER : employee.getRole());
                user.setActive(employee.isActive());
                userDAO.update(connection, user);
                employeeDAO.update(connection, employee);
                connection.commit();
            } catch (SQLException | RuntimeException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(oldAutoCommit);
            }
        }
    }

    public void resetPassword(int userId, String password) throws SQLException {
        if (userId <= 0) {
            throw new ValidationException("Chọn tài khoản cần đổi mật khẩu.");
        }
        String hash = PasswordUtils.sha256(ValidationUtils.requireText(password, "Mật khẩu mới"));
        try (Connection connection = DatabaseManager.getInstance().getConnection()) {
            userDAO.updatePassword(connection, userId, hash);
        }
    }

    private void validate(Employee employee) {
        employee.setFullName(ValidationUtils.requireText(employee.getFullName(), "Họ tên"));
        if (employee.getRole() == null) {
            employee.setRole(Role.CASHIER);
        }
    }
}
