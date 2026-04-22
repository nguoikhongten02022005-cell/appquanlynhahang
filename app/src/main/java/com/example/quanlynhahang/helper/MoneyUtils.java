package com.example.quanlynhahang.helper;

import androidx.annotation.Nullable;

import java.util.Locale;

public final class MoneyUtils {

    private MoneyUtils() {
    }

    public static long tachGiaTienTuChuoi(@Nullable String chuoiGia) {
        if (chuoiGia == null || chuoiGia.isEmpty()) {
            return 0;
        }
        String chuoiDaLamSach = chuoiGia.replaceAll("[^0-9]", "");
        try {
            return Long.parseLong(chuoiDaLamSach);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    public static String dinhDangTienViet(long soTien) {
        return String.format(Locale.forLanguageTag("vi-VN"), "%,d đ", soTien).replace(',', '.');
    }
}
