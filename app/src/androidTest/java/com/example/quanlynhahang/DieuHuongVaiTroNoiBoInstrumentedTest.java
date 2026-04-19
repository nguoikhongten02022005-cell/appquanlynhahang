package com.example.quanlynhahang;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.helper.CauHinhTinhNangHelper;
import com.example.quanlynhahang.helper.DieuHuongNoiBoHelper;
import com.example.quanlynhahang.helper.DieuHuongVaiTroHelper;
import com.example.quanlynhahang.model.NguoiDung;
import com.example.quanlynhahang.model.VaiTroNguoiDung;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class DieuHuongVaiTroNoiBoInstrumentedTest {

    private static final String EMAIL_NOI_BO = "noi.bo.bridge.task6@example.com";
    private static final String SO_DIEN_THOAI_NOI_BO = "0900000106";

    private Context appContext;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    @Before
    public void setUp() {
        appContext = ApplicationProvider.getApplicationContext();
        databaseHelper = new DatabaseHelper(appContext);
        sessionManager = new SessionManager(appContext);
        CauHinhTinhNangHelper.setChoPhepNoiBoShellMoi(true);
        databaseHelper.chuanBiCoSoDuLieu();
        sessionManager.xoaPhienDangNhap();
        dangNhapNhanVienNoiBo();
    }

    @After
    public void tearDown() {
        sessionManager.xoaPhienDangNhap();
        CauHinhTinhNangHelper.setChoPhepNoiBoShellMoi(false);
    }

    @Test
    public void taoIntentTheoVaiTro_khiBatCoNoiBoShellMoi_voiNhanVien_danVeTrungTamNoiBoTongQuan() {
        Intent intent = DieuHuongVaiTroHelper.taoIntentTheoVaiTro(appContext, VaiTroNguoiDung.NHAN_VIEN);

        assertNotNull(intent);
        assertEquals(TrungTamNoiBoActivity.class.getName(), intent.getComponent().getClassName());
        assertEquals(
                DieuHuongNoiBoHelper.TAB_TONG_QUAN,
                intent.getStringExtra(DieuHuongNoiBoHelper.EXTRA_TAB_NOI_BO)
        );
    }

    @Test
    public void mapTabNhanVienCu_voiTabDatBan_giuNguyenTab() {
        assertEquals(
                DieuHuongNoiBoHelper.TAB_DAT_BAN,
                DieuHuongNoiBoHelper.mapTabNhanVienCu(NhanVienActivity.TAB_DAT_BAN)
        );
    }

    @Test
    public void nhanVienActivity_luonChuyenHuongSauKhiXacThucVaThemDayDuTaskFlags() {
        CauHinhTinhNangHelper.setChoPhepNoiBoShellMoi(false);

        Intent intentMoNhanVien = new Intent(appContext, NhanVienActivity.class);
        intentMoNhanVien.putExtra(NhanVienActivity.EXTRA_TAB_MUC_TIEU, NhanVienActivity.TAB_DAT_BAN);
        intentMoNhanVien.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intentMoNhanVien.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        Intents.init();
        try (ActivityScenario<NhanVienActivity> scenario = ActivityScenario.launch(intentMoNhanVien)) {
            intended(allOf(
                    hasComponent(TrungTamNoiBoActivity.class.getName()),
                    hasExtra(DieuHuongNoiBoHelper.EXTRA_TAB_NOI_BO, DieuHuongNoiBoHelper.TAB_DAT_BAN),
                    hasExtra(NhanVienActivity.EXTRA_TAB_MUC_TIEU, NhanVienActivity.TAB_DAT_BAN),
                    coTaskFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK)
            ));
        } finally {
            Intents.release();
        }
    }

    @Test
    public void nhanVienActivity_khiDangNhapKhachHang_thiChuyenTheoVaiTroVaGiuCoCheKhachHang() {
        dangNhapKhachHang();

        Intent intentMoNhanVien = new Intent(appContext, NhanVienActivity.class);
        intentMoNhanVien.putExtra(NhanVienActivity.EXTRA_TAB_MUC_TIEU, NhanVienActivity.TAB_DON_HANG);

        Intents.init();
        try (ActivityScenario<NhanVienActivity> scenario = ActivityScenario.launch(intentMoNhanVien)) {
            intended(allOf(
                    hasComponent(MainActivity.class.getName()),
                    hasExtra(MainActivity.EXTRA_CHO_PHEP_XEM_GIAO_DIEN_KHACH, true),
                    coTaskFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK)
            ));
        } finally {
            Intents.release();
        }
    }

    private void dangNhapNhanVienNoiBo() {
        NguoiDung nguoiDungNoiBo = databaseHelper.getUserByEmail(EMAIL_NOI_BO);
        long idNguoiDungNoiBo = nguoiDungNoiBo != null
                ? nguoiDungNoiBo.layId()
                : databaseHelper.insertUser(
                        "Nhan vien cau noi task 6",
                        EMAIL_NOI_BO,
                        SO_DIEN_THOAI_NOI_BO,
                        "12345678",
                        VaiTroNguoiDung.NHAN_VIEN,
                        true
                );

        if (idNguoiDungNoiBo <= 0) {
            throw new IllegalStateException("Khong the chuan bi nguoi dung noi bo cho test cau noi");
        }

        sessionManager.luuPhienNoiBo(idNguoiDungNoiBo, VaiTroNguoiDung.NHAN_VIEN);
        sessionManager.damBaoVaiTroSession(databaseHelper);
    }

    private void dangNhapKhachHang() {
        long idKhachHang = databaseHelper.insertUser(
                "Khach hang cau noi task 6",
                "khach.hang.task6@example.com",
                "0900000107",
                "12345678",
                VaiTroNguoiDung.KHACH_HANG,
                true
        );
        if (idKhachHang <= 0) {
            throw new IllegalStateException("Khong the chuan bi phien khach hang cho test cau noi");
        }
        sessionManager.luuPhienKhachHang(idKhachHang);
    }

    private static Matcher<Intent> coTaskFlags(final int flagsKyVong) {
        return new TypeSafeMatcher<Intent>() {
            @Override
            protected boolean matchesSafely(Intent intent) {
                int taskFlags = intent.getFlags() & (Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                return taskFlags == flagsKyVong;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Intent co task flags bang ").appendValue(flagsKyVong);
            }
        };
    }
}
