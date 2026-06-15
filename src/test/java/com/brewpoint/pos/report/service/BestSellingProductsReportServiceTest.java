package com.brewpoint.pos.report.service;

import com.brewpoint.pos.model.ProductSalesStat;
import com.brewpoint.pos.report.AbstractReportTest;
import com.brewpoint.pos.report.datasource.ReportDataSource;
import net.sf.jasperreports.engine.JasperPrint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BestSellingProductsReportServiceTest extends AbstractReportTest {
    @Mock
    private ReportDataSource reportDataSource;

    @Test
    void build_withProductStats_generatesReport() throws Exception {
        when(reportDataSource.loadBestSellingProducts(YearMonth.of(2026, 6))).thenReturn(Arrays.asList(
                new ProductSalesStat("Trà sữa trân châu đường đen", 180, new BigDecimal("8100000")),
                new ProductSalesStat("Trà đào cam sả", 145, new BigDecimal("5800000")),
                new ProductSalesStat("Bạc xỉu", 120, new BigDecimal("3840000"))
        ));

        JasperPrint print = new BestSellingProductsReportService(reportDataSource).build(2026, 6);

        assertNotNull(print);
    }

    @Test
    void build_withEmptyDataset_generatesReport() throws Exception {
        when(reportDataSource.loadBestSellingProducts(YearMonth.of(2026, 5)))
                .thenReturn(Collections.<ProductSalesStat>emptyList());

        JasperPrint print = new BestSellingProductsReportService(reportDataSource).build(2026, 5);

        assertNotNull(print);
    }
}
