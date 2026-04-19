package com.example.quanlynhahang;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.quanlynhahang.helper.DieuHuongNoiBoHelper;
import com.example.quanlynhahang.helper.DieuHuongVaiTroHelper;
import com.example.quanlynhahang.model.VaiTroNguoiDung;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.release;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class LegacyBridgeRegressionInstrumentedTest {

    @Test
    public void taoIntentTheoVaiTro_voiAdminVaNhanVien_quayVeTrungTamNoiBo() {
        Context appContext = ApplicationProvider.getApplicationContext();

        Intent intentAdmin = DieuHuongVaiTroHelper.taoIntentTheoVaiTro(appContext, VaiTroNguoiDung.ADMIN);
        Intent intentNhanVien = DieuHuongVaiTroHelper.taoIntentTheoVaiTro(appContext, VaiTroNguoiDung.NHAN_VIEN);

        assertEquals(TrungTamNoiBoActivity.class.getName(), intentAdmin.getComponent().getClassName());
        assertEquals(TrungTamNoiBoActivity.class.getName(), intentNhanVien.getComponent().getClassName());
        assertEquals(
                DieuHuongNoiBoHelper.TAB_TONG_QUAN,
                intentAdmin.getStringExtra(DieuHuongNoiBoHelper.EXTRA_TAB_NOI_BO)
        );
        assertEquals(
                DieuHuongNoiBoHelper.TAB_TONG_QUAN,
                intentNhanVien.getStringExtra(DieuHuongNoiBoHelper.EXTRA_TAB_NOI_BO)
        );
    }

    @Test
    public void taoIntentTrungTamQuanTri_voiSectionNguoiDung_luuExtraDung() {
        Context appContext = ApplicationProvider.getApplicationContext();

        Intent intent = TrungTamQuanTriActivity.taoIntent(appContext, DieuHuongNoiBoHelper.SECTION_NGUOI_DUNG);

        assertEquals(
                DieuHuongNoiBoHelper.SECTION_NGUOI_DUNG,
                intent.getStringExtra(DieuHuongNoiBoHelper.EXTRA_SECTION_QUAN_TRI)
        );
    }

    @Test
    public void quanTriActivity_chuyenHuongSangTrungTamQuanTriVaGiuSectionCuThe() {
        Context appContext = ApplicationProvider.getApplicationContext();
        Intent intent = new Intent(appContext, QuanTriActivity.class);
        intent.putExtra(
                DieuHuongNoiBoHelper.EXTRA_SECTION_QUAN_TRI,
                DieuHuongNoiBoHelper.SECTION_NGUOI_DUNG
        );

        init();
        try (ActivityScenario<QuanTriActivity> scenario = ActivityScenario.launch(intent)) {
            intended(hasComponent(TrungTamQuanTriActivity.class.getName()));
            intended(hasExtra(
                    DieuHuongNoiBoHelper.EXTRA_SECTION_QUAN_TRI,
                    DieuHuongNoiBoHelper.SECTION_NGUOI_DUNG
            ));
        } finally {
            release();
        }
    }

    @Test
    public void nhanVienActivity_voiTabCu_datBan_chuyenHuongSangTrungTamNoiBoVaGiuExtra() {
        Context appContext = ApplicationProvider.getApplicationContext();
        Intent intent = new Intent(appContext, NhanVienActivity.class);
        intent.putExtra(NhanVienActivity.EXTRA_TAB_MUC_TIEU, NhanVienActivity.TAB_DAT_BAN);

        init();
        try (ActivityScenario<NhanVienActivity> scenario = ActivityScenario.launch(intent)) {
            intended(hasComponent(TrungTamNoiBoActivity.class.getName()));
            intended(hasExtra(
                    NhanVienActivity.EXTRA_TAB_MUC_TIEU,
                    NhanVienActivity.TAB_DAT_BAN
            ));
            intended(hasExtra(
                    DieuHuongNoiBoHelper.EXTRA_TAB_NOI_BO,
                    DieuHuongNoiBoHelper.TAB_DAT_BAN
            ));
        } finally {
            release();
        }
    }
}
