package com.example.quanlynhahang.model;

public enum VaiTro {
    ADMIN("ADMIN"),
    NHAN_VIEN("NHAN_VIEN"),
    KHACH_HANG("KHACH_HANG");

    private final String giaTri;

    VaiTro(String giaTri) {
        this.giaTri = giaTri;
    }

    public String layGiaTri() {
        return giaTri;
    }

    public static VaiTro tuChuoi(String chuoi) {
        for (VaiTro v : values()) {
            if (v.giaTri.equals(chuoi)) return v;
        }
        return KHACH_HANG;
    }
}