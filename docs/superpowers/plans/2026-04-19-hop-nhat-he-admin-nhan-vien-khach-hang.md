# Hợp nhất hệ admin, nhân viên, khách hàng Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Xây internal shell chung cho admin và nhân viên, tách admin shell con, cô lập customer preview của admin bằng session khách hàng riêng, và chuyển `NhanVienActivity` / `QuanTriActivity` sang legacy redirect an toàn.

**Architecture:** Refactor theo lớp: khóa route contract và feature flag trước, sau đó tách session/cart theo namespace, rồi dựng internal shell mới dựa trên logic vận hành hiện có của `NhanVienActivity`. Khi internal shell ổn định, tách admin shell từ `QuanTriActivity`, cuối cùng mới bật customer preview cô lập và đổi toàn bộ launcher/helper sang route mới để vẫn giữ rollback path qua activity cũ.

**Tech Stack:** Java 11, Android Views + Fragments, RecyclerView, SharedPreferences, JUnit4, AndroidJUnit4, Espresso, ActivityScenario, Espresso Intents.

---

## File structure map

### Route and feature flag
- Create: `app/src/main/java/com/example/quanlynhahang/helper/DieuHuongNoiBoHelper.java` — contract route mới, normalize tab/section, build intent cho internal shell, admin shell, customer preview, và return route.
- Create: `app/src/main/java/com/example/quanlynhahang/helper/CauHinhTinhNangHelper.java` — kill switch cho internal shell mới.

### Session and customer scope
- Modify: `app/src/main/java/com/example/quanlynhahang/data/SessionManager.java` — tách internal/customer namespace, route bookkeeping, preview source route.
- Modify: `app/src/main/java/com/example/quanlynhahang/data/QuanLyGioHang.java` — keyed instance theo customer scope, bỏ phụ thuộc singleton toàn cục.

### Internal shell
- Create: `app/src/main/java/com/example/quanlynhahang/TrungTamNoiBoActivity.java`
- Create: `app/src/main/java/com/example/quanlynhahang/TongQuanNoiBoFragment.java`
- Create: `app/src/main/java/com/example/quanlynhahang/DonHangNoiBoFragment.java`
- Create: `app/src/main/java/com/example/quanlynhahang/DatBanNoiBoFragment.java`
- Create: `app/src/main/java/com/example/quanlynhahang/YeuCauNoiBoFragment.java`
- Create: `app/src/main/res/layout/activity_trung_tam_noi_bo.xml`
- Create: `app/src/main/res/layout/fragment_tong_quan_noi_bo.xml`
- Create: `app/src/main/res/layout/fragment_don_hang_noi_bo.xml`
- Create: `app/src/main/res/layout/fragment_dat_ban_noi_bo.xml`
- Create: `app/src/main/res/layout/fragment_yeu_cau_noi_bo.xml`

### Admin shell
- Create: `app/src/main/java/com/example/quanlynhahang/TrungTamQuanTriActivity.java`
- Create: `app/src/main/java/com/example/quanlynhahang/BaoCaoQuanTriFragment.java`
- Create: `app/src/main/java/com/example/quanlynhahang/CaiDatQuanTriFragment.java`
- Create: `app/src/main/java/com/example/quanlynhahang/MonAnQuanTriFragment.java`
- Create: `app/src/main/java/com/example/quanlynhahang/NguoiDungQuanTriFragment.java`
- Create: `app/src/main/res/layout/activity_trung_tam_quan_tri.xml`
- Create: `app/src/main/res/layout/fragment_bao_cao_quan_tri.xml`
- Create: `app/src/main/res/layout/fragment_cai_dat_quan_tri.xml`
- Create: `app/src/main/res/layout/fragment_mon_an_quan_tri.xml`
- Create: `app/src/main/res/layout/fragment_nguoi_dung_quan_tri.xml`

### Preview and legacy bridge
- Modify: `app/src/main/java/com/example/quanlynhahang/CustomerLauncherActivity.java`
- Modify: `app/src/main/java/com/example/quanlynhahang/MainActivity.java`
- Modify: `app/src/main/java/com/example/quanlynhahang/DangNhapActivity.java`
- Modify: `app/src/main/java/com/example/quanlynhahang/NhanVienActivity.java`
- Modify: `app/src/main/java/com/example/quanlynhahang/QuanTriActivity.java`
- Modify: `app/src/main/java/com/example/quanlynhahang/StaffLauncherActivity.java`
- Modify: `app/src/main/java/com/example/quanlynhahang/helper/DieuHuongVaiTroHelper.java`
- Modify: `app/src/main/res/layout/activity_main.xml`
- Modify: `app/src/main/AndroidManifest.xml`
- Modify: `app/src/main/res/values/strings.xml`
- Modify: `gradle/libs.versions.toml`
- Modify: `app/build.gradle.kts`

### Tests
- Create: `app/src/test/java/com/example/quanlynhahang/DieuHuongNoiBoHelperTest.java`
- Create: `app/src/androidTest/java/com/example/quanlynhahang/SessionManagerNamespaceInstrumentedTest.java`
- Modify: `app/src/test/java/com/example/quanlynhahang/QuanLyGioHangTest.java`
- Create: `app/src/androidTest/java/com/example/quanlynhahang/TrungTamNoiBoOverviewInstrumentedTest.java`
- Create: `app/src/androidTest/java/com/example/quanlynhahang/TrungTamNoiBoOperationsInstrumentedTest.java`
- Create: `app/src/androidTest/java/com/example/quanlynhahang/DieuHuongVaiTroNoiBoInstrumentedTest.java`
- Create: `app/src/androidTest/java/com/example/quanlynhahang/TrungTamQuanTriInstrumentedTest.java`
- Create: `app/src/androidTest/java/com/example/quanlynhahang/KhachHangPreviewInstrumentedTest.java`
- Create: `app/src/androidTest/java/com/example/quanlynhahang/LegacyBridgeRegressionInstrumentedTest.java`

## Scope note

Plan này vẫn là **một plan duy nhất** vì internal shell, admin shell, session split, scoped cart, preview route, và legacy redirect đều cùng phụ thuộc vào một route/session layer. Nếu tách thành nhiều plan nhỏ ở đây sẽ làm tăng độ lệch contract giữa các bước.

---

### Task 1: Khóa route contract và feature flag

**Files:**
- Create: `app/src/main/java/com/example/quanlynhahang/helper/DieuHuongNoiBoHelper.java`
- Create: `app/src/main/java/com/example/quanlynhahang/helper/CauHinhTinhNangHelper.java`
- Test: `app/src/test/java/com/example/quanlynhahang/DieuHuongNoiBoHelperTest.java`

- [ ] **Step 1: Write the failing route-contract test**

```java
package com.example.quanlynhahang;

import static org.junit.Assert.assertEquals;

import com.example.quanlynhahang.helper.DieuHuongNoiBoHelper;

import org.junit.Test;

public class DieuHuongNoiBoHelperTest {

    @Test
    public void chuanHoaTab_traVeGiaTriMacDinhKhiNullHoacLaTabLa() {
        assertEquals(DieuHuongNoiBoHelper.TAB_TONG_QUAN, DieuHuongNoiBoHelper.chuanHoaTab(null));
        assertEquals(DieuHuongNoiBoHelper.TAB_TONG_QUAN, DieuHuongNoiBoHelper.chuanHoaTab("khong_hop_le"));
    }

    @Test
    public void mapTabNhanVienCu_giuDungContractLegacy() {
        assertEquals(DieuHuongNoiBoHelper.TAB_DON_HANG, DieuHuongNoiBoHelper.mapTabNhanVienCu("orders"));
        assertEquals(DieuHuongNoiBoHelper.TAB_DAT_BAN, DieuHuongNoiBoHelper.mapTabNhanVienCu("reservations"));
        assertEquals(DieuHuongNoiBoHelper.TAB_YEU_CAU, DieuHuongNoiBoHelper.mapTabNhanVienCu("service_requests"));
        assertEquals(DieuHuongNoiBoHelper.TAB_DON_HANG, DieuHuongNoiBoHelper.mapTabNhanVienCu(""));
    }

    @Test
    public void taoRouteNoiBo_vaTaoRouteQuanTri_dungDungMauSpec() {
        assertEquals("internal:overview", DieuHuongNoiBoHelper.taoRouteNoiBo(DieuHuongNoiBoHelper.TAB_TONG_QUAN));
        assertEquals("admin:users", DieuHuongNoiBoHelper.taoRouteQuanTri(DieuHuongNoiBoHelper.SECTION_NGUOI_DUNG));
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew testDebugUnitTest --tests "com.example.quanlynhahang.DieuHuongNoiBoHelperTest"`
Expected: FAIL with `cannot find symbol DieuHuongNoiBoHelper`

