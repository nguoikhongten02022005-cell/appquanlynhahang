package com.example.quanlynhahang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import javax.xml.parsers.DocumentBuilderFactory;

public class InternalShellCleanupTest {

    @Test
    public void internalShellLayout_usesSimpleLinearRootInsteadOfUnusedDrawerLayout() throws Exception {
        Document document = docXml(
                "src/main/res/layout/activity_trung_tam_noi_bo.xml",
                "app/src/main/res/layout/activity_trung_tam_noi_bo.xml"
        );

        Node root = document.getDocumentElement();
        assertNotNull(root);
        assertTrue("Root phải là LinearLayout", "LinearLayout".equals(root.getNodeName()));
        assertNotNull("Thiếu container nội bộ", timNodeTheoId(document, "@+id/noiBoFragmentContainer"));
        assertFalse("Không nên còn DrawerLayout thừa", coNodeTheoTen(document, "androidx.drawerlayout.widget.DrawerLayout"));
    }

    @Test
    public void internalShellActivity_removesUnusedAdminIntentWrapper() throws Exception {
        String source = readText(
                "src/main/java/com/example/quanlynhahang/TrungTamNoiBoActivity.java",
                "app/src/main/java/com/example/quanlynhahang/TrungTamNoiBoActivity.java"
        );

        assertTrue(source.contains("public static Intent taoIntent(Context context, String tab)"));
        assertFalse(source.contains("taoIntentQuanTri(Context context, String section)"));
    }

    @Test
    public void roleRoutingSource_sendsAdminToAdminCenterAndEmployeeToInternalShell() throws Exception {
        String source = readText(
                "src/main/java/com/example/quanlynhahang/helper/DieuHuongVaiTroHelper.java",
                "app/src/main/java/com/example/quanlynhahang/helper/DieuHuongVaiTroHelper.java"
        );

        assertTrue(source.contains("if (vaiTroHienTai == VaiTroNguoiDung.ADMIN && CauHinhTinhNangHelper.coNoiBoShellMoi())"));
        assertTrue(source.contains("return TrungTamQuanTriActivity.taoIntent(context, DieuHuongNoiBoHelper.SECTION_BAO_CAO);"));
        assertTrue(source.contains("if (vaiTroHienTai == VaiTroNguoiDung.NHAN_VIEN && CauHinhTinhNangHelper.coNoiBoShellMoi())"));
        assertTrue(source.contains("return DieuHuongNoiBoHelper.taoIntentTrungTamNoiBo(context, DieuHuongNoiBoHelper.TAB_TONG_QUAN);"));
    }

    @Test
    public void staffLauncherSource_sendsAdminSessionToAdminCenterByDefault() throws Exception {
        String source = readText(
                "src/main/java/com/example/quanlynhahang/StaffLauncherActivity.java",
                "app/src/main/java/com/example/quanlynhahang/StaffLauncherActivity.java"
        );

        assertTrue(source.contains("if (vaiTroDangNhap == VaiTroNguoiDung.ADMIN)"));
        assertTrue(source.contains("chuyenDen(TrungTamQuanTriActivity.taoIntent(this, DieuHuongNoiBoHelper.SECTION_BAO_CAO));"));
        assertTrue(source.contains("if (vaiTroDangNhap == VaiTroNguoiDung.NHAN_VIEN)"));
        assertTrue(source.contains("chuyenDen(DieuHuongNoiBoHelper.taoIntentTrungTamNoiBo(this, DieuHuongNoiBoHelper.TAB_TONG_QUAN));"));
    }

    @Test
    public void adminCenterSource_supportsExpandedMenuSections() throws Exception {
        String source = readText(
                "src/main/java/com/example/quanlynhahang/TrungTamQuanTriActivity.java",
                "app/src/main/java/com/example/quanlynhahang/TrungTamQuanTriActivity.java"
        );

        assertTrue(source.contains("DieuHuongNoiBoHelper.SECTION_DON_HANG"));
        assertTrue(source.contains("DieuHuongNoiBoHelper.SECTION_HOA_DON"));
        assertTrue(source.contains("DieuHuongNoiBoHelper.SECTION_YEU_CAU"));
        assertTrue(source.contains("new DonHangNoiBoFragment()"));
        assertTrue(source.contains("new HoaDonQuanTriFragment()"));
        assertTrue(source.contains("new YeuCauNoiBoFragment()"));
    }

