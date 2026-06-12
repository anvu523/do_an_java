package com.brewpoint.pos.service;

import com.brewpoint.pos.dao.EmployeeDAO;
import com.brewpoint.pos.dao.UserDAO;
import com.brewpoint.pos.model.Employee;
import com.brewpoint.pos.model.User;
import com.brewpoint.pos.util.PasswordUtils;
import com.brewpoint.pos.util.ValidationException;
import com.brewpoint.pos.util.ValidationUtils;

import java.sql.SQLException;

public class AuthService {
    private final UserDAO userDAO = new UserDAO();
    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    public User login(String username, String password) throws SQLException {
        String cleanUsername = ValidationUtils.requireText(username, "Tên đăng nhập");
        String cleanPassword = ValidationUtils.requireText(password, "Mật khẩu");
        User user = userDAO.findByUsername(cleanUsername)
                .orElseThrow(() -> new ValidationException("Tài khoản không tồn tại."));
        if (!user.isActive()) {
            throw new ValidationException("Tài khoản đã bị khóa.");
        }
        if (!PasswordUtils.matches(cleanPassword, user.getPasswordHash())) {
            throw new ValidationException("Mật khẩu không đúng.");
        }
        return user;
    }

    public Employee findEmployee(User user) throws SQLException {
        if (user == null) {
            return null;
        }
        return employeeDAO.findByUserId(user.getUserId());
    }
}