- [ ] **Step 3: Write the minimal route helper and feature flag**

```java
package com.example.quanlynhahang.helper;

import androidx.annotation.Nullable;

public final class DieuHuongNoiBoHelper {

    public static final String TAB_TONG_QUAN = "overview";
    public static final String TAB_DON_HANG = "orders";
    public static final String TAB_DAT_BAN = "reservations";
    public static final String TAB_YEU_CAU = "service_requests";

    public static final String SECTION_MON_AN = "dishes";
    public static final String SECTION_NGUOI_DUNG = "users";
    public static final String SECTION_BAO_CAO = "reports";
    public static final String SECTION_CAI_DAT = "settings";

    public static final String EXTRA_TAB_NOI_BO = "extra_internal_tab";
    public static final String EXTRA_SECTION_QUAN_TRI = "extra_admin_section";
    public static final String EXTRA_ROUTE_TRA_VE_NOI_BO = "extra_return_internal_route";
    public static final String EXTRA_CHE_DO_PREVIEW_KHACH = "extra_customer_preview_mode";

    private DieuHuongNoiBoHelper() {
    }

    public static String chuanHoaTab(@Nullable String tab) {
        if (TAB_DON_HANG.equals(tab) || TAB_DAT_BAN.equals(tab) || TAB_YEU_CAU.equals(tab)) {
            return tab;
        }
        return TAB_TONG_QUAN;
    }

    public static String chuanHoaSection(@Nullable String section) {
        if (SECTION_MON_AN.equals(section) || SECTION_NGUOI_DUNG.equals(section)
                || SECTION_BAO_CAO.equals(section) || SECTION_CAI_DAT.equals(section)) {
            return section;
        }
        return SECTION_BAO_CAO;
    }

    public static String mapTabNhanVienCu(@Nullable String legacyTab) {
        if (TAB_DAT_BAN.equals(legacyTab)) {
            return TAB_DAT_BAN;
        }
        if (TAB_YEU_CAU.equals(legacyTab)) {
            return TAB_YEU_CAU;
        }
        return TAB_DON_HANG;
    }

    public static String taoRouteNoiBo(String tab) {
        return "internal:" + chuanHoaTab(tab);
    }

    public static String taoRouteQuanTri(String section) {
        return "admin:" + chuanHoaSection(section);
    }
}
```

```java
package com.example.quanlynhahang.helper;

public final class CauHinhTinhNangHelper {

    private static final boolean BAT_TRUNG_TAM_NOI_BO_MOI = true;

    private CauHinhTinhNangHelper() {
    }

    public static boolean batTrungTamNoiBoMoi() {
        return BAT_TRUNG_TAM_NOI_BO_MOI;
    }
}
```

- [ ] **Step 4: Run the route test again**

Run: `./gradlew testDebugUnitTest --tests "com.example.quanlynhahang.DieuHuongNoiBoHelperTest"`
Expected: PASS with `3 tests completed, 0 failed`

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/example/quanlynhahang/helper/DieuHuongNoiBoHelper.java app/src/main/java/com/example/quanlynhahang/helper/CauHinhTinhNangHelper.java app/src/test/java/com/example/quanlynhahang/DieuHuongNoiBoHelperTest.java
git commit -m "feat: lock internal route contract"
```

### Task 2: Tách internal session và customer session

**Files:**
- Modify: `app/src/main/java/com/example/quanlynhahang/data/SessionManager.java`
- Test: `app/src/androidTest/java/com/example/quanlynhahang/SessionManagerNamespaceInstrumentedTest.java`

- [ ] **Step 1: Write the failing instrumentation test for namespace ownership**

```java
package com.example.quanlynhahang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.model.VaiTroNguoiDung;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SessionManagerNamespaceInstrumentedTest {

    private SessionManager sessionManager;

    @Before
    public void setUp() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        sessionManager = new SessionManager(appContext);
        sessionManager.xoaPhienNoiBo();
        sessionManager.xoaPhienKhachHang();
    }

    @Test
    public void luuPhienNoiBo_khongGhiDePhienKhach() {
        sessionManager.luuPhienKhachHang(11L);
        sessionManager.luuPhienNoiBo(21L, VaiTroNguoiDung.ADMIN);

        assertTrue(sessionManager.daDangNhapKhachHang());
        assertTrue(sessionManager.daDangNhapNoiBo());
        assertEquals(11L, sessionManager.layIdKhachHangHienTai());
        assertEquals(21L, sessionManager.layIdNguoiDungNoiBo());
        assertEquals(VaiTroNguoiDung.ADMIN, sessionManager.layVaiTroNoiBoHopLe());
    }

    @Test
    public void luuNguonPreview_giuRouteNoiBoTachKhoiCustomerRoute() {
        sessionManager.luuDuongDanNoiBoCuoi("internal:orders");
        sessionManager.luuNguonPreviewKhachHang("internal:orders");
        sessionManager.luuDuongDanKhachHangCuoi("customer:home");

        assertEquals("internal:orders", sessionManager.layDuongDanNoiBoCuoi());
        assertEquals("internal:orders", sessionManager.layNguonPreviewKhachHang());
        assertEquals("customer:home", sessionManager.layDuongDanKhachHangCuoi());
    }
}
```

- [ ] **Step 2: Run the instrumentation test to verify it fails**

Run: `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.quanlynhahang.SessionManagerNamespaceInstrumentedTest`
Expected: FAIL with `cannot find symbol method luuPhienNoiBo` and related missing methods

- [ ] **Step 3: Implement namespaced session APIs and backward-compatible bridge methods**

```java
private static final String PREFS_INTERNAL = "auth_internal_prefs";
private static final String PREFS_CUSTOMER = "auth_customer_prefs";

private static final String KEY_IS_LOGGED_IN = "is_logged_in";
private static final String KEY_USER_ID = "current_user_id";
private static final String KEY_USER_ROLE = "current_user_role";
private static final String KEY_CURRENT_TABLE = "current_table";
private static final String KEY_LAST_ROUTE = "last_route";
private static final String KEY_PREVIEW_SOURCE_ROUTE = "preview_source_route";

private final SharedPreferences boNhoNoiBo;
private final SharedPreferences boNhoKhachHang;

public SessionManager(Context context) {
    ungDungContext = context.getApplicationContext();
    boNhoNoiBo = ungDungContext.getSharedPreferences(PREFS_INTERNAL, Context.MODE_PRIVATE);
    boNhoKhachHang = ungDungContext.getSharedPreferences(PREFS_CUSTOMER, Context.MODE_PRIVATE);
}

public void luuPhienNoiBo(long idNguoiDung, VaiTroNguoiDung vaiTro) {
    boNhoNoiBo.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putLong(KEY_USER_ID, idNguoiDung)
            .putString(KEY_USER_ROLE, vaiTro == null ? VaiTroNguoiDung.NHAN_VIEN.name() : vaiTro.name())
            .apply();
}

public void luuPhienKhachHang(long idNguoiDung) {
    boNhoKhachHang.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putLong(KEY_USER_ID, idNguoiDung)
            .apply();
}

public boolean daDangNhapNoiBo() {
    return boNhoNoiBo.getBoolean(KEY_IS_LOGGED_IN, false) && layIdNguoiDungNoiBo() > 0;
}

public boolean daDangNhapKhachHang() {
    return boNhoKhachHang.getBoolean(KEY_IS_LOGGED_IN, false) && layIdKhachHangHienTai() > 0;
}

public long layIdNguoiDungNoiBo() {
    return boNhoNoiBo.getLong(KEY_USER_ID, -1);
}

public long layIdKhachHangHienTai() {
    return boNhoKhachHang.getLong(KEY_USER_ID, -1);
}

@Nullable
public VaiTroNguoiDung layVaiTroNoiBoHopLe() {
    return VaiTroNguoiDung.tuChuoiNghiemNhat(boNhoNoiBo.getString(KEY_USER_ROLE, null));
}

public void luuDuongDanNoiBoCuoi(@Nullable String route) {
    boNhoNoiBo.edit().putString(KEY_LAST_ROUTE, route == null ? "" : route.trim()).apply();
}

public void luuDuongDanKhachHangCuoi(@Nullable String route) {
    boNhoKhachHang.edit().putString(KEY_LAST_ROUTE, route == null ? "" : route.trim()).apply();
}

public String layDuongDanNoiBoCuoi() {
    return boNhoNoiBo.getString(KEY_LAST_ROUTE, "");
}

public String layDuongDanKhachHangCuoi() {
    return boNhoKhachHang.getString(KEY_LAST_ROUTE, "");
}

