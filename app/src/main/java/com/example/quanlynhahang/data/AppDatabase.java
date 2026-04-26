package com.example.quanlynhahang.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.quanlynhahang.model.BangBanAn;
import com.example.quanlynhahang.model.DatBan;
import com.example.quanlynhahang.model.DonHang;
import com.example.quanlynhahang.model.DonHangItem;
import com.example.quanlynhahang.model.MonAn;
import com.example.quanlynhahang.model.NguoiDung;
import com.example.quanlynhahang.model.YeuCauPhucVu;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
    entities = {
        NguoiDung.class,
        MonAn.class,
        DonHang.class,
        DonHangItem.class,
        BangBanAn.class,
        DatBan.class,
        YeuCauPhucVu.class
    },
    version = 1,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    
    private static final String TEN_CSDL = "nhahang.db";
    private static volatile AppDatabase instance;
    
    public abstract NguoiDungDao nguoiDungDao();
    public abstract MonAnDao monAnDao();
    public abstract DonHangDao donHangDao();
    public abstract BangBanAnDao bangBanAnDao();
    public abstract DatBanDao datBanDao();
    public abstract YeuCauPhucVuDao yeuCauPhucVuDao();
    
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    
    public static AppDatabase layInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class,
                        TEN_CSDL
                    )
                    .addCallback(new RoomDatabase.Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            executor.execute(() -> {
                                TaoDuLieuMacDinh(instance);
                            });
                        }
                    })
                    .build();
                }
            }
        }
        return instance;
    }
    
    private static void TaoDuLieuMacDinh(AppDatabase db) {
        NguoiDung admin = new NguoiDung(
            "Quản trị viên",
            "admin@nhahang.com",
            "admin123",
            "",
            "ADMIN"
        );
        db.nguoiDungDao().chen(admin);
        
        for (int i = 1; i <= 10; i++) {
            BangBanAn ban = new BangBanAn();
            ban.maBan = "B" + String.format("%02d", i);
            ban.tenBan = "Bàn " + String.format("%02d", i);
            ban.soCho = 4;
            ban.khuVuc = i <= 5 ? "Tầng 1" : "Tầng 2";
            ban.trangThai = "TRONG";
            db.bangBanAnDao().chen(ban);
        }
    }
}