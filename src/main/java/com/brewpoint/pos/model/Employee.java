package com.brewpoint.pos.model;

public class Employee {
    private int employeeId;
    private int userId;
    private String username;
    private Role role;
    private String fullName;
    private String phone;
    private String email;
    private boolean active;

    public Employee() {
    }

    public Employee(int employeeId, int userId, String username, Role role, String fullName,
                    String phone, String email, boolean active) {
        this.employeeId = employeeId;
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.active = active;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return fullName == null ? "" : fullName;
    }
}