public void luuNguonPreviewKhachHang(@Nullable String route) {
    boNhoKhachHang.edit().putString(KEY_PREVIEW_SOURCE_ROUTE, route == null ? "" : route.trim()).apply();
}

public String layNguonPreviewKhachHang() {
    return boNhoKhachHang.getString(KEY_PREVIEW_SOURCE_ROUTE, "");
}

public String layKhoaPhienKhachHang() {
    return daDangNhapKhachHang() ? "customer:" + layIdKhachHangHienTai() : "customer:guest";
}

public void xoaPhienNoiBo() {
    boNhoNoiBo.edit().clear().apply();
}

public void xoaPhienKhachHang() {
    boNhoKhachHang.edit().clear().apply();
}

public boolean daDangNhap() {
    return daDangNhapNoiBo() || daDangNhapKhachHang();
}

public long layIdNguoiDungHienTai() {
    return daDangNhapNoiBo() ? layIdNguoiDungNoiBo() : layIdKhachHangHienTai();
}

@Nullable
public VaiTroNguoiDung layVaiTroSessionHopLe() {
    if (daDangNhapNoiBo()) {
        return layVaiTroNoiBoHopLe();
    }
    if (daDangNhapKhachHang()) {
        return VaiTroNguoiDung.KHACH_HANG;
    }
    return null;
}

public void luuPhienDangNhap(long idNguoiDung, VaiTroNguoiDung vaiTro) {
    if (vaiTro == VaiTroNguoiDung.ADMIN || vaiTro == VaiTroNguoiDung.NHAN_VIEN) {
        luuPhienNoiBo(idNguoiDung, vaiTro);
    } else {
        luuPhienKhachHang(idNguoiDung);
    }
}
```

- [ ] **Step 4: Run the instrumentation test again**

Run: `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.quanlynhahang.SessionManagerNamespaceInstrumentedTest`
Expected: PASS with `2 tests successful`

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/example/quanlynhahang/data/SessionManager.java app/src/androidTest/java/com/example/quanlynhahang/SessionManagerNamespaceInstrumentedTest.java
git commit -m "feat: split internal and customer sessions"
```

### Task 3: Scope giỏ hàng theo customer session và migrate toàn bộ caller phía khách

**Files:**
- Modify: `app/src/main/java/com/example/quanlynhahang/data/QuanLyGioHang.java`
- Modify: `app/src/main/java/com/example/quanlynhahang/MainActivity.java`
- Modify: `app/src/main/java/com/example/quanlynhahang/TrangChuFragment.java`
- Modify: `app/src/main/java/com/example/quanlynhahang/ThucDonFragment.java`
- Modify: `app/src/main/java/com/example/quanlynhahang/GioHangActivity.java`
- Modify: `app/src/main/java/com/example/quanlynhahang/XacNhanDonHangActivity.java`
- Modify: `app/src/main/java/com/example/quanlynhahang/TrungTamHoatDongFragment.java`
- Modify: `app/src/main/java/com/example/quanlynhahang/YeuCauFragment.java`
- Test: `app/src/test/java/com/example/quanlynhahang/QuanLyGioHangTest.java`

- [ ] **Step 1: Add the failing scoped-cart unit test**

```java
@Test
public void scopedInstances_doNotShareItemsOrContext() {
    QuanLyGioHang gioKhachA = QuanLyGioHang.layInstance("customer:11");
    QuanLyGioHang gioKhachB = QuanLyGioHang.layInstance("customer:22");
    gioKhachA.xoaToanBoGio();
    gioKhachB.xoaToanBoGio();

    gioKhachA.themVaoGio(new MonAnDeXuat(1, "Bò lúc lắc", "145.000 đ", true, "Món chính", 10));
    gioKhachA.capNhatNguCanhDonHang(DonHang.HinhThucDon.AN_TAI_QUAN, "Bàn 03", "Ít cay");

    assertEquals(1, gioKhachA.layTongSoLuong());
    assertEquals(0, gioKhachB.layTongSoLuong());
    assertEquals("Bàn 03", gioKhachA.layNguCanhDonHang().laySoBan());
    assertEquals("", gioKhachB.layNguCanhDonHang().laySoBan());
}
```

- [ ] **Step 2: Run the unit test to verify it fails**

Run: `./gradlew testDebugUnitTest --tests "com.example.quanlynhahang.QuanLyGioHangTest.scopedInstances_doNotShareItemsOrContext"`
Expected: FAIL with `method layInstance in class QuanLyGioHang cannot be applied to given types`

- [ ] **Step 3: Implement keyed cart instances and migrate customer callers to explicit scope**

```java
private static final Map<String, QuanLyGioHang> instances = new LinkedHashMap<>();

public static synchronized QuanLyGioHang layInstance(String phamVi) {
    String khoa = (phamVi == null || phamVi.trim().isEmpty()) ? "customer:guest" : phamVi.trim();
    QuanLyGioHang gioHang = instances.get(khoa);
    if (gioHang == null) {
        gioHang = new QuanLyGioHang();
        instances.put(khoa, gioHang);
    }
    return gioHang;
}
```

```java
// MainActivity.java
private QuanLyGioHang layGioKhachHang() {
    return QuanLyGioHang.layInstance(sessionManager.layKhoaPhienKhachHang());
}

@Override
protected void onStart() {
    super.onStart();
    layGioKhachHang().themLangNghe(cartListener);
    lamMoiTrangThaiHeader();
}

@Override
protected void onStop() {
    layGioKhachHang().xoaLangNghe(cartListener);
    super.onStop();
}
```

```java
// TrangChuFragment.java và ThucDonFragment.java
private QuanLyGioHang layGioKhachHang() {
    return QuanLyGioHang.layInstance(new SessionManager(requireContext()).layKhoaPhienKhachHang());
}

layGioKhachHang().themVaoGio(item);
```

```java
// GioHangActivity.java và XacNhanDonHangActivity.java
private QuanLyGioHang layGioKhachHang() {
    return QuanLyGioHang.layInstance(sessionManager.layKhoaPhienKhachHang());
}
```

```java
// TrungTamHoatDongFragment.java và YeuCauFragment.java
private QuanLyGioHang layGioKhachHang() {
    return QuanLyGioHang.layInstance(sessionManager.layKhoaPhienKhachHang());
}
```

- [ ] **Step 4: Run the cart unit tests again**

Run: `./gradlew testDebugUnitTest --tests "com.example.quanlynhahang.QuanLyGioHangTest"`
Expected: PASS with `all QuanLyGioHangTest tests successful`

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/example/quanlynhahang/data/QuanLyGioHang.java app/src/main/java/com/example/quanlynhahang/MainActivity.java app/src/main/java/com/example/quanlynhahang/TrangChuFragment.java app/src/main/java/com/example/quanlynhahang/ThucDonFragment.java app/src/main/java/com/example/quanlynhahang/GioHangActivity.java app/src/main/java/com/example/quanlynhahang/XacNhanDonHangActivity.java app/src/main/java/com/example/quanlynhahang/TrungTamHoatDongFragment.java app/src/main/java/com/example/quanlynhahang/YeuCauFragment.java app/src/test/java/com/example/quanlynhahang/QuanLyGioHangTest.java
git commit -m "feat: scope customer cart by session"
```

### Task 4: Dựng internal shell mới với tab Tổng quan

**Files:**
- Modify: `gradle/libs.versions.toml`
- Modify: `app/build.gradle.kts`
- Modify: `app/src/main/AndroidManifest.xml`
- Modify: `app/src/main/res/values/strings.xml`
- Create: `app/src/main/java/com/example/quanlynhahang/TrungTamNoiBoActivity.java`
- Create: `app/src/main/java/com/example/quanlynhahang/TongQuanNoiBoFragment.java`
- Create: `app/src/main/res/layout/activity_trung_tam_noi_bo.xml`
- Create: `app/src/main/res/layout/fragment_tong_quan_noi_bo.xml`
- Test: `app/src/androidTest/java/com/example/quanlynhahang/TrungTamNoiBoOverviewInstrumentedTest.java`

- [ ] **Step 1: Add the failing instrumentation test and Android test dependencies**

```java
package com.example.quanlynhahang;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.helper.DieuHuongNoiBoHelper;
import com.example.quanlynhahang.model.VaiTroNguoiDung;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TrungTamNoiBoOverviewInstrumentedTest {

    private Context appContext;

    @Before
    public void setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SessionManager sessionManager = new SessionManager(appContext);
        sessionManager.xoaPhienNoiBo();
        sessionManager.luuPhienNoiBo(2L, VaiTroNguoiDung.NHAN_VIEN);
    }

    @Test
    public void moInternalShellMacDinh_hienTongQuan() {
        try (ActivityScenario<TrungTamNoiBoActivity> ignored = ActivityScenario.launch(
                TrungTamNoiBoActivity.taoIntent(appContext, DieuHuongNoiBoHelper.TAB_TONG_QUAN))) {
            onView(withText(R.string.employee_overview_title)).check(matches(isDisplayed()));
        }
    }
}
```

```toml
# gradle/libs.versions.toml
androidxTestCore = "1.7.0"
espressoIntents = "3.7.0"
androidx-test-core = { group = "androidx.test", name = "core", version.ref = "androidxTestCore" }
espresso-intents = { group = "androidx.test.espresso", name = "espresso-intents", version.ref = "espressoIntents" }
```

```kotlin
// app/build.gradle.kts
androidTestImplementation(libs.androidx.test.core)
androidTestImplementation(libs.espresso.intents)
```

- [ ] **Step 2: Run the instrumentation test to verify it fails**

Run: `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.quanlynhahang.TrungTamNoiBoOverviewInstrumentedTest`
Expected: FAIL with `ClassNotFoundException: com.example.quanlynhahang.TrungTamNoiBoActivity`

- [ ] **Step 3: Create the internal shell activity, overview fragment, layout, and manifest entry**

```java
package com.example.quanlynhahang;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.helper.DieuHuongNoiBoHelper;
import com.example.quanlynhahang.model.VaiTroNguoiDung;

