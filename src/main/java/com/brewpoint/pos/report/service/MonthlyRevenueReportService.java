package com.brewpoint.pos.report.service;

import com.brewpoint.pos.report.datasource.MonthlyRevenueDayReportRow;
import com.brewpoint.pos.report.datasource.MonthlyRevenueDayRow;
import com.brewpoint.pos.report.datasource.ReportDataSource;
import com.brewpoint.pos.report.util.ReportParameterBuilder;
import com.brewpoint.pos.report.util.ReportTemplate;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MonthlyRevenueReportService extends AbstractJasperReportService {
    private final ReportDataSource reportDataSource;

    public MonthlyRevenueReportService() {
        this(new ReportDataSource());
    }

    public MonthlyRevenueReportService(ReportDataSource reportDataSource) {
        this.reportDataSource = reportDataSource;
    }

    public JasperPrint build(int year, int month) throws SQLException, JRException {
        ReportParameterBuilder.validateMonth(year, month);
        List<MonthlyRevenueDayRow> rawRows = reportDataSource.loadMonthlyRevenueDays(year, month);
        List<MonthlyRevenueDayReportRow> rows = new ArrayList<MonthlyRevenueDayReportRow>();
        for (MonthlyRevenueDayRow row : rawRows) {
            rows.add(new MonthlyRevenueDayReportRow(row));
        }
        Map<String, Object> parameters = ReportParameterBuilder.monthlyRevenue(
                year,
                month,
                reportDataSource.loadMonthlyRevenueTotal(year, month)
        );
        return fill(ReportTemplate.MONTHLY_REVENUE, parameters, rows);
    }

    public String defaultPdfName(int year, int month) {
        return String.format("MonthlyRevenue_%04d-%02d.pdf", Integer.valueOf(year), Integer.valueOf(month));
    }
}
