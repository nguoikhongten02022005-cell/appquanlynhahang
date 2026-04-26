package com.example.quanlynhahang.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "bang_yeu_cau_phuc_vu")
public class YeuCauPhucVu {
    @PrimaryKey(autoGenerate = true)
    public long id;
    
    public long nguoiDungId;
    public String noiDung;
    public long guiLuc;
    public String trangThai;
    public String loaiYeuCau;
    public String soBan;
    public long donHangId;
    public long xuLyLuc;
    
    public YeuCauPhucVu() {}
    
    @Ignore
    public YeuCauPhucVu(long id, LoaiYeuCau loaiYeuCau, String noiDung,
                        String guiLuc, String xuLyLuc, String soBan,
                        long donHangId, TrangThai trangThai) {
        this.id = id;
        this.loaiYeuCau = loaiYeuCau != null ? loaiYeuCau.name() : LoaiYeuCau.GOI_PHUC_VU.name();
        this.noiDung = noiDung;
        this.guiLuc = parseTimeToLong(guiLuc);
        this.xuLyLuc = parseTimeToLong(xuLyLuc);
        this.soBan = soBan;
        this.donHangId = donHangId;
        this.trangThai = trangThai != null ? trangThai.name() : TrangThai.CHO_XU_LY.name();
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
        DANG_CHO,
        CHO_XU_LY,
        DANG_XU_LY,
        DA_XU_LY,
        DA_HUY
    }
    
    public enum LoaiYeuCau {
        GOI_NHAN_VIEN,
        GOI_PHUC_VU,
        THEM_NUOC,
        THANH_TOAN,
        YEU_CAU_NUOC,
        YEU_CAU_BILL,
        KHAC
    }
    
    public TrangThai layTrangThai() {
        try {
            return TrangThai.valueOf(trangThai);
        } catch (Exception e) {
            return TrangThai.CHO_XU_LY;
        }
    }
    
    public LoaiYeuCau layLoaiYeuCau() {
        try {
            return LoaiYeuCau.valueOf(loaiYeuCau);
        } catch (Exception e) {
            return LoaiYeuCau.GOI_PHUC_VU;
        }
    }
    
    public long layId() {
        return id;
    }

    public String layNoiDung() {
        return noiDung != null ? noiDung : "";
    }

    public String laySoBan() {
        return soBan != null ? soBan : "";
    }

    public boolean coBanLienQuan() {
        return !laySoBan().trim().isEmpty();
    }

    public String layThoiGianGui() {
        return String.valueOf(guiLuc);
    }
    
    public long layIdDonHang() {
        return donHangId;
    }
    
    public boolean coDonHangLienQuan() {
        return donHangId > 0;
    }

    public boolean dangHoatDong() {
        TrangThai tt = layTrangThai();
        return tt == TrangThai.DANG_CHO || tt == TrangThai.CHO_XU_LY || tt == TrangThai.DANG_XU_LY;
    }

    public boolean coTheHuy() {
        return dangHoatDong();
    }

    public void danhDauDaHuy() {
        this.trangThai = TrangThai.DA_HUY.name();
        this.xuLyLuc = System.currentTimeMillis();
    }
}
