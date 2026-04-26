package com.example.quanlynhahang;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class SqliteDataSourceSpecTest {

    private static final Path ROOT = Path.of(System.getProperty("user.dir"));

    @Test
    public void seedDataHelper_doesNotContainBusinessSeedRecordsDirectly() throws Exception {
        String source = read("src/main/java/com/example/quanlynhahang/data/SeedDataHelper.java");

        assertFalse(source.contains("Cơm chiên hải sản"));
        assertFalse(source.contains("quanghuy@nhahang.local"));
        assertFalse(source.contains("#DH10001"));
        assertFalse(source.contains("#GB10001"));
        assertFalse(source.contains("Khách cần thêm chén"));
        assertFalse(source.contains("\"B01\""));
    }

    @Test
    public void seedDataAsset_containsBusinessSeedRecords() throws Exception {
        String json = read("src/main/assets/seed_data.json");

        assertTrue(json.contains("Cơm chiên hải sản"));
        assertTrue(json.contains("quanghuy@nhahang.local"));
        assertTrue(json.contains("#DH10001"));
        assertTrue(json.contains("#GB10001"));
        assertTrue(json.contains("Khách cần thêm chén"));
        assertTrue(json.contains("\"tables\""));
    }

    @Test
    public void tableSelectors_doNotGenerateOneToTwentyTables() throws Exception {
        String reservation = read("src/main/java/com/example/quanlynhahang/DatBanFragment.java");
        String internalReservation = read("src/main/java/com/example/quanlynhahang/DatBanNoiBoFragment.java");
        String cart = read("src/main/java/com/example/quanlynhahang/GioHangActivity.java");

        assertFalse(reservation.contains("SO_BAN_TOI_DA"));
        assertFalse(reservation.contains("for (int soBan = 1"));
        assertFalse(internalReservation.contains("for (int soBan = 1; soBan <= 20"));
        assertFalse(cart.contains("SO_BAN_TOI_DA"));
        assertFalse(cart.contains("for (int index = 0; index <"));
    }

    @Test
    public void homeCategories_areNotHardcodedInFragment() throws Exception {
        String source = read("src/main/java/com/example/quanlynhahang/TrangChuFragment.java");

        assertFalse(source.contains("danhSachDanhMuc.add(new DanhMucMon"));
        assertTrue(source.contains("layDanhMucMonAn"));
    }

    @Test
    public void quickLoginCredentials_areResolvedFromDatabase() throws Exception {
        String source = read("src/main/java/com/example/quanlynhahang/DangNhapActivity.java");

        assertFalse(source.contains("TAI_KHOAN_KHACH_HANG_MAC_DINH"));
        assertFalse(source.contains("TAI_KHOAN_NHAN_VIEN_MAC_DINH"));
        assertFalse(source.contains("TAI_KHOAN_ADMIN_MAC_DINH"));
        assertFalse(source.contains("MAT_KHAU_MAC_DINH"));
        assertTrue(source.contains("layTaiKhoanDemoTheoVaiTro"));
    }

    private static String read(String relativePath) throws IOException {
        return Files.readString(ROOT.resolve(relativePath), StandardCharsets.UTF_8);
    }
}
