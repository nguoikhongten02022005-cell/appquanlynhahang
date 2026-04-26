package com.example.quanlynhahang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.example.quanlynhahang.adapter.BoDieuHopNguoiDungQuanTri;
import com.example.quanlynhahang.model.NguoiDung;
import com.example.quanlynhahang.model.VaiTroNguoiDung;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import javax.xml.parsers.DocumentBuilderFactory;

public class AdminUserManagementUiSpecTest {

    @Test
    public void adminShellLayout_exposesAccountTabInBottomNavigation() throws Exception {
        Document document = docXml("app/src/main/res/layout/activity_trung_tam_quan_tri.xml");

        assertNotNull("Thiếu tab tài khoản", timNodeTheoId(document, "@+id/navAdminAccounts"));
        String source = readText("app/src/main/res/layout/activity_trung_tam_quan_tri.xml");
        assertTrue(source.contains("@string/admin_bottom_nav_accounts"));
        assertTrue(source.contains("@+id/iconAdminAccounts"));
        assertTrue(source.contains("@+id/tvAdminAccountsLabel"));
    }

    @Test
    public void adminUserFragmentLayout_usesClearSearchAndRuntimeSummaryLabels() throws Exception {
        String source = readText("app/src/main/res/layout/fragment_nguoi_dung_quan_tri.xml");

        assertTrue(source.contains("@string/admin_search_users_hint"));
        assertTrue(source.contains("@string/admin_filter_role_label"));
        assertFalse("Subtitle tài khoản phải được tính từ SQLite runtime, không dùng số liệu mẫu hardcode", source.contains("@string/admin_user_sample_summary"));
        assertFalse("Search không nên gợi ý tìm theo vai trò khi đã có bộ lọc riêng", source.contains("SĐT hoặc vai trò"));
    }

    @Test
    public void adminUserStrings_useRuntimeFormatsInsteadOfHardcodedSampleCounts() throws Exception {
        String source = readText("app/src/main/res/values/strings.xml");

        assertTrue(source.contains("<string name=\"admin_title_accounts\">Quản lý tài khoản</string>"));
        assertTrue(source.contains("<string name=\"admin_user_summary_format\">%1$d tài khoản · %2$d đang hoạt động</string>"));
        assertTrue(source.contains("<string name=\"admin_filter_all_roles_format\">Tất cả: %1$d</string>"));
        assertTrue(source.contains("<string name=\"admin_filter_admins_format\">Admin: %1$d</string>"));
        assertTrue(source.contains("<string name=\"admin_filter_employees_format\">Nhân viên: %1$d</string>"));
        assertFalse("Không giữ subtitle mẫu hardcode khi dữ liệu phải chạy từ SQLite", source.contains("admin_user_sample_summary"));
        assertFalse("Không giữ filter tất cả hardcode số 7", source.contains("<string name=\"admin_filter_all_roles\">Tất cả: 7</string>"));
        assertFalse("Không giữ filter admin hardcode số 2", source.contains("<string name=\"admin_filter_admins\">Admin: 2</string>"));
        assertFalse("Không giữ filter nhân viên hardcode số 5", source.contains("<string name=\"admin_filter_employees\">Nhân viên: 5</string>"));
        assertTrue(source.contains("<string name=\"admin_add_user\">Thêm tài khoản</string>"));
        assertTrue(source.contains("<string name=\"admin_search_users_hint\">Tìm tên, email hoặc SĐT</string>"));
        assertTrue(source.contains("<string name=\"admin_user_edit_account\">Sửa tài khoản</string>"));
        assertTrue(source.contains("<string name=\"admin_user_lock_account\">Khóa tài khoản</string>"));
        assertTrue(source.contains("<string name=\"admin_user_unlock_account\">Mở khóa tài khoản</string>"));
        assertTrue(source.contains("<string name=\"admin_user_delete_account\">Xóa tài khoản</string>"));
    }

    @Test
    public void adminUserFragment_updatesRoleFilterLabelsFromLoadedSqliteUsers() throws Exception {
        String source = readText("app/src/main/java/com/example/quanlynhahang/NguoiDungQuanTriFragment.java");

        assertTrue("Màn tài khoản phải tính lại nhãn filter sau khi đọc SQLite", source.contains("capNhatNhanBoLocVaiTro(danhSachNguoiDung)"));
        assertTrue(source.contains("R.string.admin_filter_all_roles_format"));
        assertTrue(source.contains("R.string.admin_filter_admins_format"));
        assertTrue(source.contains("R.string.admin_filter_employees_format"));
        assertFalse("Không dùng nhãn filter count hardcode", source.contains("getString(R.string.admin_filter_all_roles)"));
        assertFalse("Không dùng nhãn filter count hardcode", source.contains("getString(R.string.admin_filter_admins)"));
        assertFalse("Không dùng nhãn filter count hardcode", source.contains("getString(R.string.admin_filter_employees)"));
    }

