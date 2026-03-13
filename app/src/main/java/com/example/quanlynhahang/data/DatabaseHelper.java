package com.example.quanlynhahang.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.quanlynhahang.R;
import com.example.quanlynhahang.model.AdminDashboardStats;
import com.example.quanlynhahang.model.EmployeeDashboardStats;
import com.example.quanlynhahang.model.Order;
import com.example.quanlynhahang.model.RecommendedDishItem;
import com.example.quanlynhahang.model.Reservation;
import com.example.quanlynhahang.model.ServiceRequest;
import com.example.quanlynhahang.model.User;
import com.example.quanlynhahang.model.UserRole;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String DATABASE_NAME = "restaurant.db";
    private static final int DATABASE_VERSION = 8;

    private static final String TEN_ANH_MAC_DINH = "ic_restaurant_24";
    private static final String TEN_ANH_DO_UONG = "ic_local_drink_24";
    private static final String BAN_MAC_DINH = "Bàn 01";
    private static final String EMAIL_TAI_KHOAN_TEST_KHACH_HANG = "kh1";
    private static final String SDT_TAI_KHOAN_TEST_KHACH_HANG = "0123456789";
    private static final String EMAIL_TAI_KHOAN_TEST_NHAN_VIEN = "nv1";
    private static final String SDT_TAI_KHOAN_TEST_NHAN_VIEN = "0123456790";
    private static final String EMAIL_TAI_KHOAN_TEST_ADMIN = "admin1";
    private static final String SDT_TAI_KHOAN_TEST_ADMIN = "0123456791";
    private static final String MAT_KHAU_TAI_KHOAN_TEST = "1";
    private static final int SO_KHACH_DAT_BAN_TOI_DA = 20;

    public static final String TABLE_USER = "users";
    public static final String TABLE_DISH = "dishes";
    public static final String TABLE_ORDER = "orders";
    public static final String TABLE_ORDER_ITEM = "order_items";
    public static final String TABLE_RESERVATION = "reservations";
    public static final String TABLE_SERVICE_REQUEST = "service_requests";

    private static final String COL_USER_ID = "id";
    private static final String COL_USER_NAME = "name";
    private static final String COL_USER_EMAIL = "email";
    private static final String COL_USER_PHONE = "phone";
    private static final String COL_USER_PASSWORD = "password";
    private static final String COL_USER_ROLE = "role";
    private static final String COL_USER_IS_ACTIVE = "is_active";

    private static final String COL_DISH_ID = "id";
    private static final String COL_DISH_NAME = "name";
    private static final String COL_DISH_PRICE = "price";
    private static final String COL_DISH_DESCRIPTION = "description";
    private static final String COL_DISH_IMAGE_RES_NAME = "image_res_name";
    private static final String COL_DISH_IS_AVAILABLE = "is_available";
    private static final String COL_DISH_CATEGORY = "category";
    private static final String COL_DISH_RECOMMEND_SCORE = "recommend_score";

    private static final String COL_ORDER_ID = "id";
    private static final String COL_ORDER_USER_ID = "user_id";
    private static final String COL_ORDER_CODE = "code";
    private static final String COL_ORDER_TIME = "time";
    private static final String COL_ORDER_TOTAL_PRICE = "total_price";
    private static final String COL_ORDER_STATUS = "status";

    private static final String COL_ORDER_ITEM_ID = "id";
    private static final String COL_ORDER_ITEM_ORDER_ID = "order_id";
    private static final String COL_ORDER_ITEM_DISH_NAME = "dish_name";
    private static final String COL_ORDER_ITEM_DISH_PRICE = "dish_price";
    private static final String COL_ORDER_ITEM_IMAGE_RES_NAME = "image_res_name";
    private static final String COL_ORDER_ITEM_IS_AVAILABLE = "is_available";
    private static final String COL_ORDER_ITEM_QUANTITY = "quantity";

    private static final String COL_RESERVATION_ID = "id";
    private static final String COL_RESERVATION_USER_ID = "user_id";
    private static final String COL_RESERVATION_TIME = "time";
    private static final String COL_RESERVATION_TABLE_NUMBER = "table_number";
    private static final String COL_RESERVATION_GUEST_COUNT = "guest_count";
    private static final String COL_RESERVATION_NOTE = "note";
    private static final String COL_RESERVATION_STATUS = "status";

    private static final String COL_SERVICE_REQUEST_ID = "id";
    private static final String COL_SERVICE_REQUEST_USER_ID = "user_id";
    private static final String COL_SERVICE_REQUEST_CONTENT = "content";
    private static final String COL_SERVICE_REQUEST_SENT_TIME = "sent_time";
    private static final String COL_SERVICE_REQUEST_STATUS = "status";

    private final Context appContext;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        appContext = context.getApplicationContext();
    }

    public void chuanBiCoSoDuLieu() {
        Log.i(TAG, "Bắt đầu mở và chuẩn hóa cơ sở dữ liệu.");
        SQLiteDatabase db = getWritableDatabase();
        Log.i(TAG, "Cơ sở dữ liệu đã sẵn sàng. version=" + db.getVersion());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate: tạo mới cơ sở dữ liệu. version=" + DATABASE_VERSION);
        try {
            damBaoSchema(db);
            damBaoDuLieuMacDinh(db);
            Log.i(TAG, "onCreate: tạo schema và dữ liệu mặc định thành công.");
        } catch (SQLiteException ex) {
            Log.e(TAG, "onCreate: lỗi khi tạo cơ sở dữ liệu.", ex);
            throw ex;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "onUpgrade: nâng cấp cơ sở dữ liệu từ version " + oldVersion + " lên " + newVersion);
        try {
            damBaoSchema(db);
            damBaoDuLieuMacDinh(db);
            Log.i(TAG, "onUpgrade: chuẩn hóa schema thành công.");
        } catch (SQLiteException ex) {
            Log.e(TAG, "onUpgrade: lỗi khi nâng cấp cơ sở dữ liệu.", ex);
            throw ex;
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (db.isReadOnly()) {
            Log.w(TAG, "onOpen: cơ sở dữ liệu đang ở chế độ chỉ đọc, bỏ qua bước chuẩn hóa schema.");
            return;
        }

        try {
            Log.d(TAG, "onOpen: kiểm tra lại schema và dữ liệu mặc định.");
            damBaoSchema(db);
            damBaoDuLieuMacDinh(db);
        } catch (SQLiteException ex) {
            Log.e(TAG, "onOpen: lỗi khi kiểm tra schema lúc mở cơ sở dữ liệu.", ex);
            throw ex;
        }
    }

    private void damBaoSchema(SQLiteDatabase db) {
        damBaoBangTonTai(db, TABLE_USER, taoBangNguoiDung());
        damBaoBangTonTai(db, TABLE_DISH, taoBangMonAn());
        damBaoBangTonTai(db, TABLE_ORDER, taoBangDonHang());
        damBaoBangTonTai(db, TABLE_ORDER_ITEM, taoBangChiTietDonHang());
        damBaoBangTonTai(db, TABLE_RESERVATION, taoBangDatBan());
        damBaoBangTonTai(db, TABLE_SERVICE_REQUEST, taoBangYeuCauPhucVu());

        damBaoCotTonTai(db, TABLE_USER, COL_USER_NAME, "TEXT NOT NULL DEFAULT ''");
        damBaoCotTonTai(db, TABLE_USER, COL_USER_EMAIL, "TEXT NOT NULL DEFAULT ''");
        damBaoCotTonTai(db, TABLE_USER, COL_USER_PHONE, "TEXT NOT NULL DEFAULT ''");
        damBaoCotTonTai(db, TABLE_USER, COL_USER_PASSWORD, "TEXT NOT NULL DEFAULT ''");
        damBaoCotTonTai(db, TABLE_USER, COL_USER_ROLE, "TEXT NOT NULL DEFAULT 'KHACH_HANG'");
        damBaoCotTonTai(db, TABLE_USER, COL_USER_IS_ACTIVE, "INTEGER NOT NULL DEFAULT 1");
        chuanHoaDuLieuNguoiDung(db);

        damBaoCotTonTai(db, TABLE_DISH, COL_DISH_NAME, "TEXT NOT NULL DEFAULT ''");
        damBaoCotTonTai(db, TABLE_DISH, COL_DISH_PRICE, "TEXT NOT NULL DEFAULT ''");
        damBaoCotTonTai(db, TABLE_DISH, COL_DISH_DESCRIPTION, "TEXT NOT NULL DEFAULT ''");
        damBaoCotTonTai(db, TABLE_DISH, COL_DISH_IMAGE_RES_NAME,
                "TEXT NOT NULL DEFAULT '" + TEN_ANH_MAC_DINH + "'");
        damBaoCotTonTai(db, TABLE_DISH, COL_DISH_IS_AVAILABLE, "INTEGER NOT NULL DEFAULT 1");
        damBaoCotTonTai(db, TABLE_DISH, COL_DISH_CATEGORY, "TEXT NOT NULL DEFAULT ''");
        damBaoCotTonTai(db, TABLE_DISH, COL_DISH_RECOMMEND_SCORE, "INTEGER NOT NULL DEFAULT 0");

        damBaoCotTonTai(db, TABLE_ORDER, COL_ORDER_USER_ID, "INTEGER NOT NULL DEFAULT 0");
        damBaoCotTonTai(db, TABLE_ORDER, COL_ORDER_CODE, "TEXT NOT NULL DEFAULT ''");
        damBaoCotTonTai(db, TABLE_ORDER, COL_ORDER_TIME, "TEXT NOT NULL DEFAULT ''");
        damBaoCotTonTai(db, TABLE_ORDER, COL_ORDER_TOTAL_PRICE, "TEXT NOT NULL DEFAULT ''");
        damBaoCotTonTai(db, TABLE_ORDER, COL_ORDER_STATUS,
                "TEXT NOT NULL DEFAULT '" + Order.Status.PENDING_CONFIRMATION.name() + "'");

        damBaoCotTonTai(db, TABLE_ORDER_ITEM, COL_ORDER_ITEM_ORDER_ID, "INTEGER NOT NULL DEFAULT 0");
        damBaoCotTonTai(db, TABLE_ORDER_ITEM, COL_ORDER_ITEM_DISH_NAME, "TEXT NOT NULL DEFAULT ''");
        damBaoCotTonTai(db, TABLE_ORDER_ITEM, COL_ORDER_ITEM_DISH_PRICE, "TEXT NOT NULL DEFAULT ''");
        damBaoCotTonTai(db, TABLE_ORDER_ITEM, COL_ORDER_ITEM_IMAGE_RES_NAME,
                "TEXT NOT NULL DEFAULT '" + TEN_ANH_MAC_DINH + "'");
        damBaoCotTonTai(db, TABLE_ORDER_ITEM, COL_ORDER_ITEM_IS_AVAILABLE, "INTEGER NOT NULL DEFAULT 1");
        damBaoCotTonTai(db, TABLE_ORDER_ITEM, COL_ORDER_ITEM_QUANTITY, "INTEGER NOT NULL DEFAULT 1");

        damBaoCotTonTai(db, TABLE_RESERVATION, COL_RESERVATION_USER_ID, "INTEGER NOT NULL DEFAULT 0");
        damBaoCotTonTai(db, TABLE_RESERVATION, COL_RESERVATION_TIME, "TEXT NOT NULL DEFAULT ''");
        damBaoCotTonTai(db, TABLE_RESERVATION, COL_RESERVATION_TABLE_NUMBER,
                "TEXT NOT NULL DEFAULT '" + BAN_MAC_DINH + "'");
        damBaoCotTonTai(db, TABLE_RESERVATION, COL_RESERVATION_GUEST_COUNT, "INTEGER NOT NULL DEFAULT 1");
        damBaoCotTonTai(db, TABLE_RESERVATION, COL_RESERVATION_NOTE, "TEXT");
        damBaoCotTonTai(db, TABLE_RESERVATION, COL_RESERVATION_STATUS,
                "TEXT NOT NULL DEFAULT '" + Reservation.Status.PENDING_APPROVAL.name() + "'");

        damBaoCotTonTai(db, TABLE_SERVICE_REQUEST, COL_SERVICE_REQUEST_USER_ID, "INTEGER NOT NULL DEFAULT 0");
        damBaoCotTonTai(db, TABLE_SERVICE_REQUEST, COL_SERVICE_REQUEST_CONTENT, "TEXT NOT NULL DEFAULT ''");
        damBaoCotTonTai(db, TABLE_SERVICE_REQUEST, COL_SERVICE_REQUEST_SENT_TIME, "TEXT NOT NULL DEFAULT ''");
        damBaoCotTonTai(db, TABLE_SERVICE_REQUEST, COL_SERVICE_REQUEST_STATUS,
                "TEXT NOT NULL DEFAULT '" + ServiceRequest.Status.PROCESSING.name() + "'");
    }

    private void damBaoDuLieuMacDinh(SQLiteDatabase db) {
        seedDishesIfEmpty(appContext, db);
        chuanHoaSeedMonAnSaiDanhMuc(db);
        ensureTestUserExists(db);
    }

    private void damBaoBangTonTai(SQLiteDatabase db, String tenBang, String cauLenhTao) {
        boolean daTonTai = bangDaTonTai(db, tenBang);
        Log.d(TAG, "Đảm bảo bảng " + tenBang + (daTonTai ? " đã tồn tại." : " sẽ được tạo mới."));
        db.execSQL(cauLenhTao);
    }

    private void damBaoCotTonTai(SQLiteDatabase db, String tenBang, String tenCot, String dinhNghiaCot) {
        if (cotDaTonTai(db, tenBang, tenCot)) {
            Log.d(TAG, "Cột " + tenBang + "." + tenCot + " đã tồn tại.");
            return;
        }

        String sql = "ALTER TABLE " + tenBang + " ADD COLUMN " + tenCot + " " + dinhNghiaCot;
        Log.i(TAG, "Bổ sung cột còn thiếu: " + tenBang + "." + tenCot);

        try {
            db.execSQL(sql);
        } catch (SQLiteException ex) {
            if (cotDaTonTai(db, tenBang, tenCot)) {
                Log.w(TAG, "Cột " + tenBang + "." + tenCot + " đã được thêm bởi luồng khác, bỏ qua lỗi trùng lặp.", ex);
                return;
            }
            Log.e(TAG, "Không thể bổ sung cột " + tenBang + "." + tenCot, ex);
            throw ex;
        }
    }

    private boolean bangDaTonTai(SQLiteDatabase db, String tenBang) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(
                    "SELECT name FROM sqlite_master WHERE type = ? AND name = ?",
                    new String[]{"table", tenBang}
            );
            return cursor.moveToFirst();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private boolean cotDaTonTai(SQLiteDatabase db, String tenBang, String tenCot) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("PRAGMA table_info(" + tenBang + ")", null);
            while (cursor.moveToNext()) {
                String tenCotHienTai = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                if (TextUtils.equals(tenCot, tenCotHienTai)) {
                    return true;
                }
            }
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private String taoBangNguoiDung() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_USER + " ("
                + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USER_NAME + " TEXT NOT NULL, "
                + COL_USER_EMAIL + " TEXT NOT NULL UNIQUE COLLATE NOCASE, "
                + COL_USER_PHONE + " TEXT NOT NULL, "
                + COL_USER_PASSWORD + " TEXT NOT NULL, "
                + COL_USER_ROLE + " TEXT NOT NULL DEFAULT 'KHACH_HANG', "
                + COL_USER_IS_ACTIVE + " INTEGER NOT NULL DEFAULT 1"
                + ")";
    }

    private void chuanHoaDuLieuNguoiDung(SQLiteDatabase db) {
        ContentValues valuesVaiTro = new ContentValues();
        valuesVaiTro.put(COL_USER_ROLE, UserRole.KHACH_HANG.name());
        db.update(
                TABLE_USER,
                valuesVaiTro,
                COL_USER_ROLE + " IS NULL OR TRIM(" + COL_USER_ROLE + ") = '' OR UPPER(TRIM(" + COL_USER_ROLE + ")) NOT IN (?, ?, ?)",
                new String[]{
                        UserRole.KHACH_HANG.name(),
                        UserRole.NHAN_VIEN.name(),
                        UserRole.ADMIN.name()
                }
        );

        ContentValues valuesTrangThai = new ContentValues();
        valuesTrangThai.put(COL_USER_IS_ACTIVE, 1);
        db.update(
                TABLE_USER,
                valuesTrangThai,
                COL_USER_IS_ACTIVE + " IS NULL",
                null
        );
    }

    private String taoBangMonAn() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_DISH + " ("
                + COL_DISH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_DISH_NAME + " TEXT NOT NULL, "
                + COL_DISH_PRICE + " TEXT NOT NULL, "
                + COL_DISH_DESCRIPTION + " TEXT NOT NULL, "
                + COL_DISH_IMAGE_RES_NAME + " TEXT NOT NULL DEFAULT '" + TEN_ANH_MAC_DINH + "', "
                + COL_DISH_IS_AVAILABLE + " INTEGER NOT NULL DEFAULT 1, "
                + COL_DISH_CATEGORY + " TEXT NOT NULL DEFAULT '', "
                + COL_DISH_RECOMMEND_SCORE + " INTEGER NOT NULL DEFAULT 0"
                + ")";
    }

    private String taoBangDonHang() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_ORDER + " ("
                + COL_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_ORDER_USER_ID + " INTEGER NOT NULL, "
                + COL_ORDER_CODE + " TEXT NOT NULL, "
                + COL_ORDER_TIME + " TEXT NOT NULL, "
                + COL_ORDER_TOTAL_PRICE + " TEXT NOT NULL, "
                + COL_ORDER_STATUS + " TEXT NOT NULL"
                + ")";
    }

    private String taoBangChiTietDonHang() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_ORDER_ITEM + " ("
                + COL_ORDER_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_ORDER_ITEM_ORDER_ID + " INTEGER NOT NULL, "
                + COL_ORDER_ITEM_DISH_NAME + " TEXT NOT NULL, "
                + COL_ORDER_ITEM_DISH_PRICE + " TEXT NOT NULL, "
                + COL_ORDER_ITEM_IMAGE_RES_NAME + " TEXT NOT NULL DEFAULT '" + TEN_ANH_MAC_DINH + "', "
                + COL_ORDER_ITEM_IS_AVAILABLE + " INTEGER NOT NULL DEFAULT 1, "
                + COL_ORDER_ITEM_QUANTITY + " INTEGER NOT NULL"
                + ")";
    }

    private String taoBangDatBan() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_RESERVATION + " ("
                + COL_RESERVATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_RESERVATION_USER_ID + " INTEGER NOT NULL, "
                + COL_RESERVATION_TIME + " TEXT NOT NULL, "
                + COL_RESERVATION_TABLE_NUMBER + " TEXT NOT NULL DEFAULT '" + BAN_MAC_DINH + "', "
                + COL_RESERVATION_GUEST_COUNT + " INTEGER NOT NULL, "
                + COL_RESERVATION_NOTE + " TEXT, "
                + COL_RESERVATION_STATUS + " TEXT NOT NULL"
                + ")";
    }

    private String taoBangYeuCauPhucVu() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_SERVICE_REQUEST + " ("
                + COL_SERVICE_REQUEST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_SERVICE_REQUEST_USER_ID + " INTEGER NOT NULL, "
                + COL_SERVICE_REQUEST_CONTENT + " TEXT NOT NULL, "
                + COL_SERVICE_REQUEST_SENT_TIME + " TEXT NOT NULL, "
                + COL_SERVICE_REQUEST_STATUS + " TEXT NOT NULL"
                + ")";
    }

    public long insertUser(String name, String email, String phone, String password) {
        return insertUser(name, email, phone, password, UserRole.KHACH_HANG, true);
    }

    public long insertUser(String name, String email, String phone, String password, UserRole role, boolean isActive) {
        if (TextUtils.isEmpty(name)
                || TextUtils.isEmpty(email)
                || TextUtils.isEmpty(phone)
                || TextUtils.isEmpty(password)) {
            return -1;
        }

        SQLiteDatabase db = getWritableDatabase();
        return insertUser(db, name, email, phone, password, role, isActive);
    }

    private long insertUser(SQLiteDatabase db, String name, String email, String phone, String password, UserRole role, boolean isActive) {
        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, name);
        values.put(COL_USER_EMAIL, email);
        values.put(COL_USER_PHONE, phone);
        values.put(COL_USER_PASSWORD, password);
        values.put(COL_USER_ROLE, role != null ? role.name() : UserRole.KHACH_HANG.name());
        values.put(COL_USER_IS_ACTIVE, isActive ? 1 : 0);

        try {
            return db.insertOrThrow(TABLE_USER, null, values);
        } catch (SQLiteConstraintException ex) {
            Log.w(TAG, "insertUser: email đã tồn tại hoặc vi phạm ràng buộc. email=" + email, ex);
            return -1;
        } catch (SQLiteException ex) {
            Log.e(TAG, "insertUser: lỗi khi thêm người dùng. email=" + email, ex);
            throw ex;
        }
    }

    @Nullable
    public User getUserByEmail(String email) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(
                    TABLE_USER,
                    new String[]{COL_USER_ID, COL_USER_NAME, COL_USER_EMAIL, COL_USER_PHONE, COL_USER_ROLE, COL_USER_IS_ACTIVE},
                    COL_USER_EMAIL + " = ?",
                    new String[]{email},
                    null,
                    null,
                    null,
                    "1"
            );

            if (cursor.moveToFirst()) {
                return mapUser(cursor);
            }
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Nullable
    public User getUserById(long userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(
                    TABLE_USER,
                    new String[]{COL_USER_ID, COL_USER_NAME, COL_USER_EMAIL, COL_USER_PHONE, COL_USER_ROLE, COL_USER_IS_ACTIVE},
                    COL_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)},
                    null,
                    null,
                    null,
                    "1"
            );

            if (cursor.moveToFirst()) {
                return mapUser(cursor);
            }
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Nullable
    public User checkLogin(String usernameOrEmail, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(
                    TABLE_USER,
                    new String[]{COL_USER_ID, COL_USER_NAME, COL_USER_EMAIL, COL_USER_PHONE, COL_USER_ROLE, COL_USER_IS_ACTIVE},
                    "(" + COL_USER_EMAIL + " = ? OR " + COL_USER_PHONE + " = ?) AND " + COL_USER_PASSWORD + " = ? AND " + COL_USER_IS_ACTIVE + " = 1",
                    new String[]{usernameOrEmail, usernameOrEmail, password},
                    null,
                    null,
                    null,
                    "1"
            );

            if (cursor.moveToFirst()) {
                return mapUser(cursor);
            }
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public boolean updateUserProfile(long userId, String name, String phone) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, name);
        values.put(COL_USER_PHONE, phone);

        int rows = db.update(
                TABLE_USER,
                values,
                COL_USER_ID + " = ?",
                new String[]{String.valueOf(userId)}
        );
        return rows > 0;
    }

    public boolean updateUserPassword(long userId, String newPassword) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_PASSWORD, newPassword);

        int rows = db.update(
                TABLE_USER,
                values,
                COL_USER_ID + " = ?",
                new String[]{String.valueOf(userId)}
        );
        return rows > 0;
    }

    public List<User> getAllUsers() {
        return getUsersByRole(null);
    }

    public List<User> getUsersByRole(@Nullable UserRole role) {
        List<User> users = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            String selection = null;
            String[] selectionArgs = null;
            if (role != null) {
                selection = COL_USER_ROLE + " = ?";
                selectionArgs = new String[]{role.name()};
            }

            cursor = db.query(
                    TABLE_USER,
                    new String[]{COL_USER_ID, COL_USER_NAME, COL_USER_EMAIL, COL_USER_PHONE, COL_USER_ROLE, COL_USER_IS_ACTIVE},
                    selection,
                    selectionArgs,
                    null,
                    null,
                    COL_USER_ROLE + " ASC, " + COL_USER_NAME + " COLLATE NOCASE ASC, " + COL_USER_ID + " ASC"
            );

            while (cursor.moveToNext()) {
                users.add(mapUser(cursor));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return users;
    }

    public boolean updateUserRole(long userId, UserRole role) {
        if (userId <= 0 || role == null) {
            return false;
        }
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_ROLE, role.name());
        int rows = db.update(TABLE_USER, values, COL_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        return rows > 0;
    }

    public boolean updateUserActive(long userId, boolean isActive) {
        if (userId <= 0) {
            return false;
        }
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_IS_ACTIVE, isActive ? 1 : 0);
        int rows = db.update(TABLE_USER, values, COL_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        return rows > 0;
    }

    public int countAllUsers() {
        return demSoBanGhi(TABLE_USER, null, null);
    }

    public int countUsersByRole(UserRole role) {
        if (role == null) {
            return countAllUsers();
        }
        return demSoBanGhi(TABLE_USER, COL_USER_ROLE + " = ?", new String[]{role.name()});
    }

    public void seedDishesIfEmpty(Context context) {
        SQLiteDatabase db = getWritableDatabase();
        seedDishesIfEmpty(context == null ? appContext : context.getApplicationContext(), db);
    }

    public List<DishRecord> getAllDishes() {
        return queryDishes(null, null);
    }

    public List<DishRecord> searchDishes(@Nullable String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            return getAllDishes();
        }
        String trimmedKeyword = keyword.trim();
        String likeValue = "%" + trimmedKeyword + "%";
        return queryDishes(
                COL_DISH_NAME + " LIKE ? OR " + COL_DISH_CATEGORY + " LIKE ? OR " + COL_DISH_DESCRIPTION + " LIKE ?",
                new String[]{likeValue, likeValue, likeValue}
        );
    }

    public long insertDishRecord(String name,
                                 String price,
                                 String description,
                                 @Nullable String imageResName,
                                 boolean isAvailable,
                                 @Nullable String category,
                                 int recommendScore) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(price) || TextUtils.isEmpty(description)) {
            return -1;
        }
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = taoGiaTriMonAn(name, price, description, imageResName, isAvailable, category, recommendScore);
        return db.insert(TABLE_DISH, null, values);
    }

    public boolean updateDishRecord(long dishId,
                                    String name,
                                    String price,
                                    String description,
                                    @Nullable String imageResName,
                                    boolean isAvailable,
                                    @Nullable String category,
                                    int recommendScore) {
        if (dishId <= 0 || TextUtils.isEmpty(name) || TextUtils.isEmpty(price) || TextUtils.isEmpty(description)) {
            return false;
        }
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = taoGiaTriMonAn(name, price, description, imageResName, isAvailable, category, recommendScore);
        int rows = db.update(TABLE_DISH, values, COL_DISH_ID + " = ?", new String[]{String.valueOf(dishId)});
        return rows > 0;
    }

    public boolean deleteDishById(long dishId) {
        if (dishId <= 0) {
            return false;
        }
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(TABLE_DISH, COL_DISH_ID + " = ?", new String[]{String.valueOf(dishId)});
        return rows > 0;
    }

    public boolean updateDishAvailability(long dishId, boolean isAvailable) {
        if (dishId <= 0) {
            return false;
        }
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_DISH_IS_AVAILABLE, isAvailable ? 1 : 0);
        int rows = db.update(TABLE_DISH, values, COL_DISH_ID + " = ?", new String[]{String.valueOf(dishId)});
        return rows > 0;
    }

    public int countAllDishes() {
        return demSoBanGhi(TABLE_DISH, null, null);
    }

    private List<DishRecord> queryDishes(@Nullable String selection, @Nullable String[] selectionArgs) {
        List<DishRecord> dishRecords = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(
                    TABLE_DISH,
                    new String[]{
                            COL_DISH_ID,
                            COL_DISH_NAME,
                            COL_DISH_PRICE,
                            COL_DISH_DESCRIPTION,
                            COL_DISH_IMAGE_RES_NAME,
                            COL_DISH_IS_AVAILABLE,
                            COL_DISH_CATEGORY,
                            COL_DISH_RECOMMEND_SCORE
                    },
                    selection,
                    selectionArgs,
                    null,
                    null,
                    COL_DISH_ID + " ASC"
            );

            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_DISH_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_DISH_NAME));
                String price = cursor.getString(cursor.getColumnIndexOrThrow(COL_DISH_PRICE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COL_DISH_DESCRIPTION));
                String imageResName = cursor.getString(cursor.getColumnIndexOrThrow(COL_DISH_IMAGE_RES_NAME));
                boolean isAvailable = cursor.getInt(cursor.getColumnIndexOrThrow(COL_DISH_IS_AVAILABLE)) == 1;
                String category = cursor.getString(cursor.getColumnIndexOrThrow(COL_DISH_CATEGORY));
                int recommendScore = cursor.getInt(cursor.getColumnIndexOrThrow(COL_DISH_RECOMMEND_SCORE));

                int imageResId = resolveImageResId(imageResName);
                RecommendedDishItem dishItem = new RecommendedDishItem(
                        imageResId,
                        name,
                        price,
                        isAvailable,
                        category,
                        recommendScore
                );
                dishRecords.add(new DishRecord(id, dishItem, description, imageResName));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return dishRecords;
    }

    public List<RecommendedDishItem> getAllDishItems() {
        List<RecommendedDishItem> dishes = new ArrayList<>();
        List<DishRecord> dishRecords = getAllDishes();

        for (DishRecord record : dishRecords) {
            dishes.add(record.getDishItem());
        }

        return dishes;
    }

    public List<RecommendedDishItem> getDanhSachMonTheoDanhMuc(@Nullable String tenDanhMuc) {
        List<RecommendedDishItem> dishes = new ArrayList<>();
        for (DishRecord record : getAllDishes()) {
            RecommendedDishItem dishItem = record.getDishItem();
            if (TextUtils.isEmpty(tenDanhMuc) || TextUtils.equals(tenDanhMuc, dishItem.getTenDanhMuc())) {
                dishes.add(dishItem);
            }
        }
        return dishes;
    }

    public List<RecommendedDishItem> getMonDeXuatTrangChu(int soLuongToiDa) {
        List<RecommendedDishItem> available = new ArrayList<>();
        List<RecommendedDishItem> fallback = new ArrayList<>();

        for (DishRecord record : getAllDishes()) {
            RecommendedDishItem dishItem = record.getDishItem();
            fallback.add(dishItem);
            if (dishItem.isConPhucVu()) {
                available.add(dishItem);
            }
        }

        List<RecommendedDishItem> source = available.isEmpty() ? fallback : available;
        source.sort((first, second) -> Integer.compare(second.getDiemDeXuat(), first.getDiemDeXuat()));

        int gioiHan = Math.min(Math.max(soLuongToiDa, 0), source.size());
        return new ArrayList<>(source.subList(0, gioiHan));
    }

    public long insertOrder(int userId,
                            String code,
                            String time,
                            String totalPrice,
                            Order.Status status,
                            List<Order.OrderDish> dishes) {
        if (userId <= 0
                || TextUtils.isEmpty(code)
                || TextUtils.isEmpty(time)
                || TextUtils.isEmpty(totalPrice)
                || status == null
                || dishes == null
                || dishes.isEmpty()) {
            return -1;
        }

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues orderValues = new ContentValues();
            orderValues.put(COL_ORDER_USER_ID, userId);
            orderValues.put(COL_ORDER_CODE, code);
            orderValues.put(COL_ORDER_TIME, time);
            orderValues.put(COL_ORDER_TOTAL_PRICE, totalPrice);
            orderValues.put(COL_ORDER_STATUS, status.name());

            long orderId = db.insert(TABLE_ORDER, null, orderValues);
            if (orderId <= 0) {
                return -1;
            }

            for (Order.OrderDish orderDish : dishes) {
                if (orderDish == null || orderDish.getDishItem() == null) {
                    return -1;
                }

                RecommendedDishItem dishItem = orderDish.getDishItem();
                ContentValues itemValues = new ContentValues();
                itemValues.put(COL_ORDER_ITEM_ORDER_ID, orderId);
                itemValues.put(COL_ORDER_ITEM_DISH_NAME, dishItem.getTenMon());
                itemValues.put(COL_ORDER_ITEM_DISH_PRICE, dishItem.getGiaBan());
                itemValues.put(COL_ORDER_ITEM_IMAGE_RES_NAME, resolveImageResName(dishItem.getImageResId()));
                itemValues.put(COL_ORDER_ITEM_IS_AVAILABLE, dishItem.isConPhucVu() ? 1 : 0);
                itemValues.put(COL_ORDER_ITEM_QUANTITY, orderDish.getQuantity());

                long itemId = db.insert(TABLE_ORDER_ITEM, null, itemValues);
                if (itemId <= 0) {
                    return -1;
                }
            }

            db.setTransactionSuccessful();
            return orderId;
        } finally {
            db.endTransaction();
        }
    }

    public List<Order> getOrdersByUserId(int userId) {
        return getOrdersByUserId((long) userId);
    }

    public List<Order> getOrdersByUserId(long userId) {
        return queryOrders(COL_ORDER_USER_ID + " = ?", new String[]{String.valueOf(userId)});
    }

    public List<Order> getAllOrders() {
        return queryOrders(null, null);
    }

    public boolean updateOrderStatus(long orderId, Order.Status status) {
        if (orderId <= 0 || status == null) {
            return false;
        }
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ORDER_STATUS, status.name());
        int rows = db.update(TABLE_ORDER, values, COL_ORDER_ID + " = ?", new String[]{String.valueOf(orderId)});
        return rows > 0;
    }

    public int countAllOrders() {
        return demSoBanGhi(TABLE_ORDER, null, null);
    }

    public int countOrdersByStatus(Order.Status status) {
        if (status == null) {
            return countAllOrders();
        }
        return demSoBanGhi(TABLE_ORDER, COL_ORDER_STATUS + " = ?", new String[]{status.name()});
    }

    public boolean cancelOrder(long orderId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ORDER_STATUS, Order.Status.CANCELED.name());

        int rows = db.update(
                TABLE_ORDER,
                values,
                COL_ORDER_ID + " = ? AND " + COL_ORDER_STATUS + " = ?",
                new String[]{String.valueOf(orderId), Order.Status.PENDING_CONFIRMATION.name()}
        );
        return rows > 0;
    }

    public List<Reservation> getReservationsByUserId(int userId) {
        return getReservationsByUserId((long) userId);
    }

    public List<Reservation> getReservationsByUserId(long userId) {
        return queryReservations(COL_RESERVATION_USER_ID + " = ?", new String[]{String.valueOf(userId)});
    }

    public List<Reservation> getAllReservations() {
        return queryReservations(null, null);
    }

    public boolean updateReservationStatus(long reservationId, Reservation.Status status) {
        if (reservationId <= 0 || status == null) {
            return false;
        }
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_RESERVATION_STATUS, status.name());
        int rows = db.update(TABLE_RESERVATION, values, COL_RESERVATION_ID + " = ?", new String[]{String.valueOf(reservationId)});
        return rows > 0;
    }

    public int countReservationsByStatus(Reservation.Status status) {
        if (status == null) {
            return demSoBanGhi(TABLE_RESERVATION, null, null);
        }
        return demSoBanGhi(TABLE_RESERVATION, COL_RESERVATION_STATUS + " = ?", new String[]{status.name()});
    }

    public long insertReservation(int userId,
                                  String time,
                                  String tableNumber,
                                  int peopleCount,
                                  String notes) {
        return insertReservation(
                (long) userId,
                time,
                tableNumber,
                peopleCount,
                notes,
                Reservation.Status.PENDING_APPROVAL
        );
    }

    public long insertReservation(long userId,
                                  String time,
                                  String tableNumber,
                                  int guestCount,
                                  @Nullable String note,
                                  Reservation.Status status) {
        if (userId <= 0
                || TextUtils.isEmpty(time)
                || TextUtils.isEmpty(tableNumber)
                || guestCount <= 0
                || guestCount > SO_KHACH_DAT_BAN_TOI_DA
                || status == null
                || parseOrderTimeToMillis(time) <= System.currentTimeMillis()) {
            return -1;
        }

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_RESERVATION_USER_ID, userId);
        values.put(COL_RESERVATION_TIME, time);
        values.put(COL_RESERVATION_TABLE_NUMBER, tableNumber);
        values.put(COL_RESERVATION_GUEST_COUNT, guestCount);
        values.put(COL_RESERVATION_NOTE, note);
        values.put(COL_RESERVATION_STATUS, status.name());
        return db.insert(TABLE_RESERVATION, null, values);
    }

    public boolean cancelReservation(long reservationId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_RESERVATION_STATUS, Reservation.Status.CANCELED.name());

        int rows = db.update(
                TABLE_RESERVATION,
                values,
                COL_RESERVATION_ID + " = ? AND " + COL_RESERVATION_STATUS + " = ?",
                new String[]{String.valueOf(reservationId), Reservation.Status.PENDING_APPROVAL.name()}
        );
        return rows > 0;
    }

    public List<ServiceRequest> getServiceRequestsByUserId(long userId) {
        return queryServiceRequests(COL_SERVICE_REQUEST_USER_ID + " = ?", new String[]{String.valueOf(userId)});
    }

    public List<ServiceRequest> getAllServiceRequests() {
        return queryServiceRequests(null, null);
    }

    public boolean updateServiceRequestStatus(long requestId, ServiceRequest.Status status) {
        if (requestId <= 0 || status == null) {
            return false;
        }
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_SERVICE_REQUEST_STATUS, status.name());
        int rows = db.update(TABLE_SERVICE_REQUEST, values, COL_SERVICE_REQUEST_ID + " = ?", new String[]{String.valueOf(requestId)});
        return rows > 0;
    }

    public int countServiceRequestsByStatus(ServiceRequest.Status status) {
        if (status == null) {
            return demSoBanGhi(TABLE_SERVICE_REQUEST, null, null);
        }
        return demSoBanGhi(TABLE_SERVICE_REQUEST, COL_SERVICE_REQUEST_STATUS + " = ?", new String[]{status.name()});
    }

    public AdminDashboardStats getAdminDashboardStats() {
        return new AdminDashboardStats(
                countAllUsers(),
                countUsersByRole(UserRole.KHACH_HANG),
                countUsersByRole(UserRole.NHAN_VIEN),
                countUsersByRole(UserRole.ADMIN),
                countAllDishes(),
                countAllOrders(),
                countOrdersByStatus(Order.Status.PENDING_CONFIRMATION),
                countReservationsByStatus(Reservation.Status.PENDING_APPROVAL),
                countServiceRequestsByStatus(ServiceRequest.Status.PROCESSING)
        );
    }

    public EmployeeDashboardStats getEmployeeDashboardStats() {
        return new EmployeeDashboardStats(
                countOrdersByStatus(Order.Status.PENDING_CONFIRMATION),
                countReservationsByStatus(Reservation.Status.PENDING_APPROVAL),
                countServiceRequestsByStatus(ServiceRequest.Status.PROCESSING)
        );
    }

    public long insertServiceRequest(long userId,
                                     String noiDung,
                                     String thoiGianGui,
                                     ServiceRequest.Status trangThai) {
        if (userId <= 0 || TextUtils.isEmpty(noiDung) || TextUtils.isEmpty(thoiGianGui) || trangThai == null) {
            return -1;
        }

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_SERVICE_REQUEST_USER_ID, userId);
        values.put(COL_SERVICE_REQUEST_CONTENT, noiDung);
        values.put(COL_SERVICE_REQUEST_SENT_TIME, thoiGianGui);
        values.put(COL_SERVICE_REQUEST_STATUS, trangThai.name());
        return db.insert(TABLE_SERVICE_REQUEST, null, values);
    }

    private void seedDishesIfEmpty(Context context, SQLiteDatabase db) {
        if (hasAnyDish(db)) {
            return;
        }

        Log.i(TAG, "Seed dữ liệu món ăn mặc định vì bảng dishes đang trống.");
        insertDish(
                db,
                context.getString(R.string.dish_bo_luc_lac),
                context.getString(R.string.price_145k),
                context.getString(R.string.menu_desc_bo_luc_lac),
                TEN_ANH_MAC_DINH,
                true,
                context.getString(R.string.category_main_course),
                96
        );
        insertDish(
                db,
                context.getString(R.string.dish_lau_thai),
                context.getString(R.string.price_259k),
                context.getString(R.string.menu_desc_lau_thai),
                TEN_ANH_MAC_DINH,
                true,
                context.getString(R.string.category_hotpot),
                93
        );
        insertDish(
                db,
                context.getString(R.string.dish_salad_ca_hoi),
                context.getString(R.string.price_129k),
                context.getString(R.string.menu_desc_salad_ca_hoi),
                TEN_ANH_MAC_DINH,
                true,
                context.getString(R.string.category_salad),
                89
        );
        insertDish(
                db,
                context.getString(R.string.dish_tra_dao),
                context.getString(R.string.price_45k),
                context.getString(R.string.menu_desc_tra_dao),
                TEN_ANH_DO_UONG,
                true,
                context.getString(R.string.category_drink),
                82
        );
    }

    private boolean hasAnyDish(SQLiteDatabase db) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_DISH, null);
            if (cursor.moveToFirst()) {
                return cursor.getInt(0) > 0;
            }
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void chuanHoaSeedMonAnSaiDanhMuc(SQLiteDatabase db) {
        chuanHoaMonAnMacDinh(
                db,
                appContext.getString(R.string.dish_bo_luc_lac),
                appContext.getString(R.string.category_main_course),
                96,
                true,
                null
        );
        chuanHoaMonAnMacDinh(
                db,
                appContext.getString(R.string.dish_lau_thai),
                appContext.getString(R.string.category_hotpot),
                93,
                true,
                null
        );
        chuanHoaMonAnMacDinh(
                db,
                appContext.getString(R.string.dish_salad_ca_hoi),
                appContext.getString(R.string.category_salad),
                89,
                true,
                appContext.getString(R.string.category_main_course)
        );
        chuanHoaMonAnMacDinh(
                db,
                appContext.getString(R.string.dish_tra_dao),
                appContext.getString(R.string.category_drink),
                82,
                true,
                null
        );
    }

    private void chuanHoaMonAnMacDinh(SQLiteDatabase db,
                                      String tenMon,
                                      String danhMucDung,
                                      int diemDeXuat,
                                      boolean conPhucVu,
                                      @Nullable String danhMucCuSai) {
        if (TextUtils.isEmpty(tenMon)) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(COL_DISH_CATEGORY, danhMucDung);
        values.put(COL_DISH_RECOMMEND_SCORE, diemDeXuat);
        values.put(COL_DISH_IS_AVAILABLE, conPhucVu ? 1 : 0);

        if (!TextUtils.isEmpty(danhMucCuSai)) {
            db.update(
                    TABLE_DISH,
                    values,
                    COL_DISH_NAME + " = ? AND " + COL_DISH_CATEGORY + " = ?",
                    new String[]{tenMon, danhMucCuSai}
            );
        }

        db.update(
                TABLE_DISH,
                values,
                COL_DISH_NAME + " = ?",
                new String[]{tenMon}
        );
    }

    private void insertDish(SQLiteDatabase db,
                            String name,
                            String price,
                            String description,
                            String imageResName,
                            boolean isAvailable,
                            String tenDanhMuc,
                            int diemDeXuat) {
        ContentValues values = taoGiaTriMonAn(name, price, description, imageResName, isAvailable, tenDanhMuc, diemDeXuat);
        db.insert(TABLE_DISH, null, values);
    }

    private ContentValues taoGiaTriMonAn(String name,
                                         String price,
                                         String description,
                                         @Nullable String imageResName,
                                         boolean isAvailable,
                                         @Nullable String category,
                                         int recommendScore) {
        ContentValues values = new ContentValues();
        values.put(COL_DISH_NAME, name != null ? name.trim() : "");
        values.put(COL_DISH_PRICE, price != null ? price.trim() : "");
        values.put(COL_DISH_DESCRIPTION, description != null ? description.trim() : "");
        values.put(COL_DISH_IMAGE_RES_NAME, TextUtils.isEmpty(imageResName) ? TEN_ANH_MAC_DINH : imageResName.trim());
        values.put(COL_DISH_IS_AVAILABLE, isAvailable ? 1 : 0);
        values.put(COL_DISH_CATEGORY, category != null ? category.trim() : "");
        values.put(COL_DISH_RECOMMEND_SCORE, Math.max(recommendScore, 0));
        return values;
    }

    private void ensureTestUserExists(SQLiteDatabase db) {
        ensureSeedUser(
                db,
                appContext.getString(R.string.db_test_customer_name),
                EMAIL_TAI_KHOAN_TEST_KHACH_HANG,
                SDT_TAI_KHOAN_TEST_KHACH_HANG,
                MAT_KHAU_TAI_KHOAN_TEST,
                UserRole.KHACH_HANG,
                true
        );
        ensureSeedUser(
                db,
                appContext.getString(R.string.db_test_employee_name),
                EMAIL_TAI_KHOAN_TEST_NHAN_VIEN,
                SDT_TAI_KHOAN_TEST_NHAN_VIEN,
                MAT_KHAU_TAI_KHOAN_TEST,
                UserRole.NHAN_VIEN,
                true
        );
        ensureSeedUser(
                db,
                appContext.getString(R.string.db_test_admin_name),
                EMAIL_TAI_KHOAN_TEST_ADMIN,
                SDT_TAI_KHOAN_TEST_ADMIN,
                MAT_KHAU_TAI_KHOAN_TEST,
                UserRole.ADMIN,
                true
        );
    }

    private void ensureSeedUser(SQLiteDatabase db,
                                String name,
                                String email,
                                String phone,
                                String password,
                                UserRole role,
                                boolean isActive) {
        Cursor cursor = null;
        try {
            cursor = db.query(
                    TABLE_USER,
                    new String[]{COL_USER_ID, COL_USER_ROLE, COL_USER_IS_ACTIVE},
                    COL_USER_EMAIL + " = ? OR " + COL_USER_PHONE + " = ?",
                    new String[]{email, phone},
                    null,
                    null,
                    null,
                    "1"
            );
            if (cursor.moveToFirst()) {
                long userId = cursor.getLong(cursor.getColumnIndexOrThrow(COL_USER_ID));
                ContentValues values = new ContentValues();
                values.put(COL_USER_ROLE, role.name());
                values.put(COL_USER_IS_ACTIVE, isActive ? 1 : 0);
                db.update(
                        TABLE_USER,
                        values,
                        COL_USER_ID + " = ?",
                        new String[]{String.valueOf(userId)}
                );
                return;
            }

            Log.i(TAG, "Tạo tài khoản thử nghiệm: " + email);
            insertUser(db, name, email, phone, password, role, isActive);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Nullable
    private User mapUser(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_USER_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_NAME));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_EMAIL));
        String phone = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PHONE));
        String roleValue = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_ROLE));
        boolean isActive = cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_IS_ACTIVE)) == 1;
        return new User(id, name, email, phone, UserRole.tuChuoi(roleValue), isActive);
    }

    private List<Order> queryOrders(@Nullable String selection, @Nullable String[] selectionArgs) {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(
                    TABLE_ORDER,
                    new String[]{COL_ORDER_ID, COL_ORDER_CODE, COL_ORDER_TIME, COL_ORDER_TOTAL_PRICE, COL_ORDER_STATUS},
                    selection,
                    selectionArgs,
                    null,
                    null,
                    COL_ORDER_ID + " DESC"
            );

            while (cursor.moveToNext()) {
                long orderId = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ORDER_ID));
                String code = cursor.getString(cursor.getColumnIndexOrThrow(COL_ORDER_CODE));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(COL_ORDER_TIME));
                String totalPrice = cursor.getString(cursor.getColumnIndexOrThrow(COL_ORDER_TOTAL_PRICE));
                String statusRaw = cursor.getString(cursor.getColumnIndexOrThrow(COL_ORDER_STATUS));
                List<Order.OrderDish> dishes = getOrderItemsByOrderId(orderId);
                orders.add(new Order(orderId, code, time, totalPrice, parseOrderStatus(statusRaw), dishes));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        orders.sort((first, second) -> Long.compare(
                parseOrderTimeToMillis(second.getTime()),
                parseOrderTimeToMillis(first.getTime())
        ));
        return orders;
    }

    private List<Reservation> queryReservations(@Nullable String selection, @Nullable String[] selectionArgs) {
        List<Reservation> reservations = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(
                    TABLE_RESERVATION,
                    new String[]{
                            COL_RESERVATION_ID,
                            COL_RESERVATION_TIME,
                            COL_RESERVATION_TABLE_NUMBER,
                            COL_RESERVATION_GUEST_COUNT,
                            COL_RESERVATION_NOTE,
                            COL_RESERVATION_STATUS
                    },
                    selection,
                    selectionArgs,
                    null,
                    null,
                    COL_RESERVATION_ID + " DESC"
            );

            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_RESERVATION_ID));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(COL_RESERVATION_TIME));
                String tableNumber = cursor.getString(cursor.getColumnIndexOrThrow(COL_RESERVATION_TABLE_NUMBER));
                int guestCount = cursor.getInt(cursor.getColumnIndexOrThrow(COL_RESERVATION_GUEST_COUNT));
                String note = cursor.getString(cursor.getColumnIndexOrThrow(COL_RESERVATION_NOTE));
                String statusRaw = cursor.getString(cursor.getColumnIndexOrThrow(COL_RESERVATION_STATUS));
                reservations.add(new Reservation(id, time, tableNumber, guestCount, note, parseReservationStatus(statusRaw)));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return reservations;
    }

    private List<ServiceRequest> queryServiceRequests(@Nullable String selection, @Nullable String[] selectionArgs) {
        List<ServiceRequest> requests = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(
                    TABLE_SERVICE_REQUEST,
                    new String[]{
                            COL_SERVICE_REQUEST_ID,
                            COL_SERVICE_REQUEST_CONTENT,
                            COL_SERVICE_REQUEST_SENT_TIME,
                            COL_SERVICE_REQUEST_STATUS
                    },
                    selection,
                    selectionArgs,
                    null,
                    null,
                    COL_SERVICE_REQUEST_ID + " DESC"
            );

            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_SERVICE_REQUEST_ID));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(COL_SERVICE_REQUEST_CONTENT));
                String sentTime = cursor.getString(cursor.getColumnIndexOrThrow(COL_SERVICE_REQUEST_SENT_TIME));
                String statusRaw = cursor.getString(cursor.getColumnIndexOrThrow(COL_SERVICE_REQUEST_STATUS));
                requests.add(new ServiceRequest(id, content, sentTime, parseServiceRequestStatus(statusRaw)));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        requests.sort((first, second) -> Long.compare(
                parseOrderTimeToMillis(second.getThoiGianGui()),
                parseOrderTimeToMillis(first.getThoiGianGui())
        ));
        return requests;
    }

    private int demSoBanGhi(String table, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(table, new String[]{"COUNT(*)"}, selection, selectionArgs, null, null, null);
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private List<Order.OrderDish> getOrderItemsByOrderId(long orderId) {
        List<Order.OrderDish> dishes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(
                    TABLE_ORDER_ITEM,
                    new String[]{
                            COL_ORDER_ITEM_DISH_NAME,
                            COL_ORDER_ITEM_DISH_PRICE,
                            COL_ORDER_ITEM_IMAGE_RES_NAME,
                            COL_ORDER_ITEM_IS_AVAILABLE,
                            COL_ORDER_ITEM_QUANTITY
                    },
                    COL_ORDER_ITEM_ORDER_ID + " = ?",
                    new String[]{String.valueOf(orderId)},
                    null,
                    null,
                    COL_ORDER_ITEM_ID + " ASC"
            );

            while (cursor.moveToNext()) {
                String dishName = cursor.getString(cursor.getColumnIndexOrThrow(COL_ORDER_ITEM_DISH_NAME));
                String dishPrice = cursor.getString(cursor.getColumnIndexOrThrow(COL_ORDER_ITEM_DISH_PRICE));
                String imageResName = cursor.getString(cursor.getColumnIndexOrThrow(COL_ORDER_ITEM_IMAGE_RES_NAME));
                boolean isAvailable = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ORDER_ITEM_IS_AVAILABLE)) == 1;
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ORDER_ITEM_QUANTITY));

                RecommendedDishItem dishItem = new RecommendedDishItem(
                        resolveImageResId(imageResName),
                        dishName,
                        dishPrice,
                        isAvailable,
                        "",
                        0
                );
                dishes.add(new Order.OrderDish(dishItem, quantity));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return dishes;
    }

    private int resolveImageResId(String imageResName) {
        if (TextUtils.isEmpty(imageResName)) {
            return R.drawable.ic_restaurant_24;
        }

        int resId = appContext.getResources().getIdentifier(
                imageResName,
                "drawable",
                appContext.getPackageName()
        );
        return resId == 0 ? R.drawable.ic_restaurant_24 : resId;
    }

    private String resolveImageResName(int imageResId) {
        if (imageResId == 0) {
            return TEN_ANH_MAC_DINH;
        }

        try {
            return appContext.getResources().getResourceEntryName(imageResId);
        } catch (Resources.NotFoundException ex) {
            return TEN_ANH_MAC_DINH;
        }
    }

    private Order.Status parseOrderStatus(String statusRaw) {
        if (TextUtils.isEmpty(statusRaw)) {
            return Order.Status.PENDING_CONFIRMATION;
        }

        try {
            return Order.Status.valueOf(statusRaw);
        } catch (IllegalArgumentException ex) {
            return Order.Status.PENDING_CONFIRMATION;
        }
    }

    private long parseOrderTimeToMillis(@Nullable String timeRaw) {
        if (TextUtils.isEmpty(timeRaw)) {
            return 0L;
        }

        try {
            Date parsedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).parse(timeRaw);
            return parsedDate == null ? 0L : parsedDate.getTime();
        } catch (ParseException ex) {
            return 0L;
        }
    }

    private Reservation.Status parseReservationStatus(String statusRaw) {
        if (TextUtils.isEmpty(statusRaw)) {
            return Reservation.Status.PENDING_APPROVAL;
        }

        try {
            return Reservation.Status.valueOf(statusRaw);
        } catch (IllegalArgumentException ex) {
            return Reservation.Status.PENDING_APPROVAL;
        }
    }

    private ServiceRequest.Status parseServiceRequestStatus(String statusRaw) {
        if (TextUtils.isEmpty(statusRaw)) {
            return ServiceRequest.Status.PROCESSING;
        }

        try {
            return ServiceRequest.Status.valueOf(statusRaw);
        } catch (IllegalArgumentException ex) {
            return ServiceRequest.Status.PROCESSING;
        }
    }

    public static class DishRecord {
        private final long id;
        private final RecommendedDishItem dishItem;
        private final String description;
        private final String imageResName;

        public DishRecord(long id, RecommendedDishItem dishItem, String description, String imageResName) {
            this.id = id;
            this.dishItem = dishItem;
            this.description = description;
            this.imageResName = imageResName;
        }

        public long getId() {
            return id;
        }

        public RecommendedDishItem getDishItem() {
            return dishItem;
        }

        public String getDescription() {
            return description;
        }

        public String getImageResName() {
            return imageResName;
        }
    }
}
