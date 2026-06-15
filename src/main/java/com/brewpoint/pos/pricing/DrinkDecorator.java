package com.brewpoint.pos.pricing;

public abstract class DrinkDecorator implements DrinkComponent {
    protected final DrinkComponent wrapped;

    protected DrinkDecorator(DrinkComponent wrapped) {
        this.wrapped = wrapped;
    }
}
