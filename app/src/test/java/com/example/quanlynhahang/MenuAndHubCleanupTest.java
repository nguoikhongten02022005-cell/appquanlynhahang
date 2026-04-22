package com.example.quanlynhahang;

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

public class MenuAndHubCleanupTest {

    @Test
    public void activityHubLayout_removesUnusedEmptySummaryRow() throws Exception {
        Document document = docXml(
                "src/main/res/layout/fragment_trung_tam_hoat_dong.xml",
                "app/src/main/res/layout/fragment_trung_tam_hoat_dong.xml"
        );

        assertNotNull(timNodeTheoId(document, "@+id/tvServiceHubSummaryTable"));
        assertNotNull(timNodeTheoId(document, "@+id/tvServiceHubSummaryOrder"));
        assertNotNull(timNodeTheoId(document, "@+id/tvServiceHubSummarySupport"));
        assertFalse(coNodeTheoId(document, "@+id/tvServiceHubSummaryEmpty"));
    }

    @Test
    public void trungTamHoatDongFragment_stopsReferencingRemovedEmptySummaryView() throws Exception {
        String source = readText(
                "src/main/java/com/example/quanlynhahang/TrungTamHoatDongFragment.java",
                "app/src/main/java/com/example/quanlynhahang/TrungTamHoatDongFragment.java"
        );

        assertFalse(source.contains("tvServiceHubSummaryEmpty"));
        assertTrue(source.contains("capNhatDongTomTat(tvServiceHubSummarySupport"));
    }

    @Test
    public void thucDonFragment_dropsUnusedAppliedSearchStateField() throws Exception {
        String source = readText(
                "src/main/java/com/example/quanlynhahang/ThucDonFragment.java",
                "app/src/main/java/com/example/quanlynhahang/ThucDonFragment.java"
        );

        assertFalse(source.contains("tuKhoaTimKiemDaApDung"));
        assertTrue(source.contains("dangCapNhatTimKiemNoiBo"));
        assertTrue(source.contains("apDungTuKhoaTimKiemNeuCan()"));
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

    private boolean coNodeTheoId(Document document, String viewId) {
        return timNodeTheoId(document, viewId) != null;
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
