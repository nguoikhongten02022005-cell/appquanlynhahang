package com.example.quanlynhahang;

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

public class QuanLyBanAdminCenterTest {

    @Test
    public void adminCenter_supportsDedicatedTableManagementSection() throws Exception {
        String helperSource = readText(
                "src/main/java/com/example/quanlynhahang/helper/DieuHuongNoiBoHelper.java",
                "app/src/main/java/com/example/quanlynhahang/helper/DieuHuongNoiBoHelper.java"
        );
        String activitySource = readText(
                "src/main/java/com/example/quanlynhahang/TrungTamQuanTriActivity.java",
                "app/src/main/java/com/example/quanlynhahang/TrungTamQuanTriActivity.java"
        );
        String reportSource = readText(
                "src/main/java/com/example/quanlynhahang/BaoCaoQuanTriFragment.java",
                "app/src/main/java/com/example/quanlynhahang/BaoCaoQuanTriFragment.java"
        );

        assertTrue(helperSource.contains("SECTION_BAN = \"tables\""));
        assertTrue(activitySource.contains("DieuHuongNoiBoHelper.SECTION_BAN"));
        assertTrue(activitySource.contains("new QuanLyBanQuanTriFragment()"));
        assertTrue(reportSource.contains("btnMoQuanLyBan"));
        assertTrue(reportSource.contains("DieuHuongNoiBoHelper.SECTION_BAN"));
    }

    @Test
    public void tableManagementLayout_exposesSearchFilterAddAndList() throws Exception {
        Document document = docXml(
                "src/main/res/layout/fragment_quan_ly_ban_quan_tri.xml",
                "app/src/main/res/layout/fragment_quan_ly_ban_quan_tri.xml"
        );

        assertNotNull("Thiếu tiêu đề quản lý bàn", timNodeTheoId(document, "@+id/tvQuanLyBanTitle"));
        assertNotNull("Thiếu ô tìm kiếm bàn", timNodeTheoId(document, "@+id/etQuanLyBanSearch"));
        assertNotNull("Thiếu bộ lọc trạng thái", timNodeTheoId(document, "@+id/autoCompleteQuanLyBanStatusFilter"));
        assertNotNull("Thiếu nút thêm bàn", timNodeTheoId(document, "@+id/btnThemBan"));
        assertNotNull("Thiếu danh sách bàn", timNodeTheoId(document, "@+id/rvDanhSachBan"));
        assertNotNull("Thiếu trạng thái rỗng", timNodeTheoId(document, "@+id/tvQuanLyBanEmpty"));
    }

    @Test
    public void tableCardLayout_showsStatusBadgeAndThreeActions() throws Exception {
        Document document = docXml(
                "src/main/res/layout/item_ban_an_quan_ly.xml",
                "app/src/main/res/layout/item_ban_an_quan_ly.xml"
        );

        assertNotNull("Thiếu tên hoặc mã bàn", timNodeTheoId(document, "@+id/tvTenBan"));
        assertNotNull("Thiếu badge trạng thái bàn", timNodeTheoId(document, "@+id/tvTrangThaiBan"));
        assertNotNull("Thiếu thông tin số chỗ và khu vực", timNodeTheoId(document, "@+id/tvThongTinBan"));
        assertNotNull("Thiếu nút xem chi tiết", timNodeTheoId(document, "@+id/btnXemChiTietBan"));
        assertNotNull("Thiếu nút sửa bàn", timNodeTheoId(document, "@+id/btnSuaBan"));
        assertNotNull("Thiếu nút xóa bàn", timNodeTheoId(document, "@+id/btnXoaBan"));
    }

