package com.brewpoint.pos.controller;

import com.brewpoint.pos.model.Employee;
import com.brewpoint.pos.model.User;
import com.brewpoint.pos.service.AuthService;

import java.sql.SQLException;

public class AuthController {
    private final AuthService authService ;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public User login(String username, String password) throws SQLException {
        return authService.login(username, password);
    }

    public Employee findEmployee(User user) throws SQLException {
        return authService.findEmployee(user);
    }
}
