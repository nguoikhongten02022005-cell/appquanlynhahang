package com.example.quanlynhahang;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProjectAuditFixTest {

    private static final Path ROOT = Path.of(System.getProperty("user.dir"));

    @Test
    public void seedData_usesExistingDrawableNamesOnly() throws Exception {
        String json = read("src/main/assets/seed_data.json");

        assertFalse(json.contains("\"image\":\"dish_6\""));
        assertFalse(json.contains("\"image\":\"menu_2\""));
        assertFalse(json.contains("\"image\":\"image3\""));
        assertTrue(json.contains("\"image\":\"menu_1\""));
    }

    @Test
    public void dishRepository_fallsBackToDefaultDrawableWhenStoredNameIsMissing() throws Exception {
        String source = read("src/main/java/com/example/quanlynhahang/data/DishRepository.java");

        assertTrue(source.contains("resolveImageResId(String imageResName)"));
        assertTrue(source.contains("return resId == 0 ? R.drawable.menu_1 : resId;"));
    }

    @Test
    public void cartAdapter_guardsAgainstNullDataAndNullListener() throws Exception {
        String source = read("src/main/java/com/example/quanlynhahang/adapter/MonTrongGioAdapter.java");

        assertTrue(source.contains("if (danhSachMon != null)"));
        assertTrue(source.contains("if (danhSachMoi != null)"));
        assertTrue(source.contains("if (onHanhDongSoLuongListener != null)"));
    }

    @Test
    public void loginActivity_closesDatabaseHelperOnDestroy() throws Exception {
        String source = read("src/main/java/com/example/quanlynhahang/DangNhapActivity.java");

        assertTrue(source.contains("protected void onDestroy()"));
        assertTrue(source.contains("databaseHelper.close();"));
    }

    @Test
    public void quickLoginNamingDoesNotExposeDemoOrTestConcepts() throws Exception {
        String loginSource = read("src/main/java/com/example/quanlynhahang/DangNhapActivity.java");
        String databaseSource = read("src/main/java/com/example/quanlynhahang/data/DatabaseHelper.java");
        String strings = read("src/main/res/values/strings.xml");
        String seed = read("src/main/assets/seed_data.json");

        assertTrue(loginSource.contains("layGoiYDangNhapNhanhTheoVaiTro"));
        assertTrue(databaseSource.contains("class GoiYDangNhapNhanh"));
        assertFalse(databaseSource.contains("TaiKhoanDemo"));
        assertFalse(databaseSource.contains("MAT_KHAU_DEMO_MAC_DINH"));
        assertFalse(strings.contains("demo"));
        assertFalse(strings.contains("mô phỏng"));
        assertFalse(strings.contains("legacy_seed_resource_prefix"));
        assertFalse(seed.contains("demo_key"));
        assertFalse(seed.contains("MO_PHONG"));
    }

    @Test
    public void paymentEnumsUseOperationalNamesAndKeepLegacyParsing() throws Exception {
        String modelSource = read("src/main/java/com/example/quanlynhahang/model/DonHang.java");
        String databaseSource = read("src/main/java/com/example/quanlynhahang/data/DatabaseHelper.java");

        assertTrue(modelSource.contains("DA_THANH_TOAN"));
        assertTrue(modelSource.contains("THANH_TOAN_NGAY"));
        assertFalse(modelSource.contains("DA_THANH_TOAN_MO_PHONG"));
        assertFalse(modelSource.contains("THANH_TOAN_NGAY_MO_PHONG"));
        assertTrue(databaseSource.contains("\"DA_THANH_TOAN_MO_PHONG\".equals(paymentStatusRaw)"));
        assertTrue(databaseSource.contains("\"THANH_TOAN_NGAY_MO_PHONG\".equals(paymentMethodRaw)"));
    }

    private static String read(String relativePath) throws IOException {
        return Files.readString(ROOT.resolve(relativePath), StandardCharsets.UTF_8);
    }
}