    @Test
    public void databaseHelper_definesTableEntityStatusMappingAndConditionalDelete() throws Exception {
        String source = readText(
                "src/main/java/com/example/quanlynhahang/data/DatabaseHelper.java",
                "app/src/main/java/com/example/quanlynhahang/data/DatabaseHelper.java"
        );

        assertTrue(source.contains("TABLE_BAN_AN"));
        assertTrue(source.contains("taoBangBanAn()"));
        assertTrue(source.contains("layTatCaBanAn()"));
        assertTrue(source.contains("themBanAn("));
        assertTrue(source.contains("capNhatBanAn("));
        assertTrue(source.contains("xoaBanAnNeuTrong("));
        assertTrue(source.contains("BanAn.TrangThai.DA_DAT"));
        assertTrue(source.contains("BanAn.TrangThai.DANG_PHUC_VU"));
    }

    @Test
    public void tableManagementSource_restrictsDeleteToEmptyTablesWithConfirmation() throws Exception {
        String source = readText(
                "src/main/java/com/example/quanlynhahang/QuanLyBanQuanTriFragment.java",
                "app/src/main/java/com/example/quanlynhahang/QuanLyBanQuanTriFragment.java"
        );

        assertTrue(source.contains("banAn.layTrangThai() != BanAn.TrangThai.TRONG"));
        assertTrue(source.contains("xoaBanAnNeuTrong("));
        assertTrue(source.contains("setPositiveButton(R.string.quan_ly_ban_xoa_ban"));
        assertTrue(source.contains("setMessage(R.string.quan_ly_ban_xac_nhan_xoa"));
    }

    @Test
    public void tableStrings_defineStatusesDeleteMessagesAndBlockedDeleteCopy() throws Exception {
        String strings = readText(
                "src/main/res/values/strings.xml",
                "app/src/main/res/values/strings.xml"
        );

        assertTrue(strings.contains("<string name=\"admin_shortcut_reservations\">Quản lý bàn</string>"));
        assertTrue(strings.contains("<string name=\"admin_table_management_title\">Quản lý bàn</string>"));
        assertTrue(strings.contains("<string name=\"quan_ly_ban_trang_thai_tat_ca\">Tất cả</string>"));
        assertTrue(strings.contains("<string name=\"quan_ly_ban_trang_thai_trong\">Trống</string>"));
        assertTrue(strings.contains("<string name=\"quan_ly_ban_trang_thai_dang_phuc_vu\">Đang phục vụ</string>"));
        assertTrue(strings.contains("<string name=\"quan_ly_ban_trang_thai_da_dat\">Đã đặt</string>"));
        assertTrue(strings.contains("<string name=\"quan_ly_ban_xoa_ban\">Xóa bàn</string>"));
        assertTrue(strings.contains("<string name=\"quan_ly_ban_xac_nhan_xoa\">"));
        assertTrue(strings.contains("<string name=\"quan_ly_ban_khong_the_xoa\">"));
    }

    @Test
    public void tableManagementSource_supportsFullStatusFilterAndDemoStates() throws Exception {
        String source = readText(
                "src/main/java/com/example/quanlynhahang/QuanLyBanQuanTriFragment.java",
                "app/src/main/java/com/example/quanlynhahang/QuanLyBanQuanTriFragment.java"
        );
        String databaseSource = readText(
                "src/main/java/com/example/quanlynhahang/data/DatabaseHelper.java",
                "app/src/main/java/com/example/quanlynhahang/data/DatabaseHelper.java"
        );

        assertTrue(source.contains("quan_ly_ban_trang_thai_tat_ca"));
        assertTrue(source.contains("quan_ly_ban_trang_thai_trong"));
        assertTrue(source.contains("quan_ly_ban_trang_thai_dang_phuc_vu"));
        assertTrue(source.contains("quan_ly_ban_trang_thai_da_dat"));
        assertTrue(databaseSource.contains("BanAn.TrangThai.TRONG"));
        assertTrue(databaseSource.contains("BanAn.TrangThai.DANG_PHUC_VU"));
        assertTrue(databaseSource.contains("BanAn.TrangThai.DA_DAT"));
    }

    private Document docXml(String modulePath, String projectPath) throws Exception {
        return DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(timFile(modulePath, projectPath));
    }

    private String readText(String modulePath, String projectPath) throws Exception {
        return Files.readString(timFile(modulePath, projectPath).toPath(), StandardCharsets.UTF_8);
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
