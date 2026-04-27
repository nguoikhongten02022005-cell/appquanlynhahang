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

public class AdminHtmlPortRegressionTest {

    @Test
    public void invoiceAdminLayout_includesRevenueSummaryBreakdownAndInvoiceList() throws Exception {
        Document document = docXml(
                "src/main/res/layout/fragment_hoa_don_quan_tri.xml",
                "app/src/main/res/layout/fragment_hoa_don_quan_tri.xml"
        );

        assertNotNull("Thiếu thẻ tổng doanh thu", timNodeTheoId(document, "@+id/cardAdminInvoiceRevenueSummary"));
        assertNotNull("Thiếu chỉ số doanh thu lớn", timNodeTheoId(document, "@+id/tvAdminInvoiceRevenueTotal"));
        assertNotNull("Thiếu nhóm phương thức thanh toán", timNodeTheoId(document, "@+id/cardAdminInvoicePaymentBreakdown"));
        assertNotNull("Thiếu danh sách hóa đơn", timNodeTheoId(document, "@+id/rvHoaDonQuanTri"));
        assertNotNull("Thiếu trạng thái rỗng hóa đơn", timNodeTheoId(document, "@+id/tvHoaDonQuanTriEmpty"));
    }

    @Test
    public void dishAndUserAdminLayouts_includeSearchAndTopSummarySections() throws Exception {
        Document monLayout = docXml(
                "src/main/res/layout/fragment_mon_an_quan_tri.xml",
                "app/src/main/res/layout/fragment_mon_an_quan_tri.xml"
        );
        Document nguoiDungLayout = docXml(
                "src/main/res/layout/fragment_nguoi_dung_quan_tri.xml",
                "app/src/main/res/layout/fragment_nguoi_dung_quan_tri.xml"
        );

        assertNotNull("Thiếu ô tìm kiếm món", timNodeTheoId(monLayout, "@+id/etAdminDishSearch"));
        assertNotNull("Thiếu thanh thống kê món", timNodeTheoId(monLayout, "@+id/layoutAdminDishCategoryChips"));
        assertNotNull("Thiếu nút thêm món nổi bật", timNodeTheoId(monLayout, "@+id/btnAdminDishAdd"));

        assertNotNull("Thiếu ô tìm kiếm tài khoản", timNodeTheoId(nguoiDungLayout, "@+id/etAdminUserSearch"));
        assertNotNull("Thiếu hàng thống kê vai trò", timNodeTheoId(nguoiDungLayout, "@+id/hScrollAdminUserStats"));
        assertNotNull("Thiếu danh sách tài khoản", timNodeTheoId(nguoiDungLayout, "@+id/rvNguoiDungQuanTri"));
    }

    @Test
    public void orderAndRequestAdminLayouts_includeSummaryBarsAndFilters() throws Exception {
        Document donLayout = docXml(
                "src/main/res/layout/fragment_don_hang_noi_bo.xml",
                "app/src/main/res/layout/fragment_don_hang_noi_bo.xml"
        );
        Document yeuCauLayout = docXml(
                "src/main/res/layout/fragment_yeu_cau_noi_bo.xml",
                "app/src/main/res/layout/fragment_yeu_cau_noi_bo.xml"
        );

        assertNotNull("Thiếu thanh tóm tắt đơn hàng", timNodeTheoId(donLayout, "@+id/layoutAdminOrderSummaryBar"));
        assertNotNull("Thiếu bộ lọc trạng thái đơn", timNodeTheoId(donLayout, "@+id/hScrollAdminOrderFilters"));
        assertNotNull("Thiếu danh sách đơn nội bộ", timNodeTheoId(donLayout, "@+id/rvDonHangNoiBo"));

        assertNotNull("Thiếu thanh tóm tắt yêu cầu", timNodeTheoId(yeuCauLayout, "@+id/layoutAdminRequestSummaryBar"));
        assertNotNull("Thiếu bộ lọc yêu cầu", timNodeTheoId(yeuCauLayout, "@+id/hScrollAdminRequestFilters"));
        assertNotNull("Thiếu danh sách yêu cầu", timNodeTheoId(yeuCauLayout, "@+id/rvYeuCauNoiBo"));
    }

    @Test
    public void adminFragments_computeScreenSpecificSummaryMetrics() throws Exception {
        String hoaDonSource = readText(
                "src/main/java/com/example/quanlynhahang/HoaDonQuanTriFragment.java",
                "app/src/main/java/com/example/quanlynhahang/HoaDonQuanTriFragment.java"
        );
        String donHangSource = readText(
                "src/main/java/com/example/quanlynhahang/DonHangNoiBoFragment.java",
                "app/src/main/java/com/example/quanlynhahang/DonHangNoiBoFragment.java"
        );
        String yeuCauSource = readText(
                "src/main/java/com/example/quanlynhahang/YeuCauNoiBoFragment.java",
                "app/src/main/java/com/example/quanlynhahang/YeuCauNoiBoFragment.java"
        );

        assertTrue(hoaDonSource.contains("capNhatTongQuanHoaDon"));
        assertTrue(donHangSource.contains("capNhatTongQuanDonHang"));
        assertTrue(yeuCauSource.contains("capNhatTongQuanYeuCau"));
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
