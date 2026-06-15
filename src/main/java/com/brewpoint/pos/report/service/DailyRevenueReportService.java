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
}
