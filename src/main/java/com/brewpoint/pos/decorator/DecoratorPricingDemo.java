package com.brewpoint.pos.decorator;

import java.math.BigDecimal;

public final class DecoratorPricingDemo {
    private DecoratorPricingDemo() {
    }

    public static BigDecimal demoOolongMilkTea() {
        DrinkComponent drink = new BaseDrink("Ô long sữa", "L", new BigDecimal("49000"));
        drink = new ToppingDecorator(drink, "Trân châu đen", new BigDecimal("10000"));
        drink = new ToppingDecorator(drink, "Pudding trứng", new BigDecimal("8000"));
        return drink.getPrice();
    }
}
