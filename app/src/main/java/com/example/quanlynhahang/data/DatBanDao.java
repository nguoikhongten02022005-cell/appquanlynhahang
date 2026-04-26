package com.example.quanlynhahang.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.quanlynhahang.model.DatBan;

import java.util.List;

@Dao
public interface DatBanDao {
    @Insert
    long chen(DatBan datBan);
    
    @Update
    void capNhat(DatBan datBan);
    
    @Query("SELECT * FROM bang_dat_ban ORDER BY thoiGian DESC")
    LiveData<List<DatBan>> layTatCa();
    
    @Query("SELECT * FROM bang_dat_ban WHERE trangThai = :trangThai ORDER BY thoiGian ASC")
    LiveData<List<DatBan>> layTheoTrangThai(String trangThai);
}