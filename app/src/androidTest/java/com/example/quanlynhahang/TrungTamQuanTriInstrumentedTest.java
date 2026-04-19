package com.example.quanlynhahang;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.helper.DieuHuongNoiBoHelper;
import com.example.quanlynhahang.model.NguoiDung;
import com.example.quanlynhahang.model.VaiTroNguoiDung;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class TrungTamQuanTriInstrumentedTest {

    private static final String EMAIL_ADMIN_KIEM_THU = "admin.shell.task7@example.com";
    private static final String SO_DIEN_THOAI_ADMIN_KIEM_THU = "0900000207";

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
        dangNhapQuanTri();
    }

    @After
    public void tearDown() {
        sessionManager.xoaPhienDangNhap();
    }

    @Test
    public void moTrungTamQuanTri_voiSectionMon_hienThiTieuDeMonAn() {
        try (ActivityScenario<TrungTamQuanTriActivity> scenario = ActivityScenario.launch(
                TrungTamQuanTriActivity.taoIntent(appContext, DieuHuongNoiBoHelper.SECTION_MON)
        )) {
            onView(withId(R.id.tvMonAnQuanTriTitle)).check(matches(isDisplayed()));
            scenario.onActivity(activity -> assertEquals(
                    DieuHuongNoiBoHelper.taoRouteQuanTri(DieuHuongNoiBoHelper.SECTION_MON),
                    sessionManager.layDuongDanNoiBoCuoi()
            ));
        }
    }

    @Test
    public void moTrungTamQuanTri_voiSectionNguoiDung_hienThiTieuDeNguoiDung() {
        try (ActivityScenario<TrungTamQuanTriActivity> scenario = ActivityScenario.launch(
                TrungTamQuanTriActivity.taoIntent(appContext, DieuHuongNoiBoHelper.SECTION_NGUOI_DUNG)
        )) {
            onView(withId(R.id.tvNguoiDungQuanTriTitle)).check(matches(isDisplayed()));
            scenario.onActivity(activity -> assertEquals(
                    DieuHuongNoiBoHelper.taoRouteQuanTri(DieuHuongNoiBoHelper.SECTION_NGUOI_DUNG),
                    sessionManager.layDuongDanNoiBoCuoi()
            ));
        }
    }

    @Test
    public void moTrungTamQuanTri_voiSectionBaoCao_hienThiTieuDeBaoCao() {
        try (ActivityScenario<TrungTamQuanTriActivity> scenario = ActivityScenario.launch(
                TrungTamQuanTriActivity.taoIntent(appContext, DieuHuongNoiBoHelper.SECTION_BAO_CAO)
        )) {
            onView(withId(R.id.tvBaoCaoQuanTriTitle)).check(matches(isDisplayed()));
            scenario.onActivity(activity -> assertEquals(
                    DieuHuongNoiBoHelper.taoRouteQuanTri(DieuHuongNoiBoHelper.SECTION_BAO_CAO),
                    sessionManager.layDuongDanNoiBoCuoi()
            ));
        }
    }

    @Test
    public void moTrungTamQuanTri_voiSectionBaoCao_hienThiDayDuNhanChiSo() {
        try (ActivityScenario<TrungTamQuanTriActivity> scenario = ActivityScenario.launch(
                TrungTamQuanTriActivity.taoIntent(appContext, DieuHuongNoiBoHelper.SECTION_BAO_CAO)
        )) {
            onView(withText(R.string.admin_report_total_users)).check(matches(isDisplayed()));
            onView(withText(R.string.admin_report_total_dishes)).check(matches(isDisplayed()));
            onView(withText(R.string.admin_report_total_orders)).check(matches(isDisplayed()));
            onView(withText(R.string.admin_report_pending_orders)).check(matches(isDisplayed()));
            onView(withText(R.string.admin_report_pending_reservations)).check(matches(isDisplayed()));
            onView(withText(R.string.admin_report_processing_requests)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void moTrungTamQuanTri_voiSectionKhongHopLe_macDinhVeBaoCaoVaLuuRouteBaoCao() {
        Intent intent = new Intent(appContext, TrungTamQuanTriActivity.class);
        intent.putExtra(DieuHuongNoiBoHelper.EXTRA_SECTION_QUAN_TRI, "unknown-section");

        try (ActivityScenario<TrungTamQuanTriActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.tvBaoCaoQuanTriTitle)).check(matches(isDisplayed()));
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
    public void moTrungTamQuanTri_voiSessionKhongHopLe_chuyenVeStaffLauncher() {
        sessionManager.xoaPhienDangNhap();
        NguoiDung nguoiDungNhanVien = databaseHelper.getUserByEmail("staff.shell.task7@example.com");
        long idNhanVien = nguoiDungNhanVien != null
                ? nguoiDungNhanVien.layId()
                : databaseHelper.insertUser(
                        "Nhan vien task 7",
                        "staff.shell.task7@example.com",
                        "0900000208",
                        "12345678",
                        VaiTroNguoiDung.NHAN_VIEN,
                        true
                );
        if (idNhanVien <= 0) {
            throw new IllegalStateException("Khong the chuan bi nhan vien cho test task 7");
        }
        sessionManager.luuPhienNoiBo(idNhanVien, VaiTroNguoiDung.NHAN_VIEN);
        sessionManager.damBaoVaiTroSession(databaseHelper);

        Intents.init();
        try (ActivityScenario<TrungTamQuanTriActivity> scenario = ActivityScenario.launch(
                TrungTamQuanTriActivity.taoIntent(appContext, DieuHuongNoiBoHelper.SECTION_BAO_CAO)
        )) {
            intended(hasComponent(StaffLauncherActivity.class.getName()));
            scenario.onActivity(activity -> {
                assertTrue(sessionManager.daDangNhapNoiBo());
                assertEquals(VaiTroNguoiDung.NHAN_VIEN, sessionManager.layVaiTroSessionHopLe());
            });
        } finally {
            Intents.release();
        }
    }

    @Test
    public void moTrungTamQuanTri_voiSectionCaiDat_hienThiTieuDeCaiDat() {
        try (ActivityScenario<TrungTamQuanTriActivity> scenario = ActivityScenario.launch(
                TrungTamQuanTriActivity.taoIntent(appContext, DieuHuongNoiBoHelper.SECTION_CAI_DAT)
        )) {
            onView(withId(R.id.tvQuanTriCaiDatTitle)).check(matches(isDisplayed()));
            scenario.onActivity(activity -> assertEquals(
                    DieuHuongNoiBoHelper.taoRouteQuanTri(DieuHuongNoiBoHelper.SECTION_CAI_DAT),
                    sessionManager.layDuongDanNoiBoCuoi()
            ));
        }
    }

    @Test
    public void moTrungTamQuanTri_voiSectionCaiDat_xemTruocKhachHangMoMainActivity() {
        Intents.init();
        try (ActivityScenario<TrungTamQuanTriActivity> scenario = ActivityScenario.launch(
                TrungTamQuanTriActivity.taoIntent(appContext, DieuHuongNoiBoHelper.SECTION_CAI_DAT)
        )) {
            onView(withId(R.id.btnQuanTriXemGiaoDienKhach)).perform(ViewActions.click());
            intended(allOf(
                    hasComponent(MainActivity.class.getName()),
                    hasExtra(MainActivity.EXTRA_CHO_PHEP_XEM_GIAO_DIEN_KHACH, true)
            ));
            scenario.onActivity(activity -> assertEquals(
                    DieuHuongNoiBoHelper.taoRouteQuanTri(DieuHuongNoiBoHelper.SECTION_CAI_DAT),
                    sessionManager.layDuongDanNoiBoCuoi()
            ));
        } finally {
            Intents.release();
        }
    }

    @Test
    public void dangXuatTuCaiDat_xoaPhienNoiBoVaChuyenVeStaffLauncher() {
        Intents.init();
        try (ActivityScenario<TrungTamQuanTriActivity> scenario = ActivityScenario.launch(
                TrungTamQuanTriActivity.taoIntent(appContext, DieuHuongNoiBoHelper.SECTION_CAI_DAT)
        )) {
            onView(withId(R.id.btnQuanTriDangXuat)).perform(ViewActions.click());
            intended(hasComponent(StaffLauncherActivity.class.getName()));
            scenario.onActivity(activity -> {
                assertFalse(sessionManager.daDangNhapNoiBo());
                assertEquals("", sessionManager.layDuongDanNoiBoCuoi());
            });
        } finally {
            Intents.release();
        }
    }

    private void dangNhapQuanTri() {
        NguoiDung nguoiDungAdmin = databaseHelper.getUserByEmail(EMAIL_ADMIN_KIEM_THU);
        long idNguoiDungAdmin = nguoiDungAdmin != null
                ? nguoiDungAdmin.layId()
                : databaseHelper.insertUser(
                        "Quan tri vien task 7",
                        EMAIL_ADMIN_KIEM_THU,
                        SO_DIEN_THOAI_ADMIN_KIEM_THU,
                        "12345678",
                        VaiTroNguoiDung.ADMIN,
                        true
                );

        if (idNguoiDungAdmin <= 0) {
            throw new IllegalStateException("Khong the chuan bi quan tri vien cho test task 7");
        }

        sessionManager.luuPhienNoiBo(idNguoiDungAdmin, VaiTroNguoiDung.ADMIN);
        sessionManager.damBaoVaiTroSession(databaseHelper);
    }
}
