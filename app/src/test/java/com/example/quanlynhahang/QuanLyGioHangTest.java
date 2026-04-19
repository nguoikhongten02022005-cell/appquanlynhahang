package com.example.quanlynhahang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.quanlynhahang.data.QuanLyGioHang;
import com.example.quanlynhahang.model.DonHang;
import com.example.quanlynhahang.model.MonAnDeXuat;

import org.junit.Before;
import org.junit.Test;

public class QuanLyGioHangTest {

    private QuanLyGioHang quanLyGioHang;

    @Before
    public void khoiTao() {
        quanLyGioHang = QuanLyGioHang.layInstance();
        quanLyGioHang.xoaToanBoGio();
    }

    @Test
    public void themVaoGio_gopMonTrungTheoKhoaOnDinh() {
        MonAnDeXuat dish = new MonAnDeXuat(1, "Bò lúc lắc", "145.000 đ", true, "Món chính", 10);

        quanLyGioHang.themVaoGio(dish);
        quanLyGioHang.themVaoGio(dish);

        assertEquals(1, quanLyGioHang.layDanhSachMon().size());
        assertEquals(2, quanLyGioHang.layDanhSachMon().get(0).laySoLuong());
        assertEquals(2, quanLyGioHang.layTongSoLuong());
    }

    @Test
    public void capNhatNguCanhDonHang_xoaSoBanKhiMangDi() {
        quanLyGioHang.capNhatNguCanhDonHang(DonHang.HinhThucDon.AN_TAI_QUAN, "Bàn 05", "Ít cay");
        quanLyGioHang.capNhatNguCanhDonHang(DonHang.HinhThucDon.MANG_DI, "Bàn 05", "Mang về");

        QuanLyGioHang.NguCanhDonHang context = quanLyGioHang.layNguCanhDonHang();
        assertEquals(DonHang.HinhThucDon.MANG_DI, context.layHinhThucDon());
        assertEquals("", context.laySoBan());
        assertEquals("Mang về", context.layGhiChu());
        assertTrue(context.hopLeDeDatHang());
    }

    @Test
    public void nguCanhAnTaiQuan_yeuCauCoSoBan() {
        quanLyGioHang.capNhatNguCanhDonHang(DonHang.HinhThucDon.AN_TAI_QUAN, "", "");

        assertFalse(quanLyGioHang.layNguCanhDonHang().hopLeDeDatHang());
    }

    @Test
    public void xoaToanBoGio_datLaiMonVaNguCanh() {
        MonAnDeXuat dish = new MonAnDeXuat(2, "Trà đào", "45.000 đ", true, "Đồ uống", 5);
        quanLyGioHang.themVaoGio(dish);
        quanLyGioHang.capNhatNguCanhDonHang(DonHang.HinhThucDon.AN_TAI_QUAN, "Bàn 02", "Không đá");

        quanLyGioHang.xoaToanBoGio();

        assertTrue(quanLyGioHang.laGioHangRong());
        assertEquals(DonHang.HinhThucDon.MANG_DI, quanLyGioHang.layNguCanhDonHang().layHinhThucDon());
        assertEquals("", quanLyGioHang.layNguCanhDonHang().laySoBan());
        assertEquals("", quanLyGioHang.layNguCanhDonHang().layGhiChu());
    }

    @Test
    public void nguCanhAnTaiQuan_catKhoangTrangSoBan() {
        quanLyGioHang.capNhatNguCanhDonHang(DonHang.HinhThucDon.AN_TAI_QUAN, "  Bàn 08  ", "  Gần cửa sổ ");

        QuanLyGioHang.NguCanhDonHang context = quanLyGioHang.layNguCanhDonHang();
        assertEquals(DonHang.HinhThucDon.AN_TAI_QUAN, context.layHinhThucDon());
        assertEquals("Bàn 08", context.laySoBan());
        assertEquals("Gần cửa sổ", context.layGhiChu());
    }
}
