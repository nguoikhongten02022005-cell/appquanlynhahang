package com.example.quanlynhahang.helper;

import androidx.annotation.Nullable;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Objects;

public final class PasswordHelper {

    private static final String PASSWORD_PREFIX_SHA256 = "sha256:";

    private PasswordHelper() {
    }

    public static boolean isHashedPassword(@Nullable String stored) {
        return stored != null && stored.startsWith(PASSWORD_PREFIX_SHA256);
    }

    public static String hashPassword(@Nullable String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest((raw == null ? "" : raw).getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder(PASSWORD_PREFIX_SHA256);
            for (byte b : bytes) {
                builder.append(String.format(Locale.US, "%02x", b));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 không khả dụng", ex);
        }
    }

    public static boolean verifyPassword(@Nullable String input, @Nullable String stored) {
        if (stored == null) {
            return false;
        }
        if (isHashedPassword(stored)) {
            return Objects.equals(hashPassword(input), stored);
        }
        return Objects.equals(input, stored);
    }
}
