package com.example.quanlynhahang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.quanlynhahang.data.CartManager;
import com.example.quanlynhahang.model.DonHang;
import com.example.quanlynhahang.model.MonAnDeXuat;

import org.junit.Before;
import org.junit.Test;

public class CartManagerTest {

    private CartManager cartManager;

    @Before
    public void setUp() {
        cartManager = CartManager.getInstance();
        cartManager.xoaToanBoGio();
    }

    @Test
    public void addToCart_mergesSameDishByStableKey() {
        MonAnDeXuat dish = new MonAnDeXuat(1, "Bò lúc lắc", "145.000 đ", true, "Món chính", 10);

        cartManager.themVaoGio(dish);
        cartManager.themVaoGio(dish);

        assertEquals(1, cartManager.layDanhSachMon().size());
        assertEquals(2, cartManager.layDanhSachMon().get(0).laySoLuong());
        assertEquals(2, cartManager.layTongSoLuong());
    }

    @Test
    public void updateOrderContext_clearsTableForTakeAway() {
        cartManager.capNhatNguCanhDonHang(DonHang.HinhThucDon.AN_TAI_QUAN, "Bàn 05", "Ít cay");
        cartManager.capNhatNguCanhDonHang(DonHang.HinhThucDon.MANG_DI, "Bàn 05", "Mang về");

        CartManager.NguCanhDonHang context = cartManager.layNguCanhDonHang();
        assertEquals(DonHang.HinhThucDon.MANG_DI, context.layHinhThucDon());
        assertEquals("", context.laySoBan());
        assertEquals("Mang về", context.layGhiChu());
        assertTrue(context.hopLeDeDatHang());
    }

    @Test
    public void dineInContext_requiresTable() {
        cartManager.capNhatNguCanhDonHang(DonHang.HinhThucDon.AN_TAI_QUAN, "", "");

        assertFalse(cartManager.layNguCanhDonHang().hopLeDeDatHang());
    }

    @Test
    public void clearCart_resetsItemsAndContext() {
        MonAnDeXuat dish = new MonAnDeXuat(2, "Trà đào", "45.000 đ", true, "Đồ uống", 5);
        cartManager.themVaoGio(dish);
        cartManager.capNhatNguCanhDonHang(DonHang.HinhThucDon.AN_TAI_QUAN, "Bàn 02", "Không đá");

        cartManager.xoaToanBoGio();

        assertTrue(cartManager.laGioHangRong());
        assertEquals(DonHang.HinhThucDon.MANG_DI, cartManager.layNguCanhDonHang().layHinhThucDon());
        assertEquals("", cartManager.layNguCanhDonHang().laySoBan());
        assertEquals("", cartManager.layNguCanhDonHang().layGhiChu());
    }

    @Test
    public void dineInContext_keepsTrimmedTableNumber() {
        cartManager.capNhatNguCanhDonHang(DonHang.HinhThucDon.AN_TAI_QUAN, "  Bàn 08  ", "  Gần cửa sổ ");

        CartManager.NguCanhDonHang context = cartManager.layNguCanhDonHang();
        assertEquals(DonHang.HinhThucDon.AN_TAI_QUAN, context.layHinhThucDon());
        assertEquals("Bàn 08", context.laySoBan());
        assertEquals("Gần cửa sổ", context.layGhiChu());
    }
}
