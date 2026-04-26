package com.example.quanlynhahang.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DonHang {
    public enum TrangThai {
        CHO_XAC_NHAN,
        DANG_CHUAN_BI,
        SAN_SANG_PHUC_VU,
        HOAN_THANH,
        DA_HUY
    }

    public enum HinhThucDon {
        AN_TAI_QUAN,
        MANG_DI
    }

    public enum TrangThaiThanhToan {
        CHUA_THANH_TOAN,
        DA_GOI_THANH_TOAN,
        DA_THANH_TOAN
    }

    public enum PhuongThucThanhToan {
        CHUA_CHON,
        TAI_QUAY,
        TIEN_MAT_KHI_NHAN,
        CHUYEN_KHOAN_NGAN_HANG,
        VI_DIEN_TU,
        THANH_TOAN_NGAY
    }

    private final long idDonHang;
    private final String maDon;
    private final String thoiGian;
    private final String tongTien;
    private final HinhThucDon hinhThucDon;
    private final String soBan;
    private final String ghiChu;
    private final TrangThaiThanhToan trangThaiThanhToan;
    private final PhuongThucThanhToan phuongThucThanhToan;
    private final long idDatBanLienKet;
    private final List<MonTrongDon> danhSachMon;
    private TrangThai trangThai;
    private boolean moRong;

    public DonHang(long idDonHang,
                   String maDon,
                   String thoiGian,
                   String tongTien,
                   HinhThucDon hinhThucDon,
                   String soBan,
                   String ghiChu,
                   TrangThai trangThai,
                   TrangThaiThanhToan trangThaiThanhToan,
                   PhuongThucThanhToan phuongThucThanhToan,
                   long idDatBanLienKet,
                   List<MonTrongDon> danhSachMon) {
        this.idDonHang = idDonHang;
        this.maDon = maDon;
        this.thoiGian = thoiGian;
        this.tongTien = tongTien;
        this.hinhThucDon = hinhThucDon == null ? HinhThucDon.MANG_DI : hinhThucDon;
        this.soBan = soBan == null ? "" : soBan.trim();
        this.ghiChu = ghiChu == null ? "" : ghiChu.trim();
        this.trangThai = trangThai == null ? TrangThai.CHO_XAC_NHAN : trangThai;
        this.trangThaiThanhToan = trangThaiThanhToan == null
                ? TrangThaiThanhToan.CHUA_THANH_TOAN
                : trangThaiThanhToan;
        this.phuongThucThanhToan = phuongThucThanhToan == null
                ? PhuongThucThanhToan.CHUA_CHON
                : phuongThucThanhToan;
        this.idDatBanLienKet = idDatBanLienKet;
        this.danhSachMon = new ArrayList<>(danhSachMon == null ? Collections.emptyList() : danhSachMon);
        this.moRong = false;
    }

    public long layId() {
        return idDonHang;
    }

    public String layMaDon() {
        return maDon;
    }

    public String layThoiGian() {
        return thoiGian;
    }

    public String layTongTien() {
        return tongTien;
    }

    public HinhThucDon layHinhThucDon() {
        return hinhThucDon;
    }

    public String laySoBan() {
        return soBan;
    }

    public String layGhiChu() {
        return ghiChu;
    }

    public TrangThai layTrangThai() {
        return trangThai;
    }

    public TrangThaiThanhToan layTrangThaiThanhToan() {
        return trangThaiThanhToan;
    }

    public PhuongThucThanhToan layPhuongThucThanhToan() {
        return phuongThucThanhToan;
    }

    public long layIdDatBanLienKet() {
        return idDatBanLienKet;
    }

    public List<MonTrongDon> layDanhSachMon() {
        return Collections.unmodifiableList(danhSachMon);
    }

    public boolean dangMoRong() {
        return moRong;
    }

    public void datTrangThaiMoRong(boolean moRong) {
        this.moRong = moRong;
    }

    public boolean laAnTaiQuan() {
        return hinhThucDon == HinhThucDon.AN_TAI_QUAN;
    }

    public boolean coBanAn() {
        return !soBan.isEmpty();
    }

    public boolean coGhiChu() {
        return !ghiChu.isEmpty();
    }

    public boolean coTheHuy() {
        return trangThai == TrangThai.CHO_XAC_NHAN;
    }

    public boolean coTheNhanVienHuy() {
        return trangThai == TrangThai.CHO_XAC_NHAN
                || trangThai == TrangThai.DANG_CHUAN_BI
                || trangThai == TrangThai.SAN_SANG_PHUC_VU;
    }

    public boolean coTheChuyenSangDangChuanBi() {
        return trangThai == TrangThai.CHO_XAC_NHAN;
    }

    public boolean coTheChuyenSangSanSangPhucVu() {
        return trangThai == TrangThai.DANG_CHUAN_BI;
    }

    public boolean coTheHoanThanh() {
        return trangThai == TrangThai.SAN_SANG_PHUC_VU;
    }

    public void capNhatTrangThai(TrangThai trangThaiMoi) {
        if (trangThaiMoi != null) {
            this.trangThai = trangThaiMoi;
        }
    }

    public void huyDon() {
        capNhatTrangThai(TrangThai.DA_HUY);
    }

    public static class MonTrongDon {
        private final MonAnDeXuat monAn;
        private final int soLuong;

        public MonTrongDon(MonAnDeXuat monAn, int soLuong) {
            this.monAn = monAn;
            this.soLuong = soLuong;
        }

        public MonAnDeXuat layMonAn() {
            return monAn;
        }

        public int laySoLuong() {
            return soLuong;
        }
    }
}
