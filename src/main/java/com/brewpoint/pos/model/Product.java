package com.brewpoint.pos.model;

import java.math.BigDecimal;

public class Product {
    private int productId;
    private int categoryId;
    private String categoryName;
    private String productCode;
    private String name;
    private String imagePath;
    private int stockQuantity;
    private ProductStatus status;
    private BigDecimal fromPrice;

    public Product() {
    }

    public Product(int productId, int categoryId, String categoryName, String productCode,
                   String name, String imagePath, int stockQuantity, ProductStatus status,
                   BigDecimal fromPrice) {
        this.productId = productId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.productCode = productCode;
        this.name = name;
        this.imagePath = imagePath;
        this.stockQuantity = stockQuantity;
        this.status = status;
        this.fromPrice = fromPrice;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public BigDecimal getFromPrice() {
        return fromPrice;
    }

    public void setFromPrice(BigDecimal fromPrice) {
        this.fromPrice = fromPrice;
    }

    @Override
    public String toString() {
        return name == null ? "" : name;
    }
}
