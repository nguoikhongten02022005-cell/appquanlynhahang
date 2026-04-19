package com.example.quanlynhahang;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.model.VaiTroNguoiDung;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class SessionManagerNamespaceInstrumentedTest {

    private SessionManager sessionManager;

    @Before
    public void setUp() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        sessionManager = new SessionManager(appContext);
        sessionManager.xoaPhienNoiBo();
        sessionManager.xoaPhienKhachHang();
    }

    private void luuVaiTroNoiBoCuNhungKhongConDangNhap(VaiTroNguoiDung vaiTro) {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SharedPreferences boNhoNoiBo = appContext.getSharedPreferences("internal_session_prefs", Context.MODE_PRIVATE);
        boNhoNoiBo.edit()
                .putBoolean("is_logged_in", false)
                .putLong("current_user_id", -1L)
                .putString("current_user_role", vaiTro.name())
                .apply();
    }

    @Test
    public void luuPhienKhachHang_sauDo_luuPhienNoiBo_khongGhiDePhienKhach() {
        sessionManager.luuPhienKhachHang(11L);
        sessionManager.luuPhienNoiBo(21L, VaiTroNguoiDung.ADMIN);

        assertTrue(sessionManager.daDangNhapKhachHang());
        assertTrue(sessionManager.daDangNhapNoiBo());
        assertEquals(11L, sessionManager.layIdKhachHangHienTai());
        assertEquals(21L, sessionManager.layIdNguoiDungNoiBo());
        assertEquals(VaiTroNguoiDung.ADMIN, sessionManager.layVaiTroNoiBoHopLe());
        assertEquals(VaiTroNguoiDung.ADMIN, sessionManager.layVaiTroSessionHopLe());
    }

    @Test
    public void luuPhienNoiBo_voiVaiTroNull_macDinhNhanVienAnToanChoNoiBo() {
        sessionManager.luuPhienNoiBo(21L, null);

        assertTrue(sessionManager.daDangNhapNoiBo());
        assertEquals(21L, sessionManager.layIdNguoiDungNoiBo());
        assertEquals(VaiTroNguoiDung.NHAN_VIEN, sessionManager.layVaiTroNoiBoHopLe());
        assertEquals(VaiTroNguoiDung.NHAN_VIEN, sessionManager.layVaiTroSessionHopLe());
    }

    @Test
    public void layKhoaPhienKhachHang_traVePhamViTheoKhachHang() {
        assertEquals("customer:guest", sessionManager.layKhoaPhienKhachHang());

        sessionManager.luuPhienKhachHang(11L);
        assertEquals("customer:11", sessionManager.layKhoaPhienKhachHang());

        sessionManager.xoaPhienKhachHang();
        assertEquals("customer:guest", sessionManager.layKhoaPhienKhachHang());
    }

    @Test
    public void luuDuongDanNoiBoNguonPreviewVaDuongDanKhachHang_duocLuuRiengBiet() {
        sessionManager.luuDuongDanNoiBoCuoi("internal:orders");
        sessionManager.luuNguonPreviewKhachHang("internal:orders");
        sessionManager.luuDuongDanKhachHangCuoi("customer:home");

        assertEquals("internal:orders", sessionManager.layDuongDanNoiBoCuoi());
        assertEquals("internal:orders", sessionManager.layNguonPreviewKhachHang());
        assertEquals("customer:home", sessionManager.layDuongDanKhachHangCuoi());
    }

    @Test
    public void vaiTroNoiBoCuKhongHopLe_khongGhiDePhienKhachHangDangHoatDong() {
        luuVaiTroNoiBoCuNhungKhongConDangNhap(VaiTroNguoiDung.ADMIN);
        sessionManager.luuPhienKhachHang(11L);

        assertTrue(sessionManager.daDangNhapKhachHang());
        assertEquals(VaiTroNguoiDung.KHACH_HANG, sessionManager.layVaiTroSessionHopLe());
    }

    @Test
    public void layDuongDanVaNguonPreview_khiChuaThietLap_traVeChuoiRong() {
        assertEquals("", sessionManager.layDuongDanNoiBoCuoi());
        assertEquals("", sessionManager.layDuongDanKhachHangCuoi());
        assertEquals("", sessionManager.layNguonPreviewKhachHang());
    }

    @Test
    public void layVaiTroSessionHopLe_khiKhongCoPhienHopLe_traVeNull() {
        luuVaiTroNoiBoCuNhungKhongConDangNhap(VaiTroNguoiDung.NHAN_VIEN);

        assertNull(sessionManager.layVaiTroSessionHopLe());
    }
}
