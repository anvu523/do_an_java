package com.brewpoint.pos.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CartLine {
    private CartLineRequest request;
    private String productName;
    private String productCode;
    private String sizeCode;
    private String sizeName;
    private BigDecimal basePrice;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;
    private List<ToppingSnapshot> toppings = new ArrayList<ToppingSnapshot>();

    public CartLineRequest getRequest() {
        return request;
    }

    public void setRequest(CartLineRequest request) {
        this.request = request;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getSizeCode() {
        return sizeCode;
    }

    public void setSizeCode(String sizeCode) {
        this.sizeCode = sizeCode;
    }

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }

    public List<ToppingSnapshot> getToppings() {
        return toppings;
    }

    public void setToppings(List<ToppingSnapshot> toppings) {
        this.toppings = toppings == null ? new ArrayList<ToppingSnapshot>() : new ArrayList<ToppingSnapshot>(toppings);
    }
}
