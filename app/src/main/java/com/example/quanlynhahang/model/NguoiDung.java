package com.example.quanlynhahang.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "bang_nguoi_dung")
public class NguoiDung {
    @PrimaryKey(autoGenerate = true)
    public long id;
    
    public String ten;
    public String email;
    public String matKhau;
    public String soDienThoai;
    public String vaiTro;
    public boolean hoatDong;
    
    public NguoiDung() {}
    
    public NguoiDung(String ten, String email, String matKhau, String soDienThoai, String vaiTro) {
        this.ten = ten;
        this.email = email;
        this.matKhau = matKhau;
        this.soDienThoai = soDienThoai;
        this.vaiTro = vaiTro;
        this.hoatDong = true;
    }
}