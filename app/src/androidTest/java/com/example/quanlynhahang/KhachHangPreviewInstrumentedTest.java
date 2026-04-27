package com.example.quanlynhahang;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
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
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
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
public class KhachHangPreviewInstrumentedTest {

    private static final String EMAIL_ADMIN_KIEM_THU = "admin.preview.task9@example.com";
    private static final String SO_DIEN_THOAI_ADMIN_KIEM_THU = "0900000309";
    private static final String DUONG_DAN_NOI_BO_XEM_TRUOC = "internal:orders";
    private static final String DUONG_DAN_QUAN_TRI_XEM_TRUOC = DieuHuongNoiBoHelper.taoRouteQuanTri(
            DieuHuongNoiBoHelper.SECTION_BAO_CAO
    );

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
        dangNhapQuanTriNoiBo();
    }

    @After
    public void tearDown() {
        sessionManager.xoaPhienDangNhap();
    }

    @Test
    public void moPreviewTuHelper_giuNguyenPhienNoiBo_vaMoMainActivityVoiExtraPreview() {
        Intents.init();
        try (ActivityScenario<CustomerLauncherActivity> scenario = ActivityScenario.launch(
                DieuHuongNoiBoHelper.taoIntentPreviewKhachHang(appContext, DUONG_DAN_NOI_BO_XEM_TRUOC)
        )) {
            intended(allOf(
                    hasComponent(MainActivity.class.getName()),
                    hasExtra(MainActivity.EXTRA_CHO_PHEP_XEM_GIAO_DIEN_KHACH, true),
                    hasExtra(MainActivity.EXTRA_CHE_DO_PREVIEW_KHACH, true),
                    hasExtra(MainActivity.EXTRA_ROUTE_TRA_VE_NOI_BO, DUONG_DAN_NOI_BO_XEM_TRUOC)
            ));
            scenario.onActivity(activity -> {
                assertTrue(sessionManager.daDangNhapNoiBo());
                assertEquals(DUONG_DAN_NOI_BO_XEM_TRUOC, sessionManager.layNguonPreviewKhachHang());
            });
        } finally {
            Intents.release();
        }
    }

    @Test
    public void previewMode_hienNutThoatXemTruoc() {
        Intent intent = taoIntentPreviewMainActivity();
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.btnExitCustomerPreview)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void thoatPreview_quayVeTrungTamQuanTriVaGiuSectionDonHang() {
        Intents.init();
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(taoIntentPreviewMainActivity())) {
            onView(withId(R.id.btnExitCustomerPreview)).perform(click());

            intended(allOf(
                    hasComponent(TrungTamQuanTriActivity.class.getName()),
                    hasExtra(DieuHuongNoiBoHelper.EXTRA_SECTION_QUAN_TRI, DieuHuongNoiBoHelper.SECTION_DON_HANG)
            ));
            scenario.onActivity(activity -> {
                assertTrue(sessionManager.daDangNhapNoiBo());
                assertEquals(DUONG_DAN_NOI_BO_XEM_TRUOC, sessionManager.layNguonPreviewKhachHang());
            });
        } finally {
            Intents.release();
        }
    }

    @Test
    public void previewMode_moDangNhapKemGioiHanPhienKhachHang() {
        Intents.init();
        intending(hasComponent(DangNhapActivity.class.getName())).respondWith(
                new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null)
        );
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(taoIntentPreviewMainActivity())) {
            onView(withId(R.id.nav_account)).perform(click());

            intended(allOf(
                    hasComponent(DangNhapActivity.class.getName()),
                    hasExtra(DangNhapActivity.EXTRA_RETURN_TO_CALLER, true),
                    hasExtra(DangNhapActivity.EXTRA_ONLY_CUSTOMER_SESSION, true)
            ));
        } finally {
            Intents.release();
        }
    }

    @Test
    public void previewMode_noiBoAdminReports_quayVeTrungTamQuanTriVoiSectionBaoCao() {
        Intents.init();
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(
                taoIntentPreviewAdminSettings()
        )) {
            onView(withId(R.id.btnExitCustomerPreview)).perform(click());

            intended(allOf(
                    hasComponent(TrungTamQuanTriActivity.class.getName()),
                    hasExtra(DieuHuongNoiBoHelper.EXTRA_SECTION_QUAN_TRI, DieuHuongNoiBoHelper.SECTION_BAO_CAO)
            ));
            scenario.onActivity(activity -> {
                assertTrue(sessionManager.daDangNhapNoiBo());
                assertEquals(DUONG_DAN_QUAN_TRI_XEM_TRUOC, sessionManager.layNguonPreviewKhachHang());
            });
        } finally {
            Intents.release();
        }
    }

    @Test
    public void previewMode_dangXuatKhachHang_giuPhienNoiBo() {
        NguoiDung khachHang = databaseHelper.getUserByEmail("kh1");
        long idKhachHang = khachHang != null
                ? khachHang.layId()
                : databaseHelper.insertUser(
                        "Khach hang task 9",
                        "kh1",
                        "0123456789",
                        "1",
                        VaiTroNguoiDung.KHACH_HANG,
                        true
                );
        sessionManager.luuPhienKhachHang(idKhachHang);

        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(taoIntentPreviewMainActivity())) {
            onView(withId(R.id.nav_account)).perform(click());
            onView(withId(R.id.btnLogout)).perform(click());
            onView(withText(R.string.account_logout)).perform(click());

            scenario.onActivity(activity -> {
                assertTrue(sessionManager.daDangNhapNoiBo());
                assertFalse(sessionManager.daDangNhapKhachHang());
            });
        }
    }

    private Intent taoIntentPreviewAdminSettings() {
        Intent intent = new Intent(appContext, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_CHO_PHEP_XEM_GIAO_DIEN_KHACH, true);
        intent.putExtra(MainActivity.EXTRA_CHE_DO_PREVIEW_KHACH, true);
        intent.putExtra(MainActivity.EXTRA_ROUTE_TRA_VE_NOI_BO, DUONG_DAN_QUAN_TRI_XEM_TRUOC);
        return intent;
    }

    private void dangNhapQuanTriNoiBo() {
        NguoiDung nguoiDungAdmin = databaseHelper.getUserByEmail(EMAIL_ADMIN_KIEM_THU);
        long idNguoiDungAdmin = nguoiDungAdmin != null
                ? nguoiDungAdmin.layId()
                : databaseHelper.insertUser(
                        "Quan tri vien task 9",
                        EMAIL_ADMIN_KIEM_THU,
                        SO_DIEN_THOAI_ADMIN_KIEM_THU,
                        "12345678",
                        VaiTroNguoiDung.ADMIN,
                        true
                );

        if (idNguoiDungAdmin <= 0) {
            throw new IllegalStateException("Khong the chuan bi quan tri vien cho test preview");
        }

        sessionManager.luuPhienNoiBo(idNguoiDungAdmin, VaiTroNguoiDung.ADMIN);
        sessionManager.luuDuongDanNoiBoCuoi(DieuHuongNoiBoHelper.taoRouteQuanTri(DieuHuongNoiBoHelper.SECTION_BAO_CAO));
        sessionManager.damBaoVaiTroSession(databaseHelper);
    }

    private Intent taoIntentPreviewMainActivity() {
        Intent intent = new Intent(appContext, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_CHO_PHEP_XEM_GIAO_DIEN_KHACH, true);
        intent.putExtra(MainActivity.EXTRA_CHE_DO_PREVIEW_KHACH, true);
        intent.putExtra(MainActivity.EXTRA_ROUTE_TRA_VE_NOI_BO, DUONG_DAN_NOI_BO_XEM_TRUOC);
        return intent;
    }
}
