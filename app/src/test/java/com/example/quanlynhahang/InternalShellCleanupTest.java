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
