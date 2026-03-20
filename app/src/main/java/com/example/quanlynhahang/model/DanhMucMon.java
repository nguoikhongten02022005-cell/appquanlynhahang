package com.example.quanlynhahang.model;

public class DanhMucMon {
    private final int idIconTaiNguyen;
    private final String tenHienThi;
    private final String tenDanhMuc;

    public DanhMucMon(int iconResId, String tenHienThi, String tenDanhMuc) {
        this.idIconTaiNguyen = iconResId;
        this.tenHienThi = tenHienThi;
        this.tenDanhMuc = tenDanhMuc;
    }

    public int layIconResId() {
        return idIconTaiNguyen;
    }

    public String layTenHienThi() {
        return tenHienThi;
    }

    public String layTenDanhMuc() {
        return tenDanhMuc;
    }

    public String layTen() {
        return layTenHienThi();
    }
}
