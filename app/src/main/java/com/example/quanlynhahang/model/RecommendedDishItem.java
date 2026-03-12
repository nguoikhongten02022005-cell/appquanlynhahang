package com.example.quanlynhahang.model;

public class RecommendedDishItem {
    private final int imageResId;
    private final String tenMon;
    private final String giaBan;
    private final boolean conPhucVu;
    private final String tenDanhMuc;
    private final int diemDeXuat;

    public RecommendedDishItem(int imageResId, String tenMon, String giaBan, boolean conPhucVu) {
        this(imageResId, tenMon, giaBan, conPhucVu, "", 0);
    }

    public RecommendedDishItem(int imageResId,
                               String tenMon,
                               String giaBan,
                               boolean conPhucVu,
                               String tenDanhMuc,
                               int diemDeXuat) {
        this.imageResId = imageResId;
        this.tenMon = tenMon;
        this.giaBan = giaBan;
        this.conPhucVu = conPhucVu;
        this.tenDanhMuc = tenDanhMuc;
        this.diemDeXuat = diemDeXuat;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getTenMon() {
        return tenMon;
    }

    public String getGiaBan() {
        return giaBan;
    }

    public boolean isConPhucVu() {
        return conPhucVu;
    }

    public String getTenDanhMuc() {
        return tenDanhMuc;
    }

    public int getDiemDeXuat() {
        return diemDeXuat;
    }

    public String getName() {
        return getTenMon();
    }

    public String getPrice() {
        return getGiaBan();
    }

    public boolean isAvailable() {
        return isConPhucVu();
    }
}
