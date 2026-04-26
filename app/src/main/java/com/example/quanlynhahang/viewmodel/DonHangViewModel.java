package com.example.quanlynhahang.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.quanlynhahang.data.AppDatabase;
import com.example.quanlynhahang.data.DonHangDao;
import com.example.quanlynhahang.model.DonHang;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DonHangViewModel extends AndroidViewModel {
    
    private final DonHangDao donHangDao;
    private final LiveData<List<DonHang>> tatCaDonHang;
    private final ExecutorService executor;
    
    public DonHangViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.layInstance(application);
        donHangDao = db.donHangDao();
        tatCaDonHang = donHangDao.layTatCa();
        executor = Executors.newSingleThreadExecutor();
    }
    
    public LiveData<List<DonHang>> layTatCa() {
        return tatCaDonHang;
    }
    
    public LiveData<List<DonHang>> layTheoNguoiDung(long nguoiDungId) {
        return donHangDao.layTheoNguoiDung(nguoiDungId);
    }
    
    public LiveData<List<DonHang>> layTheoKhoangThoiGian(long tuLuc, long denLuc) {
        return donHangDao.layTheoKhoangThoiGian(tuLuc, denLuc);
    }
}