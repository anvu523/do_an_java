package com.brewpoint.pos.model;

import java.math.BigDecimal;

public class CheckoutResult {
    private long orderId;
    private String orderCode;
    private BigDecimal totalAmount;
    private BigDecimal changeAmount;

    public CheckoutResult(long orderId, String orderCode, BigDecimal totalAmount, BigDecimal changeAmount) {
        this.orderId = orderId;
        this.orderCode = orderCode;
        this.totalAmount = totalAmount;
        this.changeAmount = changeAmount;
    }

    public long getOrderId() {
        return orderId;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BigDecimal getChangeAmount() {
        return changeAmount;
    }
}
