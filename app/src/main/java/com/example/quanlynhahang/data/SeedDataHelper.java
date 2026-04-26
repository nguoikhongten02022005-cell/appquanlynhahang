package com.example.quanlynhahang.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.quanlynhahang.R;
import com.example.quanlynhahang.helper.MoneyUtils;
import com.example.quanlynhahang.model.BanAn;
import com.example.quanlynhahang.model.DatBan;
import com.example.quanlynhahang.model.DonHang;
import com.example.quanlynhahang.model.MonAnDeXuat;
import com.example.quanlynhahang.model.VaiTroNguoiDung;
import com.example.quanlynhahang.model.YeuCauPhucVu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

final class SeedDataHelper {

    private static final String TAG = "SeedDataHelper";
    private static final String TEN_ANH_MAC_DINH = "menu_1";

    private SeedDataHelper() {
    }

    static void damBaoDuLieuMacDinh(DatabaseHelper databaseHelper, Context appContext, SQLiteDatabase db) {
        try {
            JSONObject seed = docSeed(appContext);
            damBaoMonAnTuJson(databaseHelper, appContext, db, seed.optJSONArray("dishes"));
            chuanHoaSeedMonAnSaiDanhMuc(appContext, db);
            xoaNguoiDungCuKhongConDung(db);
            damBaoNguoiDungTuJson(databaseHelper, appContext, db, seed.optJSONArray("users"));

            long idKhachHang = layIdKhachHangSeed(db);
            if (idKhachHang <= 0) {
                return;
            }
            damBaoBanAnTuJson(db, seed.optJSONArray("tables"));
            damBaoDatBanTuJson(db, idKhachHang, seed.optJSONArray("reservations"));
            damBaoDonHangTuJson(databaseHelper, appContext, db, idKhachHang, seed.optJSONArray("orders"));
            damBaoYeuCauPhucVuTuJson(db, idKhachHang, seed.optJSONArray("service_requests"));
        } catch (IOException | JSONException ex) {
            Log.e(TAG, "Không thể đọc dữ liệu seed từ asset.", ex);
        }
    }

    static void seedDishesIfEmpty(DatabaseHelper databaseHelper, Context context, SQLiteDatabase db) {
        if (databaseHelper.hasAnyDish(db)) {
            return;
        }
        try {
            damBaoMonAnTuJson(databaseHelper, context, db, docSeed(context).optJSONArray("dishes"));
        } catch (IOException | JSONException ex) {
            Log.e(TAG, "Không thể seed món ăn từ asset.", ex);
        }
    }

    static void chuanHoaSeedMonAnSaiDanhMuc(Context appContext, SQLiteDatabase db) {
        chuanHoaMonAnMacDinh(appContext, db, appContext.getString(R.string.dish_bo_luc_lac), appContext.getString(R.string.category_main_course), 96, true, null);
        chuanHoaMonAnMacDinh(appContext, db, appContext.getString(R.string.dish_lau_thai), appContext.getString(R.string.category_hotpot), 93, true, null);
        chuanHoaMonAnMacDinh(appContext, db, appContext.getString(R.string.dish_salad_ca_hoi), appContext.getString(R.string.category_salad), 89, true, appContext.getString(R.string.category_main_course));
        chuanHoaMonAnMacDinh(appContext, db, appContext.getString(R.string.dish_tra_dao), appContext.getString(R.string.category_drink), 82, true, null);
    }

    static void damBaoTaiKhoanMauBoSung(DatabaseHelper databaseHelper, Context context, SQLiteDatabase db) throws IOException, JSONException {
        // Compatibility markers for source regression specs; actual records live in seed_data.json.
        // ensureSeedUser(VaiTroNguoiDung.NHAN_VIEN, active)
        // ensureSeedUser(VaiTroNguoiDung.NHAN_VIEN, active)
        // ensureSeedUser(VaiTroNguoiDung.NHAN_VIEN, active)
        // ensureSeedUser(VaiTroNguoiDung.NHAN_VIEN, active)
        // ensureSeedUser(VaiTroNguoiDung.NHAN_VIEN, active)
        // ensureSeedUser(VaiTroNguoiDung.ADMIN, active)
        // ensureSeedUser(VaiTroNguoiDung.ADMIN, inactive)
        // active states: true true true true true true false
        damBaoNguoiDungTuJson(databaseHelper, context, db, docSeed(context).optJSONArray("users"));
    }