    @Test
    public void adminUserPalette_matchesMobileSpecColors() throws Exception {
        String source = readText("app/src/main/res/values/colors.xml");

        assertTrue(source.contains("<color name=\"admin_account_background\">#FAF6EF</color>"));
        assertTrue(source.contains("<color name=\"admin_account_primary\">#DD873C</color>"));
        assertTrue(source.contains("<color name=\"admin_account_primary_dark\">#CC6F34</color>"));
        assertTrue(source.contains("<color name=\"admin_account_text_primary\">#2B2B2B</color>"));
        assertTrue(source.contains("<color name=\"admin_account_text_secondary\">#77736C</color>"));
        assertTrue(source.contains("<color name=\"admin_account_card\">#FFFFFF</color>"));
        assertTrue(source.contains("<color name=\"admin_account_border\">#E6D8CA</color>"));
    }

    @Test
    public void adminUserItemLayout_prioritizesReadableIdentityAndSingleActionMenu() throws Exception {
        Document document = docXml("app/src/main/res/layout/item_admin_user.xml");
        String source = readText("app/src/main/res/layout/item_admin_user.xml");

        assertNotNull("Thiếu avatar chữ cái", timNodeTheoId(document, "@+id/tvAdminUserAvatar"));
        assertNotNull("Thiếu email nhận diện", timNodeTheoId(document, "@+id/tvAdminUserEmail"));
        assertNotNull("Thiếu số điện thoại nhận diện", timNodeTheoId(document, "@+id/tvAdminUserPhone"));
        assertNotNull("Thiếu chấm trạng thái", timNodeTheoId(document, "@+id/viewAdminUserStatusDot"));
        assertNotNull("Thiếu nút mở thao tác", timNodeTheoId(document, "@+id/btnAdminUserActions"));
        assertFalse("Không nên còn nút đổi vai trò riêng trên card", source.contains("@+id/btnAdminDoiVaiTro"));
        assertFalse("Không nên còn nút khoá/mở riêng trên card", source.contains("@+id/btnAdminUserToggleActive"));
    }

    @Test
    public void adminUserActionDialog_showsAccountContextAndSpecActions() throws Exception {
        Document document = docXml("app/src/main/res/layout/dialog_admin_user_actions.xml");
        String source = readText("app/src/main/res/layout/dialog_admin_user_actions.xml");

        assertNotNull("Thiếu tên tài khoản trong popup", timNodeTheoId(document, "@+id/tvAdminActionUserName"));
        assertNotNull("Thiếu email tài khoản trong popup", timNodeTheoId(document, "@+id/tvAdminActionUserEmail"));
        assertNotNull("Thiếu trạng thái tài khoản trong popup", timNodeTheoId(document, "@+id/tvAdminActionUserStatus"));
        assertNotNull("Thiếu action sửa tài khoản", timNodeTheoId(document, "@+id/btnAdminUserEditAccount"));
        assertNotNull("Thiếu action khoá/mở khoá", timNodeTheoId(document, "@+id/btnAdminUserToggleStatus"));
        assertNotNull("Thiếu action xoá tài khoản", timNodeTheoId(document, "@+id/btnAdminUserDeleteAccount"));
        assertTrue(source.contains("@string/admin_user_edit_account"));
        assertTrue(source.contains("@string/admin_user_delete_account"));
        assertFalse("Popup theo spec không hiển thị action xem chi tiết riêng", source.contains("@+id/btnAdminUserViewDetail"));
        assertFalse("Popup theo spec không hiển thị action đổi vai trò riêng", source.contains("@+id/btnAdminUserChangeRole"));
        assertFalse("Popup theo spec không hiển thị action đặt lại mật khẩu riêng", source.contains("@+id/btnAdminUserResetPassword"));
    }

