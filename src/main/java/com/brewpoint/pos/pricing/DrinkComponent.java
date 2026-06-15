package com.brewpoint.pos.pricing;

import java.math.BigDecimal;

public interface DrinkComponent {
    String getDescription();

    BigDecimal getPrice();
}
