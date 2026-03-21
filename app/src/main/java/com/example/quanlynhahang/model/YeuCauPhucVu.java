package com.example.quanlynhahang.model;

public class YeuCauPhucVu {

    public enum LoaiYeuCau {
        GOI_NHAN_VIEN,
        THEM_NUOC,
        THANH_TOAN
    }

    public enum TrangThai {
        DANG_XU_LY,
        DA_XU_LY,
        DA_HUY
    }

    private final long idYeuCau;
    private final LoaiYeuCau loaiYeuCau;
    private final String noiDung;
    private final String thoiGianGui;
    private final String thoiGianXuLy;
    private final String soBan;
    private final long orderId;
    private TrangThai trangThai;

    public YeuCauPhucVu(long idYeuCau,
                        LoaiYeuCau loaiYeuCau,
                        String noiDung,
                        String thoiGianGui,
                        String thoiGianXuLy,
                        String soBan,
                        long orderId,
                        TrangThai trangThai) {
        this.idYeuCau = idYeuCau;
        this.loaiYeuCau = loaiYeuCau == null ? LoaiYeuCau.GOI_NHAN_VIEN : loaiYeuCau;
        this.noiDung = noiDung == null ? "" : noiDung.trim();
        this.thoiGianGui = thoiGianGui == null ? "" : thoiGianGui.trim();
        this.thoiGianXuLy = thoiGianXuLy == null ? "" : thoiGianXuLy.trim();
        this.soBan = soBan == null ? "" : soBan.trim();
        this.orderId = orderId;
        this.trangThai = trangThai == null ? TrangThai.DANG_XU_LY : trangThai;
    }

    public long layId() {
        return idYeuCau;
    }

    public LoaiYeuCau layLoaiYeuCau() {
        return loaiYeuCau;
    }

    public String layNoiDung() {
        return noiDung;
    }

    public String layThoiGianGui() {
        return thoiGianGui;
    }

    public String layThoiGianXuLy() {
        return thoiGianXuLy;
    }

    public String laySoBan() {
        return soBan;
    }

    public long layOrderId() {
        return orderId;
    }

    public TrangThai layTrangThai() {
        return trangThai;
    }

    public boolean coBanLienQuan() {
        return !soBan.isEmpty();
    }

    public boolean coThoiGianXuLy() {
        return !thoiGianXuLy.isEmpty();
    }

    public boolean coDonHangLienQuan() {
        return orderId > 0;
    }

    public boolean coTheHuy() {
        return trangThai == TrangThai.DANG_XU_LY;
    }

    public void danhDauDaXong() {
        trangThai = TrangThai.DA_XU_LY;
    }

    public void danhDauDaHuy() {
        trangThai = TrangThai.DA_HUY;
    }

    public void capNhatDaXuLyXong() {
        danhDauDaXong();
    }
}
