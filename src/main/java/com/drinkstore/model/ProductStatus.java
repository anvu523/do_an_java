package com.drinkstore.model;

public enum ProductStatus {
    ACTIVE("Còn bán"),
    INACTIVE("Ngừng bán");

    private final String displayName;

    ProductStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ProductStatus fromDatabase(String value) {
        return ProductStatus.valueOf(value.toUpperCase());
    }

    @Override
    public String toString() {
        return displayName;
    }
}
