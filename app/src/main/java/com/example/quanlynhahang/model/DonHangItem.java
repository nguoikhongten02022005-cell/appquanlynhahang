package com.example.quanlynhahang.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "bang_don_hang_item")
public class DonHangItem {
    @PrimaryKey(autoGenerate = true)
    public long id;
    
    public long donHangId;
    public String tenMon;
    public double giaMon;
    public String hinhAnh;
    public boolean coSan;
    public int soLuong;
    
    public DonHangItem() {}
}