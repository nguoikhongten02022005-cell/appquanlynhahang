package com.example.quanlynhahang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.example.quanlynhahang.model.VaiTroNguoiDung;

import org.junit.Test;

public class RoleAndReservationRuleTest {

    @Test
    public void strictRoleParsing_returnsNullForInvalidOrBlankValues() {
        assertNull(VaiTroNguoiDung.tuChuoiNghiemNhat(null));
        assertNull(VaiTroNguoiDung.tuChuoiNghiemNhat(""));
        assertNull(VaiTroNguoiDung.tuChuoiNghiemNhat("guest"));
    }

    @Test
    public void strictRoleParsing_acceptsKnownRolesCaseInsensitive() {
        assertEquals(VaiTroNguoiDung.KHACH_HANG, VaiTroNguoiDung.tuChuoiNghiemNhat("khach_hang"));
        assertEquals(VaiTroNguoiDung.NHAN_VIEN, VaiTroNguoiDung.tuChuoiNghiemNhat("NHAN_VIEN"));
        assertEquals(VaiTroNguoiDung.ADMIN, VaiTroNguoiDung.tuChuoiNghiemNhat("admin"));
    }
}