public class TrungTamNoiBoActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;

    public static Intent taoIntent(Context context, String tab) {
        Intent intent = new Intent(context, TrungTamNoiBoActivity.class);
        intent.putExtra(DieuHuongNoiBoHelper.EXTRA_TAB_NOI_BO, DieuHuongNoiBoHelper.chuanHoaTab(tab));
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trung_tam_noi_bo);
        sessionManager = new SessionManager(this);
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.chuanBiCoSoDuLieu();

        if (!sessionManager.daDangNhapNoiBo()) {
            finish();
            return;
        }

        if (savedInstanceState == null) {
            hienFragment(new TongQuanNoiBoFragment(), "tong_quan_noi_bo");
            sessionManager.luuDuongDanNoiBoCuoi(DieuHuongNoiBoHelper.taoRouteNoiBo(DieuHuongNoiBoHelper.TAB_TONG_QUAN));
        }
    }

    private void hienFragment(Fragment fragment, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.noiBoFragmentContainer, fragment, tag)
                .commit();
    }

    public boolean laAdmin() {
        return sessionManager.layVaiTroNoiBoHopLe() == VaiTroNguoiDung.ADMIN;
    }
}
```

```java
package com.example.quanlynhahang;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.model.ThongKeTongQuanNhanVien;

public class TongQuanNoiBoFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tong_quan_noi_bo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DatabaseHelper databaseHelper = new DatabaseHelper(requireContext());
        ThongKeTongQuanNhanVien thongKe = databaseHelper.layThongKeTongQuanNhanVien();
        ((TextView) view.findViewById(R.id.tvNoiBoPendingOrders)).setText(String.valueOf(thongKe.getPendingDonHangs()));
        ((TextView) view.findViewById(R.id.tvNoiBoPendingReservations)).setText(String.valueOf(thongKe.getPendingReservations()));
        ((TextView) view.findViewById(R.id.tvNoiBoProcessingRequests)).setText(String.valueOf(thongKe.getProcessingServiceRequests()));
    }
}
```

```xml
<!-- activity_trung_tam_noi_bo.xml -->
<androidx.drawerlayout.widget.DrawerLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/drawerNoiBoRoot"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">

        <TextView
            android:id="@+id/tvNoiBoHeaderTitle"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:padding="20dp"
            android:text="@string/internal_shell_title"
            android:textSize="18sp"
            android:textStyle="bold" />

        <FrameLayout
            android:id="@+id/noiBoFragmentContainer"
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:layout_weight="1" />
    </LinearLayout>
</androidx.drawerlayout.widget.DrawerLayout>
```

```xml
<!-- fragment_tong_quan_noi_bo.xml -->
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="20dp">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical">

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/employee_overview_title"
            android:textSize="22sp"
            android:textStyle="bold" />

        <TextView
            android:id="@+id/tvNoiBoPendingOrders"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="16dp"
            android:text="0"
            android:textSize="24sp"
            android:textStyle="bold" />

        <TextView
            android:id="@+id/tvNoiBoPendingReservations"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="12dp"
            android:text="0" />

        <TextView
            android:id="@+id/tvNoiBoProcessingRequests"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="12dp"
            android:text="0" />
    </LinearLayout>
