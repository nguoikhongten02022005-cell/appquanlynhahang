package com.example.quanlynhahang.model;

public class DatBan {

    public enum TrangThai {
        PENDING_APPROVAL,
        CONFIRMED,
        COMPLETED,
        CANCELED
    }

    private final long idDatBan;
    private final String thoiGian;
    private final String soBan;
    private final int soKhach;
    private final String ghiChu;
    private TrangThai trangThai;

    public DatBan(long id,
                  String time,
                  String tableNumber,
                  int guestCount,
                  String note,
                  TrangThai status) {
        this.idDatBan = id;
        this.thoiGian = time;
        this.soBan = tableNumber;
        this.soKhach = guestCount;
        this.ghiChu = note;
        this.trangThai = status;
    }

    public long layId() {
        return idDatBan;
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

    public boolean coTheHuy() {
        return trangThai == TrangThai.PENDING_APPROVAL;
    }

    public void huyDatBan() {
        if (coTheHuy()) {
            trangThai = TrangThai.CANCELED;
        }
    }
}
