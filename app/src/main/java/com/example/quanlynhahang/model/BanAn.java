package com.example.quanlynhahang.model;

public class BanAn {

    public enum TrangThai {
        TRONG,
        DANG_PHUC_VU,
        DA_DAT
    }

    private final long id;
    private final String maBan;
    private final String tenBan;
    private final int soCho;
    private final String khuVuc;
    private TrangThai trangThai;

    public BanAn(long id,
                 String maBan,
                 String tenBan,
                 int soCho,
                 String khuVuc,
                 TrangThai trangThai) {
        this.id = id;
        this.maBan = maBan == null ? "" : maBan.trim();
        this.tenBan = tenBan == null ? "" : tenBan.trim();
        this.soCho = Math.max(soCho, 1);
        this.khuVuc = khuVuc == null ? "" : khuVuc.trim();
        this.trangThai = trangThai == null ? TrangThai.TRONG : trangThai;
    }

    public long layId() {
        return id;
    }

    public String layMaBan() {
        return maBan;
    }

    public String layTenBan() {
        return tenBan;
    }

    public int laySoCho() {
        return soCho;
    }

    public String layKhuVuc() {
        return khuVuc;
    }

    public TrangThai layTrangThai() {
        return trangThai;
    }

    public boolean coTheXoa() {
        return trangThai == TrangThai.TRONG;
    }

    public void capNhatTrangThai(TrangThai trangThaiMoi) {
        if (trangThaiMoi != null) {
            this.trangThai = trangThaiMoi;
        }
    }
}