package com.brewpoint.pos.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class DateUtils {
    private static final DateTimeFormatter VIETNAM_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String INVALID_MESSAGE = "Ngày không hợp lệ. Nhập theo dạng dd/MM/yyyy.";

    private DateUtils() {
    }

    public static String format(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(VIETNAM_DATE);
    }

    public static LocalDate parseRequired(String value) {
        String clean = value == null ? "" : value.trim();
        if (clean.isEmpty()) {
            throw new IllegalArgumentException("Ngày không được để trống.");
        }
        return parse(clean);
    }

    public static LocalDate parseOptional(String value) {
        String clean = value == null ? "" : value.trim();
        if (clean.isEmpty()) {
            return null;
        }
        return parse(clean);
    }

    private static LocalDate parse(String clean) {
        try {
            return LocalDate.parse(clean, VIETNAM_DATE);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(INVALID_MESSAGE);
        }
    }
}
