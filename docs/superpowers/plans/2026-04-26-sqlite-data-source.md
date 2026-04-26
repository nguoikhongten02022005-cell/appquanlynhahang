# SQLite Data Source Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Move remaining runtime/business/sample data out of Java hardcoded lists and into SQLite-backed flows seeded from an asset file.

**Architecture:** Add a `seed_data.json` asset as the business/sample data source, keep `SeedDataHelper` as the SQLite importer, and update UI flows to query SQLite for tables, categories, and demo login accounts. Preserve existing Java/XML app architecture and existing SQLite tables.

**Tech Stack:** Android Java, SQLiteOpenHelper, org.json, JUnit/source tests, Gradle.

---

## File Structure

- Create: `app/src/main/assets/seed_data.json` — business/sample seed data records.
- Modify: `app/src/main/java/com/example/quanlynhahang/data/SeedDataHelper.java` — read JSON asset, parse records, call existing insert/update helpers.
- Modify: `app/src/main/java/com/example/quanlynhahang/data/DatabaseHelper.java` — expose category and demo account lookup helpers.
- Modify: `app/src/main/java/com/example/quanlynhahang/TrangChuFragment.java` — load categories from SQLite categories.
- Modify: `app/src/main/java/com/example/quanlynhahang/DatBanFragment.java` — load selectable tables from `ban_an`.
- Modify: `app/src/main/java/com/example/quanlynhahang/DatBanNoiBoFragment.java` — load change-table options from `ban_an`.
- Modify: `app/src/main/java/com/example/quanlynhahang/GioHangActivity.java` — load cart table picker from `ban_an`.
- Modify: `app/src/main/java/com/example/quanlynhahang/DangNhapActivity.java` — resolve quick-login accounts from SQLite.
- Test: `app/src/test/java/com/example/quanlynhahang/SqliteDataSourceSpecTest.java` — source tests for removed hardcoded data and SQLite-backed flows.

---

### Task 1: Add source tests for no hardcoded runtime data

**Files:**
- Create: `app/src/test/java/com/example/quanlynhahang/SqliteDataSourceSpecTest.java`

- [ ] **Step 1: Write failing source tests**

Create `SqliteDataSourceSpecTest.java` with:

```java
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
        assertFalse(source.contains("old-local-domain.example"));
        assertFalse(source.contains("#DH10001"));
        assertFalse(source.contains("#GB10001"));
        assertFalse(source.contains("Khách cần thêm chén"));
        assertFalse(source.contains("taoBanAnNeuChuaCo(db, \"B01\""));
    }

    @Test
    public void seedDataAsset_containsBusinessSeedRecords() throws Exception {
        String json = read("src/main/assets/seed_data.json");

        assertTrue(json.contains("Cơm chiên hải sản"));
        assertTrue(json.contains("minhanh@nhahang.vn"));
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
```

- [ ] **Step 2: Run focused test to verify it fails**

Run:

```bash
JAVA_HOME="/opt/android-studio/jbr" ./gradlew testDebugUnitTest --tests com.example.quanlynhahang.SqliteDataSourceSpecTest
```

Expected: FAIL because `seed_data.json` does not exist and Java files still contain hardcoded data.

---

### Task 2: Create seed data JSON asset

**Files:**
- Create: `app/src/main/assets/seed_data.json`

- [ ] **Step 1: Create asset directory and JSON file**

Create `app/src/main/assets/seed_data.json` containing the existing seed records from `SeedDataHelper.java`: default users, additional staff/admin users, dishes, tables, reservations, orders, and service requests. Keep field names simple and explicit:

