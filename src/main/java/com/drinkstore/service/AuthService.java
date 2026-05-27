package com.drinkstore.service;

import com.drinkstore.dao.UserDAO;
import com.drinkstore.model.User;
import com.drinkstore.util.PasswordUtil;
import com.drinkstore.util.ValidationException;
import com.drinkstore.util.ValidationUtil;

import java.sql.SQLException;

public class AuthService {
    private final UserDAO userDAO = new UserDAO();

    public User login(String username, String password) throws SQLException {
        String cleanUsername = ValidationUtil.requireText(username, "Tên đăng nhập");
        String cleanPassword = ValidationUtil.requireText(password, "Mật khẩu");
        User user = userDAO.findByUsername(cleanUsername)
                .orElseThrow(() -> new ValidationException("Tài khoản không tồn tại."));
        if (!user.isActive()) {
            throw new ValidationException("Tài khoản đã bị khóa.");
        }
        if (!PasswordUtil.matches(cleanPassword, user.getPasswordHash())) {
            throw new ValidationException("Mật khẩu không đúng.");
        }
        return user;
    }
}
