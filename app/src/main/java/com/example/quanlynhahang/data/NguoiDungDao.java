package com.example.quanlynhahang.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.quanlynhahang.model.NguoiDung;

import java.util.List;

@Dao
public interface NguoiDungDao {
    @Insert
    long chen(NguoiDung nguoiDung);
    
    @Update
    void capNhat(NguoiDung nguoiDung);
    
    @Query("SELECT * FROM bang_nguoi_dung WHERE id = :id")
    LiveData<NguoiDung> layTheoId(long id);
    
    @Query("SELECT * FROM bang_nguoi_dung WHERE id = :id")
    NguoiDung layTheoIdDongBo(long id);
    
    @Query("SELECT * FROM bang_nguoi_dung WHERE email = :email AND matKhau = :matKhau LIMIT 1")
    NguoiDung dangNhap(String email, String matKhau);
    
    @Query("SELECT * FROM bang_nguoi_dung WHERE vaiTro = :vaiTro AND hoatDong = 1")
    LiveData<List<NguoiDung>> layTheoVaiTro(String vaiTro);
    
    @Query("SELECT * FROM bang_nguoi_dung WHERE vaiTro = :vaiTro AND hoatDong = 1")
    List<NguoiDung> layTheoVaiTroDongBo(String vaiTro);
    
    @Query("SELECT * FROM bang_nguoi_dung WHERE hoatDong = 1")
    LiveData<List<NguoiDung>> layTatCa();
}