package com.example.quanlynhahang.model;

import android.text.TextUtils;

import java.util.Locale;

public enum VaiTroNguoiDung {
    KHACH_HANG,
    NHAN_VIEN,
    ADMIN;

    public static VaiTroNguoiDung tuChuoi(String giaTri) {
        if (TextUtils.isEmpty(giaTri)) {
            return KHACH_HANG;
        }

        String giaTriChuanHoa = giaTri.trim().toUpperCase(Locale.ROOT);
        for (VaiTroNguoiDung vaiTro : values()) {
            if (vaiTro.name().equals(giaTriChuanHoa)) {
                return vaiTro;
            }
        }
        return KHACH_HANG;
    }
}
