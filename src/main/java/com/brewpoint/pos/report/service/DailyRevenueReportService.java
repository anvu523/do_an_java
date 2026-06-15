package com.brewpoint.pos.report.service;

import com.brewpoint.pos.report.datasource.DailyRevenueMetrics;
import com.brewpoint.pos.report.datasource.ReportDataSource;
import com.brewpoint.pos.report.util.ReportParameterBuilder;
import com.brewpoint.pos.report.util.ReportTemplate;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;

public class DailyRevenueReportService extends AbstractJasperReportService {
    private final ReportDataSource reportDataSource;

    public DailyRevenueReportService() {
        this(new ReportDataSource());
    }

    public DailyRevenueReportService(ReportDataSource reportDataSource) {
        this.reportDataSource = reportDataSource;
    }

    public JasperPrint build(LocalDate date) throws SQLException, JRException {
        ReportParameterBuilder.validateReportDate(date);
        DailyRevenueMetrics metrics = reportDataSource.loadDailyRevenue(date);
        Map<String, Object> parameters = ReportParameterBuilder.dailyRevenue(metrics);
        return fill(ReportTemplate.DAILY_REVENUE, parameters);
    }

    public String defaultPdfName(LocalDate date) {
        LocalDate safeDate = date == null ? LocalDate.now() : date;
        return "DailyRevenue_" + safeDate + ".pdf";
    }

    public JasperPrint build(LocalDate startDate, LocalDate endDate) throws SQLException, JRException {
        ReportParameterBuilder.validateReportDate(startDate);
        ReportParameterBuilder.validateReportDate(endDate);
        DailyRevenueMetrics metrics = reportDataSource.loadDailyRevenue(startDate, endDate);
        Map<String, Object> parameters = ReportParameterBuilder.dailyRevenue(metrics, startDate, endDate);
        return fill(ReportTemplate.DAILY_REVENUE, parameters);
    }

    public String defaultPdfName(LocalDate startDate, LocalDate endDate) {
        LocalDate safeStart = startDate == null ? LocalDate.now() : startDate;
        LocalDate safeEnd = endDate == null ? LocalDate.now() : endDate;
        return "DailyRevenue_" + safeStart + "_to_" + safeEnd + ".pdf";
    }
}
