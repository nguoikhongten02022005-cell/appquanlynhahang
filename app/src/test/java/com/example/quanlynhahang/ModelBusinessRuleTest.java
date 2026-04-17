package com.example.quanlynhahang;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.quanlynhahang.model.DatBan;
import com.example.quanlynhahang.model.DonHang;
import com.example.quanlynhahang.model.MonAnDeXuat;
import com.example.quanlynhahang.model.YeuCauPhucVu;

import org.junit.Test;

import java.util.Collections;

public class ModelBusinessRuleTest {

    @Test
    public void orderCancel_onlyAllowedWhenPending() {
        DonHang pendingOrder = new DonHang(
                1,
                "#DH1",
                "17/04/2026 18:30",
                "145.000 đ",
                DonHang.HinhThucDon.MANG_DI,
                "",
                "",
                DonHang.TrangThai.CHO_XAC_NHAN,
                DonHang.TrangThaiThanhToan.CHUA_THANH_TOAN,
                DonHang.PhuongThucThanhToan.CHUA_CHON,
                0,
                Collections.singletonList(new DonHang.MonTrongDon(new MonAnDeXuat(1, "Bò", "145.000 đ", true, "Món chính", 0), 1))
        );
        DonHang preparingOrder = new DonHang(
                2,
                "#DH2",
                "17/04/2026 18:30",
                "145.000 đ",
                DonHang.HinhThucDon.MANG_DI,
                "",
                "",
                DonHang.TrangThai.DANG_CHUAN_BI,
                DonHang.TrangThaiThanhToan.CHUA_THANH_TOAN,
                DonHang.PhuongThucThanhToan.CHUA_CHON,
                0,
                Collections.singletonList(new DonHang.MonTrongDon(new MonAnDeXuat(1, "Bò", "145.000 đ", true, "Món chính", 0), 1))
        );

        assertTrue(pendingOrder.coTheHuy());
        assertFalse(preparingOrder.coTheHuy());
        assertTrue(preparingOrder.coTheNhanVienHuy());
    }

    @Test
    public void reservationLifecycle_flagsReflectStatus() {
        DatBan pendingReservation = new DatBan(1, "#GB1", "17/04/2026 19:00", "Bàn 03", 4, "", DatBan.TrangThai.PENDING, 0);
        DatBan activeReservation = new DatBan(2, "#GB2", "17/04/2026 19:00", "Bàn 04", 4, "", DatBan.TrangThai.ACTIVE, 10);
        DatBan completedReservation = new DatBan(3, "#GB3", "17/04/2026 19:00", "Bàn 05", 4, "", DatBan.TrangThai.COMPLETED, 11);

        assertTrue(pendingReservation.coTheHuy());
        assertTrue(pendingReservation.laDangHieuLuc());
        assertTrue(activeReservation.laDangHieuLuc());
        assertTrue(completedReservation.daHoanTatGuiMon());
        assertTrue(completedReservation.daKetThuc());
        assertFalse(completedReservation.laDangHieuLuc());
    }

    @Test
    public void serviceRequestCancel_onlyAllowedWhenPending() {
        YeuCauPhucVu pendingRequest = new YeuCauPhucVu(1, YeuCauPhucVu.LoaiYeuCau.GOI_NHAN_VIEN, "Gọi nhân viên", "17/04/2026 18:45", "", "Bàn 02", 0, YeuCauPhucVu.TrangThai.DANG_CHO);
        YeuCauPhucVu processingRequest = new YeuCauPhucVu(2, YeuCauPhucVu.LoaiYeuCau.THANH_TOAN, "Thanh toán", "17/04/2026 18:46", "", "Bàn 02", 9, YeuCauPhucVu.TrangThai.DANG_XU_LY);

        assertTrue(pendingRequest.coTheHuy());
        assertTrue(pendingRequest.dangHoatDong());
        assertFalse(processingRequest.coTheHuy());
        assertTrue(processingRequest.dangHoatDong());
    }
}
