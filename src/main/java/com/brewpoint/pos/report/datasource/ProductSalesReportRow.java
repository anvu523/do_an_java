package com.brewpoint.pos.report.datasource;

public class ProductSalesReportRow {
    private final String productName;
    private final int quantity;
    private final String revenueText;

    public ProductSalesReportRow(String productName, int quantity, String revenueText) {
        this.productName = productName;
        this.quantity = quantity;
        this.revenueText = revenueText;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getRevenueText() {
        return revenueText;
    }
}
