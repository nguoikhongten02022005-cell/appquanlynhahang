package com.example.quanlynhahang.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.quanlynhahang.model.BangBanAn;

import java.util.List;

@Dao
public interface BangBanAnDao {
    @Insert
    long chen(BangBanAn banAn);
    
    @Update
    void capNhat(BangBanAn banAn);
    
    @Query("SELECT * FROM bang_ban_an ORDER BY maBan ASC")
    LiveData<List<BangBanAn>> layTatCa();
    
    @Query("SELECT * FROM bang_ban_an WHERE trangThai = :trangThai ORDER BY maBan ASC")
    LiveData<List<BangBanAn>> layTheoTrangThai(String trangThai);
    
    @Query("SELECT * FROM bang_ban_an WHERE id = :id")
    BangBanAn layTheoIdDongBo(long id);
}