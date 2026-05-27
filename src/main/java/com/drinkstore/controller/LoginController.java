package com.drinkstore.controller;

import com.drinkstore.model.User;
import com.drinkstore.service.AuthService;

import java.sql.SQLException;

public class LoginController {
    private final AuthService authService = new AuthService();

    public User login(String username, String password) throws SQLException {
        return authService.login(username, password);
    }
}
