package com.example.quanlynhahang;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class FragmentAdapterCleanupTest {

    @Test
    public void datBanFragment_usesDateTimeUtilsInsteadOfLocalSimpleDateFormatField() throws Exception {
        String source = readText(
                "src/main/java/com/example/quanlynhahang/DatBanFragment.java",
                "app/src/main/java/com/example/quanlynhahang/DatBanFragment.java"
        );

        assertTrue(source.contains("DateTimeUtils"));
        assertFalse(source.contains("private static final SimpleDateFormat"));
        assertFalse(source.contains("new SimpleDateFormat(\"dd/MM/yyyy HH:mm\""));
    }

    @Test
    public void donHangAdapter_usesMoneyUtilsForSummaryPriceFormatting() throws Exception {
        String source = readText(
                "src/main/java/com/example/quanlynhahang/adapter/DonHangAdapter.java",
                "app/src/main/java/com/example/quanlynhahang/adapter/DonHangAdapter.java"
        );

        assertTrue(source.contains("MoneyUtils"));
        assertFalse(source.contains("DecimalFormat"));
        assertFalse(source.contains("replaceAll(\"[^0-9]\""));
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
