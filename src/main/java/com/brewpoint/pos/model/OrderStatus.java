package com.brewpoint.pos.model;

public enum OrderStatus {
    COMPLETED("Hoàn tất"),
    CANCELLED("Đã hủy");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static OrderStatus fromDatabase(String value) {
        if (value == null) {
            return COMPLETED;
        }
        for (OrderStatus status : values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        return COMPLETED;
    }
}
