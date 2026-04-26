package com.example.quanlynhahang.model;

import androidx.room.Entity;
import androidx.room.Ignore;
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
    
    @Ignore
    public NguoiDung(String ten, String email, String matKhau, String soDienThoai, String vaiTro) {
        this.ten = ten;
        this.email = email;
        this.matKhau = matKhau;
        this.soDienThoai = soDienThoai;
        this.vaiTro = vaiTro;
        this.hoatDong = true;
    }

    @Ignore
    public NguoiDung(long id, String ten, String email, String soDienThoai, VaiTroNguoiDung vaiTro, boolean hoatDong) {
        this.id = id;
        this.ten = ten;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.vaiTro = vaiTro == null ? VaiTroNguoiDung.KHACH_HANG.name() : vaiTro.name();
        this.hoatDong = hoatDong;
    }

    public long layId() {
        return id;
    }

    public VaiTroNguoiDung layVaiTro() {
        return VaiTroNguoiDung.tuChuoi(vaiTro);
    }

    public boolean dangHoatDong() {
        return hoatDong;
    }

    public String layHoTen() {
        return ten != null ? ten : "";
    }

    public String layEmail() {
        return email != null ? email : "";
    }

    public String laySoDienThoai() {
        return soDienThoai != null ? soDienThoai : "";
    }

    public boolean laAdmin() {
        return layVaiTro() == VaiTroNguoiDung.ADMIN;
    }

    public boolean laNhanVien() {
        return layVaiTro() == VaiTroNguoiDung.NHAN_VIEN;
    }
}