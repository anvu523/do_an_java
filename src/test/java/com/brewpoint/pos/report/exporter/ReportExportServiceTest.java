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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReportExportServiceTest {

    @Mock
    private ReportExportStrategy strategy;

    @TempDir
    Path tempDir;

    @Test
    void exportToFile_delegatesToStrategy() throws Exception {
        ReportExportService service = new ReportExportService();
        JasperPrint print = mock(JasperPrint.class);
        File target = tempDir.resolve("DailyRevenue_2026-06-15.pdf").toFile();

        service.exportToFile(print, target, strategy);

        verify(strategy).export(print, target);
    }

    @Test
    void exportToFile_whenStrategyFails_propagatesException() throws Exception {
        ReportExportService service = new ReportExportService();
        JasperPrint print = mock(JasperPrint.class);
        File target = tempDir.resolve("fail.pdf").toFile();
        doThrow(new JRException("Export lỗi")).when(strategy).export(print, target);

        assertThrows(JRException.class, () -> {
            service.exportToFile(print, target, strategy);
        });
    }

    @Test
    void exportToFile_rejectsNullPrint() {
        ReportExportService service = new ReportExportService();
        File target = tempDir.resolve("x.pdf").toFile();

        assertThrows(IllegalArgumentException.class, () -> {
            service.exportToFile(null, target, strategy);
        });
    }

    @Test
    void resolveTarget_appendsExtensionWhenMissing() {
        ReportExportService service = new ReportExportService();
        File resolved = service.resolveTarget(new File("MonthlyRevenue_2026-06"), "pdf");
        assertEquals("MonthlyRevenue_2026-06.pdf", resolved.getName());

        File resolvedXlsx = service.resolveTarget(new File("MonthlyRevenue_2026-06"), "xlsx");
        assertEquals("MonthlyRevenue_2026-06.xlsx", resolvedXlsx.getName());
    }

    @Test
    void resolveTarget_correctsInvalidExtension() {
        ReportExportService service = new ReportExportService();
        File resolved = service.resolveTarget(new File("MonthlyRevenue_2026-06.pdf"), "xlsx");
        assertEquals("MonthlyRevenue_2026-06.xlsx", resolved.getName());
    }

    @Test
    void sanitizeFileName_replacesInvalidCharacters() {
        ReportExportService service = new ReportExportService();
        String safe = service.sanitizeFileName("Invoice:HD*001", "pdf");
        assertEquals("Invoice_HD_001.pdf", safe);

        String safeXlsx = service.sanitizeFileName("Invoice:HD*001", "xlsx");
        assertEquals("Invoice_HD_001.xlsx", safeXlsx);
    }

    @Test
    void sanitizeFileName_rejectsBlankName() {
        ReportExportService service = new ReportExportService();

        assertThrows(IllegalArgumentException.class, () -> {
            service.sanitizeFileName("  ", "pdf");
        });
    }
}
