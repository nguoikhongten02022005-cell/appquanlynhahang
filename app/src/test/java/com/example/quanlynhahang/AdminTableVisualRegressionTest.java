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

public class AdminTableVisualRegressionTest {

    @Test
    public void tableManagementLayout_includesLegendOccupancyAndFloorMapSections() throws Exception {
        Document document = docXml(
                "src/main/res/layout/fragment_quan_ly_ban_quan_tri.xml",
                "app/src/main/res/layout/fragment_quan_ly_ban_quan_tri.xml"
        );

        assertNotNull(timNodeTheoId(document, "@+id/layoutQuanLyBanLegend"));
        assertNotNull(timNodeTheoId(document, "@+id/cardQuanLyBanOccupancy"));
        assertNotNull(timNodeTheoId(document, "@+id/tvQuanLyBanOccupancyRate"));
        assertNotNull(timNodeTheoId(document, "@+id/progressQuanLyBanOccupancy"));
        assertNotNull(timNodeTheoId(document, "@+id/rvDanhSachBan"));
    }

    @Test
    public void tableManagementSource_updatesOccupancySummaryFromFilteredDataset() throws Exception {
        String source = readText(
                "src/main/java/com/example/quanlynhahang/QuanLyBanQuanTriFragment.java",
                "app/src/main/java/com/example/quanlynhahang/QuanLyBanQuanTriFragment.java"
        );

        assertTrue(source.contains("capNhatTongQuanBan(danhSachLoc)"));
        assertTrue(source.contains("tvQuanLyBanOccupancyRate"));
        assertTrue(source.contains("progressQuanLyBanOccupancy"));
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
