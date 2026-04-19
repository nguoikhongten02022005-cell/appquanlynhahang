package com.example.quanlynhahang.model;

public class NguoiDung {

    private final long idNguoiDung;
    private final String hoTen;
    private final String diaChiEmail;
    private final String soDienThoai;
    private final VaiTroNguoiDung vaiTro;
    private final boolean hoatDong;

    public NguoiDung(long idNguoiDung,
                     String hoTen,
                     String diaChiEmail,
                     String soDienThoai,
                     VaiTroNguoiDung vaiTro,
                     boolean hoatDong) {
        this.idNguoiDung = idNguoiDung;
        this.hoTen = hoTen;
        this.diaChiEmail = diaChiEmail;
        this.soDienThoai = soDienThoai;
        this.vaiTro = vaiTro;
        this.hoatDong = hoatDong;
    }

    public long layId() {
        return idNguoiDung;
    }

    public String layHoTen() {
        return hoTen;
    }

    public String layEmail() {
        return diaChiEmail;
    }

    public String laySoDienThoai() {
        return soDienThoai;
    }

    public VaiTroNguoiDung layVaiTro() {
        return vaiTro;
    }

    public boolean dangHoatDong() {
        return hoatDong;
    }

    public boolean laKhachHang() {
        return vaiTro == VaiTroNguoiDung.KHACH_HANG;
    }

    public boolean laNhanVien() {
        return vaiTro == VaiTroNguoiDung.NHAN_VIEN;
    }

    public boolean laAdmin() {
        return vaiTro == VaiTroNguoiDung.ADMIN;
    }
}
