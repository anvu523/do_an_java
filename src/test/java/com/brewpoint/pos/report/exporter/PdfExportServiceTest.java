package com.brewpoint.pos.report.exporter;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PdfExportServiceTest {
    @Mock
    private JasperReportExporter jasperReportExporter;

    @TempDir
    Path tempDir;

    @Test
    void exportToFile_delegatesToExporter() throws Exception {
        PdfExportService service = new PdfExportService(jasperReportExporter);
        JasperPrint print = mock(JasperPrint.class);
        File target = tempDir.resolve("DailyRevenue_2026-06-15.pdf").toFile();

        service.exportToFile(print, target);

        verify(jasperReportExporter).exportPdf(print, target);
        assertTrue(target.getName().endsWith(".pdf"));
    }

    @Test
    void exportToFile_whenExporterFails_propagatesException() throws Exception {
        PdfExportService service = new PdfExportService(jasperReportExporter);
        JasperPrint print = mock(JasperPrint.class);
        File target = tempDir.resolve("fail.pdf").toFile();
        doThrow(new JRException("PDF lỗi")).when(jasperReportExporter).exportPdf(print, target);

        assertThrows(JRException.class, new org.junit.jupiter.api.function.Executable() {
            public void execute() throws Throwable {
                service.exportToFile(print, target);
            }
        });
    }

    @Test
    void exportToFile_rejectsNullPrint() {
        PdfExportService service = new PdfExportService(jasperReportExporter);
        File target = tempDir.resolve("x.pdf").toFile();

        assertThrows(IllegalArgumentException.class, new org.junit.jupiter.api.function.Executable() {
            public void execute() throws JRException {
                service.exportToFile(null, target);
            }
        });
    }

    @Test
    void resolvePdfTarget_appendsExtensionWhenMissing() {
        PdfExportService service = new PdfExportService();
        File resolved = service.resolvePdfTarget(new File("MonthlyRevenue_2026-06"));

        assertEquals("MonthlyRevenue_2026-06.pdf", resolved.getName());
    }

    @Test
    void sanitizePdfFileName_replacesInvalidCharacters() {
        PdfExportService service = new PdfExportService();
        String safe = service.sanitizePdfFileName("Invoice:HD*001");

        assertEquals("Invoice_HD_001.pdf", safe);
    }

    @Test
    void sanitizePdfFileName_rejectsBlankName() {
        PdfExportService service = new PdfExportService();

        assertThrows(IllegalArgumentException.class, new org.junit.jupiter.api.function.Executable() {
            public void execute() {
                service.sanitizePdfFileName("  ");
            }
        });
    }
}
