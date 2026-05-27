package com.drinkstore.controller;

import com.drinkstore.model.Employee;
import com.drinkstore.model.Role;
import com.drinkstore.service.EmployeeService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class EmployeeController {
    private final EmployeeService employeeService = new EmployeeService();

    public List<Employee> findAll() throws SQLException {
        return employeeService.findAll();
    }

    public List<Employee> search(String keyword) throws SQLException {
        return employeeService.search(keyword);
    }

    public Optional<Employee> findByUserId(int userId) throws SQLException {
        return employeeService.findByUserId(userId);
    }

    public int create(Employee employee, String username, String rawPassword, Role role) throws SQLException {
        return employeeService.create(employee, username, rawPassword, role);
    }

    public void update(Employee employee, String username, String rawPassword, Role role) throws SQLException {
        employeeService.update(employee, username, rawPassword, role);
    }

    public void delete(int employeeId) throws SQLException {
        employeeService.delete(employeeId);
    }
}
