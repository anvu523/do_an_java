package com.drinkstore.service;

import com.drinkstore.dao.EmployeeDAO;
import com.drinkstore.factory.UserFactory;
import com.drinkstore.model.Employee;
import com.drinkstore.model.Role;
import com.drinkstore.model.User;
import com.drinkstore.util.PasswordUtil;
import com.drinkstore.util.ValidationException;
import com.drinkstore.util.ValidationUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class EmployeeService {
    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    public List<Employee> findAll() throws SQLException {
        return employeeDAO.findAll();
    }

    public List<Employee> search(String keyword) throws SQLException {
        return employeeDAO.search(keyword == null ? "" : keyword.trim());
    }

    public Optional<Employee> findByUserId(int userId) throws SQLException {
        return employeeDAO.findByUserId(userId);
    }

    public int create(Employee employee, String username, String rawPassword, Role role) throws SQLException {
        validateEmployee(employee);
        String cleanUsername = ValidationUtil.requireText(username, "Tên đăng nhập");
        String cleanPassword = ValidationUtil.requireText(rawPassword, "Mật khẩu");
        employee.setUser(UserFactory.createUser(cleanUsername, cleanPassword, role, employee.isActive()));
        return employeeDAO.insert(employee);
    }

    public void update(Employee employee, String username, String rawPassword, Role role) throws SQLException {
        validateEmployee(employee);
        String cleanUsername = ValidationUtil.requireText(username, "Tên đăng nhập");
        User user = employee.getUser();
        if (user == null || user.getUserId() <= 0) {
            throw new ValidationException("Thiếu tài khoản nhân viên.");
        }
        user.setUsername(cleanUsername);
        user.setRole(role);
        user.setActive(employee.isActive());
        boolean updatePassword = rawPassword != null && !rawPassword.trim().isEmpty();
        if (updatePassword) {
            user.setPasswordHash(PasswordUtil.sha256(rawPassword.trim()));
        }
        employeeDAO.update(employee, updatePassword);
    }

    public void delete(int employeeId) throws SQLException {
        employeeDAO.delete(employeeId);
    }

    private void validateEmployee(Employee employee) {
        employee.setFullName(ValidationUtil.requireText(employee.getFullName(), "Họ tên"));
        ValidationUtil.validateOptionalPhone(employee.getPhone());
        ValidationUtil.validateOptionalEmail(employee.getEmail());
        if (employee.getPhone() != null) {
            employee.setPhone(employee.getPhone().trim());
        }
        if (employee.getEmail() != null) {
            employee.setEmail(employee.getEmail().trim());
        }
        if (employee.getAddress() != null) {
            employee.setAddress(employee.getAddress().trim());
        }
    }
}