```json
{
  "dishes": [
    {"name_res":"dish_bo_luc_lac","price_res":"price_145k","description_res":"menu_desc_bo_luc_lac","image":"menu_1","available":true,"category_res":"category_main_course","score":96},
    {"name_res":"dish_lau_thai","price_res":"price_259k","description_res":"menu_desc_lau_thai","image":"dish_6","available":true,"category_res":"category_hotpot","score":93},
    {"name_res":"dish_salad_ca_hoi","price_res":"price_129k","description_res":"menu_desc_salad_ca_hoi","image":"menu_2","available":true,"category_res":"category_salad","score":89},
    {"name_res":"dish_tra_dao","price_res":"price_45k","description_res":"menu_desc_tra_dao","image":"image3","available":true,"category_res":"category_drink","score":82},
    {"name":"Cơm chiên hải sản","price":"98.000 đ","description":"Cơm chiên tơi hạt cùng tôm, mực và rau củ.","image":"menu_1","available":true,"category_res":"category_main_course","score":88},
    {"name":"Mì Ý bò bằm","price":"115.000 đ","description":"Mì Ý sốt bò bằm đậm vị, dùng kèm phô mai bào.","image":"dish_6","available":true,"category_res":"category_main_course","score":84},
    {"name":"Gỏi tôm xoài xanh","price":"92.000 đ","description":"Tôm tươi, xoài xanh bào sợi, rau thơm và nước mắm chua ngọt.","image":"menu_2","available":true,"category_res":"category_salad","score":80},
    {"name":"Nước cam ép","price":"39.000 đ","description":"Nước cam tươi nguyên chất, không dùng syrup.","image":"image3","available":true,"category_res":"category_drink","score":76}
  ],
  "users": [
    {"name":"Trần Quốc Bảo","email":"quocbao@nhahang.vn","phone":"0912 345 678","password":"1","role":"KHACH_HANG","active":true},
    {"name":"Lê Thảo Vy","email":"thaovy@nhahang.vn","phone":"0912 345 679","password":"1","role":"NHAN_VIEN","active":true},
    {"name":"Nguyễn Minh Anh","email":"minhanh@nhahang.vn","phone":"0912 345 680","password":"1","role":"ADMIN","active":true}
  ],
  "tables": [
    {"code":"B01","name":"Bàn 01","seats":4,"area":"Tầng trệt","status":"TRONG"},
    {"code":"B02","name":"Bàn 02","seats":4,"area":"Tầng trệt","status":"DANG_PHUC_VU"},
    {"code":"B05","name":"Bàn 05","seats":6,"area":"Ban công","status":"DA_DAT"},
    {"code":"B08","name":"Bàn 08","seats":8,"area":"Phòng riêng","status":"TRONG"},
    {"code":"B10","name":"Bàn 10","seats":4,"area":"Tầng trệt","status":"TRONG"}
  ],
  "reservations": [
    {"code":"#GB10001","time":"20/04/2026 18:30","table":"Bàn 05","guests":4,"note":"Sinh nhật gia đình, ưu tiên khu yên tĩnh.","status":"PENDING","linked_order_id":0},
    {"code":"#GB10002","time":"19/04/2026 19:00","table":"Bàn 02","guests":2,"note":"Khách đã tới quán.","status":"ACTIVE","linked_order_id":0},
    {"code":"#GB10003","time":"18/04/2026 18:00","table":"Bàn 08","guests":6,"note":"Đã dùng bữa xong.","status":"COMPLETED","linked_order_id":0},
    {"code":"#GB10004","time":"17/04/2026 20:00","table":"Bàn 10","guests":3,"note":"Khách báo bận nên hủy.","status":"CANCELLED","linked_order_id":0}
  ],
  "orders": [
    {"code":"#DH10001","time":"19/04/2026 18:45","status":"CHO_XAC_NHAN","type":"MANG_DI","table":"","note":"Ít đá, giao ngay khi xong.","payment_status":"CHUA_THANH_TOAN","payment_method":"TIEN_MAT_KHI_NHAN","reservation_code":"","items":[{"dish":"Bò lúc lắc","quantity":1},{"dish":"Trà đào cam sả","quantity":2}]},
    {"code":"#DH10002","time":"19/04/2026 19:05","status":"DANG_CHUAN_BI","type":"AN_TAI_QUAN","table":"Bàn 02","note":"Đang phục vụ món chính.","payment_status":"CHUA_THANH_TOAN","payment_method":"TAI_QUAY","reservation_code":"#GB10002","items":[{"dish":"Lẩu Thái hải sản","quantity":1},{"dish":"Trà đào cam sả","quantity":2}]},
    {"code":"#DH10003","time":"18/04/2026 18:20","status":"SAN_SANG_PHUC_VU","type":"AN_TAI_QUAN","table":"Bàn 08","note":"Báo khách món đã lên đủ.","payment_status":"DA_GOI_THANH_TOAN","payment_method":"TAI_QUAY","reservation_code":"","items":[{"dish":"Bò lúc lắc","quantity":2},{"dish":"Salad cá hồi","quantity":1},{"dish":"Trà đào cam sả","quantity":3}]},
    {"code":"#DH10004","time":"17/04/2026 12:10","status":"HOAN_THANH","type":"MANG_DI","table":"","note":"Khách đã nhận món.","payment_status":"DA_THANH_TOAN_MO_PHONG","payment_method":"THANH_TOAN_NGAY_MO_PHONG","reservation_code":"","items":[{"dish":"Bò lúc lắc","quantity":1},{"dish":"Salad cá hồi","quantity":1}]},
    {"code":"#DH10005","time":"16/04/2026 20:15","status":"DA_HUY","type":"MANG_DI","table":"","note":"Khách đổi ý sau khi đặt.","payment_status":"CHUA_THANH_TOAN","payment_method":"CHUA_CHON","reservation_code":"","items":[{"dish":"Lẩu Thái hải sản","quantity":1},{"dish":"Trà đào cam sả","quantity":1}]}
  ],
  "service_requests": [
    {"type":"GOI_NHAN_VIEN","content":"Khách cần thêm chén và đĩa.","table":"Bàn 02","order_code":"#DH10002","sent_time":"19/04/2026 19:08","status":"DANG_CHO","handled_time":""},
    {"type":"THEM_NUOC","content":"Xin thêm nước lọc cho bàn 02.","table":"Bàn 02","order_code":"#DH10002","sent_time":"19/04/2026 19:10","status":"DANG_XU_LY","handled_time":"19/04/2026 19:12"},
    {"type":"THANH_TOAN","content":"Yêu cầu thanh toán cho bàn 08.","table":"Bàn 08","order_code":"#DH10003","sent_time":"18/04/2026 19:15","status":"DA_XU_LY","handled_time":"18/04/2026 19:20"},
    {"type":"GOI_NHAN_VIEN","content":"Khách đã tự xử lý nên không cần hỗ trợ nữa.","table":"Bàn 08","order_code":"#DH10003","sent_time":"18/04/2026 18:40","status":"DA_HUY","handled_time":"18/04/2026 18:45"}
  ]
}
```

