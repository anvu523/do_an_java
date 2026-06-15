package com.brewpoint.pos.report.util;

import com.brewpoint.pos.model.PaymentMethod;
import com.brewpoint.pos.model.ProductSalesStat;
import com.brewpoint.pos.report.datasource.DailyRevenueMetrics;
import com.brewpoint.pos.model.OrderSummary;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportParameterBuilderTest {

    @Test
    void dailyRevenue_buildsExpectedParameters() {
        DailyRevenueMetrics metrics = new DailyRevenueMetrics(
                LocalDate.of(2026, 6, 15),
                new BigDecimal("1250000"),
                42,
                new BigDecimal("29762")
        );

        Map<String, Object> parameters = ReportParameterBuilder.dailyRevenue(metrics);

        assertEquals("Báo cáo doanh thu theo ngày", parameters.get("REPORT_TITLE"));
        assertEquals("15/06/2026", parameters.get("REPORT_DATE"));
        assertEquals("1.250.000 ₫", parameters.get("TOTAL_REVENUE"));
        assertEquals("42", parameters.get("ORDER_COUNT"));
        assertEquals("29.762 ₫", parameters.get("AVERAGE_ORDER"));
    }

    @Test
    void dailyRevenue_rejectsNullMetrics() {
        assertThrows(IllegalArgumentException.class, new org.junit.jupiter.api.function.Executable() {
            public void execute() {
                ReportParameterBuilder.dailyRevenue(null);
            }
        });
    }

    @Test
    void monthlyRevenue_buildsExpectedParameters() {
        Map<String, Object> parameters = ReportParameterBuilder.monthlyRevenue(2026, 6, new BigDecimal("8900000"));

        assertEquals("Báo cáo doanh thu theo tháng", parameters.get("REPORT_TITLE"));
        assertEquals("06/2026", parameters.get("REPORT_MONTH"));
        assertEquals("8.900.000 ₫", parameters.get("TOTAL_REVENUE"));
    }

    @Test
    void monthlyRevenue_rejectsInvalidMonth() {
        assertThrows(IllegalArgumentException.class, new org.junit.jupiter.api.function.Executable() {
            public void execute() {
                ReportParameterBuilder.validateMonth(2026, 13);
            }
        });
    }

    @Test
    void receipt_formatsCashPayment() {
        OrderSummary order = new OrderSummary();
        order.setOrderCode("HD20260615001");
        order.setEmployeeName("Nguyễn Thị Lan");
        order.setOrderTime(LocalDateTime.of(2026, 6, 15, 12, 30));
        order.setPaymentMethod(PaymentMethod.CASH);
        order.setTotalAmount(new BigDecimal("89000"));
        order.setReceivedAmount(new BigDecimal("100000"));
        order.setChangeAmount(new BigDecimal("11000"));

        Map<String, Object> parameters = ReportParameterBuilder.receipt(order);

        assertEquals("HD20260615001", parameters.get("ORDER_CODE"));
        assertEquals("Nguyễn Thị Lan", parameters.get("CASHIER_NAME"));
        assertEquals("89.000 ₫", parameters.get("TOTAL_AMOUNT"));
        assertEquals("100.000 ₫", parameters.get("CASH_RECEIVED"));
        assertEquals("11.000 ₫", parameters.get("CHANGE_AMOUNT"));
    }

    @Test
    void receipt_formatsTransferPayment() {
        OrderSummary order = new OrderSummary();
        order.setOrderCode("HD20260615002");
        order.setEmployeeName("Trần Văn Minh");
        order.setOrderTime(LocalDateTime.of(2026, 6, 15, 19, 0));
        order.setPaymentMethod(PaymentMethod.MANUAL_BANK_TRANSFER);
        order.setTotalAmount(new BigDecimal("55000"));

        Map<String, Object> parameters = ReportParameterBuilder.receipt(order);

        assertEquals("Chuyển khoản", parameters.get("PAYMENT_METHOD"));
        assertEquals("—", parameters.get("CASH_RECEIVED"));
        assertEquals("—", parameters.get("CHANGE_AMOUNT"));
    }

    @Test
    void productSalesRow_formatsMoney() {
        ProductSalesStat stat = new ProductSalesStat("Trà sữa trân châu đường đen", 120, new BigDecimal("5400000"));

        assertEquals("Trà sữa trân châu đường đen", ReportParameterBuilder.productSalesRow(stat).getProductName());
        assertEquals("5.400.000 ₫", ReportParameterBuilder.productSalesRow(stat).getRevenueText());
    }

    @Test
    void validateReportDate_rejectsNull() {
        assertThrows(IllegalArgumentException.class, new org.junit.jupiter.api.function.Executable() {
            public void execute() {
                ReportParameterBuilder.validateReportDate(null);
            }
        });
    }

    @Test
    void bestSellingProducts_requiresPeriodDesc() {
        Map<String, Object> parameters = ReportParameterBuilder.bestSellingProducts("Tháng 05/2026");
        assertTrue(parameters.get("REPORT_TITLE").toString().contains("bán chạy"));
    }
}
