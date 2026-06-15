package com.brewpoint.pos.report.service;

import com.brewpoint.pos.report.AbstractReportTest;
import com.brewpoint.pos.report.datasource.DailyRevenueMetrics;
import com.brewpoint.pos.report.datasource.ReportDataSource;
import net.sf.jasperreports.engine.JasperPrint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DailyRevenueReportServiceTest extends AbstractReportTest {
    @Mock
    private ReportDataSource reportDataSource;

    @Test
    void build_withValidData_generatesReport() throws Exception {
        LocalDate date = LocalDate.of(2026, 6, 15);
        when(reportDataSource.loadDailyRevenue(date)).thenReturn(new DailyRevenueMetrics(
                date,
                new BigDecimal("890000"),
                25,
                new BigDecimal("35600")
        ));

        JasperPrint print = new DailyRevenueReportService(reportDataSource).build(date);

        assertNotNull(print);
        assertTrue(print.getPages().size() > 0);
    }

    @Test
    void build_withNullDate_throwsValidationError() {
        assertThrows(IllegalArgumentException.class, new org.junit.jupiter.api.function.Executable() {
            public void execute() throws Exception {
                new DailyRevenueReportService(reportDataSource).build(null);
            }
        });
    }

    @Test
    void build_whenDataSourceFails_propagatesException() throws Exception {
        LocalDate date = LocalDate.of(2026, 5, 10);
        when(reportDataSource.loadDailyRevenue(date)).thenThrow(new SQLException("DB lỗi"));

        assertThrows(SQLException.class, new org.junit.jupiter.api.function.Executable() {
            public void execute() throws Throwable {
                new DailyRevenueReportService(reportDataSource).build(date);
            }
        });
    }

    @Test
    void defaultPdfName_usesIsoDate() {
        String name = new DailyRevenueReportService(reportDataSource).defaultPdfName(LocalDate.of(2026, 6, 15));
        assertTrue(name.contains("DailyRevenue_2026-06-15.pdf"));
    }

    @Test
    void build_withRangeValidData_generatesReport() throws Exception {
        LocalDate start = LocalDate.of(2026, 6, 15);
        LocalDate end = LocalDate.of(2026, 6, 20);
        when(reportDataSource.loadDailyRevenue(start, end)).thenReturn(new DailyRevenueMetrics(
                start,
                new BigDecimal("1890000"),
                50,
                new BigDecimal("37800")
        ));

        JasperPrint print = new DailyRevenueReportService(reportDataSource).build(start, end);

        assertNotNull(print);
        assertTrue(print.getPages().size() > 0);
    }

    @Test
    void defaultPdfName_withRange_usesRangeDates() {
        String name = new DailyRevenueReportService(reportDataSource).defaultPdfName(LocalDate.of(2026, 6, 15), LocalDate.of(2026, 6, 20));
        assertTrue(name.contains("DailyRevenue_2026-06-15_to_2026-06-20.pdf"));
    }
}
