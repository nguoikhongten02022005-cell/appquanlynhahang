package com.example.quanlynhahang;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.helper.CauHinhTinhNangHelper;
import com.example.quanlynhahang.helper.DieuHuongNoiBoHelper;
import com.example.quanlynhahang.helper.DieuHuongVaiTroHelper;
import com.example.quanlynhahang.model.NguoiDung;
import com.example.quanlynhahang.model.VaiTroNguoiDung;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
    public void taoIntentTheoVaiTro_khiBatCoNoiBoShellMoi_voiNhanVien_danVeTrungTamQuanTriTongQuan() {
        Intent intent = DieuHuongVaiTroHelper.taoIntentTheoVaiTro(appContext, VaiTroNguoiDung.NHAN_VIEN);

        assertNotNull(intent);
        assertEquals(TrungTamQuanTriActivity.class.getName(), intent.getComponent().getClassName());
        assertEquals(
                DieuHuongNoiBoHelper.SECTION_BAO_CAO,
                intent.getStringExtra(DieuHuongNoiBoHelper.EXTRA_SECTION_QUAN_TRI)
        );
    }

    @Test
    public void mapTabNhanVienCu_voiTabDatBan_giuNguyenTab() {
        assertEquals(
                DieuHuongNoiBoHelper.TAB_DAT_BAN,
                DieuHuongNoiBoHelper.mapTabNhanVienCu(DieuHuongNoiBoHelper.TAB_DAT_BAN)
        );
    }

    @Test
    public void taoIntentTrungTamNoiBo_voiTabDatBan_mapSangTrungTamQuanTriBan() {
        Intent intent = DieuHuongNoiBoHelper.taoIntentTrungTamNoiBo(appContext, DieuHuongNoiBoHelper.TAB_DAT_BAN);

        assertEquals(TrungTamQuanTriActivity.class.getName(), intent.getComponent().getClassName());
        assertEquals(
                DieuHuongNoiBoHelper.SECTION_BAN,
                intent.getStringExtra(DieuHuongNoiBoHelper.EXTRA_SECTION_QUAN_TRI)
        );
    }

    @Test
    public void taoIntentSaiVaiTro_khiChiConPhienKhachHang_thiChuyenTheoVaiTroVaGiuCoCheKhachHang() {
        sessionManager.xoaPhienNoiBo();
        dangNhapKhachHang();

        Intent intent = DieuHuongVaiTroHelper.taoIntentSaiVaiTro(appContext, sessionManager, true);

        assertEquals(MainActivity.class.getName(), intent.getComponent().getClassName());
        assertTrue(intent.getBooleanExtra(MainActivity.EXTRA_CHO_PHEP_XEM_GIAO_DIEN_KHACH, false));
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
}
