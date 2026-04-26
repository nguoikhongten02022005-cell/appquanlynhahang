package com.example.quanlynhahang.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "bang_don_hang")
public class DonHang {
    @PrimaryKey(autoGenerate = true)
    public long id;
    
    public long nguoiDungId;
    public String maDon;
    public long thoiGian;
    public double tongTien;
    public String trangThai;
    public String loaiDon;
    public String soBan;
    public String ghiChu;
    public String trangThaiThanhToan;
    public String phuongThucThanhToan;
    public long datBanId;
    
    public DonHang() {}
}