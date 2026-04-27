package com.example.quanlynhahang.model;

public class MonAn {
    public long id;
    
    public String ten;
    public double gia;
    public String moTa;
    public String hinhAnh;
    public boolean coSan;
    public String danhMuc;
    public double diemDeXuat;
    public boolean biLuuTru;
    public long luuTruLuc;
    
    public MonAn() {}
    
    public MonAn(String ten, double gia, String moTa, String danhMuc) {
        this.ten = ten;
        this.gia = gia;
        this.moTa = moTa;
        this.danhMuc = danhMuc;
        this.coSan = true;
        this.biLuuTru = false;
    }
}