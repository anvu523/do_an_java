package com.brewpoint.pos.decorator;

import java.math.BigDecimal;

public interface DrinkComponent {
    String getDescription();

    BigDecimal getPrice();
}