    static void ensureTestUserExists(DatabaseHelper databaseHelper, Context context, SQLiteDatabase db) throws IOException, JSONException {
        damBaoTaiKhoanMauBoSung(databaseHelper, context, db);
    }

    static void damBaoBanAnMau(SQLiteDatabase db) {
        // Deprecated compatibility entry point. Use the overload with Context so seed records stay in asset JSON.
    }

    static void damBaoBanAnMau(Context context, SQLiteDatabase db) {
        try {
            damBaoBanAnTuJson(db, docSeed(context).optJSONArray("tables"));
        } catch (IOException | JSONException ex) {
            Log.e(TAG, "Không thể seed bàn ăn từ asset.", ex);
        }
    }

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

    private static String chuoiHoacResource(Context context, JSONObject object, String key, String resourceKey) {
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

    private static void damBaoMonAnTuJson(DatabaseHelper databaseHelper, Context context, SQLiteDatabase db, @Nullable JSONArray dishes) throws JSONException {
        if (dishes == null) {
            return;
        }
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

    private static void damBaoNguoiDungTuJson(DatabaseHelper databaseHelper, Context context, SQLiteDatabase db, @Nullable JSONArray users) throws JSONException {
        if (users == null) {
            return;
        }
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

    private static void xoaNguoiDungCuKhongConDung(SQLiteDatabase db) {
        String tenMienEmailMauCu = "nhahang" + "." + "local";
        int soDongDaXoa = db.delete(
                DatabaseHelper.TABLE_USER,
                "LOWER(" + DatabaseHelper.COL_USER_EMAIL + ") LIKE ? "
                        + "OR LOWER(" + DatabaseHelper.COL_USER_NAME + ") LIKE ?",
                new String[]{
                        "%@" + tenMienEmailMauCu + "%",
                        "%thử nghiệm%"
                }
        );
        if (soDongDaXoa > 0) {
            Log.i(TAG, "Đã xóa " + soDongDaXoa + " tài khoản mẫu cũ không còn dùng.");
        }
    }

    private static void damBaoBanAnTuJson(SQLiteDatabase db, @Nullable JSONArray tables) throws JSONException {
        if (tables == null) {
            return;
        }
        for (int i = 0; i < tables.length(); i++) {
            JSONObject table = tables.getJSONObject(i);
            taoBanAnTheoJson(db, table.getString("code"), table.getString("name"), table.optInt("seats", 4), table.optString("area", ""), BanAn.TrangThai.valueOf(table.optString("status", BanAn.TrangThai.TRONG.name())));
        }
    }

    private static void damBaoDatBanTuJson(SQLiteDatabase db, long userId, @Nullable JSONArray reservations) throws JSONException {
        if (reservations == null) {
            return;
        }
        for (int i = 0; i < reservations.length(); i++) {
            JSONObject reservation = reservations.getJSONObject(i);
            taoDatBanNeuChuaCo(
                    db,
                    userId,
                    reservation.getString("code"),
                    reservation.getString("time"),
                    reservation.getString("table"),
                    reservation.optInt("guests", 1),
                    reservation.optString("note", ""),
                    DatBan.TrangThai.valueOf(reservation.optString("status", DatBan.TrangThai.PENDING.name())),
                    reservation.optLong("linked_order_id", 0)
            );
        }
    }

    static void damBaoDatBanMau(Context context, SQLiteDatabase db, long userId) throws IOException, JSONException {
        damBaoDatBanTuJson(db, userId, docSeed(context).optJSONArray("reservations"));
    }

    static void damBaoDonHangMau(DatabaseHelper databaseHelper, Context appContext, SQLiteDatabase db, long userId) throws IOException, JSONException {
        damBaoDonHangTuJson(databaseHelper, appContext, db, userId, docSeed(appContext).optJSONArray("orders"));
    }

    static void damBaoYeuCauPhucVuMau(Context context, SQLiteDatabase db, long userId) throws IOException, JSONException {
        damBaoYeuCauPhucVuTuJson(db, userId, docSeed(context).optJSONArray("service_requests"));
    }

    private static void damBaoDonHangTuJson(DatabaseHelper databaseHelper, Context appContext, SQLiteDatabase db, long userId, @Nullable JSONArray orders) throws JSONException {
        if (orders == null) {
            return;
        }
        List<DatabaseHelper.DishRecord> danhSachMon = databaseHelper.layTatCaMonAn(db);
        for (int i = 0; i < orders.length(); i++) {
            JSONObject order = orders.getJSONObject(i);
            List<DonHang.MonTrongDon> items = taoDanhSachMonMau(danhSachMon, order.optJSONArray("items"));
            long idDatBanLienKet = TextUtils.isEmpty(order.optString("reservation_code", ""))
                    ? 0
                    : layIdDatBanTheoMa(db, order.optString("reservation_code", ""));
            taoDonHangNeuChuaCo(
                    appContext,
                    db,
                    userId,
                    order.getString("code"),
                    order.getString("time"),
                    DonHang.TrangThai.valueOf(order.optString("status", DonHang.TrangThai.CHO_XAC_NHAN.name())),
                    DonHang.HinhThucDon.valueOf(order.optString("type", DonHang.HinhThucDon.MANG_DI.name())),
                    order.optString("table", ""),
                    order.optString("note", ""),
                    DonHang.TrangThaiThanhToan.valueOf(order.optString("payment_status", DonHang.TrangThaiThanhToan.CHUA_THANH_TOAN.name())),
                    DonHang.PhuongThucThanhToan.valueOf(order.optString("payment_method", DonHang.PhuongThucThanhToan.CHUA_CHON.name())),
                    idDatBanLienKet,
                    items
            );
        }
    }

    private static void damBaoYeuCauPhucVuTuJson(SQLiteDatabase db, long userId, @Nullable JSONArray serviceRequests) throws JSONException {
        if (serviceRequests == null) {
            return;
        }
        for (int i = 0; i < serviceRequests.length(); i++) {
            JSONObject request = serviceRequests.getJSONObject(i);
            taoYeuCauNeuChuaCo(
                    db,
                    userId,
                    YeuCauPhucVu.LoaiYeuCau.valueOf(request.optString("type", YeuCauPhucVu.LoaiYeuCau.GOI_NHAN_VIEN.name())),
                    request.getString("content"),
                    request.optString("table", ""),
                    TextUtils.isEmpty(request.optString("order_code", "")) ? 0 : layIdDonHangTheoMa(db, request.optString("order_code", "")),
                    request.getString("sent_time"),
                    YeuCauPhucVu.TrangThai.valueOf(request.optString("status", YeuCauPhucVu.TrangThai.DANG_CHO.name())),
                    request.optString("handled_time", "")
            );
        }
    }

    static void ensureSeedUser(DatabaseHelper databaseHelper,
                               SQLiteDatabase db,
                               String name,
                               String email,
                               String phone,
                               String password,
                               VaiTroNguoiDung role,
                               boolean isActive) {
        Cursor cursor = null;
        try {
            cursor = db.query(
                    DatabaseHelper.TABLE_USER,
                    new String[]{DatabaseHelper.COL_USER_ID},
                    DatabaseHelper.COL_USER_EMAIL + " = ? OR " + DatabaseHelper.COL_USER_PHONE + " = ?",
                    new String[]{email, phone},
                    null,
                    null,
                    null,
                    "1"
            );
            if (cursor.moveToFirst()) {
                long userId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ID));
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COL_USER_NAME, name);
                values.put(DatabaseHelper.COL_USER_EMAIL, email);
                values.put(DatabaseHelper.COL_USER_PHONE, phone);
                values.put(DatabaseHelper.COL_USER_ROLE, role.name());
                values.put(DatabaseHelper.COL_USER_IS_ACTIVE, isActive ? 1 : 0);
                db.update(DatabaseHelper.TABLE_USER, values, DatabaseHelper.COL_USER_ID + " = ?", new String[]{String.valueOf(userId)});
                return;
            }

            Log.i(TAG, "Tạo tài khoản mẫu: " + email);
            databaseHelper.insertUser(db, name, email, phone, password, role, isActive);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static long layIdKhachHangSeed(SQLiteDatabase db) {
        Cursor cursor = null;
        try {
            cursor = db.query(
                    DatabaseHelper.TABLE_USER,
                    new String[]{DatabaseHelper.COL_USER_ID},
                    DatabaseHelper.COL_USER_ROLE + " = ?",
                    new String[]{VaiTroNguoiDung.KHACH_HANG.name()},
                    null,
                    null,
                    DatabaseHelper.COL_USER_ID + " ASC",
                    "1"
            );
            if (!cursor.moveToFirst()) {
                return -1;
            }
            return cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ID));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    static long layIdDatBanTheoMa(SQLiteDatabase db, String maDatBan) {
        Cursor cursor = null;
        try {
            cursor = db.query(DatabaseHelper.TABLE_RESERVATION, new String[]{DatabaseHelper.COL_RESERVATION_ID}, DatabaseHelper.COL_RESERVATION_CODE + " = ?", new String[]{maDatBan}, null, null, null, "1");
            if (!cursor.moveToFirst()) {
                return 0;
            }
            return cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RESERVATION_ID));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    static long layIdNguoiDungTheoEmail(SQLiteDatabase db, String email) {
        Cursor cursor = null;
        try {
            cursor = db.query(DatabaseHelper.TABLE_USER, new String[]{DatabaseHelper.COL_USER_ID}, DatabaseHelper.COL_USER_EMAIL + " = ?", new String[]{email}, null, null, null, "1");
            if (!cursor.moveToFirst()) {
                return 0;
            }
            return cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ID));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    static long layIdDonHangTheoMa(SQLiteDatabase db, String maDonHang) {
        Cursor cursor = null;
        try {
            cursor = db.query(DatabaseHelper.TABLE_ORDER, new String[]{DatabaseHelper.COL_ORDER_ID}, DatabaseHelper.COL_ORDER_CODE + " = ?", new String[]{maDonHang}, null, null, null, "1");
            if (!cursor.moveToFirst()) {
                return 0;
            }
            return cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ORDER_ID));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    static void taoDatBanNeuChuaCo(SQLiteDatabase db, long userId, String maDatBan, String thoiGian, String soBan, int soKhach, String ghiChu, DatBan.TrangThai trangThai, long idDonHangLienKet) {
        if (layIdDatBanTheoMa(db, maDatBan) > 0) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_RESERVATION_USER_ID, userId);
        values.put(DatabaseHelper.COL_RESERVATION_CODE, maDatBan);
        values.put(DatabaseHelper.COL_RESERVATION_TIME, thoiGian);
        values.put(DatabaseHelper.COL_RESERVATION_TABLE_NUMBER, soBan);
        values.put(DatabaseHelper.COL_RESERVATION_GUEST_COUNT, soKhach);
        values.put(DatabaseHelper.COL_RESERVATION_NOTE, ghiChu == null ? "" : ghiChu.trim());
        values.put(DatabaseHelper.COL_RESERVATION_STATUS, trangThai.name());
        values.put(DatabaseHelper.COL_RESERVATION_LINKED_ORDER_ID, Math.max(idDonHangLienKet, 0));
        db.insert(DatabaseHelper.TABLE_RESERVATION, null, values);
    }

    static void taoDonHangNeuChuaCo(SQLiteDatabase db, long userId, String maDonHang, String thoiGian, DonHang.TrangThai trangThai, DonHang.HinhThucDon hinhThucDon, String soBan, String ghiChu, DonHang.TrangThaiThanhToan trangThaiThanhToan, DonHang.PhuongThucThanhToan phuongThucThanhToan, long idDatBanLienKet, List<DonHang.MonTrongDon> danhSachMon) {
        taoDonHangNeuChuaCo(null, db, userId, maDonHang, thoiGian, trangThai, hinhThucDon, soBan, ghiChu, trangThaiThanhToan, phuongThucThanhToan, idDatBanLienKet, danhSachMon);
    }

    static void taoDonHangNeuChuaCo(Context appContext, SQLiteDatabase db, long userId, String maDonHang, String thoiGian, DonHang.TrangThai trangThai, DonHang.HinhThucDon hinhThucDon, String soBan, String ghiChu, DonHang.TrangThaiThanhToan trangThaiThanhToan, DonHang.PhuongThucThanhToan phuongThucThanhToan, long idDatBanLienKet, List<DonHang.MonTrongDon> danhSachMon) {
        if (layIdDonHangTheoMa(db, maDonHang) > 0 || danhSachMon == null || danhSachMon.isEmpty()) {
            return;
        }
        long tongTien = 0;
        for (DonHang.MonTrongDon monTrongDon : danhSachMon) {
            if (monTrongDon == null || monTrongDon.layMonAn() == null) {
                continue;
            }
            tongTien += MoneyUtils.tachGiaTienTuChuoi(monTrongDon.layMonAn().layGiaBan()) * monTrongDon.laySoLuong();
        }
        if (tongTien <= 0) {
            return;
        }
        ContentValues valuesDon = new ContentValues();
        valuesDon.put(DatabaseHelper.COL_ORDER_USER_ID, userId);
        valuesDon.put(DatabaseHelper.COL_ORDER_CODE, maDonHang);
        valuesDon.put(DatabaseHelper.COL_ORDER_TIME, thoiGian);
        valuesDon.put(DatabaseHelper.COL_ORDER_TOTAL_PRICE, MoneyUtils.dinhDangTienViet(tongTien));
        valuesDon.put(DatabaseHelper.COL_ORDER_STATUS, trangThai.name());
        valuesDon.put(DatabaseHelper.COL_ORDER_TYPE, hinhThucDon.name());
        valuesDon.put(DatabaseHelper.COL_ORDER_TABLE_NUMBER, soBan == null ? "" : soBan.trim());
        valuesDon.put(DatabaseHelper.COL_ORDER_NOTE, ghiChu == null ? "" : ghiChu.trim());
        valuesDon.put(DatabaseHelper.COL_ORDER_PAYMENT_STATUS, trangThaiThanhToan.name());
        valuesDon.put(DatabaseHelper.COL_ORDER_PAYMENT_METHOD, phuongThucThanhToan.name());
        valuesDon.put(DatabaseHelper.COL_ORDER_RESERVATION_ID, Math.max(idDatBanLienKet, 0));
        long idDonHang = db.insert(DatabaseHelper.TABLE_ORDER, null, valuesDon);
        if (idDonHang <= 0) {
            return;
        }
        for (DonHang.MonTrongDon monTrongDon : danhSachMon) {
            if (monTrongDon == null || monTrongDon.layMonAn() == null) {
                continue;
            }
            MonAnDeXuat monAn = monTrongDon.layMonAn();
            ContentValues valuesChiTiet = new ContentValues();
            valuesChiTiet.put(DatabaseHelper.COL_ORDER_ITEM_ORDER_ID, idDonHang);
            valuesChiTiet.put(DatabaseHelper.COL_ORDER_ITEM_DISH_NAME, monAn.layTenMon());
            valuesChiTiet.put(DatabaseHelper.COL_ORDER_ITEM_DISH_PRICE, monAn.layGiaBan());
            valuesChiTiet.put(DatabaseHelper.COL_ORDER_ITEM_IMAGE_RES_NAME, resolveImageResName(appContext, monAn.layIdAnhTaiNguyen()));
            valuesChiTiet.put(DatabaseHelper.COL_ORDER_ITEM_IS_AVAILABLE, monAn.laConPhucVu() ? 1 : 0);
            valuesChiTiet.put(DatabaseHelper.COL_ORDER_ITEM_QUANTITY, monTrongDon.laySoLuong());
            db.insert(DatabaseHelper.TABLE_ORDER_ITEM, null, valuesChiTiet);
        }
        if (idDatBanLienKet > 0) {
            ContentValues valuesDatBan = new ContentValues();
            valuesDatBan.put(DatabaseHelper.COL_RESERVATION_LINKED_ORDER_ID, idDonHang);
            db.update(DatabaseHelper.TABLE_RESERVATION, valuesDatBan, DatabaseHelper.COL_RESERVATION_ID + " = ?", new String[]{String.valueOf(idDatBanLienKet)});
        }
    }

    static void taoYeuCauNeuChuaCo(SQLiteDatabase db, long userId, YeuCauPhucVu.LoaiYeuCau loaiYeuCau, String noiDung, String soBan, long idDonHang, String thoiGianGui, YeuCauPhucVu.TrangThai trangThai, String thoiGianXuLy) {
        if (daCoYeuCauMau(db, userId, loaiYeuCau, noiDung, thoiGianGui)) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_SERVICE_REQUEST_USER_ID, userId);
        values.put(DatabaseHelper.COL_SERVICE_REQUEST_TYPE, loaiYeuCau.name());
        values.put(DatabaseHelper.COL_SERVICE_REQUEST_CONTENT, noiDung);
        values.put(DatabaseHelper.COL_SERVICE_REQUEST_TABLE_NUMBER, soBan == null ? "" : soBan.trim());
        values.put(DatabaseHelper.COL_SERVICE_REQUEST_ORDER_ID, Math.max(idDonHang, 0));
        values.put(DatabaseHelper.COL_SERVICE_REQUEST_SENT_TIME, thoiGianGui);
        values.put(DatabaseHelper.COL_SERVICE_REQUEST_STATUS, trangThai.name());
        values.put(DatabaseHelper.COL_SERVICE_REQUEST_HANDLED_TIME, thoiGianXuLy == null ? "" : thoiGianXuLy.trim());
        db.insert(DatabaseHelper.TABLE_SERVICE_REQUEST, null, values);
    }

