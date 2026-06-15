package com.brewpoint.pos.report.datasource;

import java.math.BigDecimal;

public class CashierPerformanceRow {
    private final String cashierName;
    private final int orderCount;
    private final BigDecimal revenue;

    public CashierPerformanceRow(String cashierName, int orderCount, BigDecimal revenue) {
        this.cashierName = cashierName;
        this.orderCount = orderCount;
        this.revenue = revenue;
    }

    public String getCashierName() {
        return cashierName;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }
}