- [ ] **Step 2: Run asset test only**

Run:

```bash
JAVA_HOME="/opt/android-studio/jbr" ./gradlew testDebugUnitTest --tests com.example.quanlynhahang.SqliteDataSourceSpecTest.seedDataAsset_containsBusinessSeedRecords
```

Expected: PASS.

---

### Task 3: Convert SeedDataHelper to read seed JSON

**Files:**
- Modify: `app/src/main/java/com/example/quanlynhahang/data/SeedDataHelper.java`

- [ ] **Step 1: Replace hardcoded seed orchestration with JSON loading**

Update `damBaoDuLieuMacDinh(...)` to read `seed_data.json` and call importer methods. Add helpers:

```java
private static JSONObject docSeed(Context context) throws IOException, JSONException {
    try (InputStream inputStream = context.getAssets().open("seed_data.json")) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int read;
        while ((read = inputStream.read(data)) != -1) {
            buffer.write(data, 0, read);
        }
        return new JSONObject(buffer.toString(StandardCharsets.UTF_8.name()));
    }
}

private static String chuoi(JSONObject object, String key) throws JSONException {
    return object.optString(key, "");
}

private static String chuoiHoacResource(Context context, JSONObject object, String key, String resourceKey) throws JSONException {
    String value = object.optString(key, "");
    if (!TextUtils.isEmpty(value)) {
        return value;
    }
    String resName = object.optString(resourceKey, "");
    if (TextUtils.isEmpty(resName)) {
        return "";
    }
    int resId = context.getResources().getIdentifier(resName, "string", context.getPackageName());
    return resId == 0 ? "" : context.getString(resId);
}
```

