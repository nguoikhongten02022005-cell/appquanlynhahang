package com.example.quanlynhahang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.quanlynhahang.helper.CauHinhTinhNangHelper;
import com.example.quanlynhahang.helper.DieuHuongNoiBoHelper;

import org.junit.Test;

public class DieuHuongNoiBoHelperTest {

    @Test
    public void chuanHoaTab_defaultsNullOrUnknownToOverview() {
        assertEquals(DieuHuongNoiBoHelper.TAB_TONG_QUAN, DieuHuongNoiBoHelper.chuanHoaTab(null));
        assertEquals(DieuHuongNoiBoHelper.TAB_TONG_QUAN, DieuHuongNoiBoHelper.chuanHoaTab(""));
        assertEquals(DieuHuongNoiBoHelper.TAB_TONG_QUAN, DieuHuongNoiBoHelper.chuanHoaTab("unknown"));
    }

    @Test
    public void chuanHoaTab_trimsAndNormalizesCase() {
        assertEquals(DieuHuongNoiBoHelper.TAB_DON_HANG, DieuHuongNoiBoHelper.chuanHoaTab(" Orders "));
    }

    @Test
    public void chuanHoaSection_defaultsUnknownToReports() {
        assertEquals(DieuHuongNoiBoHelper.SECTION_BAO_CAO, DieuHuongNoiBoHelper.chuanHoaSection(null));
        assertEquals(DieuHuongNoiBoHelper.SECTION_BAO_CAO, DieuHuongNoiBoHelper.chuanHoaSection(""));
        assertEquals(DieuHuongNoiBoHelper.SECTION_BAO_CAO, DieuHuongNoiBoHelper.chuanHoaSection("unknown"));
    }

    @Test
    public void chuanHoaSection_trimsAndNormalizesCase() {
        assertEquals(DieuHuongNoiBoHelper.SECTION_NGUOI_DUNG, DieuHuongNoiBoHelper.chuanHoaSection(" USERS "));
    }

    @Test
    public void extraKeys_keepExistingIntentStrings() {
        assertEquals("extra_internal_tab", DieuHuongNoiBoHelper.EXTRA_TAB_NOI_BO);
        assertEquals("extra_admin_section", DieuHuongNoiBoHelper.EXTRA_SECTION_QUAN_TRI);
        assertEquals("extra_return_internal_route", DieuHuongNoiBoHelper.EXTRA_ROUTE_TRA_VE_NOI_BO);
        assertEquals("extra_customer_preview_mode", DieuHuongNoiBoHelper.EXTRA_CHE_DO_PREVIEW_KHACH);
    }

    @Test
    public void mapTabNhanVienCu_preservesLegacyValuesAndDefaultsBlankToOrders() {
        assertEquals(DieuHuongNoiBoHelper.TAB_DON_HANG, DieuHuongNoiBoHelper.mapTabNhanVienCu(DieuHuongNoiBoHelper.TAB_DON_HANG));
        assertEquals(DieuHuongNoiBoHelper.TAB_DAT_BAN, DieuHuongNoiBoHelper.mapTabNhanVienCu(DieuHuongNoiBoHelper.TAB_DAT_BAN));
        assertEquals(DieuHuongNoiBoHelper.TAB_YEU_CAU, DieuHuongNoiBoHelper.mapTabNhanVienCu(DieuHuongNoiBoHelper.TAB_YEU_CAU));
        assertEquals(DieuHuongNoiBoHelper.TAB_DON_HANG, DieuHuongNoiBoHelper.mapTabNhanVienCu(null));
        assertEquals(DieuHuongNoiBoHelper.TAB_DON_HANG, DieuHuongNoiBoHelper.mapTabNhanVienCu(""));
        assertEquals(DieuHuongNoiBoHelper.TAB_DON_HANG, DieuHuongNoiBoHelper.mapTabNhanVienCu("unknown"));
    }

    @Test
    public void taoRouteNoiBoVaQuanTri_buildExpectedRoutes() {
        assertEquals("internal:overview", DieuHuongNoiBoHelper.taoRouteNoiBo(DieuHuongNoiBoHelper.TAB_TONG_QUAN));
        assertEquals("admin:users", DieuHuongNoiBoHelper.taoRouteQuanTri(DieuHuongNoiBoHelper.SECTION_NGUOI_DUNG));
    }

    @Test
    public void featureFlagDefaultsToDisabledAndCanBeToggled() {
        CauHinhTinhNangHelper.setChoPhepNoiBoShellMoi(false);
        assertFalse(CauHinhTinhNangHelper.coNoiBoShellMoi());

        CauHinhTinhNangHelper.setChoPhepNoiBoShellMoi(true);
        assertTrue(CauHinhTinhNangHelper.coNoiBoShellMoi());
    }
}
