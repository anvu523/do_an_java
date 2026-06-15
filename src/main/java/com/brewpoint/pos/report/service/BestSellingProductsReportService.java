package com.brewpoint.pos.report.service;

import com.brewpoint.pos.model.ProductSalesStat;
import com.brewpoint.pos.report.datasource.ProductSalesReportRow;
import com.brewpoint.pos.report.datasource.ReportDataSource;
import com.brewpoint.pos.report.util.ReportParameterBuilder;
import com.brewpoint.pos.report.util.ReportTemplate;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

import java.sql.SQLException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BestSellingProductsReportService extends AbstractJasperReportService {
    private final ReportDataSource reportDataSource;

    public BestSellingProductsReportService() {
        this(new ReportDataSource());
    }

    public BestSellingProductsReportService(ReportDataSource reportDataSource) {
        this.reportDataSource = reportDataSource;
    }

    public JasperPrint build(int year, int month) throws SQLException, JRException {
        ReportParameterBuilder.validateMonth(year, month);
        YearMonth yearMonth = YearMonth.of(year, month);
        List<ProductSalesStat> stats = reportDataSource.loadBestSellingProducts(yearMonth);
        List<ProductSalesReportRow> rows = new ArrayList<ProductSalesReportRow>();
        for (ProductSalesStat stat : stats) {
            rows.add(ReportParameterBuilder.productSalesRow(stat));
        }
        Map<String, Object> parameters = ReportParameterBuilder.bestSellingProducts(yearMonth);
        return fill(ReportTemplate.BEST_SELLING_PRODUCTS, parameters, rows);
    }

    public String defaultPdfName(int year, int month) {
        return String.format("BestSellingProducts_%04d-%02d.pdf", Integer.valueOf(year), Integer.valueOf(month));
    }
}
