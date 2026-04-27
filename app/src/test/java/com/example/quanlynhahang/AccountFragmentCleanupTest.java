package com.example.quanlynhahang;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class AccountFragmentCleanupTest {

    @Test
    public void taiKhoanFragment_consolidatesPreviewAndNormalUserLoadingHelpers() throws Exception {
        String source = readText(
                "src/main/java/com/example/quanlynhahang/TaiKhoanFragment.java",
                "app/src/main/java/com/example/quanlynhahang/TaiKhoanFragment.java"
        );

        assertTrue(source.contains("private boolean coCheDoPreviewKhach()"));
        assertTrue(source.contains("private NguoiDung taiNguoiDungHienTaiHopLe("));
        assertTrue(source.contains("private boolean capNhatGiaoDienChoNguoiDungHienTai("));
        assertFalse(source.contains("private boolean phienKhachHangPreviewHopLe(boolean hienToast)"));
    }

    @Test
    public void trungTamHoatDongFragment_keepsSummaryHelpersExplicit() throws Exception {
        String source = readText(
                "src/main/java/com/example/quanlynhahang/TrungTamHoatDongFragment.java",
                "app/src/main/java/com/example/quanlynhahang/TrungTamHoatDongFragment.java"
        );

        assertTrue(source.contains("capNhatTomTatDichVu()"));
        assertTrue(source.contains("capNhatDongTomTat("));
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