- [ ] **Step 2: Import users from JSON**

Replace hardcoded `ensureTestUserExists(...)` and `damBaoTaiKhoanMauBoSung(...)` contents with loop over `users`:

```java
private static void damBaoNguoiDungTuJson(DatabaseHelper databaseHelper, Context context, SQLiteDatabase db, JSONArray users) throws JSONException {
    for (int i = 0; i < users.length(); i++) {
        JSONObject user = users.getJSONObject(i);
        ensureSeedUser(
                databaseHelper,
                db,
                chuoiHoacResource(context, user, "name", "name_res"),
                user.getString("email"),
                user.getString("phone"),
                user.getString("password"),
                VaiTroNguoiDung.valueOf(user.getString("role")),
                user.optBoolean("active", true)
        );
    }
}
```

- [ ] **Step 3: Import dishes, tables, reservations, orders, requests from JSON**

Implement JSON loops mirroring existing helper calls:

```java
private static void damBaoMonAnTuJson(DatabaseHelper databaseHelper, Context context, SQLiteDatabase db, JSONArray dishes) throws JSONException {
    for (int i = 0; i < dishes.length(); i++) {
        JSONObject dish = dishes.getJSONObject(i);
        taoMonAnNeuChuaCo(
                databaseHelper,
                db,
                chuoiHoacResource(context, dish, "name", "name_res"),
                chuoiHoacResource(context, dish, "price", "price_res"),
                chuoiHoacResource(context, dish, "description", "description_res"),
                dish.optString("image", TEN_ANH_MAC_DINH),
                dish.optBoolean("available", true),
                chuoiHoacResource(context, dish, "category", "category_res"),
                dish.optInt("score", 0)
        );
    }
}
```

For orders, build `DonHang.MonTrongDon` from `items` by matching dish name against `databaseHelper.layTatCaMonAn(db)`. For reservation/order/request references, reuse existing helper methods `layIdDatBanTheoMa(...)` and `layIdDonHangTheoMa(...)`.

- [ ] **Step 4: Keep idempotent helper methods, remove literal seed records**

Do not delete helper methods like `taoMonAnNeuChuaCo`, `taoDatBanNeuChuaCo`, `taoDonHangNeuChuaCo`, `taoYeuCauNeuChuaCo`, `ensureSeedUser`. Remove direct sample values from Java.

- [ ] **Step 5: Run focused source tests**

Run:

```bash
JAVA_HOME="/opt/android-studio/jbr" ./gradlew testDebugUnitTest --tests com.example.quanlynhahang.SqliteDataSourceSpecTest.seedDataHelper_doesNotContainBusinessSeedRecordsDirectly
```

Expected: PASS.

---

### Task 4: Add SQLite helper methods for categories and demo accounts

**Files:**
- Modify: `app/src/main/java/com/example/quanlynhahang/data/DatabaseHelper.java`

- [ ] **Step 1: Add `TaiKhoanDemo` value type**

Add inside `DatabaseHelper`:

```java
public static final class TaiKhoanDemo {
    public final String email;
    public final String matKhau;

    public TaiKhoanDemo(String email, String matKhau) {
        this.email = email;
        this.matKhau = matKhau;
    }
}
```

