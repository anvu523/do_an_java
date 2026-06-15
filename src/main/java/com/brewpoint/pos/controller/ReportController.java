package com.brewpoint.pos.controller;

import com.brewpoint.pos.report.exporter.JasperReportExporter;
import com.brewpoint.pos.report.exporter.ReportExportService;
import com.brewpoint.pos.report.exporter.ReportExportStrategy;
import com.brewpoint.pos.report.exporter.PdfReportExportStrategy;
import com.brewpoint.pos.report.exporter.XlsxReportExportStrategy;
import com.brewpoint.pos.report.exporter.DocxReportExportStrategy;
import com.brewpoint.pos.report.service.ReportServiceFacade;
import com.brewpoint.pos.util.UiUtils;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

import java.awt.Component;
import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;

public class ReportController {
    private final ReportServiceFacade reportService = new ReportServiceFacade();
    private final JasperReportExporter jasperReportExporter = new JasperReportExporter();
    private final ReportExportService reportExportService = new ReportExportService();
    private final ReportExportStrategy pdfStrategy = new PdfReportExportStrategy();
    private final ReportExportStrategy xlsxStrategy = new XlsxReportExportStrategy();
    private final ReportExportStrategy docxStrategy = new DocxReportExportStrategy();

    public ReportExportStrategy getPdfStrategy() {
        return pdfStrategy;
    }

    public ReportExportStrategy getXlsxStrategy() {
        return xlsxStrategy;
    }

    public ReportExportStrategy getDocxStrategy() {
        return docxStrategy;
    }

    public void previewReceipt(long orderId, Component parent) {
        run(parent, "Không xem được hóa đơn.", new ReportAction() {
            public void run() throws Exception {
                JasperPrint print = reportService.receipt(orderId);
                jasperReportExporter.preview(print, "Hóa đơn", parent);
            }
        });
    }

    public void printReceipt(long orderId, Component parent) {
        run(parent, "Không in được hóa đơn.", new ReportAction() {
            public void run() throws Exception {
                jasperReportExporter.print(reportService.receipt(orderId), parent);
            }
        });
    }

    public void exportReceiptPdf(long orderId, Component parent) {
        run(parent, "Không xuất PDF hóa đơn.", new ReportAction() {
            public void run() throws Exception {
                exportReport(parent, reportService.receipt(orderId), reportService.receiptPdfName(orderId), pdfStrategy);
            }
        });
    }

    public void previewDailyRevenue(LocalDate date, Component parent) {
        run(parent, "Không xem được báo cáo.", new ReportAction() {
            public void run() throws Exception {
                jasperReportExporter.preview(reportService.dailyRevenue(date), "Doanh thu ngày", parent);
            }
        });
    }

    public void printDailyRevenue(LocalDate date, Component parent) {
        run(parent, "Không in được báo cáo.", new ReportAction() {
            public void run() throws Exception {
                jasperReportExporter.print(reportService.dailyRevenue(date), parent);
            }
        });
    }

    public void exportDailyRevenue(LocalDate date, ReportExportStrategy strategy, Component parent) {
        String ext = strategy.getExtension().toUpperCase();
        run(parent, "Không xuất báo cáo " + ext + ".", new ReportAction() {
            public void run() throws Exception {
                exportReport(parent, reportService.dailyRevenue(date), reportService.dailyRevenuePdfName(date), strategy);
            }
        });
    }

    public void previewMonthlyRevenue(int year, int month, Component parent) {
        run(parent, "Không xem được báo cáo.", new ReportAction() {
            public void run() throws Exception {
                jasperReportExporter.preview(reportService.monthlyRevenue(year, month), "Doanh thu tháng", parent);
            }
        });
    }

    public void printMonthlyRevenue(int year, int month, Component parent) {
        run(parent, "Không in được báo cáo.", new ReportAction() {
            public void run() throws Exception {
                jasperReportExporter.print(reportService.monthlyRevenue(year, month), parent);
            }
        });
    }

    public void exportMonthlyRevenue(int year, int month, ReportExportStrategy strategy, Component parent) {
        String ext = strategy.getExtension().toUpperCase();
        run(parent, "Không xuất báo cáo " + ext + ".", new ReportAction() {
            public void run() throws Exception {
                exportReport(parent, reportService.monthlyRevenue(year, month),
                        reportService.monthlyRevenuePdfName(year, month), strategy);
            }
        });
    }

    public void previewBestSellingProducts(int year, int month, Component parent) {
        run(parent, "Không xem được báo cáo.", new ReportAction() {
            public void run() throws Exception {
                jasperReportExporter.preview(reportService.bestSellingProducts(year, month), "Sản phẩm bán chạy", parent);
            }
        });
    }

    public void printBestSellingProducts(int year, int month, Component parent) {
        run(parent, "Không in được báo cáo.", new ReportAction() {
            public void run() throws Exception {
                jasperReportExporter.print(reportService.bestSellingProducts(year, month), parent);
            }
        });
    }

    public void exportBestSellingProducts(int year, int month, ReportExportStrategy strategy, Component parent) {
        String ext = strategy.getExtension().toUpperCase();
        run(parent, "Không xuất báo cáo " + ext + ".", new ReportAction() {
            public void run() throws Exception {
                exportReport(parent, reportService.bestSellingProducts(year, month),
                        reportService.bestSellingProductsPdfName(year, month), strategy);
            }
        });
    }

    public void previewCashierPerformance(int year, int month, Component parent) {
        run(parent, "Không xem được báo cáo.", new ReportAction() {
            public void run() throws Exception {
                jasperReportExporter.preview(reportService.cashierPerformance(year, month), "Doanh thu từng thu ngân", parent);
            }
        });
    }

    public void printCashierPerformance(int year, int month, Component parent) {
        run(parent, "Không in được báo cáo.", new ReportAction() {
            public void run() throws Exception {
                jasperReportExporter.print(reportService.cashierPerformance(year, month), parent);
            }
        });
    }

    public void exportCashierPerformance(int year, int month, ReportExportStrategy strategy, Component parent) {
        String ext = strategy.getExtension().toUpperCase();
        run(parent, "Không xuất báo cáo " + ext + ".", new ReportAction() {
            public void run() throws Exception {
                exportReport(parent, reportService.cashierPerformance(year, month),
                        reportService.cashierPerformancePdfName(year, month), strategy);
            }
        });
    }

    private void exportReport(Component parent, JasperPrint print, String defaultFileName, ReportExportStrategy strategy) throws JRException {
        File exported = reportExportService.exportWithChooser(parent, print, defaultFileName, strategy);
        if (exported != null) {
            String formatName = strategy.getExtension().toUpperCase();
            UiUtils.showInfo(parent, "Đã xuất báo cáo định dạng " + formatName + " thành công:\n" + exported.getAbsolutePath());
        }
    }

    private void run(Component parent, String fallbackMessage, ReportAction action) {
        try {
            action.run();
        } catch (SQLException | JRException | RuntimeException ex) {
            if (ex.getMessage() != null && ex.getMessage().trim().length() > 0) {
                UiUtils.showError(parent, ex instanceof Exception ? (Exception) ex : new RuntimeException(ex));
            } else {
                UiUtils.showError(parent, new RuntimeException(fallbackMessage));
            }
        } catch (Exception ex) {
            UiUtils.showError(parent, ex);
        }
    }

    private interface ReportAction {
        void run() throws Exception;
    }
}