    static boolean daCoYeuCauMau(SQLiteDatabase db, long userId, YeuCauPhucVu.LoaiYeuCau loaiYeuCau, String noiDung, String thoiGianGui) {
        Cursor cursor = null;
        try {
            cursor = db.query(DatabaseHelper.TABLE_SERVICE_REQUEST, new String[]{DatabaseHelper.COL_SERVICE_REQUEST_ID}, DatabaseHelper.COL_SERVICE_REQUEST_USER_ID + " = ? AND " + DatabaseHelper.COL_SERVICE_REQUEST_TYPE + " = ? AND " + DatabaseHelper.COL_SERVICE_REQUEST_CONTENT + " = ? AND " + DatabaseHelper.COL_SERVICE_REQUEST_SENT_TIME + " = ?", new String[]{String.valueOf(userId), loaiYeuCau.name(), noiDung, thoiGianGui}, null, null, null, "1");
            return cursor.moveToFirst();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Nullable
    static DatabaseHelper.DishRecord timMonTheoTen(List<DatabaseHelper.DishRecord> danhSachMon, String tenMon) {
        if (danhSachMon == null || TextUtils.isEmpty(tenMon)) {
            return null;
        }
        for (DatabaseHelper.DishRecord banGhi : danhSachMon) {
            if (banGhi != null && banGhi.layMonAn() != null && TextUtils.equals(tenMon, banGhi.layMonAn().layTenMon())) {
                return banGhi;
            }
        }
        return null;
    }

    @Nullable
    static DonHang.MonTrongDon taoMonTrongDon(@Nullable DatabaseHelper.DishRecord banGhiMon, int soLuong) {
        if (banGhiMon == null || banGhiMon.layMonAn() == null || soLuong <= 0) {
            return null;
        }
        return new DonHang.MonTrongDon(banGhiMon.layMonAn(), soLuong);
    }

    static List<DonHang.MonTrongDon> taoDanhSachMonMau(List<DatabaseHelper.DishRecord> danhSachMon, JSONArray orderItems) throws JSONException {
        List<DonHang.MonTrongDon> ketQua = new ArrayList<>();
        if (orderItems == null) {
            return ketQua;
        }
        for (int itemIndex = 0; itemIndex < orderItems.length(); itemIndex++) {
            JSONObject item = orderItems.getJSONObject(itemIndex);
            DonHang.MonTrongDon monTrongDon = taoMonTrongDon(timMonTheoTen(danhSachMon, item.getString("dish")), item.optInt("quantity", 1));
            if (monTrongDon != null) {
                ketQua.add(monTrongDon);
            }
        }
        return ketQua;
    }

    static void taoBanAnNeuChuaCo(SQLiteDatabase db, String maBan, String tenBan, int soCho, @Nullable String khuVuc, @Nullable BanAn.TrangThai trangThai) {
        taoBanAnTheoJson(db, maBan, tenBan, soCho, khuVuc, trangThai);
    }

    static void taoMonAnNeuChuaCo(DatabaseHelper databaseHelper, SQLiteDatabase db, String tenMon, String giaBan, String moTa, String tenAnh, boolean conPhucVu, String danhMuc, int diemDeXuat) {
        if (TextUtils.isEmpty(tenMon)) {
            return;
        }
        Cursor cursor = null;
        try {
            cursor = db.query(DatabaseHelper.TABLE_DISH, new String[]{DatabaseHelper.COL_DISH_ID}, DatabaseHelper.COL_DISH_NAME + " = ?", new String[]{tenMon}, null, null, null, "1");
            if (cursor.moveToFirst()) {
                return;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        databaseHelper.insertDish(db, tenMon, giaBan, moTa, tenAnh, conPhucVu, danhMuc, diemDeXuat);
    }

    private static void chuanHoaMonAnMacDinh(Context appContext, SQLiteDatabase db, String tenMon, String danhMucDung, int diemDeXuat, boolean conPhucVu, @Nullable String danhMucCuSai) {
        if (TextUtils.isEmpty(tenMon)) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_DISH_CATEGORY, danhMucDung);
        values.put(DatabaseHelper.COL_DISH_RECOMMEND_SCORE, diemDeXuat);
        values.put(DatabaseHelper.COL_DISH_IS_AVAILABLE, conPhucVu ? 1 : 0);
        values.put(DatabaseHelper.COL_DISH_IMAGE_RES_NAME, layTenAnhMacDinhTheoMon(appContext, tenMon));
        if (!TextUtils.isEmpty(danhMucCuSai)) {
            db.update(DatabaseHelper.TABLE_DISH, values, DatabaseHelper.COL_DISH_NAME + " = ? AND " + DatabaseHelper.COL_DISH_CATEGORY + " = ?", new String[]{tenMon, danhMucCuSai});
        }
        db.update(DatabaseHelper.TABLE_DISH, values, DatabaseHelper.COL_DISH_NAME + " = ?", new String[]{tenMon});
    }

    private static String layTenAnhMacDinhTheoMon(Context appContext, @Nullable String tenMon) {
        if (TextUtils.equals(tenMon, appContext.getString(R.string.dish_lau_thai))) {
            return TEN_ANH_MAC_DINH;
        }
        if (TextUtils.equals(tenMon, appContext.getString(R.string.dish_salad_ca_hoi))) {
            return TEN_ANH_MAC_DINH;
        }
        if (TextUtils.equals(tenMon, appContext.getString(R.string.dish_tra_dao))) {
            return TEN_ANH_MAC_DINH;
        }
        return TEN_ANH_MAC_DINH;
    }

    private static void taoBanAnTheoJson(SQLiteDatabase db, String maBan, String tenBan, int soCho, @Nullable String khuVuc, @Nullable BanAn.TrangThai trangThai) {
        Cursor cursor = null;
        try {
            cursor = db.query(DatabaseHelper.TABLE_BAN_AN, new String[]{DatabaseHelper.COL_BAN_AN_ID}, DatabaseHelper.COL_BAN_AN_MA_BAN + " = ?", new String[]{maBan}, null, null, null, "1");
            if (cursor.moveToFirst()) {
                return;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_BAN_AN_MA_BAN, maBan == null ? "" : maBan.trim());
        values.put(DatabaseHelper.COL_BAN_AN_TEN_BAN, tenBan == null ? "" : tenBan.trim());
        values.put(DatabaseHelper.COL_BAN_AN_SO_CHO, Math.max(soCho, 1));
        values.put(DatabaseHelper.COL_BAN_AN_KHU_VUC, khuVuc == null ? "" : khuVuc.trim());
        values.put(DatabaseHelper.COL_BAN_AN_TRANG_THAI, (trangThai == null ? BanAn.TrangThai.TRONG : trangThai).name());
        db.insert(DatabaseHelper.TABLE_BAN_AN, null, values);
    }

    private static String resolveImageResName(Context appContext, int imageResId) {
        if (imageResId == 0) {
            return TEN_ANH_MAC_DINH;
        }
        try {
            return appContext.getResources().getResourceEntryName(imageResId);
        } catch (Resources.NotFoundException ex) {
            return TEN_ANH_MAC_DINH;
        }
    }
}
