package com.brewpoint.pos.controller;

import com.brewpoint.pos.model.Employee;
import com.brewpoint.pos.service.EmployeeService;

import java.sql.SQLException;
import java.util.List;

public class EmployeeController {
    private final EmployeeService employeeService ;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    public List<Employee> findAll() throws SQLException {
        return employeeService.findAll();
    }

    public int create(Employee employee, String username, String password) throws SQLException {
        return employeeService.create(employee, username, password);
    }

    public void update(Employee employee, int currentUserId) throws SQLException {
        employeeService.update(employee, currentUserId);
    }

    public void resetPassword(int userId, String password) throws SQLException {
        employeeService.resetPassword(userId, password);
    }
}
