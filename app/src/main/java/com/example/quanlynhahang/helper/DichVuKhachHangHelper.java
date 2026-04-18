package com.example.quanlynhahang.helper;

import androidx.annotation.Nullable;

import com.example.quanlynhahang.model.DonHang;
import com.example.quanlynhahang.model.YeuCauPhucVu;

import java.util.List;

public final class DichVuKhachHangHelper {

    private DichVuKhachHangHelper() {
    }

    @Nullable
    public static DonHang timDonHangDangHoatDong(List<DonHang> danhSachDon) {
        if (danhSachDon == null) {
            return null;
        }
        for (DonHang donHang : danhSachDon) {
            if (donHang == null) {
                continue;
            }
            if (laDonDangHoatDong(donHang)) {
                return donHang;
            }
        }
        return null;
    }

    @Nullable
    public static DonHang timDonHangTaiQuanDangHoatDong(List<DonHang> danhSachDon) {
        if (danhSachDon == null) {
            return null;
        }
        for (DonHang donHang : danhSachDon) {
            if (donHang == null) {
                continue;
            }
            if (donHang.laAnTaiQuan() && laDonDangHoatDong(donHang)) {
                return donHang;
            }
        }
        return null;
    }

    public static boolean laDonDangHoatDong(@Nullable DonHang donHang) {
        if (donHang == null) {
            return false;
        }
        return donHang.layTrangThai() != DonHang.TrangThai.HOAN_THANH
                && donHang.layTrangThai() != DonHang.TrangThai.DA_HUY;
    }

    public static boolean laDonTaiQuanDangHoatDong(@Nullable DonHang donHang) {
        return donHang != null && donHang.laAnTaiQuan() && laDonDangHoatDong(donHang);
    }

    public static boolean laDonMangDiDangHoatDong(@Nullable DonHang donHang) {
        return donHang != null && !donHang.laAnTaiQuan() && laDonDangHoatDong(donHang);
    }

    public static boolean coYeuCauHoTroDangXuLy(List<YeuCauPhucVu> danhSachYeuCau) {
        if (danhSachYeuCau == null) {
            return false;
        }
        for (YeuCauPhucVu yeuCau : danhSachYeuCau) {
            if (yeuCau != null && yeuCau.dangHoatDong()) {
                return true;
            }
        }
        return false;
    }

    public static boolean coDonTaiQuanDangDungBan(@Nullable DonHang donHang, @Nullable String soBan) {
        if (donHang == null || soBan == null || soBan.trim().isEmpty()) {
            return false;
        }
        return laDonTaiQuanDangHoatDong(donHang) && soBan.trim().equalsIgnoreCase(donHang.laySoBan());
    }

    public static boolean laBanHienTai(@Nullable String banHienTai, @Nullable String soBan) {
        if (banHienTai == null || soBan == null) {
            return false;
        }
        return !banHienTai.trim().isEmpty() && banHienTai.trim().equalsIgnoreCase(soBan.trim());
    }

    @Nullable
    public static String layNhanTrangThaiBan(@Nullable String banHienTai,
                                             @Nullable String soBan,
                                             boolean dangDuocGiu,
                                             boolean dangDuocDung) {
        if (laBanHienTai(banHienTai, soBan)) {
            return "current";
        }
        if (dangDuocDung) {
            return "busy";
        }
        if (dangDuocGiu) {
            return "reserved";
        }
        return "available";
    }

    public static boolean coYeuCauDangCho(@Nullable YeuCauPhucVu yeuCau) {
        return yeuCau != null && yeuCau.layTrangThai() == YeuCauPhucVu.TrangThai.DANG_CHO;
    }

    @Nullable
    public static YeuCauPhucVu timYeuCauHoTroDangXuLy(List<YeuCauPhucVu> danhSachYeuCau) {
        if (danhSachYeuCau == null) {
            return null;
        }
        for (YeuCauPhucVu yeuCau : danhSachYeuCau) {
            if (yeuCau != null && yeuCau.dangHoatDong()) {
                return yeuCau;
            }
        }
        return null;
    }

    @Nullable
    public static String timBanHienTai(@Nullable String banTuSession,
                                       @Nullable DonHang donTaiQuanDangHoatDong,
                                       @Nullable CartContextProvider cartContextProvider) {
        if (donTaiQuanDangHoatDong != null && donTaiQuanDangHoatDong.coBanAn()) {
            return donTaiQuanDangHoatDong.laySoBan();
        }
        if (cartContextProvider != null) {
            String banTrongGio = cartContextProvider.laySoBanTrongNguCanh();
            if (banTrongGio != null && !banTrongGio.trim().isEmpty()) {
                return banTrongGio.trim();
            }
        }
        if (banTuSession != null && !banTuSession.trim().isEmpty()) {
            return banTuSession.trim();
        }
        return null;
    }

    public interface CartContextProvider {
        @Nullable String laySoBanTrongNguCanh();
    }
}
