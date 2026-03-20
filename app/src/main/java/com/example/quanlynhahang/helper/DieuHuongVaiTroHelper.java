package com.example.quanlynhahang.helper;

import android.content.Context;
import android.content.Intent;

import com.example.quanlynhahang.QuanTriActivity;
import com.example.quanlynhahang.NhanVienActivity;
import com.example.quanlynhahang.MainActivity;
import com.example.quanlynhahang.model.VaiTroNguoiDung;

public final class DieuHuongVaiTroHelper {

    private DieuHuongVaiTroHelper() {
    }

    public static Intent taoIntentTheoVaiTro(Context context, VaiTroNguoiDung vaiTro) {
        Class<?> dichDen = MainActivity.class;
        VaiTroNguoiDung vaiTroHienTai = vaiTro != null ? vaiTro : VaiTroNguoiDung.KHACH_HANG;

        if (vaiTroHienTai == VaiTroNguoiDung.NHAN_VIEN) {
            dichDen = NhanVienActivity.class;
        } else if (vaiTroHienTai == VaiTroNguoiDung.ADMIN) {
            dichDen = QuanTriActivity.class;
        }

        return new Intent(context, dichDen);
    }
}
