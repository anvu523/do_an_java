package com.drinkstore.model;

import java.math.BigDecimal;

public class ProductSalesStat {
    private final int productId;
    private final String productName;
    private final int totalQuantity;
    private final BigDecimal totalRevenue;

    public ProductSalesStat(int productId, String productName, int totalQuantity, BigDecimal totalRevenue) {
        this.productId = productId;
        this.productName = productName;
        this.totalQuantity = totalQuantity;
        this.totalRevenue = totalRevenue;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }
}
