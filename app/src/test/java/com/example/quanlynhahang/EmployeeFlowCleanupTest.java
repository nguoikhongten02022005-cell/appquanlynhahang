package com.example.quanlynhahang;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class EmployeeFlowCleanupTest {

    @Test
    public void donHangFragment_removesUnusedPositionParameterFromCancelFlow() throws Exception {
        String source = readText(
                "src/main/java/com/example/quanlynhahang/DonHangFragment.java",
                "app/src/main/java/com/example/quanlynhahang/DonHangFragment.java"
        );

        assertTrue(source.contains("private void huyDonHang(DonHang donHang)"));
        assertTrue(source.contains("private void thucHienHuyDon(DonHang donHang)"));
        assertFalse(source.contains("private void huyDonHang(DonHang donHang, int viTri)"));
        assertFalse(source.contains("private void thucHienHuyDon(DonHang donHang, int viTri)"));
    }

    @Test
    public void employeeInternalShell_keepsTableAndOrderFragmentsRefreshingThroughHelpers() throws Exception {
        String donHangNoiBoSource = readText(
                "src/main/java/com/example/quanlynhahang/DonHangNoiBoFragment.java",
                "app/src/main/java/com/example/quanlynhahang/DonHangNoiBoFragment.java"
        );
        String datBanNoiBoSource = readText(
                "src/main/java/com/example/quanlynhahang/DatBanNoiBoFragment.java",
                "app/src/main/java/com/example/quanlynhahang/DatBanNoiBoFragment.java"
        );

        assertTrue(donHangNoiBoSource.contains("taiDanhSachDonHang()"));
        assertTrue(datBanNoiBoSource.contains("taiDanhSachDatBan()"));
        assertTrue(datBanNoiBoSource.contains("hienDialogDoiBan("));
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
