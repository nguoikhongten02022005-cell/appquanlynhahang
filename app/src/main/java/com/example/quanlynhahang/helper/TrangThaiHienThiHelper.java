package com.example.quanlynhahang.helper;

import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;

import com.example.quanlynhahang.R;
import com.example.quanlynhahang.model.DatBan;
import com.example.quanlynhahang.model.DonHang;
import com.example.quanlynhahang.model.YeuCauPhucVu;

public final class TrangThaiHienThiHelper {

    private TrangThaiHienThiHelper() {
    }

    @StringRes
    public static int layTextTrangThaiDon(DonHang donHang) {
        if (donHang == null) {
            return R.string.order_status_pending;
        }
        DonHang.TrangThai trangThai = donHang.layTrangThai();
        if (trangThai == DonHang.TrangThai.CHO_XAC_NHAN) {
            return R.string.order_status_pending;
        }
        if (trangThai == DonHang.TrangThai.DANG_CHUAN_BI) {
            return R.string.order_status_making;
        }
        if (trangThai == DonHang.TrangThai.SAN_SANG_PHUC_VU) {
            return donHang.laAnTaiQuan() ? R.string.order_status_ready : R.string.order_status_ready_takeaway;
        }
        if (trangThai == DonHang.TrangThai.HOAN_THANH) {
            return R.string.order_status_completed;
        }
        return R.string.order_status_canceled;
    }

    @ColorRes
    public static int layMauTrangThaiDon(DonHang.TrangThai trangThai) {
        if (trangThai == DonHang.TrangThai.CHO_XAC_NHAN) {
            return R.color.warning;
        }
        if (trangThai == DonHang.TrangThai.DANG_CHUAN_BI) {
            return R.color.brand_orange;
        }
        if (trangThai == DonHang.TrangThai.SAN_SANG_PHUC_VU) {
            return R.color.primary;
        }
        if (trangThai == DonHang.TrangThai.HOAN_THANH) {
            return R.color.success;
        }
        return R.color.error;
    }

    @StringRes
    public static int layTextTrangThaiDatBan(DatBan.TrangThai trangThai) {
        if (trangThai == DatBan.TrangThai.PENDING) {
            return R.string.reservation_status_pending;
        }
        if (trangThai == DatBan.TrangThai.ACTIVE) {
            return R.string.reservation_status_confirmed;
        }
        if (trangThai == DatBan.TrangThai.COMPLETED) {
            return R.string.reservation_status_completed;
        }
        if (trangThai == DatBan.TrangThai.EXPIRED) {
            return R.string.reservation_status_expired;
        }
        return R.string.reservation_status_canceled;
    }

    @ColorRes
    public static int layMauTrangThaiDatBan(DatBan.TrangThai trangThai) {
        if (trangThai == DatBan.TrangThai.PENDING) {
            return R.color.warning;
        }
        if (trangThai == DatBan.TrangThai.ACTIVE) {
            return R.color.success;
        }
        if (trangThai == DatBan.TrangThai.COMPLETED) {
            return R.color.primary;
        }
        if (trangThai == DatBan.TrangThai.EXPIRED) {
            return R.color.on_surface_variant;
        }
        return R.color.error;
    }

    @StringRes
    public static int layTextTrangThaiYeuCau(YeuCauPhucVu.TrangThai trangThai) {
        if (trangThai == YeuCauPhucVu.TrangThai.DANG_CHO) {
            return R.string.service_request_status_pending;
        }
        if (trangThai == YeuCauPhucVu.TrangThai.DANG_XU_LY) {
            return R.string.service_request_status_processing;
        }
        if (trangThai == YeuCauPhucVu.TrangThai.DA_HUY) {
            return R.string.service_request_status_canceled;
        }
        return R.string.service_request_status_done;
    }

    @ColorRes
    public static int layMauTrangThaiYeuCau(YeuCauPhucVu.TrangThai trangThai) {
        if (trangThai == YeuCauPhucVu.TrangThai.DANG_CHO) {
            return R.color.primary;
        }
        if (trangThai == YeuCauPhucVu.TrangThai.DANG_XU_LY) {
            return R.color.warning;
        }
        if (trangThai == YeuCauPhucVu.TrangThai.DA_HUY) {
            return R.color.error;
        }
        return R.color.success;
    }

    @StringRes
    public static int layTextLoaiYeuCau(YeuCauPhucVu.LoaiYeuCau loaiYeuCau) {
        if (loaiYeuCau == YeuCauPhucVu.LoaiYeuCau.THEM_NUOC) {
            return R.string.service_request_type_more_water;
        }
        if (loaiYeuCau == YeuCauPhucVu.LoaiYeuCau.THANH_TOAN) {
            return R.string.service_request_type_payment;
        }
        return R.string.service_request_type_call_staff;
    }
}
