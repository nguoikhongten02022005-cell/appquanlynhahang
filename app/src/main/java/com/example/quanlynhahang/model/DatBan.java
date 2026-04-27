package com.example.quanlynhahang.model;

public class DatBan {
    public long id;

    public long nguoiDungId;
    public String thoiGian;
    public String soBan;
    public int soKhach;
    public String ghiChu;
    public String trangThai;
    public String maDatBan;
    public long donHangLienKetId;

    public DatBan() {
        this.thoiGian = "";
        this.soBan = "";
        this.ghiChu = "";
        this.trangThai = TrangThai.CHO_XAC_NHAN.name();
        this.maDatBan = "";
    }

    public DatBan(long id,
                  String maDatBan,
                  String thoiGian,
                  String soBan,
                  int soKhach,
                  String ghiChu,
                  TrangThai trangThai,
                  long donHangLienKetId) {
        this.id = id;
        this.maDatBan = maDatBan == null ? "" : maDatBan.trim();
        this.thoiGian = thoiGian == null ? "" : thoiGian.trim();
        this.soBan = soBan == null ? "" : soBan.trim();
        this.soKhach = Math.max(soKhach, 0);
        this.ghiChu = ghiChu == null ? "" : ghiChu.trim();
        this.trangThai = trangThai != null
                ? trangThai.name()
                : TrangThai.CHO_XAC_NHAN.name();
        this.donHangLienKetId = donHangLienKetId;
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
        return thoiGian != null ? thoiGian : "";
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

    public long layIdDonHangLienKet() {
        return donHangLienKetId;
    }

    public boolean laDangHieuLuc() {
        TrangThai tt = layTrangThai();
        return tt == TrangThai.CHO_XAC_NHAN
                || tt == TrangThai.DA_XAC_NHAN
                || tt == TrangThai.DA_DEN
                || tt == TrangThai.DANG_PHUC_VU
                || tt == TrangThai.PENDING
                || tt == TrangThai.ACTIVE;
    }

    public boolean daKetThuc() {
        TrangThai tt = layTrangThai();
        return tt == TrangThai.HOAN_THANH
                || tt == TrangThai.DA_HUY
                || tt == TrangThai.KHONG_DEN
                || tt == TrangThai.COMPLETED
                || tt == TrangThai.CANCELLED
                || tt == TrangThai.EXPIRED;
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
        return tt == TrangThai.ACTIVE
                || tt == TrangThai.DA_XAC_NHAN
                || tt == TrangThai.DA_DEN
                || tt == TrangThai.DANG_PHUC_VU;
    }

    public boolean coTheHuy() {
        TrangThai tt = layTrangThai();
        return tt == TrangThai.PENDING
                || tt == TrangThai.CHO_XAC_NHAN
                || tt == TrangThai.DA_XAC_NHAN;
    }
}
