package com.example.quanlynhahang;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class SeedDataExtractionTest {

    @Test
    public void databaseHelper_delegatesSampleSeedToSeedDataHelper() throws Exception {
        String databaseHelperSource = readText(
                "src/main/java/com/example/quanlynhahang/data/DatabaseHelper.java",
                "app/src/main/java/com/example/quanlynhahang/data/DatabaseHelper.java"
        );

        assertTrue(databaseHelperSource.contains("SeedDataHelper"));
        assertTrue(databaseHelperSource.contains("damBaoDuLieuMacDinh(SQLiteDatabase db)"));
        assertTrue(databaseHelperSource.contains("SeedDataHelper.damBaoDuLieuMacDinh"));
    }

    @Test
    public void seedDataHelper_containsSeedEntryPointsForAllSampleDomains() throws Exception {
        String seedHelperSource = readText(
                "src/main/java/com/example/quanlynhahang/data/SeedDataHelper.java",
                "app/src/main/java/com/example/quanlynhahang/data/SeedDataHelper.java"
        );

        assertTrue(seedHelperSource.contains("class SeedDataHelper"));
        assertTrue(seedHelperSource.contains("seedDishesIfEmpty"));
        assertTrue(seedHelperSource.contains("ensureTestUserExists"));
        assertTrue(seedHelperSource.contains("damBaoBanAnMau"));
        assertTrue(seedHelperSource.contains("damBaoDonHangMau"));
        assertTrue(seedHelperSource.contains("damBaoDatBanMau"));
        assertTrue(seedHelperSource.contains("damBaoYeuCauPhucVuMau"));
    }

    @Test
    public void seedOnlyHelpers_moveOutOfDatabaseHelper() throws Exception {
        String databaseHelperSource = readText(
                "src/main/java/com/example/quanlynhahang/data/DatabaseHelper.java",
                "app/src/main/java/com/example/quanlynhahang/data/DatabaseHelper.java"
        );
        String seedHelperSource = readText(
                "src/main/java/com/example/quanlynhahang/data/SeedDataHelper.java",
                "app/src/main/java/com/example/quanlynhahang/data/SeedDataHelper.java"
        );

        assertTrue(seedHelperSource.contains("taoBanAnNeuChuaCo"));
        assertTrue(seedHelperSource.contains("taoMonAnNeuChuaCo"));
        assertTrue(seedHelperSource.contains("taoDatBanNeuChuaCo"));
        assertTrue(seedHelperSource.contains("taoDonHangNeuChuaCo"));
        assertTrue(seedHelperSource.contains("taoYeuCauNeuChuaCo"));
        assertTrue(seedHelperSource.contains("timMonTheoTen"));
        assertTrue(seedHelperSource.contains("taoMonTrongDon"));
        assertTrue(seedHelperSource.contains("taoDanhSachMonMau"));
        assertTrue(seedHelperSource.contains("layIdNguoiDungTheoEmail"));
        assertTrue(seedHelperSource.contains("layIdDatBanTheoMa"));
        assertTrue(seedHelperSource.contains("layIdDonHangTheoMa"));
        assertTrue(seedHelperSource.contains("ensureSeedUser"));
        assertTrue(seedHelperSource.contains("daCoYeuCauMau"));

        assertFalse(databaseHelperSource.contains("void taoBanAnNeuChuaCo(SQLiteDatabase db"));
        assertFalse(databaseHelperSource.contains("void taoMonAnNeuChuaCo(SQLiteDatabase db"));
        assertFalse(databaseHelperSource.contains("void taoDatBanNeuChuaCo(SQLiteDatabase db"));
        assertFalse(databaseHelperSource.contains("void taoDonHangNeuChuaCo(SQLiteDatabase db"));
        assertFalse(databaseHelperSource.contains("void taoYeuCauNeuChuaCo(SQLiteDatabase db"));
        assertFalse(databaseHelperSource.contains("DishRecord timMonTheoTen"));
        assertFalse(databaseHelperSource.contains("DonHang.MonTrongDon taoMonTrongDon"));
        assertFalse(databaseHelperSource.contains("List<DonHang.MonTrongDon> taoDanhSachMonMau"));
        assertFalse(databaseHelperSource.contains("long layIdNguoiDungTheoEmail(SQLiteDatabase db"));
        assertFalse(databaseHelperSource.contains("long layIdDatBanTheoMa(SQLiteDatabase db"));
        assertFalse(databaseHelperSource.contains("long layIdDonHangTheoMa(SQLiteDatabase db"));
        assertFalse(databaseHelperSource.contains("void ensureSeedUser(SQLiteDatabase db"));
        assertFalse(databaseHelperSource.contains("boolean daCoYeuCauMau(SQLiteDatabase db"));
        assertFalse(databaseHelperSource.contains("private void damBaoBanAnMau(SQLiteDatabase db)"));
    }

    private String readText(String modulePath, String projectPath) throws Exception {
        return new String(Files.readAllBytes(timFile(modulePath, projectPath).toPath()), StandardCharsets.UTF_8);
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
