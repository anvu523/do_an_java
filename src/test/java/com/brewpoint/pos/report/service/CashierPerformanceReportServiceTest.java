package com.brewpoint.pos.report.service;

import com.brewpoint.pos.report.AbstractReportTest;
import com.brewpoint.pos.report.datasource.CashierPerformanceRow;
import com.brewpoint.pos.report.datasource.ReportDataSource;
import net.sf.jasperreports.engine.JasperPrint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CashierPerformanceReportServiceTest extends AbstractReportTest {
    @Mock
    private ReportDataSource reportDataSource;

    @Test
    void build_withCashierRows_generatesReport() throws Exception {
        LocalDate start = LocalDate.of(2026, 6, 1);
        LocalDate end = LocalDate.of(2026, 6, 30);
        when(reportDataSource.loadCashierPerformance(start, end)).thenReturn(Arrays.asList(
                new CashierPerformanceRow("Nguyễn Thị Lan", 210, new BigDecimal("9200000")),
                new CashierPerformanceRow("Trần Văn Minh", 205, new BigDecimal("9050000")),
                new CashierPerformanceRow("Lê Hoàng An", 198, new BigDecimal("8800000"))
        ));

        JasperPrint print = new CashierPerformanceReportService(reportDataSource).build(start, end, "Tháng 06/2026");

        assertNotNull(print);
    }
}
