package com.example.quanlynhahang.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.quanlynhahang.data.AppDatabase;
import com.example.quanlynhahang.data.NguoiDungDao;
import com.example.quanlynhahang.model.NguoiDung;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NguoiDungViewModel extends AndroidViewModel {
    
    private final NguoiDungDao nguoiDungDao;
    private final LiveData<List<NguoiDung>> tatCaNguoiDung;
    private final ExecutorService executor;
    
    public NguoiDungViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.layInstance(application);
        nguoiDungDao = db.nguoiDungDao();
        tatCaNguoiDung = nguoiDungDao.layTatCa();
        executor = Executors.newSingleThreadExecutor();
    }
    
    public LiveData<List<NguoiDung>> layTatCa() {
        return tatCaNguoiDung;
    }
    
    public LiveData<List<NguoiDung>> layNhanVien() {
        return nguoiDungDao.layTheoVaiTro("NHAN_VIEN");
    }
    
    public LiveData<List<NguoiDung>> layKhachHang() {
        return nguoiDungDao.layTheoVaiTro("KHACH_HANG");
    }
    
    public void dangNhap(String email, String matKhau, DangNhapCallback callback) {
        executor.execute(() -> {
            NguoiDung nguoiDung = nguoiDungDao.dangNhap(email, matKhau);
            callback.sauKhiDangNhap(nguoiDung);
        });
    }
    
    public void chen(NguoiDung nguoiDung, ChenCallback callback) {
        executor.execute(() -> {
            long id = nguoiDungDao.chen(nguoiDung);
            callback.sauKhiChen(id);
        });
    }
    
    public interface DangNhapCallback {
        void sauKhiDangNhap(NguoiDung nguoiDung);
    }
    
    public interface ChenCallback {
        void sauKhiChen(long id);
    }
}