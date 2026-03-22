package com.example.quanlynhahang.model;

public class DatBan {

    public enum TrangThai {
        PENDING,
        ACTIVE,
        COMPLETED,
        CANCELLED,
        EXPIRED
    }

    private final long idDatBan;
    private final String maDatBan;
    private final String thoiGian;
    private final String soBan;
    private final int soKhach;
    private final String ghiChu;
    private final long linkedOrderId;
    private TrangThai trangThai;

    public DatBan(long idDatBan,
                  String maDatBan,
                  String thoiGian,
                  String soBan,
                  int soKhach,
                  String ghiChu,
                  TrangThai trangThai,
                  long linkedOrderId) {
        this.idDatBan = idDatBan;
        this.maDatBan = maDatBan == null ? "" : maDatBan.trim();
        this.thoiGian = thoiGian;
        this.soBan = soBan;
        this.soKhach = soKhach;
        this.ghiChu = ghiChu == null ? "" : ghiChu.trim();
        this.trangThai = trangThai == null ? TrangThai.PENDING : trangThai;
        this.linkedOrderId = linkedOrderId;
    }

    public long layId() {
        return idDatBan;
    }

    public String layMaDatBan() {
        return maDatBan;
    }

    public String layThoiGian() {
        return thoiGian;
    }

    public String laySoBan() {
        return soBan;
    }

    public int laySoKhach() {
        return soKhach;
    }

    public String layGhiChu() {
        return ghiChu;
    }

    public TrangThai layTrangThai() {
        return trangThai;
    }

    public long layLinkedOrderId() {
        return linkedOrderId;
    }

    public boolean coMaDatBan() {
        return !maDatBan.isEmpty();
    }

    public boolean coGhiChu() {
        return !ghiChu.isEmpty();
    }

    public boolean coTheHuy() {
        return trangThai == TrangThai.PENDING;
    }

    public boolean coTheXacNhan() {
        return false;
    }

    public boolean coTheHoanTat() {
        return false;
    }

    public boolean laDangChoDenGio() {
        return trangThai == TrangThai.PENDING;
    }

    public boolean laDangTrongKhungGio() {
        return trangThai == TrangThai.ACTIVE;
    }

    public boolean laDangHieuLuc() {
        return trangThai == TrangThai.PENDING || trangThai == TrangThai.ACTIVE;
    }

    public boolean daHoanTatGuiMon() {
        return trangThai == TrangThai.COMPLETED;
    }

    public boolean daKetThuc() {
        return trangThai == TrangThai.COMPLETED
                || trangThai == TrangThai.CANCELLED
                || trangThai == TrangThai.EXPIRED;
    }

    public void capNhatTrangThai(TrangThai trangThaiMoi) {
        if (trangThaiMoi != null) {
            this.trangThai = trangThaiMoi;
        }
    }

    public void huyDatBan() {
        capNhatTrangThai(TrangThai.CANCELLED);
    }
}
