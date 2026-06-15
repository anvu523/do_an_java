package com.brewpoint.pos.pricing;

import java.math.BigDecimal;

public final class ToppingDecorator extends DrinkDecorator {
    private final String toppingName;
    private final BigDecimal extraPrice;

    public ToppingDecorator(DrinkComponent wrapped, String toppingName, BigDecimal extraPrice) {
        super(wrapped);
        this.toppingName = toppingName;
        this.extraPrice = extraPrice;
    }

    public String getDescription() {
        return wrapped.getDescription() + " + " + toppingName;
    }

    public BigDecimal getPrice() {
        return wrapped.getPrice().add(extraPrice);
    }
}
