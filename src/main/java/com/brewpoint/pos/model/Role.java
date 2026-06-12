package com.brewpoint.pos.model;

public enum Role {
    ADMIN("Quản trị"),
    CASHIER("Thu ngân");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Role fromDatabase(String value) {
        if (value == null) {
            return CASHIER;
        }
        for (Role role : values()) {
            if (role.name().equalsIgnoreCase(value)) {
                return role;
            }
        }
        return CASHIER;
    }
}
