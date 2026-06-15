package com.brewpoint.pos.report.datasource;

import com.brewpoint.pos.report.util.ReportFormatUtils;

public class MonthlyRevenueDayReportRow {
    private final String saleDateText;
    private final int orderCount;
    private final String revenueText;

    public MonthlyRevenueDayReportRow(MonthlyRevenueDayRow row) {
        this.saleDateText = ReportFormatUtils.date(row.getSaleDate());
        this.orderCount = row.getOrderCount();
        this.revenueText = ReportFormatUtils.money(row.getRevenue());
    }

    public String getSaleDateText() {
        return saleDateText;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public String getRevenueText() {
        return revenueText;
    }
}
