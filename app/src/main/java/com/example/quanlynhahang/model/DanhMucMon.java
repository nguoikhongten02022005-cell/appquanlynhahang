package com.example.quanlynhahang.model;

public class DanhMucMon {
    private final int idIconTaiNguyen;
    private final String tenHienThi;
    private final String tenDanhMuc;

    public DanhMucMon(int idIconTaiNguyen, String tenHienThi, String tenDanhMuc) {
        this.idIconTaiNguyen = idIconTaiNguyen;
        this.tenHienThi = tenHienThi;
        this.tenDanhMuc = tenDanhMuc;
    }

    public int layIdIconTaiNguyen() {
        return idIconTaiNguyen;
    }

    public String layTenHienThi() {
        return tenHienThi;
    }

    public String layTenDanhMuc() {
        return tenDanhMuc;
    }
}
