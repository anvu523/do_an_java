package com.brewpoint.pos.model;

public enum PaymentMethod {
    CASH("Tiền mặt"),
    MANUAL_BANK_TRANSFER("Chuyển khoản");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