</ScrollView>
```

```xml
<!-- AndroidManifest.xml -->
<activity android:name=".TrungTamNoiBoActivity" android:exported="false" />
```

```xml
<!-- strings.xml -->
<string name="internal_shell_title">Trung tâm nội bộ</string>
<string name="internal_menu_overview">Tổng quan</string>
<string name="internal_menu_orders">Đơn hàng</string>
<string name="internal_menu_reservations">Đặt bàn</string>
<string name="internal_menu_service_requests">Yêu cầu phục vụ</string>
<string name="internal_menu_admin_section">Quản trị</string>
<string name="internal_menu_customer_preview">Xem giao diện khách hàng</string>
```

- [ ] **Step 4: Run the overview instrumentation test again**

Run: `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.quanlynhahang.TrungTamNoiBoOverviewInstrumentedTest`
Expected: PASS with `1 test successful`

- [ ] **Step 5: Commit**

```bash
git add gradle/libs.versions.toml app/build.gradle.kts app/src/main/AndroidManifest.xml app/src/main/res/values/strings.xml app/src/main/java/com/example/quanlynhahang/TrungTamNoiBoActivity.java app/src/main/java/com/example/quanlynhahang/TongQuanNoiBoFragment.java app/src/main/res/layout/activity_trung_tam_noi_bo.xml app/src/main/res/layout/fragment_tong_quan_noi_bo.xml app/src/androidTest/java/com/example/quanlynhahang/TrungTamNoiBoOverviewInstrumentedTest.java
git commit -m "feat: scaffold internal shell overview"
```

### Task 5: Hoàn thiện 3 khu vận hành trong internal shell

**Files:**
- Create: `app/src/main/java/com/example/quanlynhahang/DonHangNoiBoFragment.java`
- Create: `app/src/main/java/com/example/quanlynhahang/DatBanNoiBoFragment.java`
- Create: `app/src/main/java/com/example/quanlynhahang/YeuCauNoiBoFragment.java`
- Create: `app/src/main/res/layout/fragment_don_hang_noi_bo.xml`
- Create: `app/src/main/res/layout/fragment_dat_ban_noi_bo.xml`
- Create: `app/src/main/res/layout/fragment_yeu_cau_noi_bo.xml`
- Modify: `app/src/main/java/com/example/quanlynhahang/TrungTamNoiBoActivity.java`
- Test: `app/src/androidTest/java/com/example/quanlynhahang/TrungTamNoiBoOperationsInstrumentedTest.java`

- [ ] **Step 1: Write the failing instrumentation test for tab routing**

```java
package com.example.quanlynhahang;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.helper.DieuHuongNoiBoHelper;
import com.example.quanlynhahang.model.VaiTroNguoiDung;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TrungTamNoiBoOperationsInstrumentedTest {

    private Context appContext;

    @Before
    public void setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SessionManager sessionManager = new SessionManager(appContext);
        sessionManager.xoaPhienNoiBo();
        sessionManager.luuPhienNoiBo(2L, VaiTroNguoiDung.NHAN_VIEN);
    }

    @Test
    public void moTabDonHang_hienTieuDeDonHang() {
        try (ActivityScenario<TrungTamNoiBoActivity> ignored = ActivityScenario.launch(
                TrungTamNoiBoActivity.taoIntent(appContext, DieuHuongNoiBoHelper.TAB_DON_HANG))) {
            onView(withText(R.string.employee_orders_title)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void moTabDatBan_hienTieuDeDatBan() {
        try (ActivityScenario<TrungTamNoiBoActivity> ignored = ActivityScenario.launch(
                TrungTamNoiBoActivity.taoIntent(appContext, DieuHuongNoiBoHelper.TAB_DAT_BAN))) {
            onView(withText(R.string.employee_reservations_title)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void moTabYeuCau_hienTieuDeYeuCau() {
        try (ActivityScenario<TrungTamNoiBoActivity> ignored = ActivityScenario.launch(
                TrungTamNoiBoActivity.taoIntent(appContext, DieuHuongNoiBoHelper.TAB_YEU_CAU))) {
            onView(withText(R.string.employee_service_requests_title)).check(matches(isDisplayed()));
        }
    }
}
```

- [ ] **Step 2: Run the operations test to verify it fails**

Run: `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.quanlynhahang.TrungTamNoiBoOperationsInstrumentedTest`
Expected: FAIL because `TrungTamNoiBoActivity` always shows overview fragment

- [ ] **Step 3: Create the three fragments and wire tab switching in the internal shell**

```java
// DonHangNoiBoFragment.java
public class DonHangNoiBoFragment extends Fragment {

    private DatabaseHelper databaseHelper;
    private DonHangNhanVienAdapter boDieuHop;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_don_hang_noi_bo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        databaseHelper = new DatabaseHelper(requireContext());
        RecyclerView rv = view.findViewById(R.id.rvNoiBoDonHangs);
        TextView tvEmpty = view.findViewById(R.id.tvNoiBoDonHangsEmpty);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        boDieuHop = new DonHangNhanVienAdapter(new DonHangNhanVienAdapter.HanhDongListener() {
            @Override public void khiXacNhan(DonHang order) { databaseHelper.capNhatTrangThaiDonHang(order.layId(), DonHang.TrangThai.DANG_CHUAN_BI); taiDanhSach(tvEmpty); }
            @Override public void khiHoanTat(DonHang order) { databaseHelper.capNhatTrangThaiDonHang(order.layId(), order.layTrangThai() == DonHang.TrangThai.DANG_CHUAN_BI ? DonHang.TrangThai.SAN_SANG_PHUC_VU : DonHang.TrangThai.HOAN_THANH); taiDanhSach(tvEmpty); }
            @Override public void khiHuy(DonHang order) { databaseHelper.capNhatTrangThaiDonHang(order.layId(), DonHang.TrangThai.DA_HUY); taiDanhSach(tvEmpty); }
        });
        rv.setAdapter(boDieuHop);
        taiDanhSach(tvEmpty);
    }

    private void taiDanhSach(TextView tvEmpty) {
        List<DonHang> danhSach = databaseHelper.layTatCaDonHang();
        boDieuHop.capNhatDanhSach(danhSach);
        tvEmpty.setVisibility(danhSach.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
```

```java
// DatBanNoiBoFragment.java
public class DatBanNoiBoFragment extends Fragment {
    // dùng DatBanNhanVienAdapter, layTatCaDatBan(), capNhatTrangThaiDatBan(), capNhatBanDatBan()
}
```

```java
// YeuCauNoiBoFragment.java
public class YeuCauNoiBoFragment extends Fragment {
    // dùng YeuCauPhucVuNhanVienAdapter, layTatCaYeuCauPhucVu(), capNhatTrangThaiYeuCauPhucVu()
}
```

```java
// TrungTamNoiBoActivity.java
private void moTab(String tab) {
    String tabHopLe = DieuHuongNoiBoHelper.chuanHoaTab(tab);
    Fragment fragment;
    String tag;
    switch (tabHopLe) {
        case DieuHuongNoiBoHelper.TAB_DON_HANG:
            fragment = new DonHangNoiBoFragment();
            tag = "don_hang_noi_bo";
            break;
        case DieuHuongNoiBoHelper.TAB_DAT_BAN:
            fragment = new DatBanNoiBoFragment();
            tag = "dat_ban_noi_bo";
            break;
        case DieuHuongNoiBoHelper.TAB_YEU_CAU:
            fragment = new YeuCauNoiBoFragment();
            tag = "yeu_cau_noi_bo";
            break;
        default:
            fragment = new TongQuanNoiBoFragment();
            tag = "tong_quan_noi_bo";
            break;
    }
    hienFragment(fragment, tag);
    sessionManager.luuDuongDanNoiBoCuoi(DieuHuongNoiBoHelper.taoRouteNoiBo(tabHopLe));
}
```

```xml
<!-- fragment_don_hang_noi_bo.xml -->
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="20dp">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/employee_orders_title"
        android:textSize="22sp"
        android:textStyle="bold" />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rvNoiBoDonHangs"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:layout_marginTop="16dp" />

    <TextView
        android:id="@+id/tvNoiBoDonHangsEmpty"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:gravity="center"
        android:text="@string/employee_empty_orders"
        android:visibility="gone" />
</LinearLayout>
```

- [ ] **Step 4: Run the operations instrumentation test again**

Run: `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.quanlynhahang.TrungTamNoiBoOperationsInstrumentedTest`
Expected: PASS with `3 tests successful`

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/example/quanlynhahang/TrungTamNoiBoActivity.java app/src/main/java/com/example/quanlynhahang/DonHangNoiBoFragment.java app/src/main/java/com/example/quanlynhahang/DatBanNoiBoFragment.java app/src/main/java/com/example/quanlynhahang/YeuCauNoiBoFragment.java app/src/main/res/layout/fragment_don_hang_noi_bo.xml app/src/main/res/layout/fragment_dat_ban_noi_bo.xml app/src/main/res/layout/fragment_yeu_cau_noi_bo.xml app/src/androidTest/java/com/example/quanlynhahang/TrungTamNoiBoOperationsInstrumentedTest.java
git commit -m "feat: add operational tabs to internal shell"
```

### Task 6: Đổi launcher, helper vai trò, và `NhanVienActivity` sang legacy redirect

**Files:**
- Modify: `app/src/main/java/com/example/quanlynhahang/helper/DieuHuongNoiBoHelper.java`
- Modify: `app/src/main/java/com/example/quanlynhahang/helper/DieuHuongVaiTroHelper.java`
- Modify: `app/src/main/java/com/example/quanlynhahang/StaffLauncherActivity.java`
- Modify: `app/src/main/java/com/example/quanlynhahang/NhanVienActivity.java`
- Test: `app/src/androidTest/java/com/example/quanlynhahang/DieuHuongVaiTroNoiBoInstrumentedTest.java`

- [ ] **Step 1: Write the failing intent-mapping instrumentation test**

```java
package com.example.quanlynhahang;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.Intent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.quanlynhahang.helper.DieuHuongNoiBoHelper;
import com.example.quanlynhahang.helper.DieuHuongVaiTroHelper;
import com.example.quanlynhahang.model.VaiTroNguoiDung;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DieuHuongVaiTroNoiBoInstrumentedTest {

    @Test
    public void taoIntentTheoVaiTro_traVeInternalShellChoNhanVien() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = DieuHuongVaiTroHelper.taoIntentTheoVaiTro(appContext, VaiTroNguoiDung.NHAN_VIEN);
        assertEquals(TrungTamNoiBoActivity.class.getName(), intent.getComponent().getClassName());
        assertEquals(DieuHuongNoiBoHelper.TAB_TONG_QUAN, intent.getStringExtra(DieuHuongNoiBoHelper.EXTRA_TAB_NOI_BO));
    }

    @Test
    public void mapLegacyNhanVienExtra_sangInternalShellDungTab() {
        assertEquals(DieuHuongNoiBoHelper.TAB_DAT_BAN, DieuHuongNoiBoHelper.mapTabNhanVienCu(NhanVienActivity.TAB_DAT_BAN));
    }
}
```

- [ ] **Step 2: Run the instrumentation test to verify it fails**

Run: `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.quanlynhahang.DieuHuongVaiTroNoiBoInstrumentedTest`
Expected: FAIL because `DieuHuongVaiTroHelper` still returns `NhanVienActivity`

- [ ] **Step 3: Build concrete internal-shell intents and convert the employee activity to redirect-only**

```java
// DieuHuongNoiBoHelper.java
public static Intent taoIntentTrungTamNoiBo(Context context, String tab) {
    return TrungTamNoiBoActivity.taoIntent(context, chuanHoaTab(tab));
}
```

```java
// DieuHuongVaiTroHelper.java
public static Intent taoIntentTheoVaiTro(Context context, @Nullable VaiTroNguoiDung vaiTro) {
    VaiTroNguoiDung vaiTroHienTai = vaiTro != null ? vaiTro : VaiTroNguoiDung.KHACH_HANG;
    if ((vaiTroHienTai == VaiTroNguoiDung.NHAN_VIEN || vaiTroHienTai == VaiTroNguoiDung.ADMIN)
            && CauHinhTinhNangHelper.batTrungTamNoiBoMoi()) {
        return DieuHuongNoiBoHelper.taoIntentTrungTamNoiBo(context, DieuHuongNoiBoHelper.TAB_TONG_QUAN);
    }
    if (vaiTroHienTai == VaiTroNguoiDung.NHAN_VIEN) {
        return new Intent(context, NhanVienActivity.class);
    }
    if (vaiTroHienTai == VaiTroNguoiDung.ADMIN) {
        return new Intent(context, QuanTriActivity.class);
    }
    return new Intent(context, MainActivity.class);
}
```

```java
// StaffLauncherActivity.java
if (sessionManager.daDangNhapNoiBo()) {
    Intent intent = DieuHuongNoiBoHelper.taoIntentTrungTamNoiBo(this, DieuHuongNoiBoHelper.TAB_TONG_QUAN);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
    finish();
    return;
}
```

```java
// NhanVienActivity.java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    String tabNoiBo = DieuHuongNoiBoHelper.mapTabNhanVienCu(getIntent().getStringExtra(EXTRA_TAB_MUC_TIEU));
    Intent intent = DieuHuongNoiBoHelper.taoIntentTrungTamNoiBo(this, tabNoiBo);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
    finish();
}
```

- [ ] **Step 4: Run the intent-mapping instrumentation test again**

Run: `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.quanlynhahang.DieuHuongVaiTroNoiBoInstrumentedTest`
Expected: PASS with `2 tests successful`

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/example/quanlynhahang/helper/DieuHuongNoiBoHelper.java app/src/main/java/com/example/quanlynhahang/helper/DieuHuongVaiTroHelper.java app/src/main/java/com/example/quanlynhahang/StaffLauncherActivity.java app/src/main/java/com/example/quanlynhahang/NhanVienActivity.java app/src/androidTest/java/com/example/quanlynhahang/DieuHuongVaiTroNoiBoInstrumentedTest.java
git commit -m "feat: route internal roles to the new shell"
```

### Task 7: Dựng admin shell với section Báo cáo và Cài đặt

**Files:**
- Modify: `app/src/main/AndroidManifest.xml`
- Modify: `app/src/main/res/values/strings.xml`
- Create: `app/src/main/java/com/example/quanlynhahang/TrungTamQuanTriActivity.java`
- Create: `app/src/main/java/com/example/quanlynhahang/BaoCaoQuanTriFragment.java`
- Create: `app/src/main/java/com/example/quanlynhahang/CaiDatQuanTriFragment.java`
- Create: `app/src/main/res/layout/activity_trung_tam_quan_tri.xml`
- Create: `app/src/main/res/layout/fragment_bao_cao_quan_tri.xml`
- Create: `app/src/main/res/layout/fragment_cai_dat_quan_tri.xml`
- Test: `app/src/androidTest/java/com/example/quanlynhahang/TrungTamQuanTriInstrumentedTest.java`

- [ ] **Step 1: Write the failing admin-shell instrumentation test**

```java
package com.example.quanlynhahang;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.helper.DieuHuongNoiBoHelper;
import com.example.quanlynhahang.model.VaiTroNguoiDung;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TrungTamQuanTriInstrumentedTest {

    private Context appContext;

    @Before
    public void setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SessionManager sessionManager = new SessionManager(appContext);
        sessionManager.xoaPhienNoiBo();
        sessionManager.luuPhienNoiBo(9L, VaiTroNguoiDung.ADMIN);
    }

    @Test
    public void moSectionBaoCao_hienTieuDeBaoCao() {
        try (ActivityScenario<TrungTamQuanTriActivity> ignored = ActivityScenario.launch(
                TrungTamQuanTriActivity.taoIntent(appContext, DieuHuongNoiBoHelper.SECTION_BAO_CAO))) {
            onView(withText(R.string.admin_reports_title)).check(matches(isDisplayed()));
        }
    }
}
```

- [ ] **Step 2: Run the admin-shell test to verify it fails**

Run: `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.quanlynhahang.TrungTamQuanTriInstrumentedTest`
Expected: FAIL with `ClassNotFoundException: com.example.quanlynhahang.TrungTamQuanTriActivity`

- [ ] **Step 3: Create the admin shell, reports fragment, and settings fragment**

```java
package com.example.quanlynhahang;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.helper.DieuHuongNoiBoHelper;
import com.example.quanlynhahang.model.VaiTroNguoiDung;

public class TrungTamQuanTriActivity extends AppCompatActivity {

    public static Intent taoIntent(Context context, String section) {
        Intent intent = new Intent(context, TrungTamQuanTriActivity.class);
        intent.putExtra(DieuHuongNoiBoHelper.EXTRA_SECTION_QUAN_TRI, DieuHuongNoiBoHelper.chuanHoaSection(section));
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trung_tam_quan_tri);
        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.layVaiTroNoiBoHopLe() != VaiTroNguoiDung.ADMIN) {
            finish();
            return;
        }
        moSection(getIntent().getStringExtra(DieuHuongNoiBoHelper.EXTRA_SECTION_QUAN_TRI));
    }

    private void moSection(String section) {
        String sectionHopLe = DieuHuongNoiBoHelper.chuanHoaSection(section);
        Fragment fragment = DieuHuongNoiBoHelper.SECTION_CAI_DAT.equals(sectionHopLe)
                ? new CaiDatQuanTriFragment()
                : new BaoCaoQuanTriFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.quanTriFragmentContainer, fragment).commit();
        new SessionManager(this).luuDuongDanNoiBoCuoi(DieuHuongNoiBoHelper.taoRouteQuanTri(sectionHopLe));
    }
}
```

```java
public class BaoCaoQuanTriFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bao_cao_quan_tri, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DatabaseHelper databaseHelper = new DatabaseHelper(requireContext());
        ThongKeTongQuanQuanTri thongKe = databaseHelper.layThongKeTongQuanQuanTri();
        ((TextView) view.findViewById(R.id.tvBaoCaoTongNguoiDung)).setText(String.valueOf(thongKe.layTongNguoiDung()));
        ((TextView) view.findViewById(R.id.tvBaoCaoTongMon)).setText(String.valueOf(thongKe.layTongMonAn()));
        ((TextView) view.findViewById(R.id.tvBaoCaoDonChoXacNhan)).setText(String.valueOf(thongKe.laySoDonHangChoXacNhan()));
    }
}
```

```java
public class CaiDatQuanTriFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cai_dat_quan_tri, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.btnQuanTriPreviewKhach).setOnClickListener(v ->
                startActivity(DieuHuongNoiBoHelper.taoIntentPreviewKhachHang(requireContext(), new SessionManager(requireContext()).layDuongDanNoiBoCuoi())));
        view.findViewById(R.id.btnQuanTriDangXuat).setOnClickListener(v -> {
            SessionManager sessionManager = new SessionManager(requireContext());
            sessionManager.xoaPhienNoiBo();
            startActivity(new Intent(requireContext(), StaffLauncherActivity.class));
            requireActivity().finish();
        });
    }
}
```

- [ ] **Step 4: Run the admin-shell instrumentation test again**

Run: `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.quanlynhahang.TrungTamQuanTriInstrumentedTest`
Expected: PASS with `1 test successful`

- [ ] **Step 5: Commit**

```bash
git add app/src/main/AndroidManifest.xml app/src/main/res/values/strings.xml app/src/main/java/com/example/quanlynhahang/TrungTamQuanTriActivity.java app/src/main/java/com/example/quanlynhahang/BaoCaoQuanTriFragment.java app/src/main/java/com/example/quanlynhahang/CaiDatQuanTriFragment.java app/src/main/res/layout/activity_trung_tam_quan_tri.xml app/src/main/res/layout/fragment_bao_cao_quan_tri.xml app/src/main/res/layout/fragment_cai_dat_quan_tri.xml app/src/androidTest/java/com/example/quanlynhahang/TrungTamQuanTriInstrumentedTest.java
git commit -m "feat: add admin shell for reports and settings"
```

### Task 8: Tách quản lý món ăn và người dùng khỏi `QuanTriActivity`, rồi đổi `QuanTriActivity` sang redirect

**Files:**
- Create: `app/src/main/java/com/example/quanlynhahang/MonAnQuanTriFragment.java`
- Create: `app/src/main/java/com/example/quanlynhahang/NguoiDungQuanTriFragment.java`
- Create: `app/src/main/res/layout/fragment_mon_an_quan_tri.xml`
- Create: `app/src/main/res/layout/fragment_nguoi_dung_quan_tri.xml`
- Modify: `app/src/main/java/com/example/quanlynhahang/TrungTamQuanTriActivity.java`
- Modify: `app/src/main/java/com/example/quanlynhahang/TrungTamNoiBoActivity.java`
- Modify: `app/src/main/java/com/example/quanlynhahang/QuanTriActivity.java`
- Test: `app/src/androidTest/java/com/example/quanlynhahang/LegacyBridgeRegressionInstrumentedTest.java`

- [ ] **Step 1: Write the failing instrumentation test for admin shell management and legacy redirect**

```java
package com.example.quanlynhahang;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.Intent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.quanlynhahang.helper.DieuHuongNoiBoHelper;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LegacyBridgeRegressionInstrumentedTest {

    @Test
    public void taoIntentQuanTriSection_dungSectionUsers() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = TrungTamQuanTriActivity.taoIntent(appContext, DieuHuongNoiBoHelper.SECTION_NGUOI_DUNG);
        assertEquals(DieuHuongNoiBoHelper.SECTION_NGUOI_DUNG,
                intent.getStringExtra(DieuHuongNoiBoHelper.EXTRA_SECTION_QUAN_TRI));
    }
}
```

- [ ] **Step 2: Run the instrumentation test to verify it fails**

Run: `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.quanlynhahang.LegacyBridgeRegressionInstrumentedTest`
Expected: FAIL because `TrungTamQuanTriActivity` chưa route được `users`

- [ ] **Step 3: Extract dish/user management into fragments and make the old admin activity redirect-only**

```java
// MonAnQuanTriFragment.java
public class MonAnQuanTriFragment extends Fragment {

