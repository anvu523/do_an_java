package com.brewpoint.pos.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class PasswordUtils {
    private PasswordUtils() {
    }

    public static String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                builder.append(String.format("%02x", Integer.valueOf(bytes[i] & 0xff)));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Không hỗ trợ SHA-256.", ex);
        }
    }

    public static boolean matches(String rawPassword, String hash) {
        return sha256(rawPassword).equalsIgnoreCase(hash);
    }
}
