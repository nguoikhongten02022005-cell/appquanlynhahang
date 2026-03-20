package com.example.quanlynhahang.model;

public class YeuCauPhucVu {

    public enum TrangThai {
        PROCESSING,
        DONE
    }

    private final long idYeuCau;
    private final String noiDung;
    private final String thoiGianGui;
    private TrangThai trangThai;

    public YeuCauPhucVu(String noiDung, String thoiGianGui, TrangThai trangThai) {
        this(0L, noiDung, thoiGianGui, trangThai);
    }

    public YeuCauPhucVu(long id, String noiDung, String thoiGianGui, TrangThai trangThai) {
        this.idYeuCau = id;
        this.noiDung = noiDung;
        this.thoiGianGui = thoiGianGui;
        this.trangThai = trangThai;
    }

    public long layId() {
        return idYeuCau;
    }

    public String layNoiDung() {
        return noiDung;
    }

    public String layThoiGianGui() {
        return thoiGianGui;
    }

    public TrangThai layTrangThai() {
        return trangThai;
    }

    public void danhDauDaXong() {
        trangThai = TrangThai.DONE;
    }

    public void capNhatDaXuLyXong() {
        danhDauDaXong();
    }
}