    private DatabaseHelper databaseHelper;
    private BoDieuHopMonQuanTri boDieuHopMon;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mon_an_quan_tri, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        databaseHelper = new DatabaseHelper(requireContext());
        RecyclerView rv = view.findViewById(R.id.rvQuanTriMonAn);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        boDieuHopMon = new BoDieuHopMonQuanTri(new BoDieuHopMonQuanTri.HanhDongListener() {
            @Override public void khiSua(DatabaseHelper.DishRecord banGhiMon) { /* giữ dialog sửa hiện tại */ }
            @Override public void khiXoa(DatabaseHelper.DishRecord banGhiMon) { databaseHelper.xoaMonAnTheoId(banGhiMon.layId()); taiDanhSach(); }
            @Override public void khiBatTatTrangThaiPhucVu(DatabaseHelper.DishRecord banGhiMon) { databaseHelper.capNhatTrangThaiPhucVuMon(banGhiMon.layId(), !banGhiMon.layMonAn().laConPhucVu()); taiDanhSach(); }
        });
        rv.setAdapter(boDieuHopMon);
        taiDanhSach();
    }

    private void taiDanhSach() {
        boDieuHopMon.capNhatDanhSach(databaseHelper.layTatCaMonAn());
    }
}
```

```java
// NguoiDungQuanTriFragment.java
public class NguoiDungQuanTriFragment extends Fragment {
    // tái dùng BoDieuHopNguoiDungQuanTri, layTatCaNguoiDung(), layNguoiDungTheoVaiTro(), capNhatVaiTroNguoiDung(), capNhatTrangThaiHoatDongNguoiDung()
}
```

```java
// TrungTamQuanTriActivity.java
private void moSection(String section) {
    String sectionHopLe = DieuHuongNoiBoHelper.chuanHoaSection(section);
    Fragment fragment;
    switch (sectionHopLe) {
        case DieuHuongNoiBoHelper.SECTION_MON_AN:
            fragment = new MonAnQuanTriFragment();
            break;
        case DieuHuongNoiBoHelper.SECTION_NGUOI_DUNG:
            fragment = new NguoiDungQuanTriFragment();
            break;
        case DieuHuongNoiBoHelper.SECTION_CAI_DAT:
            fragment = new CaiDatQuanTriFragment();
            break;
        default:
            fragment = new BaoCaoQuanTriFragment();
            break;
    }
    getSupportFragmentManager().beginTransaction().replace(R.id.quanTriFragmentContainer, fragment).commit();
}
```

```java
// TrungTamNoiBoActivity.java
private void moAdminShell(String section) {
    startActivity(TrungTamQuanTriActivity.taoIntent(this, section));
}
```

```java
// QuanTriActivity.java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Intent intent = DieuHuongNoiBoHelper.taoIntentTrungTamNoiBo(this, DieuHuongNoiBoHelper.TAB_TONG_QUAN);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
    finish();
}
```

- [ ] **Step 4: Run the management/legacy bridge instrumentation test again**

Run: `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.quanlynhahang.LegacyBridgeRegressionInstrumentedTest`
Expected: PASS with `1 test successful`

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/example/quanlynhahang/MonAnQuanTriFragment.java app/src/main/java/com/example/quanlynhahang/NguoiDungQuanTriFragment.java app/src/main/res/layout/fragment_mon_an_quan_tri.xml app/src/main/res/layout/fragment_nguoi_dung_quan_tri.xml app/src/main/java/com/example/quanlynhahang/TrungTamQuanTriActivity.java app/src/main/java/com/example/quanlynhahang/TrungTamNoiBoActivity.java app/src/main/java/com/example/quanlynhahang/QuanTriActivity.java app/src/androidTest/java/com/example/quanlynhahang/LegacyBridgeRegressionInstrumentedTest.java
git commit -m "feat: extract admin management into the admin shell"
```

