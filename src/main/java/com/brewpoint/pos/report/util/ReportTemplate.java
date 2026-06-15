package com.brewpoint.pos.report.util;

public enum ReportTemplate {
    RECEIPT_58MM("receipt_58mm.jrxml"),
    DAILY_REVENUE("daily_revenue.jrxml"),
    MONTHLY_REVENUE("monthly_revenue.jrxml"),
    BEST_SELLING_PRODUCTS("best_selling_products.jrxml"),
    CASHIER_PERFORMANCE("cashier_performance.jrxml");

    private final String fileName;

    ReportTemplate(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