- [ ] **Step 2: Add category query**

Add public method:

```java
public List<String> layDanhMucMonAn() {
    List<String> categories = new ArrayList<>();
    Cursor cursor = null;
    try {
        cursor = getReadableDatabase().query(
                true,
                TABLE_DISH,
                new String[]{COL_DISH_CATEGORY},
                COL_DISH_IS_ARCHIVED + " = 0 AND " + COL_DISH_CATEGORY + " IS NOT NULL AND " + COL_DISH_CATEGORY + " != ?",
                new String[]{""},
                null,
                null,
                COL_DISH_CATEGORY + " COLLATE NOCASE ASC",
                null
        );
        while (cursor.moveToNext()) {
            categories.add(cursor.getString(cursor.getColumnIndexOrThrow(COL_DISH_CATEGORY)));
        }
    } finally {
        if (cursor != null) {
            cursor.close();
        }
    }
    return categories;
}
```

- [ ] **Step 3: Add demo-account lookup**

Add public method:

```java
@Nullable
public TaiKhoanDemo layTaiKhoanDemoTheoVaiTro(VaiTroNguoiDung vaiTro) {
    Cursor cursor = null;
    try {
        cursor = getReadableDatabase().query(
                TABLE_USER,
                new String[]{COL_USER_EMAIL, COL_USER_PASSWORD},
                COL_USER_ROLE + " = ? AND " + COL_USER_IS_ACTIVE + " = 1",
                new String[]{vaiTro.name()},
                null,
                null,
                COL_USER_ID + " ASC",
                "1"
        );
        if (cursor.moveToFirst()) {
            return new TaiKhoanDemo(
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PASSWORD))
            );
        }
    } finally {
        if (cursor != null) {
            cursor.close();
        }
    }
    return null;
}
```

- [ ] **Step 4: Run compile check**

Run:

```bash
JAVA_HOME="/opt/android-studio/jbr" ./gradlew compileDebugJavaWithJavac
```

Expected: BUILD SUCCESSFUL.

---

### Task 5: Update home categories to use SQLite

**Files:**
- Modify: `app/src/main/java/com/example/quanlynhahang/TrangChuFragment.java`

- [ ] **Step 1: Replace static category additions**

Replace `thietLapDuLieuDanhMuc()` with:

```java
private void thietLapDuLieuDanhMuc() {
    danhSachDanhMuc.clear();
    for (String tenDanhMuc : databaseHelper.layDanhMucMonAn()) {
        danhSachDanhMuc.add(new DanhMucMon(
                layIconDanhMuc(tenDanhMuc),
                tenDanhMuc,
                tenDanhMuc
        ));
    }
}

private int layIconDanhMuc(@Nullable String tenDanhMuc) {
    if (TextUtils.equals(tenDanhMuc, getString(R.string.category_hotpot))) {
        return R.drawable.ic_receipt_24;
    }
    if (TextUtils.equals(tenDanhMuc, getString(R.string.category_drink))) {
        return R.drawable.ic_local_drink_24;
    }
    if (TextUtils.equals(tenDanhMuc, getString(R.string.category_salad))) {
        return R.drawable.ic_calendar_24;
    }
    return R.drawable.ic_restaurant_24;
}
```

Ensure `android.text.TextUtils` is imported.

- [ ] **Step 2: Run focused source test**

Run:

```bash
JAVA_HOME="/opt/android-studio/jbr" ./gradlew testDebugUnitTest --tests com.example.quanlynhahang.SqliteDataSourceSpecTest.homeCategories_areNotHardcodedInFragment
```

Expected: PASS.

---

### Task 6: Update table selectors to use `ban_an`

