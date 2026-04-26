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

public class AdminDishAndUiCleanupTest {

    @Test
    public void monAnQuanTriFragment_hoanThienThemSuaVaKhongConToastTam() throws Exception {
        String fragmentSource = readText(
                "src/main/java/com/example/quanlynhahang/MonAnQuanTriFragment.java",
                "app/src/main/java/com/example/quanlynhahang/MonAnQuanTriFragment.java"
        );
        Document layout = docXml(
                "src/main/res/layout/fragment_mon_an_quan_tri.xml",
                "app/src/main/res/layout/fragment_mon_an_quan_tri.xml"
        );

        assertFalse(fragmentSource.contains("Chức năng sửa món đang tạm tắt"));
        assertTrue(fragmentSource.contains("R.layout.dialog_add_edit_dish"));
        assertTrue(fragmentSource.contains("databaseHelper.capNhatBanGhiMonAn("));
        assertTrue(fragmentSource.contains("databaseHelper.themBanGhiMonAn("));
        assertNotNull(timNodeTheoId(layout, "@+id/btnAdminDishAdd"));
    }

    @Test
    public void dialogThemSuaMon_hienFormQuanLyBepThucTeVaAnDiemNoiBo() throws Exception {
        String layoutSource = readText(
                "src/main/res/layout/dialog_add_edit_dish.xml",
                "app/src/main/res/layout/dialog_add_edit_dish.xml"
        );
        String fragmentSource = readText(
                "src/main/java/com/example/quanlynhahang/MonAnQuanTriFragment.java",
                "app/src/main/java/com/example/quanlynhahang/MonAnQuanTriFragment.java"
        );
        String stringsSource = readText(
                "src/main/res/values/strings.xml",
                "app/src/main/res/values/strings.xml"
        );

        assertFalse(layoutSource.contains("etAdminDishScore"));
        assertFalse(stringsSource.contains("admin_dish_score_hint"));
        assertFalse(fragmentSource.contains("findViewById(R.id.etAdminDishScore)"));
        assertTrue(layoutSource.contains("TextInputLayout"));
        assertTrue(layoutSource.contains("@string/admin_dish_price_helper"));
        assertTrue(layoutSource.contains("@string/admin_dish_status_helper"));
        assertTrue(stringsSource.contains("name=\"admin_dish_category_hint\">Danh mục</string>"));
        assertTrue(layoutSource.contains("MaterialAutoCompleteTextView"));
        assertTrue(layoutSource.contains("@+id/autoCompleteAdminDishCategory"));
        assertFalse(layoutSource.contains("android:id=\"@+id/etAdminDishCategory\""));
        assertTrue(layoutSource.contains("android:id=\"@+id/autoCompleteAdminDishCategory\""));
        assertTrue(layoutSource.contains("android:inputType=\"none\""));
        assertTrue(layoutSource.contains("app:boxCollapsedPaddingTop=\"6dp\""));
        assertTrue(layoutSource.contains("app:hintTextColor=\"@color/primary\""));
        assertTrue(fragmentSource.contains("caiDatLuaChonDanhMuc"));
        assertTrue(fragmentSource.contains("private int layDiemDeXuatMacDinh"));
    }

    @Test
    public void uiCustomerFlows_tanDungMoneyUtilsVaDateTimeUtils() throws Exception {
        String gioHangSource = readText(
                "src/main/java/com/example/quanlynhahang/GioHangActivity.java",
                "app/src/main/java/com/example/quanlynhahang/GioHangActivity.java"
        );
        String xacNhanSource = readText(
                "src/main/java/com/example/quanlynhahang/XacNhanDonHangActivity.java",
                "app/src/main/java/com/example/quanlynhahang/XacNhanDonHangActivity.java"
        );
        String yeuCauSource = readText(
                "src/main/java/com/example/quanlynhahang/YeuCauFragment.java",
                "app/src/main/java/com/example/quanlynhahang/YeuCauFragment.java"
        );
        String chiTietSource = readText(
                "src/main/java/com/example/quanlynhahang/ChiTietDonHangActivity.java",
                "app/src/main/java/com/example/quanlynhahang/ChiTietDonHangActivity.java"
        );

        assertTrue(gioHangSource.contains("MoneyUtils"));
        assertFalse(gioHangSource.contains("replaceAll(\"[^0-9]\""));

        assertTrue(xacNhanSource.contains("MoneyUtils"));
        assertTrue(xacNhanSource.contains("DateTimeUtils"));
        assertFalse(xacNhanSource.contains("replaceAll(\"[^0-9]\""));
        assertFalse(xacNhanSource.contains("new SimpleDateFormat(\"dd/MM/yyyy HH:mm\""));

        assertTrue(yeuCauSource.contains("DateTimeUtils"));
        assertFalse(yeuCauSource.contains("new SimpleDateFormat(\"dd/MM/yyyy HH:mm\""));

        assertTrue(chiTietSource.contains("DateTimeUtils"));
        assertFalse(chiTietSource.contains("new java.text.SimpleDateFormat(\"dd/MM/yyyy HH:mm\""));
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
