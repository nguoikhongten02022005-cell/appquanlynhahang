package com.example.quanlynhahang.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "bang_don_hang")
public class DonHang {
    @PrimaryKey(autoGenerate = true)
    public long id;
    
    public long nguoiDungId;
    public String maDon;
    public long thoiGian;
    public double tongTien;
    public String trangThai;
    public String loaiDon;
    public String soBan;
    public String ghiChu;
    public String trangThaiThanhToan;
    public String phuongThucThanhToan;
    public long datBanId;
    
    @Ignore
    private List<MonTrongDon> danhSachMon;
    
    public DonHang() {}
    
    @Ignore
    public DonHang(long id, String maDon, String thoiGian, String tongTien,
                   HinhThucDon hinhThucDon, String soBan, String ghiChu,
                   TrangThai trangThai, TrangThaiThanhToan trangThaiThanhToan,
                   PhuongThucThanhToan phuongThucThanhToan, long datBanId,
                   List<MonTrongDon> danhSachMon) {
        this.id = id;
        this.maDon = maDon;
        this.thoiGian = parseTimeToLong(thoiGian);
        this.tongTien = parsePriceToDouble(tongTien);
        this.loaiDon = hinhThucDon != null ? hinhThucDon.name() : HinhThucDon.MANG_DI.name();
        this.soBan = soBan;
        this.ghiChu = ghiChu;
        this.trangThai = trangThai != null ? trangThai.name() : TrangThai.CHO_XAC_NHAN.name();
        this.trangThaiThanhToan = trangThaiThanhToan != null ? trangThaiThanhToan.name() : TrangThaiThanhToan.CHUA_THANH_TOAN.name();
        this.phuongThucThanhToan = phuongThucThanhToan != null ? phuongThucThanhToan.name() : PhuongThucThanhToan.CHUA_CHON.name();
        this.datBanId = datBanId;
        this.danhSachMon = danhSachMon;
    }
    
    private long parseTimeToLong(String time) {
        if (time == null || time.isEmpty()) return System.currentTimeMillis();
        try {
            return Long.parseLong(time);
        } catch (NumberFormatException e) {
            return System.currentTimeMillis();
        }
    }
    
    private double parsePriceToDouble(String price) {
        if (price == null || price.isEmpty()) return 0;
        try {
            return Double.parseDouble(price.replace(",", "").replace(".", ""));
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
        return String.valueOf(thoiGian);
    }
    
    public String layTongTien() {
        return String.format("%.0f", tongTien);
    }
    
    public TrangThai layTrangThai() {
        try {
            return TrangThai.valueOf(trangThai);
        } catch (Exception e) {
            return TrangThai.CHO_XAC_NHAN;
        }
    }
    
    public TrangThaiThanhToan layTrangThaiThanhToan() {
        try {
            return TrangThaiThanhToan.valueOf(trangThaiThanhToan);
        } catch (Exception e) {
            return TrangThaiThanhToan.CHUA_THANH_TOAN;
        }
    }
    
    public PhuongThucThanhToan layPhuongThucThanhToan() {
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
        return loaiDon != null && loaiDon.equals(HinhThucDon.AN_TAI_QUAN.name());
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
        return false;
    }

    public void datTrangThaiMoRong(boolean moRong) {
        // Trạng thái mở rộng chỉ phục vụ UI adapter; model Room không lưu trạng thái này.
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
        return tt == TrangThai.CHO_XAC_NHAN || tt == TrangThai.DANG_CHUAN_BI || tt == TrangThai.SAN_SANG_PHUC_VU;
    }
    
    public List<MonTrongDon> layDanhSachMon() {
        return danhSachMon;
    }
    
    public static class MonTrongDon {
        private MonAnDeXuat monAn;
        private int soLuong;
        
        public MonTrongDon() {}
        
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