**Files:**
- Modify: `app/src/main/java/com/example/quanlynhahang/DatBanFragment.java`
- Modify: `app/src/main/java/com/example/quanlynhahang/DatBanNoiBoFragment.java`
- Modify: `app/src/main/java/com/example/quanlynhahang/GioHangActivity.java`

- [ ] **Step 1: Update `DatBanFragment`**

Remove `SO_BAN_TOI_DA`. In `capNhatDanhSachBanTheoKhungGio()`, replace loop with:

```java
List<BanAn> tatCaBan = databaseHelper.layTatCaBanAn();
for (BanAn banAn : tatCaBan) {
    String tenBan = banAn.layTenBan();
    if (!occupiedTableSet.contains(tenBan)) {
        tableOptions.add(tenBan);
    }
}
```

Replace all-table-available condition with:

```java
int tongSoBan = databaseHelper.layTatCaBanAn().size();
if (tableOptions.isEmpty()) {
    tvReservationAvailableTables.setText(getString(R.string.reservation_no_tables_available));
} else if (tableOptions.size() == tongSoBan) {
    tvReservationAvailableTables.setText(getString(R.string.reservation_all_tables_available));
} else {
    tvReservationAvailableTables.setText(getString(
            R.string.reservation_available_tables_format,
            tableOptions.size(),
            TextUtils.join(", ", tableOptions)
    ));
}
```

Keep guest limit `SO_KHACH_TOI_DA`; this is a validation rule, not table inventory.

- [ ] **Step 2: Update `DatBanNoiBoFragment`**

Replace `for (int soBan = 1; soBan <= 20; soBan++)` with:

```java
for (BanAn banAn : databaseHelper.layTatCaBanAn()) {
    String tenBan = banAn.layTenBan();
    if (tenBan.equalsIgnoreCase(datBan.laySoBan())
            || !databaseHelper.layDanhSachBanDaDat(datBan.layThoiGian(), datBan.layId()).contains(tenBan)) {
        danhSachBanTrong.add(tenBan);
    }
}
```

Import `com.example.quanlynhahang.model.BanAn` if missing.

- [ ] **Step 3: Update `GioHangActivity`**

Remove `SO_BAN_TOI_DA`. Replace the loop in `taoDanhSachBanUi()` with:

```java
for (BanAn banAn : databaseHelper.layTatCaBanAn()) {
    String tenBan = banAn.layTenBan();
    boolean laBanHienTai = DichVuKhachHangHelper.laBanHienTai(banHienTai, tenBan);
    boolean dangDung = banDangDung.contains(tenBan) && !laBanHienTai;
    boolean daGiu = banDaGiu.contains(tenBan) && !laBanHienTai && !dangDung;
    LoaiTrangThaiBan loaiTrangThai = laBanHienTai
            ? LoaiTrangThaiBan.BAN_HIEN_TAI
            : dangDung ? LoaiTrangThaiBan.DANG_DUNG
            : daGiu ? LoaiTrangThaiBan.DA_GIU
            : LoaiTrangThaiBan.TRONG;
    danhSachBan.add(new MucBanUi(
            tenBan,
            loaiTrangThai,
            loaiTrangThai == LoaiTrangThaiBan.TRONG || loaiTrangThai == LoaiTrangThaiBan.BAN_HIEN_TAI,
            laBanHienTai,
            layNhanTrangThaiBan(loaiTrangThai)
    ));
}
```

Import `com.example.quanlynhahang.model.BanAn` if missing.

- [ ] **Step 4: Run focused source test**

Run:

```bash
JAVA_HOME="/opt/android-studio/jbr" ./gradlew testDebugUnitTest --tests com.example.quanlynhahang.SqliteDataSourceSpecTest.tableSelectors_doNotGenerateOneToTwentyTables
```

Expected: PASS.

---

### Task 7: Update quick login to resolve from SQLite

**Files:**
- Modify: `app/src/main/java/com/example/quanlynhahang/DangNhapActivity.java`

- [ ] **Step 1: Remove hardcoded credential constants**

