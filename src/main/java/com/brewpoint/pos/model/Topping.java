package com.brewpoint.pos.model;

import java.math.BigDecimal;

public class Topping {
    private int toppingId;
    private String toppingCode;
    private String name;
    private BigDecimal extraPrice;
    private boolean active;

    public Topping() {
    }

    public Topping(int toppingId, String toppingCode, String name, BigDecimal extraPrice, boolean active) {
        this.toppingId = toppingId;
        this.toppingCode = toppingCode;
        this.name = name;
        this.extraPrice = extraPrice;
        this.active = active;
    }

    public int getToppingId() {
        return toppingId;
    }

    public void setToppingId(int toppingId) {
        this.toppingId = toppingId;
    }

    public String getToppingCode() {
        return toppingCode;
    }

    public void setToppingCode(String toppingCode) {
        this.toppingCode = toppingCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getExtraPrice() {
        return extraPrice;
    }

    public void setExtraPrice(BigDecimal extraPrice) {
        this.extraPrice = extraPrice;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return name == null ? "" : name;
    }
}
