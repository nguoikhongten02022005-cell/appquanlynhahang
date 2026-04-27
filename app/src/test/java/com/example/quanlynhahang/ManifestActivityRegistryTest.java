package com.example.quanlynhahang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;

import javax.xml.parsers.DocumentBuilderFactory;

public class ManifestActivityRegistryTest {

    @Test
    public void manifest_keepsOnlyCurrentActivityEntriesAndCustomerLauncherAsLauncher() throws Exception {
        Document document = docXml(
                "src/main/AndroidManifest.xml",
                "app/src/main/AndroidManifest.xml"
        );

        assertNotNull(timActivity(document, ".DangKyActivity"));
        assertNotNull(timActivity(document, ".CustomerLauncherActivity"));
        assertNotNull(timActivity(document, ".StaffLauncherActivity"));
        assertNotNull(timActivity(document, ".DangNhapActivity"));
        assertNotNull(timActivity(document, ".GioHangActivity"));
        assertNotNull(timActivity(document, ".ChiTietDonHangActivity"));
        assertNotNull(timActivity(document, ".XacNhanDonHangActivity"));
        assertNotNull(timActivity(document, ".TrungTamQuanTriActivity"));
        assertNotNull(timActivity(document, ".MainActivity"));

        assertNull(timActivity(document, ".NhanVienActivity"));
        assertNull(timActivity(document, ".QuanTriActivity"));
        assertNull(timActivity(document, ".TrungTamNoiBoActivity"));

        assertEquals(9, demSoActivity(document));
        assertLauncher(document, ".CustomerLauncherActivity");
    }

    private Document docXml(String modulePath, String projectPath) throws Exception {
        return DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(timFile(modulePath, projectPath));
    }

    private File timFile(String modulePath, String projectPath) {
        File moduleFile = new File(modulePath);
        if (moduleFile.isFile()) {
            return moduleFile;
        }
        return new File(projectPath);
    }

    private Node timActivity(Document document, String activityName) {
        NodeList nodes = document.getElementsByTagName("activity");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            Node nameAttribute = node.getAttributes() == null ? null : node.getAttributes().getNamedItem("android:name");
            if (nameAttribute != null && activityName.equals(nameAttribute.getNodeValue())) {
                return node;
            }
        }
        return null;
    }

    private int demSoActivity(Document document) {
        return document.getElementsByTagName("activity").getLength();
    }

    private void assertLauncher(Document document, String activityName) {
        Node activity = timActivity(document, activityName);
        assertNotNull(activity);

        NodeList children = activity.getChildNodes();
        boolean coActionMain = false;
        boolean coCategoryLauncher = false;
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (!"intent-filter".equals(child.getNodeName())) {
                continue;
            }
            NodeList intentFilterChildren = child.getChildNodes();
            for (int j = 0; j < intentFilterChildren.getLength(); j++) {
                Node intentFilterChild = intentFilterChildren.item(j);
                if ("action".equals(intentFilterChild.getNodeName())) {
                    Node nameAttribute = intentFilterChild.getAttributes() == null ? null : intentFilterChild.getAttributes().getNamedItem("android:name");
                    if (nameAttribute != null && "android.intent.action.MAIN".equals(nameAttribute.getNodeValue())) {
                        coActionMain = true;
                    }
                }
                if ("category".equals(intentFilterChild.getNodeName())) {
                    Node nameAttribute = intentFilterChild.getAttributes() == null ? null : intentFilterChild.getAttributes().getNamedItem("android:name");
                    if (nameAttribute != null && "android.intent.category.LAUNCHER".equals(nameAttribute.getNodeValue())) {
                        coCategoryLauncher = true;
                    }
                }
            }
        }

        assertEquals(true, coActionMain);
        assertEquals(true, coCategoryLauncher);
    }
}