    @Test
    public void adminCenterLayout_usesCompactMobileShellWithoutDrawerMenu() throws Exception {
        Document document = docXml(
                "src/main/res/layout/activity_trung_tam_quan_tri.xml",
                "app/src/main/res/layout/activity_trung_tam_quan_tri.xml"
        );

        Node root = document.getDocumentElement();
        assertNotNull(root);
        assertTrue("Root phải là LinearLayout", "LinearLayout".equals(root.getNodeName()));
        assertNotNull("Thiếu shell nội dung quản trị", timNodeTheoId(document, "@+id/layoutAdminShell"));
        assertNotNull("Thiếu bottom nav quản trị", timNodeTheoId(document, "@+id/cardAdminBottomNav"));
        assertNotNull("Thiếu tab tổng quan", timNodeTheoId(document, "@+id/navAdminOverview"));
        assertNotNull("Thiếu tab đơn hàng", timNodeTheoId(document, "@+id/navAdminOrders"));
        assertNotNull("Thiếu tab bàn", timNodeTheoId(document, "@+id/navAdminTables"));
        assertNotNull("Thiếu tab thực đơn", timNodeTheoId(document, "@+id/navAdminDishes"));
        assertNotNull("Thiếu container quản trị", timNodeTheoId(document, "@+id/quanTriFragmentContainer"));
        assertFalse("Không nên còn DrawerLayout thừa", coNodeTheoTen(document, "androidx.drawerlayout.widget.DrawerLayout"));
        assertNotNull("Thiếu nút tìm kiếm góc phải", timNodeTheoId(document, "@+id/btnAdminHeroSearch"));
        assertFalse("Không nên còn hamburger", readText(
                "src/main/res/layout/activity_trung_tam_quan_tri.xml",
                "app/src/main/res/layout/activity_trung_tam_quan_tri.xml"
        ).contains("@+id/btnAdminOpenSidebar"));
        assertFalse("Không nên còn tab menu", readText(
                "src/main/res/layout/activity_trung_tam_quan_tri.xml",
                "app/src/main/res/layout/activity_trung_tam_quan_tri.xml"
        ).contains("@+id/navAdminMenu"));
    }

    @Test
    public void adminCenterStrings_useOverviewWordingAndExpandedDrawerLabels() throws Exception {
        String source = readText(
                "src/main/res/values/strings.xml",
                "app/src/main/res/values/strings.xml"
        );

        assertTrue(source.contains("<string name=\"admin_reports_title\">Tổng quan</string>"));
        assertTrue(source.contains("<string name=\"admin_reports_subtitle\">Theo dõi nhanh tình hình nhà hàng trong ngày</string>"));
        assertTrue(source.contains("<string name=\"admin_stats_section_title\">Thống kê nhanh</string>"));
        assertTrue(source.contains("<string name=\"admin_shortcut_invoices\">Hóa đơn &amp; thanh toán</string>"));
        assertTrue(source.contains("<string name=\"admin_shortcut_service_requests\">Yêu cầu phục vụ</string>"));
        assertTrue(source.contains("<string name=\"admin_tab_dishes\">Quản lý món ăn</string>"));
        assertTrue(source.contains("<string name=\"admin_tab_users\">Quản lý tài khoản</string>"));
        assertFalse(source.contains("<string name=\"admin_reports_title\">Báo cáo</string>"));
        assertFalse(source.contains("<string name=\"admin_quick_actions_title\">"));
        assertFalse(source.contains("<string name=\"admin_quick_actions_subtitle\">"));
    }

