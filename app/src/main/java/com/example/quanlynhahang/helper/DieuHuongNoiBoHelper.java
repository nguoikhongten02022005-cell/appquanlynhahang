package com.example.quanlynhahang.helper;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.example.quanlynhahang.CustomerLauncherActivity;
import com.example.quanlynhahang.TrungTamNoiBoActivity;
import com.example.quanlynhahang.TrungTamQuanTriActivity;

import java.util.Locale;

public final class DieuHuongNoiBoHelper {

    public static final String TAB_TONG_QUAN = "overview";
    public static final String TAB_DON_HANG = "orders";
    public static final String TAB_DAT_BAN = "reservations";
    public static final String TAB_YEU_CAU = "service_requests";

    public static final String SECTION_MON = "dishes";
    public static final String SECTION_NGUOI_DUNG = "users";
    public static final String SECTION_BAO_CAO = "reports";
    public static final String SECTION_CAI_DAT = "settings";

    public static final String EXTRA_TAB_NOI_BO = "extra_internal_tab";
    public static final String EXTRA_SECTION_QUAN_TRI = "extra_admin_section";
    public static final String EXTRA_ROUTE_TRA_VE_NOI_BO = "extra_return_internal_route";
    public static final String EXTRA_CHE_DO_PREVIEW_KHACH = "extra_customer_preview_mode";

    private static final String ROUTE_INTERNAL_PREFIX = "internal:";
    private static final String ROUTE_ADMIN_PREFIX = "admin:";

    private DieuHuongNoiBoHelper() {
    }

    public static String chuanHoaTab(@Nullable String tab) {
        String giaTri = chuanHoaChuoi(tab);
        if (TAB_TONG_QUAN.equals(giaTri)
                || TAB_DON_HANG.equals(giaTri)
                || TAB_DAT_BAN.equals(giaTri)
                || TAB_YEU_CAU.equals(giaTri)) {
            return giaTri;
        }
        return TAB_TONG_QUAN;
    }

    public static String chuanHoaSection(@Nullable String section) {
        String giaTri = chuanHoaChuoi(section);
        if (SECTION_MON.equals(giaTri)
                || SECTION_NGUOI_DUNG.equals(giaTri)
                || SECTION_BAO_CAO.equals(giaTri)
                || SECTION_CAI_DAT.equals(giaTri)) {
            return giaTri;
        }
        return SECTION_BAO_CAO;
    }

    public static Intent taoIntentTrungTamNoiBo(Context context, @Nullable String tab) {
        return TrungTamNoiBoActivity.taoIntent(context, chuanHoaTab(tab));
    }

    public static Intent taoIntentPreviewKhachHang(Context context, @Nullable String returnRoute) {
        Intent intent = new Intent(context, CustomerLauncherActivity.class);
        String duongDanTraVe = chuanHoaDuongDanNoiBo(returnRoute);
        intent.putExtra(EXTRA_CHE_DO_PREVIEW_KHACH, true);
        intent.putExtra(EXTRA_ROUTE_TRA_VE_NOI_BO, duongDanTraVe);
        return intent;
    }

    public static Intent taoIntentTraVeNoiBoTuRoute(Context context, @Nullable String route) {
        String duongDanNoiBo = chuanHoaDuongDanNoiBo(route);
        if (duongDanNoiBo.startsWith(ROUTE_ADMIN_PREFIX)) {
            return TrungTamQuanTriActivity.taoIntent(
                    context,
                    layGiaTriCuoiCung(duongDanNoiBo, ROUTE_ADMIN_PREFIX, SECTION_BAO_CAO)
            );
        }
        if (duongDanNoiBo.startsWith(ROUTE_INTERNAL_PREFIX)) {
            return TrungTamNoiBoActivity.taoIntent(
                    context,
                    layGiaTriCuoiCung(duongDanNoiBo, ROUTE_INTERNAL_PREFIX, TAB_TONG_QUAN)
            );
        }
        return TrungTamNoiBoActivity.taoIntent(context, TAB_TONG_QUAN);
    }

    public static String mapTabNhanVienCu(@Nullable String tab) {
        String giaTri = chuanHoaChuoi(tab);
        if (TAB_DON_HANG.equals(giaTri)
                || TAB_DAT_BAN.equals(giaTri)
                || TAB_YEU_CAU.equals(giaTri)) {
            return giaTri;
        }
        return TAB_DON_HANG;
    }

    public static String taoRouteNoiBo(@Nullable String tab) {
        return ROUTE_INTERNAL_PREFIX + chuanHoaTab(tab);
    }

    public static String taoRouteQuanTri(@Nullable String section) {
        return ROUTE_ADMIN_PREFIX + chuanHoaSection(section);
    }

    private static String chuanHoaChuoi(@Nullable String giaTri) {
        if (giaTri == null) {
            return "";
        }
        return giaTri.trim().toLowerCase(Locale.ROOT);
    }

    private static String chuanHoaDuongDanNoiBo(@Nullable String route) {
        return chuanHoaChuoi(route);
    }

    private static String layGiaTriCuoiCung(String route, String prefix, String fallback) {
        if (!route.startsWith(prefix)) {
            return fallback;
        }
        String giaTri = route.substring(prefix.length());
        if (giaTri.isEmpty()) {
            return fallback;
        }
        return prefix.equals(ROUTE_ADMIN_PREFIX) ? chuanHoaSection(giaTri) : chuanHoaTab(giaTri);
    }
}