### Task 9: Cô lập customer preview của admin và thêm nút thoát preview

**Files:**
- Modify: `app/src/main/java/com/example/quanlynhahang/helper/DieuHuongNoiBoHelper.java`
- Modify: `app/src/main/java/com/example/quanlynhahang/CustomerLauncherActivity.java`
- Modify: `app/src/main/java/com/example/quanlynhahang/MainActivity.java`
- Modify: `app/src/main/java/com/example/quanlynhahang/DangNhapActivity.java`
- Modify: `app/src/main/java/com/example/quanlynhahang/CaiDatQuanTriFragment.java`
- Modify: `app/src/main/res/layout/activity_main.xml`
- Modify: `app/src/main/res/values/strings.xml`
- Test: `app/src/androidTest/java/com/example/quanlynhahang/KhachHangPreviewInstrumentedTest.java`

- [ ] **Step 1: Write the failing acceptance test for admin customer preview**

```java
package com.example.quanlynhahang;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.helper.DieuHuongNoiBoHelper;
import com.example.quanlynhahang.model.VaiTroNguoiDung;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class KhachHangPreviewInstrumentedTest {

    private Context appContext;

    @Before
    public void setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SessionManager sessionManager = new SessionManager(appContext);
        sessionManager.xoaPhienNoiBo();
        sessionManager.xoaPhienKhachHang();
        sessionManager.luuPhienNoiBo(31L, VaiTroNguoiDung.ADMIN);
        sessionManager.luuDuongDanNoiBoCuoi(DieuHuongNoiBoHelper.taoRouteNoiBo(DieuHuongNoiBoHelper.TAB_DON_HANG));
    }

    @Test
    public void moPreview_khongMatPhienNoiBo_vaThoatVeDungTab() {
        Intents.init();
        try {
            try (ActivityScenario<CustomerLauncherActivity> ignored = ActivityScenario.launch(
                    DieuHuongNoiBoHelper.taoIntentPreviewKhachHang(appContext, DieuHuongNoiBoHelper.taoRouteNoiBo(DieuHuongNoiBoHelper.TAB_DON_HANG)))) {
                SessionManager sessionManager = new SessionManager(appContext);
                assertTrue(sessionManager.daDangNhapNoiBo());
                assertEquals(VaiTroNguoiDung.ADMIN, sessionManager.layVaiTroNoiBoHopLe());
                onView(withId(R.id.btnExitCustomerPreview)).check(matches(isDisplayed()));
                onView(withId(R.id.btnExitCustomerPreview)).perform(click());
            }
            intended(hasComponent(TrungTamNoiBoActivity.class.getName()));
            intended(hasExtra(DieuHuongNoiBoHelper.EXTRA_TAB_NOI_BO, DieuHuongNoiBoHelper.TAB_DON_HANG));
        } finally {
            Intents.release();
        }
    }
}
```

- [ ] **Step 2: Run the preview acceptance test to verify it fails**

Run: `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.quanlynhahang.KhachHangPreviewInstrumentedTest`
Expected: FAIL because preview route, preview button, and return intent chưa tồn tại

- [ ] **Step 3: Implement isolated preview routing, customer-only login guard, and exit-preview button**

```java
// DieuHuongNoiBoHelper.java
public static Intent taoIntentPreviewKhachHang(Context context, String returnRoute) {
    Intent intent = new Intent(context, CustomerLauncherActivity.class);
    intent.putExtra(EXTRA_CHE_DO_PREVIEW_KHACH, true);
    intent.putExtra(EXTRA_ROUTE_TRA_VE_NOI_BO, returnRoute);
    return intent;
}

public static Intent taoIntentTraVeNoiBoTuRoute(Context context, String route) {
    if (route != null && route.startsWith("admin:")) {
        return TrungTamQuanTriActivity.taoIntent(context, route.substring("admin:".length()));
    }
    String tab = route != null && route.startsWith("internal:")
            ? route.substring("internal:".length())
            : TAB_TONG_QUAN;
    return TrungTamNoiBoActivity.taoIntent(context, tab);
}
```

