package com.brewpoint.pos.util;

import java.math.BigDecimal;

public final class ValidationUtils {
    private ValidationUtils() {
    }

    public static String requireText(String value, String fieldName) {
        String clean = value == null ? "" : value.trim();
        if (clean.isEmpty()) {
            throw new ValidationException(fieldName + " không được để trống.");
        }
        return clean;
    }

    public static int requirePositiveInt(String value, String fieldName) {
        int number = parseInteger(value, fieldName);
        if (number <= 0) {
            throw new ValidationException(fieldName + " phải lớn hơn 0.");
        }
        return number;
    }

    public static int requireNonNegativeInt(String value, String fieldName) {
        int number = parseInteger(value, fieldName);
        if (number < 0) {
            throw new ValidationException(fieldName + " không được âm.");
        }
        return number;
    }

    public static BigDecimal requirePositiveMoney(String value, String fieldName) {
        BigDecimal money = parseVnd(value, fieldName);
        if (money.signum() <= 0) {
            throw new ValidationException(fieldName + " phải lớn hơn 0.");
        }
        return money;
    }

    public static BigDecimal parseNonNegativeMoney(String value, String fieldName) {
        BigDecimal money = parseVnd(value, fieldName);
        if (money.signum() < 0) {
            throw new ValidationException(fieldName + " không được âm.");
        }
        return money;
    }

    public static String normalizeNote(String note) {
        if (note == null) {
            return "";
        }
        String clean = note.trim().replaceAll("\\s+", " ");
        if (clean.length() > 200) {
            throw new ValidationException("Ghi chú tối đa 200 ký tự.");
        }
        return clean;
    }

    private static int parseInteger(String value, String fieldName) {
        String clean = requireText(value, fieldName).replace(".", "").replace(",", "");
        try {
            return Integer.parseInt(clean);
        } catch (NumberFormatException ex) {
            throw new ValidationException(fieldName + " phải là số nguyên.");
        }
    }

    private static BigDecimal parseVnd(String value, String fieldName) {
        String clean = requireText(value, fieldName).replace(".", "").replace(",", "");
        if (!clean.matches("\\d+")) {
            throw new ValidationException(fieldName + " chỉ nhận số nguyên VND.");
        }
        return new BigDecimal(clean);
    }
}
