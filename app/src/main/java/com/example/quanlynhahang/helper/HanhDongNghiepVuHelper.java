package com.example.quanlynhahang.helper;

import androidx.annotation.StringRes;

import com.example.quanlynhahang.R;
import com.example.quanlynhahang.model.DatBan;
import com.example.quanlynhahang.model.DonHang;
import com.example.quanlynhahang.model.YeuCauPhucVu;

public final class HanhDongNghiepVuHelper {

    private HanhDongNghiepVuHelper() {
    }

    public static boolean khachCoTheHuyDon(DonHang donHang) {
        return donHang != null && donHang.coTheHuy();
    }

    public static boolean nhanVienCoTheNhanDon(DonHang donHang) {
        return donHang != null && donHang.coTheChuyenSangDangChuanBi();
    }

    public static boolean nhanVienCoTheChuyenSangPhucVu(DonHang donHang) {
        return donHang != null && donHang.coTheChuyenSangSanSangPhucVu();
    }

    public static boolean nhanVienCoTheHoanTatDon(DonHang donHang) {
        return donHang != null && donHang.coTheHoanThanh();
    }

    public static boolean nhanVienCoTheHuyDon(DonHang donHang) {
        return donHang != null && donHang.coTheNhanVienHuy();
    }

    @StringRes
    public static int layTextHanhDongChinhDon(DonHang donHang) {
        if (donHang == null) {
            return R.string.employee_order_action_accept;
        }
        if (donHang.coTheChuyenSangDangChuanBi()) {
            return R.string.employee_order_action_accept;
        }
        if (donHang.coTheChuyenSangSanSangPhucVu()) {
            return R.string.employee_order_action_move_to_serving;
        }
        return R.string.employee_order_action_complete;
    }

    public static boolean nhanVienCoTheXacNhanDatBan(DatBan datBan) {
        return datBan != null && datBan.coTheXacNhan();
    }

    public static boolean nhanVienCoTheHoanTatDatBan(DatBan datBan) {
        return datBan != null && datBan.coTheHoanTat();
    }

    public static boolean nhanVienCoTheHuyDatBan(DatBan datBan) {
        return datBan != null && (datBan.coTheHuy() || datBan.coTheHoanTat());
    }

    public static boolean nhanVienCoTheDoiBan(DatBan datBan) {
        return datBan != null && datBan.laDangHieuLuc();
    }

    public static boolean nhanVienCoTheNhanXuLyYeuCau(YeuCauPhucVu yeuCau) {
        return yeuCau != null && yeuCau.layTrangThai() == YeuCauPhucVu.TrangThai.DANG_CHO;
    }

    public static boolean nhanVienCoTheHoanTatYeuCau(YeuCauPhucVu yeuCau) {
        return yeuCau != null && yeuCau.layTrangThai() == YeuCauPhucVu.TrangThai.DANG_XU_LY;
    }

    public static boolean nhanVienCoTheHuyYeuCau(YeuCauPhucVu yeuCau) {
        return yeuCau != null && yeuCau.dangHoatDong();
    }
}
