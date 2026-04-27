package com.example.quanlynhahang.model;

public class DatBan {
    public long id;
    
    public long nguoiDungId;
    public long thoiGian;
    public String soBan;
    public int soKhach;
    public String ghiChu;
    public String trangThai;
    public String maDatBan;
    public long donHangLienKetId;
    
    public DatBan() {}
    
    public DatBan(long id, String maDatBan, String thoiGian, String soBan,
                  int soKhach, String ghiChu, TrangThai trangThai, long donHangLienKetId) {
        this.id = id;
        this.maDatBan = maDatBan;
        this.thoiGian = parseTimeToLong(thoiGian);
        this.soBan = soBan;
        this.soKhach = soKhach;
        this.ghiChu = ghiChu;
        this.trangThai = trangThai != null ? trangThai.name() : TrangThai.CHO_XAC_NHAN.name();
        this.donHangLienKetId = donHangLienKetId;
    }
    
    private long parseTimeToLong(String time) {
        if (time == null || time.isEmpty()) return System.currentTimeMillis();
        try {
            return Long.parseLong(time);
        } catch (NumberFormatException e) {
            return System.currentTimeMillis();
        }
    }
    
    public enum TrangThai {
        PENDING,
        ACTIVE,
        CHO_XAC_NHAN,
        DA_XAC_NHAN,
        DA_DEN,
        DANG_PHUC_VU,
        HOAN_THANH,
        COMPLETED,
        DA_HUY,
        CANCELLED,
        KHONG_DEN,
        EXPIRED
    }
    
    public long layId() {
        return id;
    }
    
    public String laySoBan() {
        return soBan != null ? soBan : "";
    }

    public int laySoKhach() {
        return soKhach;
    }

    public String layGhiChu() {
        return ghiChu != null ? ghiChu : "";
    }

    public boolean coGhiChu() {
        return !layGhiChu().trim().isEmpty();
    }

    public String layMaDatBan() {
        return maDatBan != null ? maDatBan : "";
    }

    public boolean coMaDatBan() {
        return !layMaDatBan().trim().isEmpty();
    }
    
    public String layThoiGian() {
        return String.valueOf(thoiGian);
    }
    
    public TrangThai layTrangThai() {
        try {
            return TrangThai.valueOf(trangThai);
        } catch (Exception e) {
            return TrangThai.CHO_XAC_NHAN;
        }
    }
    
    public long layIdDonHangLienKet() {
        return donHangLienKetId;
    }
    
    public boolean laDangHieuLuc() {
        TrangThai tt = layTrangThai();
        return tt == TrangThai.CHO_XAC_NHAN || tt == TrangThai.DA_XAC_NHAN || 
               tt == TrangThai.DA_DEN || tt == TrangThai.DANG_PHUC_VU ||
               tt == TrangThai.PENDING || tt == TrangThai.ACTIVE;
    }
    
    public boolean daKetThuc() {
        TrangThai tt = layTrangThai();
        return tt == TrangThai.HOAN_THANH || tt == TrangThai.DA_HUY || 
               tt == TrangThai.KHONG_DEN || tt == TrangThai.COMPLETED ||
               tt == TrangThai.CANCELLED || tt == TrangThai.EXPIRED;
    }

    public boolean daHoanTatGuiMon() {
        TrangThai tt = layTrangThai();
        return tt == TrangThai.HOAN_THANH || tt == TrangThai.COMPLETED;
    }

    public boolean coTheXacNhan() {
        TrangThai tt = layTrangThai();
        return tt == TrangThai.PENDING || tt == TrangThai.CHO_XAC_NHAN;
    }

    public boolean coTheHoanTat() {
        TrangThai tt = layTrangThai();
        return tt == TrangThai.ACTIVE || tt == TrangThai.DA_XAC_NHAN || tt == TrangThai.DA_DEN || tt == TrangThai.DANG_PHUC_VU;
    }

    public boolean coTheHuy() {
        TrangThai tt = layTrangThai();
        return tt == TrangThai.PENDING || tt == TrangThai.CHO_XAC_NHAN || tt == TrangThai.DA_XAC_NHAN;
    }
}
