package com.example.quanlynhahang;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

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
        String orderRepositorySource = read("src/main/java/com/example/quanlynhahang/data/OrderRepository.java");

        assertTrue(modelSource.contains("DA_THANH_TOAN"));
        assertTrue(modelSource.contains("THANH_TOAN_NGAY"));
        assertFalse(modelSource.contains("DA_THANH_TOAN_MO_PHONG"));
        assertFalse(modelSource.contains("THANH_TOAN_NGAY_MO_PHONG"));
        assertTrue(databaseSource.contains("\"DA_THANH_TOAN_MO_PHONG\".equals(paymentStatusRaw)"));
        assertTrue(databaseSource.contains("\"THANH_TOAN_NGAY_MO_PHONG\".equals(paymentMethodRaw)"));
        assertTrue(orderRepositorySource.contains("\"DA_THANH_TOAN_MO_PHONG\".equals(paymentStatusRaw)"));
        assertTrue(orderRepositorySource.contains("\"THANH_TOAN_NGAY_MO_PHONG\".equals(paymentMethodRaw)"));
    }

    @Test
    public void viewBindingIsEnabledAndOnlyAllowedFindViewByIdRemains() throws Exception {
        String gradle = read("build.gradle.kts");
        assertTrue(gradle.contains("viewBinding = true"));

        Path sourceRoot = ROOT.resolve("src/main/java/com/example/quanlynhahang");
        try (Stream<Path> files = Files.walk(sourceRoot)) {
            files.filter(path -> path.toString().endsWith(".java"))
                    .forEach(path -> assertAllowedFindViewById(path, readUnchecked(path)));
        }
    }

    @Test
    public void modelsDoNotParseDisplayTimeToLong() throws Exception {
        String orderModel = read("src/main/java/com/example/quanlynhahang/model/DonHang.java");
        String reservationModel = read("src/main/java/com/example/quanlynhahang/model/DatBan.java");

        assertFalse(orderModel.contains("parseTimeToLong"));
        assertFalse(reservationModel.contains("parseTimeToLong"));
    }

    @Test
    public void adminUserDisplayLabelsComeFromResources() throws Exception {
        String adapterSource = read("src/main/java/com/example/quanlynhahang/adapter/NguoiDungQuanTriAdapter.java");

        assertTrue(adapterSource.contains("R.string.admin_user_role_admin_short"));
        assertTrue(adapterSource.contains("R.string.admin_user_role_employee_short"));
        assertTrue(adapterSource.contains("R.string.admin_role_customer"));
        assertTrue(adapterSource.contains("R.string.admin_user_status_active"));
        assertTrue(adapterSource.contains("R.string.admin_user_status_locked"));
        assertFalse(adapterSource.contains("return \"Admin\""));
        assertFalse(adapterSource.contains("return \"Nhân viên\""));
        assertFalse(adapterSource.contains("return \"Quản lý\""));
        assertFalse(adapterSource.contains("? \"Đang hoạt động\" : \"Đã khóa\""));
        assertFalse(adapterSource.contains("String.valueOf(R.string.admin_user_role_admin_short)"));
        assertFalse(adapterSource.contains("String.valueOf(R.string.admin_user_status_active"));
        assertFalse(adapterSource.contains("NguyenChuoiNguoiDung"));
    }

    @Test
    public void adminReportLayoutLabelsComeFromResources() throws Exception {
        String layout = read("src/main/res/layout/fragment_bao_cao_quan_tri.xml");

        assertTrue(layout.contains("@string/admin_revenue_today_label"));
        assertTrue(layout.contains("@string/admin_orders_label"));
        assertTrue(layout.contains("@string/admin_tables_in_use_label"));
        assertTrue(layout.contains("@string/admin_today_metrics_title"));
        assertTrue(layout.contains("@string/admin_revenue_7_days_title"));
        assertTrue(layout.contains("@string/admin_order_status_title"));
        assertTrue(layout.contains("@string/admin_recent_orders_short_title"));
        assertFalse(layout.contains("android:text=\"Doanh thu\""));
        assertFalse(layout.contains("android:text=\"Đơn hàng\""));
        assertFalse(layout.contains("android:text=\"Bàn dùng\""));
        assertFalse(layout.contains("android:text=\"Chỉ số hôm nay\""));
        assertFalse(layout.contains("android:text=\"Doanh thu 7 ngày\""));
        assertFalse(layout.contains("android:text=\"Tình trạng đơn\""));
        assertFalse(layout.contains("android:text=\"Đơn gần đây\""));
    }

    @Test
    public void menuFragmentNavigationSearchBlockStaysInsideMethodScope() throws Exception {
        String source = read("src/main/java/com/example/quanlynhahang/ThucDonFragment.java");

        assertFalse(source.contains("    }\n\n\n            moBanPhimTimKiem();"));
        assertTrue(source.contains("        if (isAdded()) {\n            apDungTuKhoaTimKiemNeuCan();\n            taiDuLieuMonAn();\n            if (moTimKiemKhiMoMan && binding != null) {"));
    }

    @Test
    public void accountFragmentUsesIncludedLayoutBindings() throws Exception {
        String source = read("src/main/java/com/example/quanlynhahang/TaiKhoanFragment.java");

        assertTrue(source.contains("binding.layoutEditProfile.getRoot().setVisibility"));
        assertTrue(source.contains("binding.layoutChangePassword.getRoot().setVisibility"));
        assertTrue(source.contains("binding.layoutEditProfile.etEditName"));
        assertTrue(source.contains("binding.layoutChangePassword.etCurrentPassword"));
        assertFalse(source.contains("binding.etEditName"));
        assertFalse(source.contains("binding.etCurrentPassword"));
        assertFalse(source.contains("binding.btnSaveProfileChanges"));
        assertFalse(source.contains("binding.btnSubmitChangePassword"));
        assertFalse(source.contains("binding.layoutEditProfile.setVisibility"));
        assertFalse(source.contains("binding.layoutChangePassword.setVisibility"));
    }

    @Test
    public void adminReportStatusHelpersRemainSeparateAndTableMetricHasNavigationId() throws Exception {
        String source = read("src/main/java/com/example/quanlynhahang/BaoCaoQuanTriFragment.java");
        String layout = read("src/main/res/layout/fragment_bao_cao_quan_tri.xml");

        assertTrue(source.contains("private String layNhanTrangThai(DonHang donHang)"));
        assertTrue(source.contains("private void ganMauTrangThai(TextView view, DonHang.TrangThai trangThai)"));
        assertFalse(source.contains("private void ganMauTrangThai(TextView view, DonHang.TrangThai trangThai) {\n        if (trangThai == DonHang.TrangThai.CHO_XAC_NHAN) {\n            return getString"));
        assertTrue(layout.contains("android:id=\"@+id/navAdminTables\""));
    }

    @Test
    public void customerOrdersEmbeddedModeDoesNotReferenceMissingFooterBinding() throws Exception {
        String source = read("src/main/java/com/example/quanlynhahang/DonHangFragment.java");

        assertFalse(source.contains("binding.layoutCartFooter"));
        assertTrue(source.contains("binding.btnCheckout.setVisibility(embedded ? View.GONE : View.VISIBLE)"));
    }

    private static void assertAllowedFindViewById(Path path, String source) {
        if (!source.contains("findViewById")) {
            return;
        }

        String relativePath = ROOT.relativize(path).toString().replace('\\', '/');
        boolean allowedActivityNavigationLookup = relativePath.equals("src/main/java/com/example/quanlynhahang/TaiKhoanFragment.java")
                && source.contains("requireActivity().findViewById(R.id.bottomNavigationView)");

        assertTrue("Unexpected findViewById in " + relativePath, allowedActivityNavigationLookup);
    }

    private static String read(String relativePath) throws IOException {
        return new String(Files.readAllBytes(ROOT.resolve(relativePath)), StandardCharsets.UTF_8);
    }

    private static String readUnchecked(Path path) {
        try {
            return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new AssertionError("Unable to read " + path, e);
        }
    }
}
