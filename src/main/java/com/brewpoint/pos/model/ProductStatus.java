package com.brewpoint.pos.model;

public enum ProductStatus {
    ACTIVE("Đang bán"),
    INACTIVE("Ngừng bán");

    private final String displayName;

    ProductStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isActive() {
        return this == ACTIVE;
    }

    public static ProductStatus fromActive(boolean active) {
        return active ? ACTIVE : INACTIVE;
    }
}
