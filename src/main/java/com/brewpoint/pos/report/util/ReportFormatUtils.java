package com.brewpoint.pos.report.util;

import com.brewpoint.pos.util.DateUtils;
import com.brewpoint.pos.util.MoneyUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public final class ReportFormatUtils {
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter MONTH_YEAR = DateTimeFormatter.ofPattern("MM/yyyy");

    private ReportFormatUtils() {
    }

    public static String money(BigDecimal amount) {
        return MoneyUtils.formatVnd(amount == null ? BigDecimal.ZERO : amount);
    }

    public static String date(LocalDate date) {
        return DateUtils.format(date);
    }

    public static String dateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DATE_TIME);
    }

    public static String generatedAt() {
        return LocalDateTime.now().format(DATE_TIME);
    }

    public static String monthYear(YearMonth yearMonth) {
        return yearMonth.format(MONTH_YEAR);
    }

    public static String monthYear(int year, int month) {
        return monthYear(YearMonth.of(year, month));
    }
}
