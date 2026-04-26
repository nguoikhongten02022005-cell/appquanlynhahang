package com.example.quanlynhahang.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "bang_yeu_cau_phuc_vu")
public class YeuCauPhucVu {
    @PrimaryKey(autoGenerate = true)
    public long id;
    
    public long nguoiDungId;
    public String noiDung;
    public long guiLuc;
    public String trangThai;
    public String loaiYeuCau;
    public String soBan;
    public long donHangId;
    public long xuLyLuc;
    
    public YeuCauPhucVu() {}
}