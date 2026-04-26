package com.example.quanlynhahang.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "bang_ban_an")
public class BangBanAn {
    @PrimaryKey(autoGenerate = true)
    public long id;
    
    public String maBan;
    public String tenBan;
    public int soCho;
    public String khuVuc;
    public String trangThai;
    
    public BangBanAn() {}
}