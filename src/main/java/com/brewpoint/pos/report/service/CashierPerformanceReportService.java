package com.brewpoint.pos.report.service;

import com.brewpoint.pos.report.datasource.CashierPerformanceReportRow;
import com.brewpoint.pos.report.datasource.CashierPerformanceRow;
import com.brewpoint.pos.report.datasource.ReportDataSource;
import com.brewpoint.pos.report.util.ReportFormatUtils;
import com.brewpoint.pos.report.util.ReportParameterBuilder;
import com.brewpoint.pos.report.util.ReportTemplate;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CashierPerformanceReportService extends AbstractJasperReportService {
    private final ReportDataSource reportDataSource;

    public CashierPerformanceReportService() {
        this(new ReportDataSource());
    }

    public CashierPerformanceReportService(ReportDataSource reportDataSource) {
        this.reportDataSource = reportDataSource;
    }

    public JasperPrint build(LocalDate startDate, LocalDate endDate, String periodDesc) throws SQLException, JRException {
        List<CashierPerformanceRow> rawRows = reportDataSource.loadCashierPerformance(startDate, endDate);
        List<CashierPerformanceReportRow> rows = new ArrayList<CashierPerformanceReportRow>();
        for (CashierPerformanceRow row : rawRows) {
            rows.add(new CashierPerformanceReportRow(
                    row.getCashierName(),
                    row.getOrderCount(),
                    ReportFormatUtils.money(row.getRevenue())
            ));
        }
        Map<String, Object> parameters = ReportParameterBuilder.cashierPerformance(periodDesc);
        return fill(ReportTemplate.CASHIER_PERFORMANCE, parameters, rows);
    }

    public String defaultPdfName(LocalDate startDate, LocalDate endDate) {
        LocalDate safeStart = startDate == null ? LocalDate.now() : startDate;
        LocalDate safeEnd = endDate == null ? LocalDate.now() : endDate;
        return "CashierPerformance_" + safeStart + "_to_" + safeEnd + ".pdf";
    }
}
