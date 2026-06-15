package com.brewpoint.pos.report.service;

import com.brewpoint.pos.model.OrderSummary;
import com.brewpoint.pos.report.datasource.ReceiptLineRow;
import com.brewpoint.pos.report.datasource.ReportDataSource;
import com.brewpoint.pos.report.util.ReportParameterBuilder;
import com.brewpoint.pos.report.util.ReportTemplate;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ReceiptReportService extends AbstractJasperReportService {
    private final ReportDataSource reportDataSource;

    public ReceiptReportService() {
        this(new ReportDataSource());
    }

    public ReceiptReportService(ReportDataSource reportDataSource) {
        this.reportDataSource = reportDataSource;
    }

    public JasperPrint build(long orderId) throws SQLException, JRException {
        OrderSummary order = reportDataSource.loadOrderSummary(orderId);
        List<ReceiptLineRow> lines = reportDataSource.loadReceiptLines(orderId);
        Map<String, Object> parameters = ReportParameterBuilder.receipt(order);
        return fill(ReportTemplate.RECEIPT_58MM, parameters, lines);
    }

    public String defaultPdfName(long orderId) throws SQLException {
        OrderSummary order = reportDataSource.loadOrderSummary(orderId);
        return "Invoice_" + order.getOrderCode() + ".pdf";
    }
}
