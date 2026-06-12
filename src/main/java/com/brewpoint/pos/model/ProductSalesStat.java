package com.brewpoint.pos.model;

import java.math.BigDecimal;

public class ProductSalesStat {
    private String productName;
    private int quantity;
    private BigDecimal revenue;

    public ProductSalesStat(String productName, int quantity, BigDecimal revenue) {
        this.productName = productName;
        this.quantity = quantity;
        this.revenue = revenue;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }
}
