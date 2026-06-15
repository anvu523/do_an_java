package com.brewpoint.pos.report.datasource;

public class CashierPerformanceReportRow {
    private final String cashierName;
    private final int orderCount;
    private final String revenueText;

    public CashierPerformanceReportRow(String cashierName, int orderCount, String revenueText) {
        this.cashierName = cashierName;
        this.orderCount = orderCount;
        this.revenueText = revenueText;
    }

    public String getCashierName() {
        return cashierName;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public String getRevenueText() {
        return revenueText;
    }
}
