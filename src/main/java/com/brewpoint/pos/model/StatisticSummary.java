package com.brewpoint.pos.model;

import java.math.BigDecimal;

public class StatisticSummary {
    private BigDecimal todayRevenue;
    private BigDecimal selectedRevenue;
    private int orderCount;

    public StatisticSummary(BigDecimal todayRevenue, BigDecimal selectedRevenue, int orderCount) {
        this.todayRevenue = todayRevenue;
        this.selectedRevenue = selectedRevenue;
        this.orderCount = orderCount;
    }

    public BigDecimal getTodayRevenue() {
        return todayRevenue;
    }

    public BigDecimal getSelectedRevenue() {
        return selectedRevenue;
    }

    public int getOrderCount() {
        return orderCount;
    }
}
