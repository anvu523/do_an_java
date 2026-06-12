package com.brewpoint.pos.model;

import java.math.BigDecimal;

public class ToppingSnapshot {
    private int toppingId;
    private String toppingCode;
    private String name;
    private BigDecimal extraPrice;

    public ToppingSnapshot() {
    }

    public ToppingSnapshot(int toppingId, String toppingCode, String name, BigDecimal extraPrice) {
        this.toppingId = toppingId;
        this.toppingCode = toppingCode;
        this.name = name;
        this.extraPrice = extraPrice;
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
}
