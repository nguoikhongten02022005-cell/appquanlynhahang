package com.example.quanlynhahang.helper;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.example.quanlynhahang.CustomerLauncherActivity;
import com.example.quanlynhahang.MainActivity;
import com.example.quanlynhahang.StaffLauncherActivity;
import com.example.quanlynhahang.TrungTamQuanTriActivity;
import com.example.quanlynhahang.TrungTamQuanTriActivity;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.model.VaiTroNguoiDung;

public final class DieuHuongVaiTroHelper {

    private DieuHuongVaiTroHelper() {
    }

    public static Intent taoIntentTheoVaiTro(Context context, @Nullable VaiTroNguoiDung vaiTro) {
        VaiTroNguoiDung vaiTroHienTai = vaiTro != null ? vaiTro : VaiTroNguoiDung.KHACH_HANG;
        if (vaiTroHienTai == VaiTroNguoiDung.ADMIN && CauHinhTinhNangHelper.coNoiBoShellMoi()) {
            return TrungTamQuanTriActivity.taoIntent(context, DieuHuongNoiBoHelper.SECTION_BAO_CAO);
        }
        if (vaiTroHienTai == VaiTroNguoiDung.NHAN_VIEN && CauHinhTinhNangHelper.coNoiBoShellMoi()) {
            return DieuHuongNoiBoHelper.taoIntentTrungTamNoiBo(context, DieuHuongNoiBoHelper.TAB_TONG_QUAN);
        }
        if (laVaiTroNoiBo(vaiTroHienTai)) {
            return new Intent(context, StaffLauncherActivity.class);
        }
        return new Intent(context, MainActivity.class);
    }

    public static Intent taoIntentSaiVaiTro(Context context,
                                            SessionManager sessionManager,
                                            boolean choPhepXemGiaoDienKhach) {
        if (sessionManager != null && sessionManager.daDangNhap()) {
            VaiTroNguoiDung vaiTroSession = sessionManager.layVaiTroSessionHopLe();
            if (laVaiTroNoiBo(vaiTroSession)) {
                return taoIntentTheoVaiTro(context, vaiTroSession);
            }
            if (choPhepXemGiaoDienKhach) {
                return taoIntentKhachHang(context, true);
            }
            return new Intent(context, CustomerLauncherActivity.class);
        }
        return choPhepXemGiaoDienKhach
                ? taoIntentKhachHang(context, true)
                : new Intent(context, CustomerLauncherActivity.class);
    }

    private static boolean laVaiTroNoiBo(@Nullable VaiTroNguoiDung vaiTro) {
        return vaiTro == VaiTroNguoiDung.NHAN_VIEN || vaiTro == VaiTroNguoiDung.ADMIN;
    }

    private static Intent taoIntentKhachHang(Context context, boolean choPhepXemGiaoDienKhach) {
        Intent intent = new Intent(context, MainActivity.class);
        if (choPhepXemGiaoDienKhach) {
            intent.putExtra(MainActivity.EXTRA_CHO_PHEP_XEM_GIAO_DIEN_KHACH, true);
        }
        return intent;
    }
}
