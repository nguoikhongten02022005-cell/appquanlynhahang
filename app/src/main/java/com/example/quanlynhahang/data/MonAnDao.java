package com.example.quanlynhahang.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.quanlynhahang.model.MonAn;

import java.util.List;

@Dao
public interface MonAnDao {
    @Insert
    long chen(MonAn monAn);
    
    @Update
    void capNhat(MonAn monAn);
    
    @Delete
    void xoa(MonAn monAn);
    
    @Query("SELECT * FROM bang_mon_an WHERE id = :id")
    LiveData<MonAn> layTheoId(long id);
    
    @Query("SELECT * FROM bang_mon_an WHERE id = :id")
    MonAn layTheoIdDongBo(long id);
    
    @Query("SELECT * FROM bang_mon_an WHERE coSan = 1 ORDER BY ten ASC")
    LiveData<List<MonAn>> layTatCa();
    
    @Query("SELECT * FROM bang_mon_an WHERE coSan = 1 AND danhMuc = :danhMuc ORDER BY ten ASC")
    LiveData<List<MonAn>> layTheoDanhMuc(String danhMuc);
    
    @Query("SELECT DISTINCT danhMuc FROM bang_mon_an ORDER BY danhMuc ASC")
    LiveData<List<String>> layTatCaDanhMuc();
    
    @Query("SELECT * FROM bang_mon_an WHERE biLuuTru = 0 ORDER BY diemDeXuat DESC LIMIT 10")
    LiveData<List<MonAn>> layMonDeXuat();
}