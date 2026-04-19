package com.example.quanlynhahang.model;

public class ThongKeTongQuanQuanTri {
    private final int tongNguoiDung;
    private final int soKhachHang;
    private final int soNhanVien;
    private final int soQuanTriVien;
    private final int tongMonAn;
    private final int tongDonHang;
    private final int soDonHangChoXacNhan;
    private final int soDatBanChoDuyet;
    private final int soYeuCauDangXuLy;

    public ThongKeTongQuanQuanTri(int tongNguoiDung,
                                  int soKhachHang,
                                  int soNhanVien,
                                  int soQuanTriVien,
                                  int tongMonAn,
                                  int tongDonHang,
                                  int soDonHangChoXacNhan,
                                  int soDatBanChoDuyet,
                                  int soYeuCauDangXuLy) {
        this.tongNguoiDung = tongNguoiDung;
        this.soKhachHang = soKhachHang;
        this.soNhanVien = soNhanVien;
        this.soQuanTriVien = soQuanTriVien;
        this.tongMonAn = tongMonAn;
        this.tongDonHang = tongDonHang;
        this.soDonHangChoXacNhan = soDonHangChoXacNhan;
        this.soDatBanChoDuyet = soDatBanChoDuyet;
        this.soYeuCauDangXuLy = soYeuCauDangXuLy;
    }

    public int layTongNguoiDung() {
        return tongNguoiDung;
    }

    public int laySoKhachHang() {
        return soKhachHang;
    }

    public int laySoNhanVien() {
        return soNhanVien;
    }

    public int laySoQuanTriVien() {
        return soQuanTriVien;
    }

    public int layTongMonAn() {
        return tongMonAn;
    }

    public int layTongDonHang() {
        return tongDonHang;
    }

    public int laySoDonHangChoXacNhan() {
        return soDonHangChoXacNhan;
    }

    public int laySoDatBanChoDuyet() {
        return soDatBanChoDuyet;
    }

    public int laySoYeuCauDangXuLy() {
        return soYeuCauDangXuLy;
    }
}
