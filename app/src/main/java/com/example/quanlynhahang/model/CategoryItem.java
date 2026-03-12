package com.example.quanlynhahang.model;

public class CategoryItem {
    private final int iconResId;
    private final String tenHienThi;
    private final String tenDanhMuc;

    public CategoryItem(int iconResId, String tenHienThi, String tenDanhMuc) {
        this.iconResId = iconResId;
        this.tenHienThi = tenHienThi;
        this.tenDanhMuc = tenDanhMuc;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }

    public String getTenDanhMuc() {
        return tenDanhMuc;
    }

    public String getName() {
        return getTenHienThi();
    }
}
