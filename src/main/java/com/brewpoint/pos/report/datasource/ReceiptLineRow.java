package com.brewpoint.pos.report.datasource;

public class ReceiptLineRow {
    private final String productName;
    private final String options;
    private final int quantity;
    private final String unitPriceText;
    private final String lineTotalText;

    public ReceiptLineRow(String productName, String options, int quantity, String unitPriceText, String lineTotalText) {
        this.productName = productName;
        this.options = options;
        this.quantity = quantity;
        this.unitPriceText = unitPriceText;
        this.lineTotalText = lineTotalText;
    }

    public String getProductName() {
        return productName;
    }

    public String getOptions() {
        return options;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getUnitPriceText() {
        return unitPriceText;
    }

    public String getLineTotalText() {
        return lineTotalText;
    }
}
