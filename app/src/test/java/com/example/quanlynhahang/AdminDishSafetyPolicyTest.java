package com.example.quanlynhahang;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class AdminDishSafetyPolicyTest {

    @Test
    public void databaseHelper_boSungSchemaLuuTruMonVaWrapperKiemTraTrungTen() throws Exception {
        String source = readText(
                "src/main/java/com/example/quanlynhahang/data/DatabaseHelper.java",
                "app/src/main/java/com/example/quanlynhahang/data/DatabaseHelper.java"
        );

        assertTrue(source.contains("private static final int DATABASE_VERSION = 10"));
        assertTrue(source.contains("COL_DISH_IS_ARCHIVED = \"is_archived\""));
        assertTrue(source.contains("COL_DISH_ARCHIVED_AT = \"archived_at\""));
        assertTrue(source.contains("damBaoCotTonTai(db, TABLE_DISH, COL_DISH_IS_ARCHIVED"));
        assertTrue(source.contains("damBaoCotTonTai(db, TABLE_DISH, COL_DISH_ARCHIVED_AT"));
        assertTrue(source.contains("COL_DISH_IS_ARCHIVED + \" INTEGER NOT NULL DEFAULT 0"));
        assertTrue(source.contains("COL_DISH_ARCHIVED_AT + \" TEXT NOT NULL DEFAULT ''"));
        assertTrue(source.contains("public boolean tenMonAnDangTonTai(String tenMon, long boQuaId)"));
    }

    @Test
    public void dishRepository_anMonDaLuuTruVaXoaTheoChinhSachAnToan() throws Exception {
        String source = readText(
                "src/main/java/com/example/quanlynhahang/data/DishRepository.java",
                "app/src/main/java/com/example/quanlynhahang/data/DishRepository.java"
        );

        assertTrue(source.contains("DateTimeUtils.layThoiGianHienTai()"));
        assertTrue(source.contains("TABLE_ORDER_ITEM"));
        assertTrue(source.contains("COL_ORDER_ITEM_DISH_NAME"));
        assertTrue(source.contains("COL_DISH_IS_ARCHIVED"));
        assertTrue(source.contains("COL_DISH_ARCHIVED_AT"));
        assertTrue(source.contains("COL_DISH_IS_AVAILABLE"));
        assertTrue(source.contains("tenMonDangDuocDung(String name, long excludeDishId)"));
        assertTrue(source.contains("taoSelectionMonChuaLuuTru"));
        assertTrue(source.contains("coLichSuDonHangChoTenMon"));
        assertTrue(source.contains("Log.i(TAG"));
    }

    @Test
    public void monAnQuanTriFragment_kiemTraTrungTenVaXacNhanXoaCoTenMon() throws Exception {
        String source = readText(
                "src/main/java/com/example/quanlynhahang/MonAnQuanTriFragment.java",
                "app/src/main/java/com/example/quanlynhahang/MonAnQuanTriFragment.java"
        );

        assertTrue(source.contains("databaseHelper.tenMonAnDangTonTai"));
        assertTrue(source.contains("R.string.admin_dish_validation_duplicate_name"));
        assertTrue(source.contains("R.string.admin_delete_confirm_message_named"));
        assertTrue(source.contains("banGhiMon.layMonAn().layTenMon()"));
    }

    @Test
    public void strings_coThongBaoAnToanChoTrungTenVaXoaLuuTru() throws Exception {
        String source = readText(
                "src/main/res/values/strings.xml",
                "app/src/main/res/values/strings.xml"
        );

        assertTrue(source.contains("name=\"admin_dish_validation_duplicate_name\""));
        assertTrue(source.contains("name=\"admin_delete_confirm_message_named\""));
        assertTrue(source.contains("%1$s"));
        assertTrue(source.contains("lưu trữ"));
        assertTrue(source.contains("Đã xoá món khỏi danh sách hiển thị"));
    }

    private String readText(String modulePath, String projectPath) throws Exception {
        return Files.readString(timFile(modulePath, projectPath).toPath(), StandardCharsets.UTF_8);
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
