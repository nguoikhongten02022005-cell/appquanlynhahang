package com.example.quanlynhahang;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.helper.DieuHuongNoiBoHelper;
import com.example.quanlynhahang.model.VaiTroNguoiDung;
import com.example.quanlynhahang.model.NguoiDung;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class TrungTamNoiBoOperationsInstrumentedTest {

    private static final String EMAIL_NOI_BO = "noi.bo.operations@example.com";
    private static final String SO_DIEN_THOAI_NOI_BO = "0900000009";

    private Context appContext;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    @Before
    public void setUp() {
        appContext = ApplicationProvider.getApplicationContext();
        databaseHelper = new DatabaseHelper(appContext);
        sessionManager = new SessionManager(appContext);

        databaseHelper.chuanBiCoSoDuLieu();
        sessionManager.xoaPhienDangNhap();
        dangNhapNhanVienNoiBo();
    }

    @After
    public void tearDown() {
        sessionManager.xoaPhienDangNhap();
    }

    @Test
    public void moKhuNoiBo_voiTabDonHang_hienThiTieuDeDonHang() {
        try (ActivityScenario<TrungTamQuanTriActivity> scenario =
                     ActivityScenario.launch(DieuHuongNoiBoHelper.taoIntentTrungTamNoiBo(appContext, DieuHuongNoiBoHelper.TAB_DON_HANG))) {
            onView(withText(R.string.employee_orders_title)).check(matches(isDisplayed()));
            scenario.onActivity(activity -> assertEquals(
                    DieuHuongNoiBoHelper.taoRouteQuanTri(DieuHuongNoiBoHelper.SECTION_DON_HANG),
                    sessionManager.layDuongDanNoiBoCuoi()
            ));
        }
    }

    @Test
    public void moKhuNoiBo_voiTabDatBan_hienThiTieuDeDatBan() {
        try (ActivityScenario<TrungTamQuanTriActivity> scenario =
                     ActivityScenario.launch(DieuHuongNoiBoHelper.taoIntentTrungTamNoiBo(appContext, DieuHuongNoiBoHelper.TAB_DAT_BAN))) {
            onView(withText(R.string.employee_reservations_title)).check(matches(isDisplayed()));
            scenario.onActivity(activity -> assertEquals(
                    DieuHuongNoiBoHelper.taoRouteQuanTri(DieuHuongNoiBoHelper.SECTION_BAN),
                    sessionManager.layDuongDanNoiBoCuoi()
            ));
        }
    }

    @Test
    public void moKhuNoiBo_voiTabYeuCau_hienThiTieuDeYeuCau() {
        try (ActivityScenario<TrungTamQuanTriActivity> scenario =
                     ActivityScenario.launch(DieuHuongNoiBoHelper.taoIntentTrungTamNoiBo(appContext, DieuHuongNoiBoHelper.TAB_YEU_CAU))) {
            onView(withText(R.string.employee_service_requests_title)).check(matches(isDisplayed()));
            scenario.onActivity(activity -> assertEquals(
                    DieuHuongNoiBoHelper.taoRouteQuanTri(DieuHuongNoiBoHelper.SECTION_YEU_CAU),
                    sessionManager.layDuongDanNoiBoCuoi()
            ));
        }
    }

    @Test
    public void moKhuNoiBo_voiTabKhongHopLe_macDinhVeTongQuanVaLuuRouteTongQuan() {
        Intent intent = DieuHuongNoiBoHelper.taoIntentTrungTamNoiBo(appContext, "khong_hop_le");

        try (ActivityScenario<TrungTamQuanTriActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withText(R.string.admin_reports_title)).check(matches(isDisplayed()));
            scenario.onActivity(activity -> assertEquals(
                    DieuHuongNoiBoHelper.taoRouteQuanTri(DieuHuongNoiBoHelper.SECTION_BAO_CAO),
                    sessionManager.layDuongDanNoiBoCuoi()
            ));
            scenario.onActivity(activity -> assertEquals(
                    DieuHuongNoiBoHelper.SECTION_BAO_CAO,
                    activity.getIntent().getStringExtra(DieuHuongNoiBoHelper.EXTRA_SECTION_QUAN_TRI)
            ));
        }
    }

    @Test
    public void moKhuNoiBo_khongCoTab_macDinhVeTongQuanVaLuuRouteTongQuan() {
        Intent intent = TrungTamQuanTriActivity.taoIntent(appContext, null);

        try (ActivityScenario<TrungTamQuanTriActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withText(R.string.admin_reports_title)).check(matches(isDisplayed()));
            scenario.onActivity(activity -> assertEquals(
                    DieuHuongNoiBoHelper.taoRouteQuanTri(DieuHuongNoiBoHelper.SECTION_BAO_CAO),
                    sessionManager.layDuongDanNoiBoCuoi()
            ));
            scenario.onActivity(activity -> assertEquals(
                    DieuHuongNoiBoHelper.SECTION_BAO_CAO,
                    activity.getIntent().getStringExtra(DieuHuongNoiBoHelper.EXTRA_SECTION_QUAN_TRI)
            ));
        }
    }

    private void dangNhapNhanVienNoiBo() {
        NguoiDung nguoiDungNoiBo = databaseHelper.getUserByEmail(EMAIL_NOI_BO);
        long idNguoiDungNoiBo;
        if (nguoiDungNoiBo == null) {
            idNguoiDungNoiBo = databaseHelper.insertUser(
                    "Nhan vien noi bo",
                    EMAIL_NOI_BO,
                    SO_DIEN_THOAI_NOI_BO,
                    "12345678",
                    VaiTroNguoiDung.NHAN_VIEN,
                    true
            );
        } else {
            idNguoiDungNoiBo = nguoiDungNoiBo.layId();
        }
        sessionManager.luuPhienNoiBo(idNguoiDungNoiBo, VaiTroNguoiDung.NHAN_VIEN);
        sessionManager.damBaoVaiTroSession(databaseHelper);
    }
}
