package com.brewpoint.pos.report.datasource;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MonthlyRevenueDayRow {
    private final LocalDate saleDate;
    private final BigDecimal revenue;
    private final int orderCount;

    public MonthlyRevenueDayRow(LocalDate saleDate, BigDecimal revenue, int orderCount) {
        this.saleDate = saleDate;
        this.revenue = revenue;
        this.orderCount = orderCount;
    }

    public LocalDate getSaleDate() {
        return saleDate;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public int getOrderCount() {
        return orderCount;
    }
}
