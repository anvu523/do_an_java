package com.brewpoint.pos.report.service;

import com.brewpoint.pos.model.OrderStatus;
import com.brewpoint.pos.model.OrderSummary;
import com.brewpoint.pos.model.PaymentMethod;
import com.brewpoint.pos.report.AbstractReportTest;
import com.brewpoint.pos.report.datasource.ReceiptLineRow;
import com.brewpoint.pos.report.datasource.ReportDataSource;
import com.brewpoint.pos.util.ValidationException;
import net.sf.jasperreports.engine.JasperPrint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReceiptReportServiceTest extends AbstractReportTest {
    @Mock
    private ReportDataSource reportDataSource;

    @Test
    void build_withValidOrder_generatesReceipt() throws Exception {
        OrderSummary order = sampleOrder();
        when(reportDataSource.loadOrderSummary(1L)).thenReturn(order);
        when(reportDataSource.loadReceiptLines(1L)).thenReturn(Arrays.asList(
                new ReceiptLineRow("Trà sữa trân châu đường đen", "Vừa (M) | Trân châu đen", 1, "55.000 ₫", "55.000 ₫")
        ));

        JasperPrint print = new ReceiptReportService(reportDataSource).build(1L);

        assertNotNull(print);
    }

    @Test
    void build_whenOrderMissing_throwsValidationException() throws Exception {
        when(reportDataSource.loadOrderSummary(99L)).thenThrow(new ValidationException("Không tìm thấy hóa đơn."));

        assertThrows(ValidationException.class, new org.junit.jupiter.api.function.Executable() {
            public void execute() throws Throwable {
                new ReceiptReportService(reportDataSource).build(99L);
            }
        });
    }

    @Test
    void build_withEmptyLines_stillGeneratesReceipt() throws Exception {
        when(reportDataSource.loadOrderSummary(2L)).thenReturn(sampleOrder());
        when(reportDataSource.loadReceiptLines(2L)).thenReturn(Collections.<ReceiptLineRow>emptyList());

        JasperPrint print = new ReceiptReportService(reportDataSource).build(2L);

        assertNotNull(print);
    }

    @Test
    void defaultPdfName_usesOrderCode() throws Exception {
        when(reportDataSource.loadOrderSummary(1L)).thenReturn(sampleOrder());

        String name = new ReceiptReportService(reportDataSource).defaultPdfName(1L);

        assertEquals("Invoice_HD20260615001.pdf", name);
    }

    private OrderSummary sampleOrder() {
        OrderSummary order = new OrderSummary();
        order.setOrderId(1L);
        order.setOrderCode("HD20260615001");
        order.setEmployeeId(2);
        order.setEmployeeName("Nguyễn Thị Lan");
        order.setOrderTime(LocalDateTime.of(2026, 6, 15, 12, 15));
        order.setStatus(OrderStatus.COMPLETED);
        order.setPaymentMethod(PaymentMethod.CASH);
        order.setTotalAmount(new BigDecimal("55000"));
        order.setReceivedAmount(new BigDecimal("100000"));
        order.setChangeAmount(new BigDecimal("45000"));
        return order;
    }
}
