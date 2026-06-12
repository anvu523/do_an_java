package com.brewpoint.pos.decorator;

public abstract class DrinkDecorator implements DrinkComponent {
    protected final DrinkComponent wrapped;

    protected DrinkDecorator(DrinkComponent wrapped) {
        this.wrapped = wrapped;
    }
}