```java
// CustomerLauncherActivity.java
boolean cheDoPreview = getIntent().getBooleanExtra(DieuHuongNoiBoHelper.EXTRA_CHE_DO_PREVIEW_KHACH, false);
String returnRoute = getIntent().getStringExtra(DieuHuongNoiBoHelper.EXTRA_ROUTE_TRA_VE_NOI_BO);
if (cheDoPreview) {
    SessionManager sessionManager = new SessionManager(this);
    sessionManager.luuNguonPreviewKhachHang(returnRoute);
    Intent intent = new Intent(this, MainActivity.class);
    intent.putExtra(MainActivity.EXTRA_CHO_PHEP_XEM_GIAO_DIEN_KHACH, true);
    intent.putExtra(DieuHuongNoiBoHelper.EXTRA_CHE_DO_PREVIEW_KHACH, true);
    intent.putExtra(DieuHuongNoiBoHelper.EXTRA_ROUTE_TRA_VE_NOI_BO, returnRoute);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
    finish();
    return;
}
```

```java
// MainActivity.java
private boolean cheDoPreviewKhach;
private String routeTraVeNoiBo;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    cheDoPreviewKhach = getIntent().getBooleanExtra(DieuHuongNoiBoHelper.EXTRA_CHE_DO_PREVIEW_KHACH, false);
    routeTraVeNoiBo = getIntent().getStringExtra(DieuHuongNoiBoHelper.EXTRA_ROUTE_TRA_VE_NOI_BO);
    View btnExitPreview = findViewById(R.id.btnExitCustomerPreview);
    btnExitPreview.setVisibility(cheDoPreviewKhach ? View.VISIBLE : View.GONE);
    btnExitPreview.setOnClickListener(v -> {
        startActivity(DieuHuongNoiBoHelper.taoIntentTraVeNoiBoTuRoute(this, routeTraVeNoiBo));
        finish();
    });
}
```

```java
// DangNhapActivity.java
public static final String EXTRA_ONLY_CUSTOMER_SESSION = "extra_only_customer_session";

boolean chiChoKhach = getIntent().getBooleanExtra(EXTRA_ONLY_CUSTOMER_SESSION, false);
if (chiChoKhach && nguoiDungDaXacThuc.layVaiTro() != VaiTroNguoiDung.KHACH_HANG) {
    Toast.makeText(this, getString(R.string.login_customer_only_mode), Toast.LENGTH_SHORT).show();
    return;
}
```

```xml
<!-- activity_main.xml -->
<com.google.android.material.button.MaterialButton
    android:id="@+id/btnExitCustomerPreview"
    style="@style/Widget.Quanlynhahang.TonalButton"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_gravity="end"
    android:text="@string/customer_preview_exit"
    android:visibility="gone" />
```

- [ ] **Step 4: Run the preview acceptance test again**

Run: `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.quanlynhahang.KhachHangPreviewInstrumentedTest`
Expected: PASS with `1 test successful`

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/example/quanlynhahang/helper/DieuHuongNoiBoHelper.java app/src/main/java/com/example/quanlynhahang/CustomerLauncherActivity.java app/src/main/java/com/example/quanlynhahang/MainActivity.java app/src/main/java/com/example/quanlynhahang/DangNhapActivity.java app/src/main/java/com/example/quanlynhahang/CaiDatQuanTriFragment.java app/src/main/res/layout/activity_main.xml app/src/main/res/values/strings.xml app/src/androidTest/java/com/example/quanlynhahang/KhachHangPreviewInstrumentedTest.java
git commit -m "feat: isolate admin customer preview sessions"
```

### Task 10: Chạy full regression và khóa exit criteria cho bridge cũ

**Files:**
- Modify: `app/src/androidTest/java/com/example/quanlynhahang/LegacyBridgeRegressionInstrumentedTest.java`
- Modify: `app/src/androidTest/java/com/example/quanlynhahang/TrungTamNoiBoOperationsInstrumentedTest.java`
- Modify: `app/src/androidTest/java/com/example/quanlynhahang/TrungTamQuanTriInstrumentedTest.java`
- Modify: `app/src/androidTest/java/com/example/quanlynhahang/KhachHangPreviewInstrumentedTest.java`

- [ ] **Step 1: Extend the regression suite with explicit bridge assertions**

```java
@Test
public void helperDieuHuongKhongConTraVeNhanVienActivityHoacQuanTriActivity() {
    Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    Intent adminIntent = DieuHuongVaiTroHelper.taoIntentTheoVaiTro(appContext, VaiTroNguoiDung.ADMIN);
    Intent employeeIntent = DieuHuongVaiTroHelper.taoIntentTheoVaiTro(appContext, VaiTroNguoiDung.NHAN_VIEN);

    assertEquals(TrungTamNoiBoActivity.class.getName(), adminIntent.getComponent().getClassName());
    assertEquals(TrungTamNoiBoActivity.class.getName(), employeeIntent.getComponent().getClassName());
}
```

- [ ] **Step 2: Run the full unit-test suite**

Run: `./gradlew testDebugUnitTest`
Expected: PASS with all unit tests green, including `DieuHuongNoiBoHelperTest`, `QuanLyGioHangTest`, `ModelBusinessRuleTest`, `RoleAndReservationRuleTest`

- [ ] **Step 3: Run the focused instrumentation suite for launchers, shells, preview, and legacy bridge**

Run: `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.quanlynhahang.TrungTamNoiBoOverviewInstrumentedTest,com.example.quanlynhahang.TrungTamNoiBoOperationsInstrumentedTest,com.example.quanlynhahang.DieuHuongVaiTroNoiBoInstrumentedTest,com.example.quanlynhahang.TrungTamQuanTriInstrumentedTest,com.example.quanlynhahang.KhachHangPreviewInstrumentedTest,com.example.quanlynhahang.LegacyBridgeRegressionInstrumentedTest`
Expected: PASS with all targeted instrumentation tests green

- [ ] **Step 4: Execute the 5 golden-flow manual regression checklist on a device/emulator**

Manual checklist:
1. Mở `CustomerLauncherActivity`, đặt món, xác nhận đơn, vào `TrungTamHoatDongFragment` xem trạng thái.
2. Giữ bàn, hủy giữ bàn, xác nhận DB và UI cùng phản ánh trạng thái.
3. Gửi yêu cầu phục vụ, chuyển sang nhân viên, nhận xử lý, quay lại khách xem nhãn thân thiện + chi tiết nội bộ.
4. Đăng nhập nhân viên qua `StaffLauncherActivity`, xác nhận 4 khu vận hành đều mở đúng route và thao tác được.
5. Đăng nhập admin, mở `TrungTamQuanTriActivity`, vào preview khách, thêm món/bàn trong preview, thoát preview, xác nhận internal session và route nội bộ vẫn giữ nguyên.

- [ ] **Step 5: Commit the regression lock**

```bash
git add app/src/androidTest/java/com/example/quanlynhahang/LegacyBridgeRegressionInstrumentedTest.java app/src/androidTest/java/com/example/quanlynhahang/TrungTamNoiBoOperationsInstrumentedTest.java app/src/androidTest/java/com/example/quanlynhahang/TrungTamQuanTriInstrumentedTest.java app/src/androidTest/java/com/example/quanlynhahang/KhachHangPreviewInstrumentedTest.java
git commit -m "test: lock regression coverage for unified internal flows"
```

## Self-review

### Spec coverage
- Route contract và mapping legacy: Task 1, 6, 10
- Feature flag / kill switch: Task 1, 6
- Session namespace + key ownership: Task 2
- Scoped cart/customer scope: Task 3
- Internal shell 4 khu: Task 4, 5
- Admin shell con: Task 7, 8
- Customer preview isolated: Task 9
- Acceptance criteria preview: Task 9, 10
- Legacy redirect cho `NhanVienActivity` / `QuanTriActivity`: Task 6, 8, 10
- Exit criteria trước khi xóa bridge cũ: Task 10

### Placeholder scan
- Không dùng `TBD`, `TODO`, `implement later`, hoặc “similar to”.
- Mọi bước code đều có code block cụ thể.
- Mọi bước test đều có lệnh `gradlew` cụ thể và expected outcome.

### Type consistency
- Internal shell route contract: `overview | orders | reservations | service_requests`
- Admin shell route contract: `dishes | users | reports | settings`
- Route helper class: `DieuHuongNoiBoHelper`
- Feature flag helper class: `CauHinhTinhNangHelper`
- Internal shell activity: `TrungTamNoiBoActivity`
- Admin shell activity: `TrungTamQuanTriActivity`

Plan complete and saved to `docs/superpowers/plans/2026-04-19-hop-nhat-he-admin-nhan-vien-khach-hang.md`. Two execution options:

**1. Subagent-Driven (recommended)** - I dispatch a fresh subagent per task, review between tasks, fast iteration

**2. Inline Execution** - Execute tasks in this session using executing-plans, batch execution with checkpoints

**Which approach?**
