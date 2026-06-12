package com.brewpoint.pos.model;

import java.math.BigDecimal;

public class ProductSize {
    private int productSizeId;
    private int productId;
    private String sizeCode;
    private String sizeName;
    private BigDecimal salePrice;
    private boolean active;

    public ProductSize() {
    }

    public ProductSize(int productSizeId, int productId, String sizeCode, String sizeName,
                       BigDecimal salePrice, boolean active) {
        this.productSizeId = productSizeId;
        this.productId = productId;
        this.sizeCode = sizeCode;
        this.sizeName = sizeName;
        this.salePrice = salePrice;
        this.active = active;
    }

    public int getProductSizeId() {
        return productSizeId;
    }

    public void setProductSizeId(int productSizeId) {
        this.productSizeId = productSizeId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
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

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return sizeName == null ? "" : sizeName;
    }
}
