package com.example.quanlynhahang.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;

import com.example.quanlynhahang.model.NguoiDung;

public class QuanLyPhien {
    
    private static final String PREFS = "phien_prefs";
    private static final String KEY_DA_DANG_NHAP = "da_dang_nhap";
    private static final String KEY_ID_NGUOI_DUNG = "id_nguoi_dung";
    private static final String KEY_VAI_TRO = "vai_tro";
    
    private final SharedPreferences boNho;
    private final AppDatabase database;
    
    public QuanLyPhien(Context context) {
        boNho = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        database = AppDatabase.layInstance(context);
    }
    
    public boolean daDangNhap() {
        return boNho.getBoolean(KEY_DA_DANG_NHAP, false);
    }
    
    public long layIdNguoiDung() {
        return boNho.getLong(KEY_ID_NGUOI_DUNG, -1);
    }
    
    public String layVaiTro() {
        return boNho.getString(KEY_VAI_TRO, "");
    }
    
    public void luuPhien(NguoiDung nguoiDung) {
        boNho.edit()
            .putBoolean(KEY_DA_DANG_NHAP, true)
            .putLong(KEY_ID_NGUOI_DUNG, nguoiDung.id)
            .putString(KEY_VAI_TRO, nguoiDung.vaiTro)
            .apply();
    }
    
    public void xoaPhien() {
        boNho.edit().clear().apply();
    }
    
    public LiveData<NguoiDung> layNguoiDungHienTai() {
        return database.nguoiDungDao().layTheoId(layIdNguoiDung());
    }
}