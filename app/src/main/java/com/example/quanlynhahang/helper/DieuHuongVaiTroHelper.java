package com.example.quanlynhahang.helper;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.example.quanlynhahang.CustomerLauncherActivity;
import com.example.quanlynhahang.MainActivity;
import com.example.quanlynhahang.NhanVienActivity;
import com.example.quanlynhahang.QuanTriActivity;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.model.VaiTroNguoiDung;

public final class DieuHuongVaiTroHelper {

    private DieuHuongVaiTroHelper() {
    }

    public static Intent taoIntentTheoVaiTro(Context context, @Nullable VaiTroNguoiDung vaiTro) {
        Class<?> dichDen = MainActivity.class;
        VaiTroNguoiDung vaiTroHienTai = vaiTro != null ? vaiTro : VaiTroNguoiDung.KHACH_HANG;

        if (vaiTroHienTai == VaiTroNguoiDung.NHAN_VIEN) {
            dichDen = NhanVienActivity.class;
        } else if (vaiTroHienTai == VaiTroNguoiDung.ADMIN) {
            dichDen = QuanTriActivity.class;
        }

        return new Intent(context, dichDen);
    }

    public static Intent taoIntentSaiVaiTro(Context context,
                                            SessionManager sessionManager,
                                            boolean choPhepXemGiaoDienKhach) {
        if (sessionManager != null && sessionManager.daDangNhap()) {
            VaiTroNguoiDung vaiTroSession = sessionManager.layVaiTroSessionHopLe();
            if (vaiTroSession == VaiTroNguoiDung.NHAN_VIEN || vaiTroSession == VaiTroNguoiDung.ADMIN) {
                return taoIntentTheoVaiTro(context, vaiTroSession);
            }
            if (choPhepXemGiaoDienKhach) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra(MainActivity.EXTRA_CHO_PHEP_XEM_GIAO_DIEN_KHACH, true);
                return intent;
            }
            return new Intent(context, CustomerLauncherActivity.class);
        }
        return choPhepXemGiaoDienKhach
                ? new Intent(context, MainActivity.class).putExtra(MainActivity.EXTRA_CHO_PHEP_XEM_GIAO_DIEN_KHACH, true)
                : new Intent(context, CustomerLauncherActivity.class);
    }
}
