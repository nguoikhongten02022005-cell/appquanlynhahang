package com.example.quanlynhahang.model;

public class DatBan {

    public enum TrangThai {
        CHO_XAC_NHAN,
        DA_XAC_NHAN,
        DA_PHUC_VU,
        DA_HUY
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
        this.trangThai = trangThai == null ? TrangThai.CHO_XAC_NHAN : trangThai;
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
        return trangThai == TrangThai.CHO_XAC_NHAN || trangThai == TrangThai.DA_XAC_NHAN;
    }

    public boolean coTheXacNhan() {
        return trangThai == TrangThai.CHO_XAC_NHAN;
    }

    public boolean coTheHoanTat() {
        return trangThai == TrangThai.DA_XAC_NHAN;
    }

    public void capNhatTrangThai(TrangThai trangThaiMoi) {
        if (trangThaiMoi != null) {
            this.trangThai = trangThaiMoi;
        }
    }

    public void huyDatBan() {
        capNhatTrangThai(TrangThai.DA_HUY);
    }
}
