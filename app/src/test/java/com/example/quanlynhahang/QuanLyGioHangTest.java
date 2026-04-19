package com.example.quanlynhahang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.quanlynhahang.data.QuanLyGioHang;
import com.example.quanlynhahang.model.DonHang;
import com.example.quanlynhahang.model.MonAnDeXuat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class QuanLyGioHangTest {

    private static final String PHAM_VI_KIEM_THU = "customer:test";
    private static final String PHAM_VI_LANG_NGHE_A = "customer:listener-a";
    private static final String PHAM_VI_LANG_NGHE_B = "customer:listener-b";

    private QuanLyGioHang quanLyGioHang;

    @Before
    public void khoiTao() {
        QuanLyGioHang.xoaInstance(PHAM_VI_KIEM_THU);
        QuanLyGioHang.xoaInstance(PHAM_VI_LANG_NGHE_A);
        QuanLyGioHang.xoaInstance(PHAM_VI_LANG_NGHE_B);
        QuanLyGioHang.xoaInstance(null);
        quanLyGioHang = QuanLyGioHang.layInstance(PHAM_VI_KIEM_THU);
        quanLyGioHang.xoaToanBoGio();
    }

    @After
    public void donDep() {
        QuanLyGioHang.xoaInstance(PHAM_VI_KIEM_THU);
        QuanLyGioHang.xoaInstance(PHAM_VI_LANG_NGHE_A);
        QuanLyGioHang.xoaInstance(PHAM_VI_LANG_NGHE_B);
        QuanLyGioHang.xoaInstance(null);
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

    @Test
    public void scopedInstances_doNotShareItemsOrContext() {
        QuanLyGioHang gioKhachA = QuanLyGioHang.layInstance("customer:11");
        QuanLyGioHang gioKhachB = QuanLyGioHang.layInstance("customer:22");
        gioKhachA.xoaToanBoGio();
        gioKhachB.xoaToanBoGio();

        gioKhachA.themVaoGio(new MonAnDeXuat(1, "Bò lúc lắc", "145.000 đ", true, "Món chính", 10));
        gioKhachA.capNhatNguCanhDonHang(DonHang.HinhThucDon.AN_TAI_QUAN, "Bàn 03", "Ít cay");

        assertEquals(1, gioKhachA.layTongSoLuong());
        assertEquals(0, gioKhachB.layTongSoLuong());
        assertEquals("Bàn 03", gioKhachA.layNguCanhDonHang().laySoBan());
        assertEquals("", gioKhachB.layNguCanhDonHang().laySoBan());
    }

    @Test
    public void layInstance_chuanHoaPhamViNullVaRongVeMacDinh() {
        QuanLyGioHang gioMacDinh = QuanLyGioHang.layInstance();
        QuanLyGioHang gioNull = QuanLyGioHang.layInstance(null);
        QuanLyGioHang gioRong = QuanLyGioHang.layInstance("   ");

        assertTrue(gioMacDinh == gioNull);
        assertTrue(gioMacDinh == gioRong);
    }

    @Test
    public void xoaLangNghe_chiAnhHuongDungPhamViDangKy() {
        QuanLyGioHang gioKhachA = QuanLyGioHang.layInstance(PHAM_VI_LANG_NGHE_A);
        QuanLyGioHang gioKhachB = QuanLyGioHang.layInstance(PHAM_VI_LANG_NGHE_B);
        int[] soLanThongBaoA = {0};
        int[] soLanThongBaoB = {0};
        QuanLyGioHang.LangNgheGioHang langNgheA = () -> soLanThongBaoA[0]++;
        QuanLyGioHang.LangNgheGioHang langNgheB = () -> soLanThongBaoB[0]++;

        gioKhachA.themLangNghe(langNgheA);
        gioKhachB.themLangNghe(langNgheB);
        gioKhachA.xoaLangNghe(langNgheA);

        gioKhachA.themVaoGio(new MonAnDeXuat(3, "Cơm chiên", "65.000 đ", true, "Món chính", 6));
        gioKhachB.themVaoGio(new MonAnDeXuat(4, "Nước cam", "35.000 đ", true, "Đồ uống", 7));

        assertEquals(0, soLanThongBaoA[0]);
        assertEquals(1, soLanThongBaoB[0]);
    }
}
