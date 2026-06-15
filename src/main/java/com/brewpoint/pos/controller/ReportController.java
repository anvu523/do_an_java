package com.brewpoint.pos.controller;

import com.brewpoint.pos.report.exporter.JasperReportExporter;
import com.brewpoint.pos.report.exporter.PdfExportService;
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
    private final PdfExportService pdfExportService = new PdfExportService();

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
                exportPdf(parent, reportService.receipt(orderId), reportService.receiptPdfName(orderId));
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

    public void exportDailyRevenuePdf(LocalDate date, Component parent) {
        run(parent, "Không xuất PDF báo cáo.", new ReportAction() {
            public void run() throws Exception {
                exportPdf(parent, reportService.dailyRevenue(date), reportService.dailyRevenuePdfName(date));
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

    public void exportMonthlyRevenuePdf(int year, int month, Component parent) {
        run(parent, "Không xuất PDF báo cáo.", new ReportAction() {
            public void run() throws Exception {
                exportPdf(parent, reportService.monthlyRevenue(year, month),
                        reportService.monthlyRevenuePdfName(year, month));
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

    public void exportBestSellingProductsPdf(int year, int month, Component parent) {
        run(parent, "Không xuất PDF báo cáo.", new ReportAction() {
            public void run() throws Exception {
                exportPdf(parent, reportService.bestSellingProducts(year, month),
                        reportService.bestSellingProductsPdfName(year, month));
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

    public void exportCashierPerformancePdf(int year, int month, Component parent) {
        run(parent, "Không xuất PDF báo cáo.", new ReportAction() {
            public void run() throws Exception {
                exportPdf(parent, reportService.cashierPerformance(year, month),
                        reportService.cashierPerformancePdfName(year, month));
            }
        });
    }

    private void exportPdf(Component parent, JasperPrint print, String defaultFileName) throws JRException {
        File exported = pdfExportService.exportWithChooser(parent, print, defaultFileName);
        if (exported != null) {
            UiUtils.showInfo(parent, "Đã xuất PDF: " + exported.getAbsolutePath());
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