    @Test
    public void adminCenterLayout_matchesMobileDashboardSectionsWithoutLegacyCards() throws Exception {
        String source = readText(
                "src/main/res/layout/activity_trung_tam_quan_tri.xml",
                "app/src/main/res/layout/activity_trung_tam_quan_tri.xml"
        );
        String dashboardSource = readText(
                "src/main/res/layout/fragment_bao_cao_quan_tri.xml",
                "app/src/main/res/layout/fragment_bao_cao_quan_tri.xml"
        );

        assertFalse(source.contains("@string/admin_sidebar_section_system"));
        assertFalse(source.contains("@+id/btnAdminSettingsShortcut"));
        assertFalse(dashboardSource.contains("@+id/cardAdminWelcomeSection"));
        assertFalse(dashboardSource.contains("@+id/cardAdminStatsSection"));
        assertFalse(dashboardSource.contains("@+id/cardAdminQuickActionsSection"));
        assertTrue(dashboardSource.contains("@+id/cardAdminHeroSection"));
        assertTrue(dashboardSource.contains("@+id/cardAdminAlertSummary"));
        assertTrue(dashboardSource.contains("@+id/gridAdminTodayMetrics"));
        assertTrue(dashboardSource.contains("@+id/cardAdminRevenueChart"));
        assertTrue(dashboardSource.contains("@+id/cardAdminOrderStatus"));
        assertFalse(dashboardSource.contains("@+id/gridAdminQuickActions"));
        assertFalse(dashboardSource.contains("@+id/cardAdminAddDishShortcut"));
        assertFalse(dashboardSource.contains("@+id/cardAdminReservationShortcut"));
        assertFalse(dashboardSource.contains("@+id/cardAdminTablesShortcut"));
        assertFalse(dashboardSource.contains("@+id/cardAdminInvoicesShortcut"));
        assertFalse(dashboardSource.contains("@+id/cardAdminUsersShortcut"));
        assertFalse(dashboardSource.contains("@+id/cardAdminRequestsShortcut"));
        assertTrue(dashboardSource.contains("@+id/cardAdminRecentOrders"));
        assertTrue(dashboardSource.contains("@+id/layoutAdminRecentOrdersList"));
    }

    @Test
    public void adminRoutingSource_keepsAdminInAdminCenterWhileSupportingExpandedSections() throws Exception {
        String roleSource = readText(
                "src/main/java/com/example/quanlynhahang/helper/DieuHuongVaiTroHelper.java",
                "app/src/main/java/com/example/quanlynhahang/helper/DieuHuongVaiTroHelper.java"
        );
        String launcherSource = readText(
                "src/main/java/com/example/quanlynhahang/StaffLauncherActivity.java",
                "app/src/main/java/com/example/quanlynhahang/StaffLauncherActivity.java"
        );
        String routeSource = readText(
                "src/main/java/com/example/quanlynhahang/helper/DieuHuongNoiBoHelper.java",
                "app/src/main/java/com/example/quanlynhahang/helper/DieuHuongNoiBoHelper.java"
        );

        assertTrue(roleSource.contains("return TrungTamQuanTriActivity.taoIntent(context, DieuHuongNoiBoHelper.SECTION_BAO_CAO);"));
        assertTrue(launcherSource.contains("chuyenDen(TrungTamQuanTriActivity.taoIntent(this, DieuHuongNoiBoHelper.SECTION_BAO_CAO));"));
        assertTrue(routeSource.contains("SECTION_DON_HANG"));
        assertTrue(routeSource.contains("SECTION_HOA_DON"));
        assertTrue(routeSource.contains("SECTION_YEU_CAU"));
        assertTrue(routeSource.contains("return TrungTamQuanTriActivity.taoIntent("));
    }

    @Test
    public void adminCenterSource_setsOverviewSubtitleOnlyForOverviewSection() throws Exception {
        String source = readText(
                "src/main/java/com/example/quanlynhahang/TrungTamQuanTriActivity.java",
                "app/src/main/java/com/example/quanlynhahang/TrungTamQuanTriActivity.java"
        );

        assertTrue(source.contains("Integer subtitleRes = R.string.admin_reports_subtitle;"));
        assertTrue(source.contains("getSupportActionBar().setSubtitle(subtitleRes == null ? null : getString(subtitleRes));"));
    }

    private Document docXml(String modulePath, String projectPath) throws Exception {
        return DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(timFile(modulePath, projectPath));
    }

    private String readText(String modulePath, String projectPath) throws Exception {
        return new String(Files.readAllBytes(timFile(modulePath, projectPath).toPath()), StandardCharsets.UTF_8);
    }

    private Node timNodeTheoId(Document document, String viewId) {
        NodeList nodes = document.getElementsByTagName("*");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            Node idAttribute = node.getAttributes() == null ? null : node.getAttributes().getNamedItem("android:id");
            if (idAttribute != null && viewId.equals(idAttribute.getNodeValue())) {
                return node;
            }
        }
        return null;
    }

    private boolean coNodeTheoTen(Document document, String nodeName) {
        return document.getElementsByTagName(nodeName).getLength() > 0;
    }

    private File timFile(String modulePath, String projectPath) {
        File moduleFile = new File(modulePath);
        if (moduleFile.isFile()) {
            return moduleFile;
        }
        File projectFile = new File(projectPath);
        if (projectFile.isFile()) {
            return projectFile;
        }
        fail("Không tìm thấy file: " + projectPath);
        return null;
    }
}
