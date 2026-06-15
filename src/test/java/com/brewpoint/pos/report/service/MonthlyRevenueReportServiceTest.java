package com.brewpoint.pos.report.service;

import com.brewpoint.pos.report.AbstractReportTest;
import com.brewpoint.pos.report.datasource.MonthlyRevenueDayRow;
import com.brewpoint.pos.report.datasource.ReportDataSource;
import net.sf.jasperreports.engine.JasperPrint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MonthlyRevenueReportServiceTest extends AbstractReportTest {
    @Mock
    private ReportDataSource reportDataSource;

    @Test
    void build_withDailyRows_generatesReport() throws Exception {
        when(reportDataSource.loadMonthlyRevenueDays(2026, 6)).thenReturn(Arrays.asList(
                new MonthlyRevenueDayRow(LocalDate.of(2026, 6, 1), new BigDecimal("450000"), 18),
                new MonthlyRevenueDayRow(LocalDate.of(2026, 6, 2), new BigDecimal("520000"), 20)
        ));
        when(reportDataSource.loadMonthlyRevenueTotal(2026, 6)).thenReturn(new BigDecimal("970000"));

        JasperPrint print = new MonthlyRevenueReportService(reportDataSource).build(2026, 6);

        assertNotNull(print);
        assertTrue(print.getPages().size() > 0);
    }

    @Test
    void build_withEmptyDataset_stillGeneratesReport() throws Exception {
        when(reportDataSource.loadMonthlyRevenueDays(2026, 5)).thenReturn(Collections.<MonthlyRevenueDayRow>emptyList());
        when(reportDataSource.loadMonthlyRevenueTotal(2026, 5)).thenReturn(BigDecimal.ZERO);

        JasperPrint print = new MonthlyRevenueReportService(reportDataSource).build(2026, 5);

        assertNotNull(print);
    }

    @Test
    void build_withInvalidMonth_throwsValidationError() {
        assertThrows(IllegalArgumentException.class, new org.junit.jupiter.api.function.Executable() {
            public void execute() throws Exception {
                new MonthlyRevenueReportService(reportDataSource).build(2026, 0);
            }
        });
    }
}
