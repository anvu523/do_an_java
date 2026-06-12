package com.brewpoint.pos.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public final class MoneyUtils {
    private static final DecimalFormat FORMATTER;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        FORMATTER = new DecimalFormat("#,##0", symbols);
        FORMATTER.setParseBigDecimal(true);
    }

    private MoneyUtils() {
    }

    public static String formatVnd(BigDecimal amount) {
        BigDecimal safeAmount = amount == null ? BigDecimal.ZERO : amount;
        return FORMATTER.format(safeAmount) + " ₫";
    }

    public static BigDecimal parseVnd(String value) {
        return ValidationUtils.parseNonNegativeMoney(value, "Số tiền");
    }
}
