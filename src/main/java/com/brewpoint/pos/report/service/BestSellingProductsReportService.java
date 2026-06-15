package com.brewpoint.pos.report.service;

import com.brewpoint.pos.model.ProductSalesStat;
import com.brewpoint.pos.report.datasource.ProductSalesReportRow;
import com.brewpoint.pos.report.datasource.ReportDataSource;
import com.brewpoint.pos.report.util.ReportParameterBuilder;
import com.brewpoint.pos.report.util.ReportTemplate;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

import java.sql.SQLException;
import java.time.LocalDate;
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

    public JasperPrint build(LocalDate startDate, LocalDate endDate, String periodDesc) throws SQLException, JRException {
        List<ProductSalesStat> stats = reportDataSource.loadBestSellingProducts(startDate, endDate);
        List<ProductSalesReportRow> rows = new ArrayList<ProductSalesReportRow>();
        for (ProductSalesStat stat : stats) {
            rows.add(ReportParameterBuilder.productSalesRow(stat));
        }
        Map<String, Object> parameters = ReportParameterBuilder.bestSellingProducts(periodDesc);
        return fill(ReportTemplate.BEST_SELLING_PRODUCTS, parameters, rows);
    }

    public String defaultPdfName(LocalDate startDate, LocalDate endDate) {
        LocalDate safeStart = startDate == null ? LocalDate.now() : startDate;
        LocalDate safeEnd = endDate == null ? java.time.LocalDate.now() : endDate;
        return "BestSellingProducts_" + safeStart + "_to_" + safeEnd + ".pdf";
    }
}
