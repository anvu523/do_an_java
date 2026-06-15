package com.brewpoint.pos.report.datasource;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DailyRevenueMetrics {
    private final LocalDate reportDate;
    private final BigDecimal totalRevenue;
    private final int orderCount;
    private final BigDecimal averageOrderValue;

    public DailyRevenueMetrics(LocalDate reportDate, BigDecimal totalRevenue, int orderCount, BigDecimal averageOrderValue) {
        this.reportDate = reportDate;
        this.totalRevenue = totalRevenue;
        this.orderCount = orderCount;
        this.averageOrderValue = averageOrderValue;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public BigDecimal getAverageOrderValue() {
        return averageOrderValue;
    }
}
