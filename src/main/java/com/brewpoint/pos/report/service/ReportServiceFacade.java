package com.brewpoint.pos.report.service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

import java.sql.SQLException;
import java.time.LocalDate;

public class ReportServiceFacade {
    private final ReceiptReportService receiptReportService = new ReceiptReportService();
    private final DailyRevenueReportService dailyRevenueReportService = new DailyRevenueReportService();
    private final MonthlyRevenueReportService monthlyRevenueReportService = new MonthlyRevenueReportService();
    private final BestSellingProductsReportService bestSellingProductsReportService = new BestSellingProductsReportService();
    private final CashierPerformanceReportService cashierPerformanceReportService = new CashierPerformanceReportService();

    public JasperPrint receipt(long orderId) throws SQLException, JRException {
        return receiptReportService.build(orderId);
    }

    public String receiptPdfName(long orderId) throws SQLException {
        return receiptReportService.defaultPdfName(orderId);
    }

    public JasperPrint dailyRevenue(LocalDate date) throws SQLException, JRException {
        return dailyRevenueReportService.build(date);
    }

    public String dailyRevenuePdfName(LocalDate date) {
        return dailyRevenueReportService.defaultPdfName(date);
    }

    public JasperPrint monthlyRevenue(int year, int month) throws SQLException, JRException {
        return monthlyRevenueReportService.build(year, month);
    }

    public String monthlyRevenuePdfName(int year, int month) {
        return monthlyRevenueReportService.defaultPdfName(year, month);
    }

    public JasperPrint bestSellingProducts(int year, int month) throws SQLException, JRException {
        return bestSellingProductsReportService.build(year, month);
    }

    public String bestSellingProductsPdfName(int year, int month) {
        return bestSellingProductsReportService.defaultPdfName(year, month);
    }

    public JasperPrint cashierPerformance(int year, int month) throws SQLException, JRException {
        return cashierPerformanceReportService.build(year, month);
    }

    public String cashierPerformancePdfName(int year, int month) {
        return cashierPerformanceReportService.defaultPdfName(year, month);
    }
}
