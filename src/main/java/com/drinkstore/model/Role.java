package com.drinkstore.model;

public enum Role {
    ADMIN,
    EMPLOYEE;

    public static Role fromDatabase(String value) {
        return Role.valueOf(value.toUpperCase());
    }
}
