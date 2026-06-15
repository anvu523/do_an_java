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
    private final ReportServiceFacade reportService ;
    private final JasperReportExporter jasperReportExporter ;
    private final ReportExportService reportExportService ;
    private final ReportExportStrategy pdfStrategy ;
    private final ReportExportStrategy xlsxStrategy ;
    private final ReportExportStrategy docxStrategy ;

    public ReportController(ReportServiceFacade reportService, JasperReportExporter jasperReportExporter, ReportExportService reportExportService, ReportExportStrategy pdfStrategy, ReportExportStrategy xlsxStrategy, ReportExportStrategy docxStrategy) {
        this.reportService = reportService;
        this.jasperReportExporter = jasperReportExporter;
        this.reportExportService = reportExportService;
        this.pdfStrategy = pdfStrategy;
        this.xlsxStrategy = xlsxStrategy;
        this.docxStrategy = docxStrategy;
    }

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

    public void previewDailyRevenue(LocalDate startDate, LocalDate endDate, Component parent) {
        run(parent, "Không xem được báo cáo.", new ReportAction() {
            public void run() throws Exception {
                jasperReportExporter.preview(reportService.dailyRevenue(startDate, endDate), "Doanh thu khoảng ngày", parent);
            }
        });
    }

    public void printDailyRevenue(LocalDate startDate, LocalDate endDate, Component parent) {
        run(parent, "Không in được báo cáo.", new ReportAction() {
            public void run() throws Exception {
                jasperReportExporter.print(reportService.dailyRevenue(startDate, endDate), parent);
            }
        });
    }

    public void exportDailyRevenue(LocalDate startDate, LocalDate endDate, ReportExportStrategy strategy, Component parent) {
        String ext = strategy.getExtension().toUpperCase();
        run(parent, "Không xuất báo cáo " + ext + ".", new ReportAction() {
            public void run() throws Exception {
                exportReport(parent, reportService.dailyRevenue(startDate, endDate), reportService.dailyRevenuePdfName(startDate, endDate), strategy);
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

    public void previewBestSellingProducts(LocalDate startDate, LocalDate endDate, String periodDesc, Component parent) {
        run(parent, "Không xem được báo cáo.", new ReportAction() {
            public void run() throws Exception {
                jasperReportExporter.preview(reportService.bestSellingProducts(startDate, endDate, periodDesc), "Sản phẩm bán chạy", parent);
            }
        });
    }

    public void printBestSellingProducts(LocalDate startDate, LocalDate endDate, String periodDesc, Component parent) {
        run(parent, "Không in được báo cáo.", new ReportAction() {
            public void run() throws Exception {
                jasperReportExporter.print(reportService.bestSellingProducts(startDate, endDate, periodDesc), parent);
            }
        });
    }

    public void exportBestSellingProducts(LocalDate startDate, LocalDate endDate, String periodDesc, ReportExportStrategy strategy, Component parent) {
        String ext = strategy.getExtension().toUpperCase();
        run(parent, "Không xuất báo cáo " + ext + ".", new ReportAction() {
            public void run() throws Exception {
                exportReport(parent, reportService.bestSellingProducts(startDate, endDate, periodDesc),
                        reportService.bestSellingProductsPdfName(startDate, endDate), strategy);
            }
        });
    }

    public void previewCashierPerformance(LocalDate startDate, LocalDate endDate, String periodDesc, Component parent) {
        run(parent, "Không xem được báo cáo.", new ReportAction() {
            public void run() throws Exception {
                jasperReportExporter.preview(reportService.cashierPerformance(startDate, endDate, periodDesc), "Doanh thu từng thu ngân", parent);
            }
        });
    }

    public void printCashierPerformance(LocalDate startDate, LocalDate endDate, String periodDesc, Component parent) {
        run(parent, "Không in được báo cáo.", new ReportAction() {
            public void run() throws Exception {
                jasperReportExporter.print(reportService.cashierPerformance(startDate, endDate, periodDesc), parent);
            }
        });
    }

    public void exportCashierPerformance(LocalDate startDate, LocalDate endDate, String periodDesc, ReportExportStrategy strategy, Component parent) {
        String ext = strategy.getExtension().toUpperCase();
        run(parent, "Không xuất báo cáo " + ext + ".", new ReportAction() {
            public void run() throws Exception {
                exportReport(parent, reportService.cashierPerformance(startDate, endDate, periodDesc),
                        reportService.cashierPerformancePdfName(startDate, endDate), strategy);
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
