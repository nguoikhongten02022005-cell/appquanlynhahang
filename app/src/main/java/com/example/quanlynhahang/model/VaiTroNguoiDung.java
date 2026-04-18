package com.example.quanlynhahang.model;

import androidx.annotation.Nullable;

import java.util.Locale;

public enum VaiTroNguoiDung {
    KHACH_HANG,
    NHAN_VIEN,
    ADMIN;

    @Nullable
    public static VaiTroNguoiDung tuChuoiNghiemNhat(@Nullable String giaTri) {
        if (giaTri == null) {
            return null;
        }

        String giaTriChuanHoa = giaTri.trim();
        if (giaTriChuanHoa.isEmpty()) {
            return null;
        }

        giaTriChuanHoa = giaTriChuanHoa.toUpperCase(Locale.ROOT);
        for (VaiTroNguoiDung vaiTro : values()) {
            if (vaiTro.name().equals(giaTriChuanHoa)) {
                return vaiTro;
            }
        }
        return null;
    }

    public static VaiTroNguoiDung tuChuoi(String giaTri) {
        VaiTroNguoiDung vaiTro = tuChuoiNghiemNhat(giaTri);
        return vaiTro != null ? vaiTro : KHACH_HANG;
    }
}