Delete:

```java
private static final String TAI_KHOAN_KHACH_HANG_MAC_DINH = "customer@example.invalid";
private static final String TAI_KHOAN_NHAN_VIEN_MAC_DINH = "staff@example.invalid";
private static final String TAI_KHOAN_ADMIN_MAC_DINH = "admin@example.invalid";
private static final String MAT_KHAU_MAC_DINH = "1";
```

- [ ] **Step 2: Update quick login listeners**

Replace listeners with:

```java
nutDangNhapNhanhKhachHang.setOnClickListener(v -> dangNhapMacDinh(VaiTroNguoiDung.KHACH_HANG, chiChoPhepPhienKhachHang));
nutDangNhapNhanhNhanVien.setOnClickListener(v -> dangNhapMacDinh(VaiTroNguoiDung.NHAN_VIEN, chiChoPhepPhienKhachHang));
nutDangNhapNhanhQuanTri.setOnClickListener(v -> dangNhapMacDinh(VaiTroNguoiDung.ADMIN, chiChoPhepPhienKhachHang));
```

Replace `dangNhapMacDinh` with:

```java
private void dangNhapMacDinh(VaiTroNguoiDung vaiTro, boolean chiChoPhepPhienKhachHang) {
    DatabaseHelper.TaiKhoanDemo taiKhoanDemo = databaseHelper.layTaiKhoanDemoTheoVaiTro(vaiTro);
    if (taiKhoanDemo == null) {
        Toast.makeText(this, R.string.login_failed, Toast.LENGTH_SHORT).show();
        return;
    }
    oNhapEmailDangNhap.setText(taiKhoanDemo.email);
    oNhapMatKhauDangNhap.setText(taiKhoanDemo.matKhau);
    xuLyDangNhap(chiChoPhepPhienKhachHang);
}
```

- [ ] **Step 3: Run focused source test**

Run:

```bash
JAVA_HOME="/opt/android-studio/jbr" ./gradlew testDebugUnitTest --tests com.example.quanlynhahang.SqliteDataSourceSpecTest.quickLoginCredentials_areResolvedFromDatabase
```

Expected: PASS.

---

### Task 8: Full verification and cleanup

**Files:**
- No new files beyond tasks above.

- [ ] **Step 1: Run all focused SQLite source tests**

Run:

```bash
JAVA_HOME="/opt/android-studio/jbr" ./gradlew testDebugUnitTest --tests com.example.quanlynhahang.SqliteDataSourceSpecTest
```

Expected: BUILD SUCCESSFUL.

- [ ] **Step 2: Run existing admin account tests**

Run:

```bash
JAVA_HOME="/opt/android-studio/jbr" ./gradlew testDebugUnitTest --tests com.example.quanlynhahang.AdminUserManagementUiSpecTest
```

Expected: BUILD SUCCESSFUL.

- [ ] **Step 3: Run full unit tests**

Run:

```bash
JAVA_HOME="/opt/android-studio/jbr" ./gradlew testDebugUnitTest
```

Expected: BUILD SUCCESSFUL.

- [ ] **Step 4: Build debug APK**

Run:

```bash
JAVA_HOME="/opt/android-studio/jbr" ./gradlew assembleDebug
```

Expected: BUILD SUCCESSFUL.

- [ ] **Step 5: Inspect git diff**

Run:

```bash
git status --short && git diff -- app/src/main/java app/src/main/assets app/src/test docs/superpowers
```

Expected: Changes limited to the planned files.

---

## Self-Review

- Spec coverage: asset seed, importer, table selectors, home categories, quick login, tests, and verification are each covered by tasks.
- Placeholder scan: no TBD/TODO placeholders remain; task code and commands are explicit.
- Type consistency: methods referenced later are defined earlier: `layDanhMucMonAn`, `layTaiKhoanDemoTheoVaiTro`, `TaiKhoanDemo`.
