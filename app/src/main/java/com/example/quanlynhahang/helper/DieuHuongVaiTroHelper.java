package com.example.quanlynhahang.helper;

import android.content.Context;
import android.content.Intent;

import com.example.quanlynhahang.AdminActivity;
import com.example.quanlynhahang.EmployeeActivity;
import com.example.quanlynhahang.MainActivity;
import com.example.quanlynhahang.model.UserRole;

public final class DieuHuongVaiTroHelper {

    private DieuHuongVaiTroHelper() {
    }

    public static Intent taoIntentTheoVaiTro(Context context, UserRole vaiTro) {
        Class<?> dichDen = MainActivity.class;
        UserRole vaiTroHienTai = vaiTro != null ? vaiTro : UserRole.KHACH_HANG;

        if (vaiTroHienTai == UserRole.NHAN_VIEN) {
            dichDen = EmployeeActivity.class;
        } else if (vaiTroHienTai == UserRole.ADMIN) {
            dichDen = AdminActivity.class;
        }

        return new Intent(context, dichDen);
    }
}
