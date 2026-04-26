package com.example.quanlynhahang.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.quanlynhahang.data.AppDatabase;
import com.example.quanlynhahang.data.MonAnDao;
import com.example.quanlynhahang.model.MonAn;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MonAnViewModel extends AndroidViewModel {
    
    private final MonAnDao monAnDao;
    private final LiveData<List<MonAn>> tatCaMonAn;
    private final LiveData<List<MonAn>> monDeXuat;
    private final ExecutorService executor;
    
    public MonAnViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.layInstance(application);
        monAnDao = db.monAnDao();
        tatCaMonAn = monAnDao.layTatCa();
        monDeXuat = monAnDao.layMonDeXuat();
        executor = Executors.newSingleThreadExecutor();
    }
    
    public LiveData<List<MonAn>> layTatCa() {
        return tatCaMonAn;
    }
    
    public LiveData<List<MonAn>> layMonDeXuat() {
        return monDeXuat;
    }
    
    public LiveData<List<MonAn>> layTheoDanhMuc(String danhMuc) {
        return monAnDao.layTheoDanhMuc(danhMuc);
    }
    
    public LiveData<List<String>> layDanhMuc() {
        return monAnDao.layTatCaDanhMuc();
    }
}