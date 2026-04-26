package com.example.quanlynhahang.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.quanlynhahang.model.YeuCauPhucVu;

import java.util.List;

@Dao
public interface YeuCauPhucVuDao {
    @Insert
    long chen(YeuCauPhucVu yeuCau);
    
    @Update
    void capNhat(YeuCauPhucVu yeuCau);
    
    @Query("SELECT * FROM bang_yeu_cau_phuc_vu WHERE trangThai = 'CHO_XU_LY' ORDER BY guiLuc ASC")
    LiveData<List<YeuCauPhucVu>> layTatCaChoXuLy();
    
    @Query("SELECT * FROM bang_yeu_cau_phuc_vu WHERE soBan = :soBan ORDER BY guiLuc DESC")
    LiveData<List<YeuCauPhucVu>> layTheoSoBan(String soBan);
}