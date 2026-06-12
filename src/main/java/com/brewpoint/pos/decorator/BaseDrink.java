package com.brewpoint.pos.decorator;

import java.math.BigDecimal;

public final class BaseDrink implements DrinkComponent {
    private final String productName;
    private final String sizeName;
    private final BigDecimal basePrice;

    public BaseDrink(String productName, String sizeName, BigDecimal basePrice) {
        this.productName = productName;
        this.sizeName = sizeName;
        this.basePrice = basePrice;
    }

    public String getDescription() {
        return productName + " " + sizeName;
    }

    public BigDecimal getPrice() {
        return basePrice;
    }
}
