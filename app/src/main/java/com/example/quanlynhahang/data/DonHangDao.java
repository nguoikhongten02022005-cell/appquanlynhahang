package com.example.quanlynhahang.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.quanlynhahang.model.DonHang;

import java.util.List;

@Dao
public interface DonHangDao {
    @Insert
    long chen(DonHang donHang);
    
    @Update
    void capNhat(DonHang donHang);
    
    @Query("SELECT * FROM bang_don_hang WHERE id = :id")
    LiveData<DonHang> layTheoId(long id);
    
    @Query("SELECT * FROM bang_don_hang WHERE nguoiDungId = :nguoiDungId ORDER BY thoiGian DESC")
    LiveData<List<DonHang>> layTheoNguoiDung(long nguoiDungId);
    
    @Query("SELECT * FROM bang_don_hang ORDER BY thoiGian DESC")
    LiveData<List<DonHang>> layTatCa();
    
    @Query("SELECT * FROM bang_don_hang WHERE trangThai = :trangThai ORDER BY thoiGian DESC")
    LiveData<List<DonHang>> layTheoTrangThai(String trangThai);
    
    @Query("SELECT * FROM bang_don_hang WHERE thoiGian >= :tuLuc AND thoiGian <= :denLuc ORDER BY thoiGian DESC")
    LiveData<List<DonHang>> layTheoKhoangThoiGian(long tuLuc, long denLuc);
}