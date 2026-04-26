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

public class AdminReviewRegressionTest {

    @Test
    public void dashboardNavigation_reusesCurrentAdminShellInsteadOfLaunchingNewActivity() throws Exception {
        String fragmentSource = readText(
                "src/main/java/com/example/quanlynhahang/BaoCaoQuanTriFragment.java",
                "app/src/main/java/com/example/quanlynhahang/BaoCaoQuanTriFragment.java"
        );
        String activitySource = readText(
                "src/main/java/com/example/quanlynhahang/TrungTamQuanTriActivity.java",
                "app/src/main/java/com/example/quanlynhahang/TrungTamQuanTriActivity.java"
        );

        assertTrue(activitySource.contains("public void dieuHuongDenSection(String section)"));
        assertTrue(fragmentSource.contains("instanceof TrungTamQuanTriActivity"));
        assertTrue(fragmentSource.contains("dieuHuongDenSection(section)"));
    }

    @Test
    public void dashboardOrderCard_keepsTodayLabelBackedByTodayOnlyCount() throws Exception {
        String fragmentSource = readText(
                "src/main/java/com/example/quanlynhahang/BaoCaoQuanTriFragment.java",
                "app/src/main/java/com/example/quanlynhahang/BaoCaoQuanTriFragment.java"
        );

        assertTrue(fragmentSource.contains("private int demDonHangHomNay(List<DonHang> donHangs)"));
        assertTrue(fragmentSource.contains("tvTongDonHang.setText(String.valueOf(demDonHangHomNay(tatCaDonHang)));"));
    }

    @Test
    public void requestSummaryLabel_matchesPaymentBasedMetric() throws Exception {
        String strings = readText(
                "src/main/res/values/strings.xml",
                "app/src/main/res/values/strings.xml"
        );
        String source = readText(
                "src/main/java/com/example/quanlynhahang/YeuCauNoiBoFragment.java",
                "app/src/main/java/com/example/quanlynhahang/YeuCauNoiBoFragment.java"
        );
        Document layout = docXml(
                "src/main/res/layout/fragment_yeu_cau_noi_bo.xml",
                "app/src/main/res/layout/fragment_yeu_cau_noi_bo.xml"
        );

        assertTrue(strings.contains("<string name=\"admin_request_payment_label\">Thanh toán</string>"));
        assertTrue(source.contains("yeuCau.layLoaiYeuCau() == YeuCauPhucVu.LoaiYeuCau.THANH_TOAN"));
        assertNotNull(timNodeTheoId(layout, "@+id/tvAdminRequestSummaryUrgent"));
    }

    @Test
    public void invoiceLayout_rendersVisibleScreenHeaderInsideFragment() throws Exception {
        Document layout = docXml(
                "src/main/res/layout/fragment_hoa_don_quan_tri.xml",
                "app/src/main/res/layout/fragment_hoa_don_quan_tri.xml"
        );

        assertNotNull(timNodeTheoId(layout, "@+id/tvHoaDonQuanTriTitle"));
        assertNotNull(timNodeTheoId(layout, "@+id/tvHoaDonQuanTriSubtitle"));
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
