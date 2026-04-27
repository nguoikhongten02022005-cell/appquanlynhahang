package com.example.quanlynhahang.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DonHang {
    public long id;

    public long nguoiDungId;
    public String maDon;
    public String thoiGian;
    public double tongTien;
    public String trangThai;
    public String loaiDon;
    public String soBan;
    public String ghiChu;
    public String trangThaiThanhToan;
    public String phuongThucThanhToan;
    public long datBanId;

    private String tongTienHienThi;
    private List<MonTrongDon> danhSachMon;
    private boolean moRong;

    public DonHang() {
        this.maDon = "";
        this.thoiGian = "";
        this.tongTienHienThi = "0";
        this.trangThai = TrangThai.CHO_XAC_NHAN.name();
        this.loaiDon = HinhThucDon.MANG_DI.name();
        this.soBan = "";
        this.ghiChu = "";
        this.trangThaiThanhToan = TrangThaiThanhToan.CHUA_THANH_TOAN.name();
        this.phuongThucThanhToan = PhuongThucThanhToan.CHUA_CHON.name();
        this.danhSachMon = new ArrayList<>();
        this.moRong = false;
    }

    public DonHang(long id,
                   String maDon,
                   String thoiGian,
                   String tongTien,
                   HinhThucDon hinhThucDon,
                   String soBan,
                   String ghiChu,
                   TrangThai trangThai,
                   TrangThaiThanhToan trangThaiThanhToan,
                   PhuongThucThanhToan phuongThucThanhToan,
                   long datBanId,
                   List<MonTrongDon> danhSachMon) {
        this.id = id;
        this.maDon = maDon == null ? "" : maDon.trim();
        this.thoiGian = thoiGian == null ? "" : thoiGian.trim();

        this.tongTienHienThi = tongTien == null ? "0" : tongTien.trim();
        this.tongTien = parsePriceToDouble(this.tongTienHienThi);

        this.loaiDon = hinhThucDon != null
                ? hinhThucDon.name()
                : HinhThucDon.MANG_DI.name();

        this.soBan = soBan == null ? "" : soBan.trim();
        this.ghiChu = ghiChu == null ? "" : ghiChu.trim();

        this.trangThai = trangThai != null
                ? trangThai.name()
                : TrangThai.CHO_XAC_NHAN.name();

        this.trangThaiThanhToan = trangThaiThanhToan != null
                ? trangThaiThanhToan.name()
                : TrangThaiThanhToan.CHUA_THANH_TOAN.name();

        this.phuongThucThanhToan = phuongThucThanhToan != null
                ? phuongThucThanhToan.name()
                : PhuongThucThanhToan.CHUA_CHON.name();

        this.datBanId = datBanId;
        this.danhSachMon = new ArrayList<>(
                danhSachMon == null ? Collections.emptyList() : danhSachMon
        );
        this.moRong = false;
    }

    private double parsePriceToDouble(String price) {
        if (price == null || price.trim().isEmpty()) {
            return 0;
        }

        String onlyDigits = price.replaceAll("[^0-9]", "");
        if (onlyDigits.isEmpty()) {
            return 0;
        }

        try {
            return Double.parseDouble(onlyDigits);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public enum TrangThai {
        CHO_XAC_NHAN,
        DA_XAC_NHAN,
        DANG_CHUAN_BI,
        DANG_PHUC_VU,
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
        TIEN_MAT,
        TIEN_MAT_KHI_NHAN,
        TAI_QUAY,
        THANH_TOAN_NGAY,
        CHUYEN_KHOAN,
        CHUYEN_KHOAN_NGAN_HANG,
        VI_DIEN_TU,
        THE
    }

    public long layId() {
        return id;
    }

    public String layMaDon() {
        return maDon != null ? maDon : "";
    }

    public String layThoiGian() {
        return thoiGian != null ? thoiGian : "";
    }

    public String layTongTien() {
        return tongTienHienThi != null ? tongTienHienThi : "0";
    }

    public TrangThai layTrangThai() {
        if (trangThai == null || trangThai.trim().isEmpty()) {
            return TrangThai.CHO_XAC_NHAN;
        }

        try {
            return TrangThai.valueOf(trangThai);
        } catch (Exception e) {
            return TrangThai.CHO_XAC_NHAN;
        }
    }

    public TrangThaiThanhToan layTrangThaiThanhToan() {
        if (trangThaiThanhToan == null || trangThaiThanhToan.trim().isEmpty()) {
            return TrangThaiThanhToan.CHUA_THANH_TOAN;
        }

        try {
            return TrangThaiThanhToan.valueOf(trangThaiThanhToan);
        } catch (Exception e) {
            return TrangThaiThanhToan.CHUA_THANH_TOAN;
        }
    }

    public PhuongThucThanhToan layPhuongThucThanhToan() {
        if (phuongThucThanhToan == null || phuongThucThanhToan.trim().isEmpty()) {
            return PhuongThucThanhToan.CHUA_CHON;
        }

        try {
            return PhuongThucThanhToan.valueOf(phuongThucThanhToan);
        } catch (Exception e) {
            return PhuongThucThanhToan.CHUA_CHON;
        }
    }

    public String laySoBan() {
        return soBan != null ? soBan : "";
    }

    public boolean coBanAn() {
        return HinhThucDon.AN_TAI_QUAN.name().equals(loaiDon);
    }

    public boolean laAnTaiQuan() {
        return coBanAn();
    }

    public String layGhiChu() {
        return ghiChu != null ? ghiChu : "";
    }

    public boolean coGhiChu() {
        return !layGhiChu().trim().isEmpty();
    }

    public boolean dangMoRong() {
        return moRong;
    }

    public void datTrangThaiMoRong(boolean moRong) {
        this.moRong = moRong;
    }

    public boolean coTheHuy() {
        TrangThai tt = layTrangThai();
        return tt == TrangThai.CHO_XAC_NHAN || tt == TrangThai.DA_XAC_NHAN;
    }

    public boolean coTheChuyenSangDangChuanBi() {
        return layTrangThai() == TrangThai.CHO_XAC_NHAN;
    }

    public boolean coTheChuyenSangSanSangPhucVu() {
        TrangThai tt = layTrangThai();
        return tt == TrangThai.DANG_CHUAN_BI || tt == TrangThai.DANG_PHUC_VU;
    }

    public boolean coTheHoanThanh() {
        return layTrangThai() == TrangThai.SAN_SANG_PHUC_VU;
    }

    public boolean coTheNhanVienHuy() {
        TrangThai tt = layTrangThai();
        return tt == TrangThai.CHO_XAC_NHAN
                || tt == TrangThai.DANG_CHUAN_BI
                || tt == TrangThai.SAN_SANG_PHUC_VU;
    }

    public List<MonTrongDon> layDanhSachMon() {
        return danhSachMon == null ? Collections.emptyList() : danhSachMon;
    }

    public static class MonTrongDon {
        private MonAnDeXuat monAn;
        private int soLuong;

        public MonTrongDon() {
        }

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

        public void datSoLuong(int soLuong) {
            this.soLuong = soLuong;
        }
    }
}
