package com.example.quanlynhahang.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "bang_dat_ban")
public class DatBan {
    @PrimaryKey(autoGenerate = true)
    public long id;
    
    public long nguoiDungId;
    public long thoiGian;
    public String soBan;
    public int soKhach;
    public String ghiChu;
    public String trangThai;
    public String maDatBan;
    public long donHangLienKetId;
    
    public DatBan() {}
}