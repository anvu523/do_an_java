package com.brewpoint.pos.report.service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

import java.sql.SQLException;
import java.time.LocalDate;

public class ReportServiceFacade {
    private final ReceiptReportService receiptReportService ;
    private final DailyRevenueReportService dailyRevenueReportService ;
    private final MonthlyRevenueReportService monthlyRevenueReportService ;
    private final BestSellingProductsReportService bestSellingProductsReportService ;
    private final CashierPerformanceReportService cashierPerformanceReportService ;

    public ReportServiceFacade(ReceiptReportService receiptReportService, DailyRevenueReportService dailyRevenueReportService, MonthlyRevenueReportService monthlyRevenueReportService, BestSellingProductsReportService bestSellingProductsReportService, CashierPerformanceReportService cashierPerformanceReportService) {
        this.receiptReportService = receiptReportService;
        this.dailyRevenueReportService = dailyRevenueReportService;
        this.monthlyRevenueReportService = monthlyRevenueReportService;
        this.bestSellingProductsReportService = bestSellingProductsReportService;
        this.cashierPerformanceReportService = cashierPerformanceReportService;
    }

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

    public JasperPrint dailyRevenue(LocalDate startDate, LocalDate endDate) throws SQLException, JRException {
        return dailyRevenueReportService.build(startDate, endDate);
    }

    public String dailyRevenuePdfName(LocalDate startDate, LocalDate endDate) {
        return dailyRevenueReportService.defaultPdfName(startDate, endDate);
    }

    public JasperPrint monthlyRevenue(int year, int month) throws SQLException, JRException {
        return monthlyRevenueReportService.build(year, month);
    }

    public String monthlyRevenuePdfName(int year, int month) {
        return monthlyRevenueReportService.defaultPdfName(year, month);
    }

    public JasperPrint bestSellingProducts(LocalDate startDate, LocalDate endDate, String periodDesc) throws SQLException, JRException {
        return bestSellingProductsReportService.build(startDate, endDate, periodDesc);
    }

    public String bestSellingProductsPdfName(LocalDate startDate, LocalDate endDate) {
        return bestSellingProductsReportService.defaultPdfName(startDate, endDate);
    }

    public JasperPrint cashierPerformance(LocalDate startDate, LocalDate endDate, String periodDesc) throws SQLException, JRException {
        return cashierPerformanceReportService.build(startDate, endDate, periodDesc);
    }

    public String cashierPerformancePdfName(LocalDate startDate, LocalDate endDate) {
        return cashierPerformanceReportService.defaultPdfName(startDate, endDate);
    }
}