    @Test
    public void adminAddUserDialog_includesRolePasswordAndStatusFields() throws Exception {
        Document document = docXml("app/src/main/res/layout/dialog_add_edit_user.xml");

        assertNotNull("Thiếu trường họ tên", timNodeTheoId(document, "@+id/etAdminUserName"));
        assertNotNull("Thiếu trường email", timNodeTheoId(document, "@+id/etAdminUserEmail"));
        assertNotNull("Thiếu trường số điện thoại", timNodeTheoId(document, "@+id/etAdminUserPhone"));
        assertNotNull("Thiếu trường vai trò", timNodeTheoId(document, "@+id/spinnerAdminUserRole"));
        assertNotNull("Thiếu trường mật khẩu", timNodeTheoId(document, "@+id/etAdminUserPassword"));
        assertNotNull("Thiếu trường trạng thái", timNodeTheoId(document, "@+id/spinnerAdminUserStatus"));
    }

    @Test
    public void seedData_includesSevenStaffAccountsWithSixActiveForAdminScreen() throws Exception {
        String source = readText("app/src/main/java/com/example/quanlynhahang/data/SeedDataHelper.java");
        String sampleSection = source.substring(
                source.indexOf("static void damBaoTaiKhoanMauBoSung"),
                source.indexOf("static void damBaoBanAnMau")
        );

        assertEquals("Seed màn quản lý cần đúng 7 tài khoản nhân sự", 7, countOccurrences(sampleSection, "ensureSeedUser("));
        assertEquals("Seed màn quản lý cần 6 tài khoản đang hoạt động", 6, countOccurrences(sampleSection, "true"));
        assertEquals("Seed màn quản lý cần 2 admin", 2, countOccurrences(sampleSection, "VaiTroNguoiDung.ADMIN"));
        assertEquals("Seed màn quản lý cần 5 nhân viên", 5, countOccurrences(sampleSection, "VaiTroNguoiDung.NHAN_VIEN"));
        assertFalse("Seed màn quản lý tài khoản không nên có vai trò khách hàng", sampleSection.contains("VaiTroNguoiDung.KHACH_HANG"));
    }

    @Test
    public void userPresenter_returnsExpectedBadgesAndStatusColors() {
        NguoiDung admin = new NguoiDung(1L, "Nguyễn Minh Anh", "minhanh@nhahang.vn", "0912 345 678", VaiTroNguoiDung.ADMIN, true);
        NguoiDung quanLy = new NguoiDung(2L, "Trần Quốc Bảo", "quocbao@nhahang.vn", "0987 654 321", VaiTroNguoiDung.KHACH_HANG, true);
        NguoiDung nhanVien = new NguoiDung(3L, "Lê Thảo Vy", "thaovy@nhahang.vn", "0901 234 567", VaiTroNguoiDung.NHAN_VIEN, false);

        assertTrue(BoDieuHopNguoiDungQuanTri.taoMoTaHienThi(admin).contains("Nguyễn Minh Anh"));
        assertTrue(BoDieuHopNguoiDungQuanTri.taoNhanVaiTro(admin).contains("Admin"));
        assertTrue(BoDieuHopNguoiDungQuanTri.taoNhanVaiTro(quanLy).contains("Quản lý"));
        assertTrue(BoDieuHopNguoiDungQuanTri.taoNhanVaiTro(nhanVien).contains("Nhân viên"));
        assertTrue(BoDieuHopNguoiDungQuanTri.taoChuCaiDaiDien(admin).contains("NM"));
        assertTrue(BoDieuHopNguoiDungQuanTri.taoChuCaiDaiDien(nhanVien).contains("LT"));
        assertTrue(BoDieuHopNguoiDungQuanTri.taoTrangThaiHienThi(admin).contains("Đang hoạt động"));
        assertTrue(BoDieuHopNguoiDungQuanTri.taoTrangThaiHienThi(nhanVien).contains("Đã khóa"));
    }

    private Document docXml(String projectPath) throws Exception {
        return DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(resolveProjectFile(projectPath));
    }

    private String readText(String projectPath) throws Exception {
        return Files.readString(resolveProjectFile(projectPath).toPath(), StandardCharsets.UTF_8);
    }

    private File resolveProjectFile(String projectPath) {
        File file = new File(projectPath);
        if (file.exists()) {
            return file;
        }
        File fromUserDir = new File(System.getProperty("user.dir"), projectPath);
        if (fromUserDir.exists()) {
            return fromUserDir;
        }
        if (projectPath.startsWith("app/")) {
            return new File(System.getProperty("user.dir"), projectPath.substring("app/".length()));
        }
        return fromUserDir;
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

    private int countOccurrences(String source, String needle) {
        int count = 0;
        int index = 0;
        while ((index = source.indexOf(needle, index)) >= 0) {
            count++;
            index += needle.length();
        }
        return count;
    }
}
