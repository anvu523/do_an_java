package com.drinkstore.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public final class ValidationUtil {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w._%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9+\\-\\s]{9,20}$");

    private ValidationUtil() {
    }

    public static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " không được để trống.");
        }
        return value.trim();
    }

    public static BigDecimal requirePositiveMoney(String value, String fieldName) {
        try {
            BigDecimal number = new BigDecimal(requireText(value, fieldName));
            if (number.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ValidationException(fieldName + " phải lớn hơn 0.");
            }
            return number;
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName + " phải là số hợp lệ.");
        }
    }

    public static int requireNonNegativeInt(String value, String fieldName) {
        try {
            int number = Integer.parseInt(requireText(value, fieldName));
            if (number < 0) {
                throw new ValidationException(fieldName + " phải lớn hơn hoặc bằng 0.");
            }
            return number;
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName + " phải là số nguyên hợp lệ.");
        }
    }

    public static int requirePositiveInt(int value, String fieldName) {
        if (value <= 0) {
            throw new ValidationException(fieldName + " phải lớn hơn 0.");
        }
        return value;
    }

    public static void validateOptionalEmail(String value) {
        if (value != null && !value.trim().isEmpty() && !EMAIL_PATTERN.matcher(value.trim()).matches()) {
            throw new ValidationException("Email không đúng định dạng.");
        }
    }

    public static void validateOptionalPhone(String value) {
        if (value != null && !value.trim().isEmpty() && !PHONE_PATTERN.matcher(value.trim()).matches()) {
            throw new ValidationException("Số điện thoại không đúng định dạng.");
        }
    }
}
