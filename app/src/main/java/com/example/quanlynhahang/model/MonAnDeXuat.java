package com.example.quanlynhahang.model;

public class MonAnDeXuat {
    private final int idAnhTaiNguyen;
    private final String tenMon;
    private final String giaBan;
    private final boolean conPhucVu;
    private final String tenDanhMuc;
    private final int diemDeXuat;

    public MonAnDeXuat(int idAnhTaiNguyen, String tenMon, String giaBan, boolean conPhucVu) {
        this(idAnhTaiNguyen, tenMon, giaBan, conPhucVu, "", 0);
    }

    public MonAnDeXuat(int idAnhTaiNguyen,
                       String tenMon,
                       String giaBan,
                       boolean conPhucVu,
                       String tenDanhMuc,
                       int diemDeXuat) {
        this.idAnhTaiNguyen = idAnhTaiNguyen;
        this.tenMon = tenMon;
        this.giaBan = giaBan;
        this.conPhucVu = conPhucVu;
        this.tenDanhMuc = tenDanhMuc;
        this.diemDeXuat = diemDeXuat;
    }

    public int layIdAnhTaiNguyen() {
        return idAnhTaiNguyen;
    }

    public String layTenMon() {
        return tenMon;
    }

    public String layGiaBan() {
        return giaBan;
    }

    public boolean laConPhucVu() {
        return conPhucVu;
    }

    public String layTenDanhMuc() {
        return tenDanhMuc;
    }

    public int layDiemDeXuat() {
        return diemDeXuat;
    }
}
