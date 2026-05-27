package com.drinkstore.model;

public class Employee {
    private int employeeId;
    private User user;
    private String fullName;
    private String phone;
    private String email;
    private String address;
    private boolean active;

    public Employee() {
    }

    public Employee(int employeeId, User user, String fullName, String phone, String email, String address, boolean active) {
        this.employeeId = employeeId;
        this.user = user;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.active = active;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return fullName;
    }
}
