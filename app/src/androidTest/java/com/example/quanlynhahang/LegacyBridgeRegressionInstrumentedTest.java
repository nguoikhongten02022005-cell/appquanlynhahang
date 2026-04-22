package com.example.quanlynhahang;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.quanlynhahang.helper.CauHinhTinhNangHelper;
import com.example.quanlynhahang.helper.DieuHuongNoiBoHelper;
import com.example.quanlynhahang.helper.DieuHuongVaiTroHelper;
import com.example.quanlynhahang.model.VaiTroNguoiDung;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class LegacyBridgeRegressionInstrumentedTest {

    @Before
    public void setUp() {
        CauHinhTinhNangHelper.setChoPhepNoiBoShellMoi(false);
    }

    @After
    public void tearDown() {
        CauHinhTinhNangHelper.setChoPhepNoiBoShellMoi(false);
    }

    @Test
    public void taoIntentTheoVaiTro_khiTatCoNoiBoShellMoi_voiAdminVaNhanVien_chuyenVeStaffLauncher() {
        Context appContext = ApplicationProvider.getApplicationContext();

        Intent intentAdmin = DieuHuongVaiTroHelper.taoIntentTheoVaiTro(appContext, VaiTroNguoiDung.ADMIN);
        Intent intentNhanVien = DieuHuongVaiTroHelper.taoIntentTheoVaiTro(appContext, VaiTroNguoiDung.NHAN_VIEN);

        assertEquals(StaffLauncherActivity.class.getName(), intentAdmin.getComponent().getClassName());
        assertEquals(StaffLauncherActivity.class.getName(), intentNhanVien.getComponent().getClassName());
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
    public void taoIntentQuanTri_tuTrungTamNoiBo_chuyenThangDenTrungTamQuanTri() {
        Context appContext = ApplicationProvider.getApplicationContext();

        Intent intent = TrungTamNoiBoActivity.taoIntentQuanTri(appContext, DieuHuongNoiBoHelper.SECTION_CAI_DAT);

        assertEquals(TrungTamQuanTriActivity.class.getName(), intent.getComponent().getClassName());
        assertEquals(
                DieuHuongNoiBoHelper.SECTION_CAI_DAT,
                intent.getStringExtra(DieuHuongNoiBoHelper.EXTRA_SECTION_QUAN_TRI)
        );
    }

    @Test
    public void taoIntentTraVeNoiBoTuRoute_voiRouteQuanTri_traVeTrungTamQuanTriDungSection() {
        Context appContext = ApplicationProvider.getApplicationContext();

        Intent intent = DieuHuongNoiBoHelper.taoIntentTraVeNoiBoTuRoute(
                appContext,
                DieuHuongNoiBoHelper.taoRouteQuanTri(DieuHuongNoiBoHelper.SECTION_BAN)
        );

        assertEquals(TrungTamQuanTriActivity.class.getName(), intent.getComponent().getClassName());
        assertEquals(
                DieuHuongNoiBoHelper.SECTION_BAN,
                intent.getStringExtra(DieuHuongNoiBoHelper.EXTRA_SECTION_QUAN_TRI)
        );
    }
}
