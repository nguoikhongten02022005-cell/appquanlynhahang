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
import com.example.quanlynhahang.model.ThongKeTongQuanQuanTri;
import com.example.quanlynhahang.model.ThongKeTongQuanNhanVien;
import com.example.quanlynhahang.model.BanAn;
import com.example.quanlynhahang.model.DonHang;
import com.example.quanlynhahang.model.MonAnDeXuat;
import com.example.quanlynhahang.model.DatBan;
import com.example.quanlynhahang.model.YeuCauPhucVu;
import com.example.quanlynhahang.model.NguoiDung;
import com.example.quanlynhahang.model.VaiTroNguoiDung;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String DATABASE_NAME = "restaurant.db";
    private static final int DATABASE_VERSION = 9;

    private static final String TEN_ANH_MAC_DINH = "menu_1";
    private static final String TEN_ANH_MON_LAU = "dish_6";
    private static final String TEN_ANH_SALAD = "menu_2";
    private static final String TEN_ANH_DO_UONG = "image3";
    private static final String BAN_MAC_DINH = "Bàn 01";
    private static final int SO_PHUT_CHAN_GUI_TRUNG_YEU_CAU = 5;
    private static final String EMAIL_TAI_KHOAN_TEST_KHACH_HANG = "kh1";
    private static final String SDT_TAI_KHOAN_TEST_KHACH_HANG = "0123456789";
    private static final String EMAIL_TAI_KHOAN_TEST_NHAN_VIEN = "nv1";
    private static final String SDT_TAI_KHOAN_TEST_NHAN_VIEN = "0123456790";
    private static final String EMAIL_TAI_KHOAN_TEST_ADMIN = "admin1";
    private static final String SDT_TAI_KHOAN_TEST_ADMIN = "0123456791";
    private static final String MAT_KHAU_TAI_KHOAN_TEST = "1";
    private static final String PASSWORD_PREFIX_SHA256 = "sha256:";
    private static final int SO_KHACH_DAT_BAN_TOI_DA = 20;
    private static final long DAT_BAN_TOI_THIEU_TRUOC_PHUT = 30L;
    private static final long CUA_SO_KICH_HOAT_DAT_BAN_PHUT = 30L;

    public static final String TABLE_USER = "users";
    public static final String TABLE_DISH = "dishes";
    public static final String TABLE_ORDER = "orders";
    public static final String TABLE_ORDER_ITEM = "order_items";
    public static final String TABLE_RESERVATION = "reservations";
    public static final String TABLE_SERVICE_REQUEST = "service_requests";
    public static final String TABLE_BAN_AN = "ban_an";

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
    private static final String COL_ORDER_TYPE = "order_type";
    private static final String COL_ORDER_TABLE_NUMBER = "table_number";
    private static final String COL_ORDER_NOTE = "note";
    private static final String COL_ORDER_PAYMENT_STATUS = "payment_status";
    private static final String COL_ORDER_PAYMENT_METHOD = "payment_method";
    private static final String COL_ORDER_RESERVATION_ID = "reservation_id";

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
    private static final String COL_RESERVATION_CODE = "reservation_code";
    private static final String COL_RESERVATION_LINKED_ORDER_ID = "linked_order_id";

    private static final String COL_SERVICE_REQUEST_ID = "id";
    private static final String COL_SERVICE_REQUEST_USER_ID = "user_id";
    private static final String COL_SERVICE_REQUEST_CONTENT = "content";
    private static final String COL_SERVICE_REQUEST_SENT_TIME = "sent_time";
    private static final String COL_SERVICE_REQUEST_STATUS = "status";
    private static final String COL_SERVICE_REQUEST_TYPE = "request_type";
    private static final String COL_SERVICE_REQUEST_TABLE_NUMBER = "table_number";
    private static final String COL_SERVICE_REQUEST_ORDER_ID = "order_id";
    private static final String COL_SERVICE_REQUEST_HANDLED_TIME = "handled_time";

    private static final String COL_BAN_AN_ID = "id";
    private static final String COL_BAN_AN_MA_BAN = "ma_ban";
    private static final String COL_BAN_AN_TEN_BAN = "ten_ban";
    private static final String COL_BAN_AN_SO_CHO = "so_cho";
    private static final String COL_BAN_AN_KHU_VUC = "khu_vuc";
    private static final String COL_BAN_AN_TRANG_THAI = "trang_thai";

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
        damBaoBangTonTai(db, TABLE_BAN_AN, taoBangBanAn());

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
                "TEXT NOT NULL DEFAULT '" + DonHang.TrangThai.CHO_XAC_NHAN.name() + "'");
        damBaoCotTonTai(db, TABLE_ORDER, COL_ORDER_TYPE,
                "TEXT NOT NULL DEFAULT '" + DonHang.HinhThucDon.MANG_DI.name() + "'");
        damBaoCotTonTai(db, TABLE_ORDER, COL_ORDER_TABLE_NUMBER, "TEXT NOT NULL DEFAULT ''");
        damBaoCotTonTai(db, TABLE_ORDER, COL_ORDER_NOTE, "TEXT NOT NULL DEFAULT ''");
        damBaoCotTonTai(db, TABLE_ORDER, COL_ORDER_PAYMENT_STATUS,
                "TEXT NOT NULL DEFAULT '" + DonHang.TrangThaiThanhToan.CHUA_THANH_TOAN.name() + "'");
        damBaoCotTonTai(db, TABLE_ORDER, COL_ORDER_PAYMENT_METHOD,
                "TEXT NOT NULL DEFAULT '" + DonHang.PhuongThucThanhToan.CHUA_CHON.name() + "'");
        damBaoCotTonTai(db, TABLE_ORDER, COL_ORDER_RESERVATION_ID, "INTEGER NOT NULL DEFAULT 0");

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
                "TEXT NOT NULL DEFAULT '" + DatBan.TrangThai.PENDING.name() + "'");
        damBaoCotTonTai(db, TABLE_RESERVATION, COL_RESERVATION_CODE, "TEXT NOT NULL DEFAULT ''");
        damBaoCotTonTai(db, TABLE_RESERVATION, COL_RESERVATION_LINKED_ORDER_ID, "INTEGER NOT NULL DEFAULT 0");

        damBaoCotTonTai(db, TABLE_SERVICE_REQUEST, COL_SERVICE_REQUEST_USER_ID, "INTEGER NOT NULL DEFAULT 0");
        damBaoCotTonTai(db, TABLE_SERVICE_REQUEST, COL_SERVICE_REQUEST_CONTENT, "TEXT NOT NULL DEFAULT ''");
        damBaoCotTonTai(db, TABLE_SERVICE_REQUEST, COL_SERVICE_REQUEST_SENT_TIME, "TEXT NOT NULL DEFAULT ''");
        damBaoCotTonTai(db, TABLE_SERVICE_REQUEST, COL_SERVICE_REQUEST_STATUS,
                "TEXT NOT NULL DEFAULT '" + YeuCauPhucVu.TrangThai.DANG_CHO.name() + "'");
        damBaoCotTonTai(db, TABLE_SERVICE_REQUEST, COL_SERVICE_REQUEST_TYPE,
                "TEXT NOT NULL DEFAULT '" + YeuCauPhucVu.LoaiYeuCau.GOI_NHAN_VIEN.name() + "'");
        damBaoCotTonTai(db, TABLE_SERVICE_REQUEST, COL_SERVICE_REQUEST_TABLE_NUMBER, "TEXT NOT NULL DEFAULT ''");
        damBaoCotTonTai(db, TABLE_SERVICE_REQUEST, COL_SERVICE_REQUEST_ORDER_ID, "INTEGER NOT NULL DEFAULT 0");
        damBaoCotTonTai(db, TABLE_SERVICE_REQUEST, COL_SERVICE_REQUEST_HANDLED_TIME, "TEXT NOT NULL DEFAULT ''");

        damBaoCotTonTai(db, TABLE_BAN_AN, COL_BAN_AN_MA_BAN, "TEXT NOT NULL DEFAULT ''");
        damBaoCotTonTai(db, TABLE_BAN_AN, COL_BAN_AN_TEN_BAN, "TEXT NOT NULL DEFAULT ''");
        damBaoCotTonTai(db, TABLE_BAN_AN, COL_BAN_AN_SO_CHO, "INTEGER NOT NULL DEFAULT 4");
        damBaoCotTonTai(db, TABLE_BAN_AN, COL_BAN_AN_KHU_VUC, "TEXT NOT NULL DEFAULT ''");
        damBaoCotTonTai(db, TABLE_BAN_AN, COL_BAN_AN_TRANG_THAI,
                "TEXT NOT NULL DEFAULT '" + BanAn.TrangThai.TRONG.name() + "'");
    }

    private void damBaoDuLieuMacDinh(SQLiteDatabase db) {
        seedDishesIfEmpty(appContext, db);
        chuanHoaSeedMonAnSaiDanhMuc(db);
        ensureTestUserExists(db);
        damBaoDuLieuTongQuanMau(db);
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
        valuesVaiTro.put(COL_USER_ROLE, VaiTroNguoiDung.KHACH_HANG.name());
        db.update(
                TABLE_USER,
                valuesVaiTro,
                COL_USER_ROLE + " IS NULL OR TRIM(" + COL_USER_ROLE + ") = '' OR UPPER(TRIM(" + COL_USER_ROLE + ")) NOT IN (?, ?, ?)",
                new String[]{
                        VaiTroNguoiDung.KHACH_HANG.name(),
                        VaiTroNguoiDung.NHAN_VIEN.name(),
                        VaiTroNguoiDung.ADMIN.name()
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
                + COL_ORDER_STATUS + " TEXT NOT NULL, "
                + COL_ORDER_TYPE + " TEXT NOT NULL DEFAULT '" + DonHang.HinhThucDon.MANG_DI.name() + "', "
                + COL_ORDER_TABLE_NUMBER + " TEXT NOT NULL DEFAULT '', "
                + COL_ORDER_NOTE + " TEXT NOT NULL DEFAULT '', "
                + COL_ORDER_PAYMENT_STATUS + " TEXT NOT NULL DEFAULT '" + DonHang.TrangThaiThanhToan.CHUA_THANH_TOAN.name() + "', "
                + COL_ORDER_PAYMENT_METHOD + " TEXT NOT NULL DEFAULT '" + DonHang.PhuongThucThanhToan.CHUA_CHON.name() + "', "
                + COL_ORDER_RESERVATION_ID + " INTEGER NOT NULL DEFAULT 0"
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
                + COL_RESERVATION_STATUS + " TEXT NOT NULL, "
                + COL_RESERVATION_CODE + " TEXT NOT NULL DEFAULT '', "
                + COL_RESERVATION_LINKED_ORDER_ID + " INTEGER NOT NULL DEFAULT 0"
                + ")";
    }

    private String taoBangYeuCauPhucVu() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_SERVICE_REQUEST + " ("
                + COL_SERVICE_REQUEST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_SERVICE_REQUEST_USER_ID + " INTEGER NOT NULL, "
                + COL_SERVICE_REQUEST_CONTENT + " TEXT NOT NULL, "
                + COL_SERVICE_REQUEST_SENT_TIME + " TEXT NOT NULL, "
                + COL_SERVICE_REQUEST_STATUS + " TEXT NOT NULL, "
                + COL_SERVICE_REQUEST_TYPE + " TEXT NOT NULL DEFAULT '" + YeuCauPhucVu.LoaiYeuCau.GOI_NHAN_VIEN.name() + "', "
                + COL_SERVICE_REQUEST_TABLE_NUMBER + " TEXT NOT NULL DEFAULT '', "
                + COL_SERVICE_REQUEST_ORDER_ID + " INTEGER NOT NULL DEFAULT 0, "
                + COL_SERVICE_REQUEST_HANDLED_TIME + " TEXT NOT NULL DEFAULT ''"
                + ")";
    }

    public long insertUser(String name, String email, String phone, String password) {
        return insertUser(name, email, phone, password, VaiTroNguoiDung.KHACH_HANG, true);
    }

    public long insertUser(String name, String email, String phone, String password, VaiTroNguoiDung role, boolean isActive) {
        if (TextUtils.isEmpty(name)
                || TextUtils.isEmpty(email)
                || TextUtils.isEmpty(phone)
                || TextUtils.isEmpty(password)) {
            return -1;
        }

        SQLiteDatabase db = getWritableDatabase();
        return insertUser(db, name, email, phone, password, role, isActive);
    }

    private long insertUser(SQLiteDatabase db, String name, String email, String phone, String password, VaiTroNguoiDung role, boolean isActive) {
        if (isPhoneInUse(db, phone, -1)) {
            return -1;
        }

        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, name);
        values.put(COL_USER_EMAIL, email);
        values.put(COL_USER_PHONE, phone);
        values.put(COL_USER_PASSWORD, hashPassword(password));
        values.put(COL_USER_ROLE, role != null ? role.name() : VaiTroNguoiDung.KHACH_HANG.name());
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
    public NguoiDung getUserByEmail(String email) {
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
    public NguoiDung getUserById(long userId) {
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
    public NguoiDung layNguoiDungTheoId(long userId) {
        return getUserById(userId);
    }

    @Nullable
    public NguoiDung getUserByPhone(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return null;
        }

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(
                    TABLE_USER,
                    new String[]{COL_USER_ID, COL_USER_NAME, COL_USER_EMAIL, COL_USER_PHONE, COL_USER_ROLE, COL_USER_IS_ACTIVE},
                    COL_USER_PHONE + " = ?",
                    new String[]{phone},
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
    public NguoiDung layNguoiDungTheoSoDienThoai(String phone) {
        return getUserByPhone(phone);
    }

    public boolean isPhoneInUse(String phone, long excludeUserId) {
        SQLiteDatabase db = getReadableDatabase();
        return isPhoneInUse(db, phone, excludeUserId);
    }

    private boolean isPhoneInUse(SQLiteDatabase db, String phone, long excludeUserId) {
        if (TextUtils.isEmpty(phone)) {
            return false;
        }

        Cursor cursor = null;
        try {
            String selection = COL_USER_PHONE + " = ?";
            List<String> selectionArgs = new ArrayList<>();
            selectionArgs.add(phone);
            if (excludeUserId > 0) {
                selection += " AND " + COL_USER_ID + " != ?";
                selectionArgs.add(String.valueOf(excludeUserId));
            }

            cursor = db.query(
                    TABLE_USER,
                    new String[]{COL_USER_ID},
                    selection,
                    selectionArgs.toArray(new String[0]),
                    null,
                    null,
                    null,
                    "1"
            );
            return cursor.moveToFirst();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public boolean soDienThoaiDaDuocSuDung(String phone, long excludeUserId) {
        return isPhoneInUse(phone, excludeUserId);
    }

    @Nullable
    public NguoiDung checkLogin(String usernameOrEmail, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(
                    TABLE_USER,
                    new String[]{COL_USER_ID, COL_USER_NAME, COL_USER_EMAIL, COL_USER_PHONE, COL_USER_ROLE, COL_USER_IS_ACTIVE, COL_USER_PASSWORD},
                    "(" + COL_USER_EMAIL + " = ? OR " + COL_USER_PHONE + " = ?) AND " + COL_USER_IS_ACTIVE + " = 1",
                    new String[]{usernameOrEmail, usernameOrEmail},
                    null,
                    null,
                    null,
                    "1"
            );

            if (!cursor.moveToFirst()) {
                return null;
            }

            String storedPassword = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PASSWORD));
            if (!verifyPassword(password, storedPassword)) {
                return null;
            }

            NguoiDung user = mapUser(cursor);
            if (user != null && !isHashedPassword(storedPassword)) {
                migrateLegacyPasswordHash(user.layId(), password);
            }
            return user;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Nullable
    public NguoiDung kiemTraDangNhap(String usernameOrEmail, String password) {
        return checkLogin(usernameOrEmail, password);
    }

    public boolean updateUserProfile(long userId, String name, String phone) {
        if (isPhoneInUse(phone, userId)) {
            return false;
        }

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

    public boolean capNhatThongTinNguoiDung(long userId, String name, String phone) {
        return updateUserProfile(userId, name, phone);
    }

    public boolean updateUserPassword(long userId, String newPassword) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_PASSWORD, hashPassword(newPassword));

        int rows = db.update(
                TABLE_USER,
                values,
                COL_USER_ID + " = ?",
                new String[]{String.valueOf(userId)}
        );
        return rows > 0;
    }

    public boolean capNhatMatKhauNguoiDung(long userId, String newPassword) {
        return updateUserPassword(userId, newPassword);
    }

    public List<NguoiDung> getAllUsers() {
        return getUsersByRole(null);
    }

    public List<NguoiDung> layTatCaNguoiDung() {
        return getAllUsers();
    }

    public List<NguoiDung> getUsersByRole(@Nullable VaiTroNguoiDung role) {
        List<NguoiDung> users = new ArrayList<>();
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

    public List<NguoiDung> layNguoiDungTheoVaiTro(@Nullable VaiTroNguoiDung role) {
        return getUsersByRole(role);
    }

    public boolean updateVaiTroNguoiDung(long userId, VaiTroNguoiDung role) {
        if (userId <= 0 || role == null) {
            return false;
        }
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_ROLE, role.name());
        int rows = db.update(TABLE_USER, values, COL_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        return rows > 0;
    }

    public boolean capNhatVaiTroNguoiDung(long userId, VaiTroNguoiDung role) {
        return updateVaiTroNguoiDung(userId, role);
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

    public boolean capNhatTrangThaiHoatDongNguoiDung(long userId, boolean isActive) {
        return updateUserActive(userId, isActive);
    }

    public int countAllUsers() {
        return demSoBanGhi(TABLE_USER, null, null);
    }

    public int countUsersByRole(VaiTroNguoiDung role) {
        if (role == null) {
            return countAllUsers();
        }
        return demSoBanGhi(TABLE_USER, COL_USER_ROLE + " = ?", new String[]{role.name()});
    }

    public void seedDishesIfEmpty(Context context) {
        SQLiteDatabase db = getWritableDatabase();
        seedDishesIfEmpty(context == null ? appContext : context.getApplicationContext(), db);
    }

    public List<DishRecord> layTatCaMonAn() {
        return queryDishes(null, null);
    }

    private List<DishRecord> layTatCaMonAn(SQLiteDatabase db) {
        return queryDishes(db, null, null);
    }

    public List<DishRecord> timKiemMonAn(@Nullable String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            return layTatCaMonAn();
        }
        String trimmedKeyword = keyword.trim();
        String likeValue = "%" + trimmedKeyword + "%";
        return queryDishes(
                COL_DISH_NAME + " LIKE ? OR " + COL_DISH_CATEGORY + " LIKE ? OR " + COL_DISH_DESCRIPTION + " LIKE ?",
                new String[]{likeValue, likeValue, likeValue}
        );
    }

    public long themBanGhiMonAn(String name,
                                String price,
                                String description,
                                @Nullable String imageResName,
                                boolean isAvailable,
                                @Nullable String category,
                                int recommendScore) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(price) || TextUtils.isEmpty(description) || TextUtils.isEmpty(category)) {
            return -1;
        }
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = taoGiaTriMonAn(name, price, description, imageResName, isAvailable, category, recommendScore);
        return db.insert(TABLE_DISH, null, values);
    }

    public boolean capNhatBanGhiMonAn(long dishId,
                                      String name,
                                      String price,
                                      String description,
                                      @Nullable String imageResName,
                                      boolean isAvailable,
                                      @Nullable String category,
                                      int recommendScore) {
        if (dishId <= 0 || TextUtils.isEmpty(name) || TextUtils.isEmpty(price) || TextUtils.isEmpty(description) || TextUtils.isEmpty(category)) {
            return false;
        }
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = taoGiaTriMonAn(name, price, description, imageResName, isAvailable, category, recommendScore);
        int rows = db.update(TABLE_DISH, values, COL_DISH_ID + " = ?", new String[]{String.valueOf(dishId)});
        return rows > 0;
    }

    public boolean xoaMonAnTheoId(long dishId) {
        if (dishId <= 0) {
            return false;
        }
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(TABLE_DISH, COL_DISH_ID + " = ?", new String[]{String.valueOf(dishId)});
        return rows > 0;
    }

    public boolean capNhatTrangThaiPhucVuMon(long dishId, boolean isAvailable) {
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
        return queryDishes(getReadableDatabase(), selection, selectionArgs);
    }

    private List<DishRecord> queryDishes(SQLiteDatabase db,
                                         @Nullable String selection,
                                         @Nullable String[] selectionArgs) {
        List<DishRecord> dishRecords = new ArrayList<>();
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
                MonAnDeXuat dishItem = new MonAnDeXuat(
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

    public List<MonAnDeXuat> layTatCaMonHienThi() {
        List<MonAnDeXuat> dishes = new ArrayList<>();
        List<DishRecord> dishRecords = layTatCaMonAn();

        for (DishRecord record : dishRecords) {
            dishes.add(record.layMonAn());
        }

        return dishes;
    }

    public List<MonAnDeXuat> layDanhSachMonTheoDanhMuc(@Nullable String tenDanhMuc) {
        List<MonAnDeXuat> dishes = new ArrayList<>();
        for (DishRecord record : layTatCaMonAn()) {
            MonAnDeXuat dishItem = record.layMonAn();
            if (TextUtils.isEmpty(tenDanhMuc) || TextUtils.equals(tenDanhMuc, dishItem.layTenDanhMuc())) {
                dishes.add(dishItem);
            }
        }
        return dishes;
    }

    public List<MonAnDeXuat> layMonDeXuatTrangChu(int soLuongToiDa) {
        List<MonAnDeXuat> available = new ArrayList<>();
        List<MonAnDeXuat> fallback = new ArrayList<>();

        for (DishRecord record : layTatCaMonAn()) {
            MonAnDeXuat dishItem = record.layMonAn();
            fallback.add(dishItem);
            if (dishItem.laConPhucVu()) {
                available.add(dishItem);
            }
        }

        List<MonAnDeXuat> source = available.isEmpty() ? fallback : available;
        source.sort((first, second) -> Integer.compare(second.layDiemDeXuat(), first.layDiemDeXuat()));

        int gioiHan = Math.min(Math.max(soLuongToiDa, 0), source.size());
        return new ArrayList<>(source.subList(0, gioiHan));
    }

    public long themDonHang(int userId,
                            String code,
                            String time,
                            String totalPrice,
                            DonHang.TrangThai status,
                            List<DonHang.MonTrongDon> dishes) {
        return themDonHang(
                userId,
                code,
                time,
                totalPrice,
                status,
                DonHang.HinhThucDon.MANG_DI,
                null,
                null,
                DonHang.TrangThaiThanhToan.CHUA_THANH_TOAN,
                DonHang.PhuongThucThanhToan.CHUA_CHON,
                0,
                dishes
        );
    }

    public long themDonHang(int userId,
                            String code,
                            String time,
                            String totalPrice,
                            DonHang.TrangThai status,
                            DonHang.HinhThucDon hinhThucDon,
                            @Nullable String tableNumber,
                            @Nullable String note,
                            DonHang.TrangThaiThanhToan paymentStatus,
                            DonHang.PhuongThucThanhToan paymentMethod,
                            long reservationId,
                            List<DonHang.MonTrongDon> dishes) {
        if (userId <= 0
                || TextUtils.isEmpty(code)
                || TextUtils.isEmpty(time)
                || TextUtils.isEmpty(totalPrice)
                || status == null
                || hinhThucDon == null
                || paymentStatus == null
                || paymentMethod == null
                || dishes == null
                || dishes.isEmpty()) {
            return -1;
        }

        String soBanDaLamSach = tableNumber == null ? "" : tableNumber.trim();
        if (hinhThucDon == DonHang.HinhThucDon.AN_TAI_QUAN && TextUtils.isEmpty(soBanDaLamSach)) {
            return -1;
        }
        if (hinhThucDon == DonHang.HinhThucDon.MANG_DI) {
            soBanDaLamSach = "";
            reservationId = 0;
        }

        long reservationIdHopLe = reservationId;
        if (hinhThucDon == DonHang.HinhThucDon.AN_TAI_QUAN) {
            reservationIdHopLe = timReservationIdPhuHopChoDonTaiQuan(userId, soBanDaLamSach, time, reservationId);
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
            orderValues.put(COL_ORDER_TYPE, hinhThucDon.name());
            orderValues.put(COL_ORDER_TABLE_NUMBER, soBanDaLamSach);
            orderValues.put(COL_ORDER_NOTE, note == null ? "" : note.trim());
            orderValues.put(COL_ORDER_PAYMENT_STATUS, paymentStatus.name());
            orderValues.put(COL_ORDER_PAYMENT_METHOD, paymentMethod.name());
            orderValues.put(COL_ORDER_RESERVATION_ID, Math.max(reservationIdHopLe, 0));

            long orderId = db.insert(TABLE_ORDER, null, orderValues);
            if (orderId <= 0) {
                return -1;
            }

            for (DonHang.MonTrongDon orderDish : dishes) {
                if (orderDish == null || orderDish.layMonAn() == null) {
                    return -1;
                }

                MonAnDeXuat dishItem = orderDish.layMonAn();
                ContentValues itemValues = new ContentValues();
                itemValues.put(COL_ORDER_ITEM_ORDER_ID, orderId);
                itemValues.put(COL_ORDER_ITEM_DISH_NAME, dishItem.layTenMon());
                itemValues.put(COL_ORDER_ITEM_DISH_PRICE, dishItem.layGiaBan());
                itemValues.put(COL_ORDER_ITEM_IMAGE_RES_NAME, resolveImageResName(dishItem.layIdAnhTaiNguyen()));
                itemValues.put(COL_ORDER_ITEM_IS_AVAILABLE, dishItem.laConPhucVu() ? 1 : 0);
                itemValues.put(COL_ORDER_ITEM_QUANTITY, orderDish.laySoLuong());

                long itemId = db.insert(TABLE_ORDER_ITEM, null, itemValues);
                if (itemId <= 0) {
                    return -1;
                }
            }

            if (reservationIdHopLe > 0) {
                ContentValues reservationValues = new ContentValues();
                reservationValues.put(COL_RESERVATION_LINKED_ORDER_ID, orderId);
                reservationValues.put(COL_RESERVATION_STATUS, DatBan.TrangThai.ACTIVE.name());
                db.update(
                        TABLE_RESERVATION,
                        reservationValues,
                        COL_RESERVATION_ID + " = ?",
                        new String[]{String.valueOf(reservationIdHopLe)}
                );
            }

            db.setTransactionSuccessful();
            return orderId;
        } finally {
            db.endTransaction();
        }
    }

    public List<DonHang> layDonHangTheoNguoiDung(int userId) {
        return layDonHangTheoNguoiDung((long) userId);
    }

    public List<DonHang> layDonHangTheoNguoiDung(long userId) {
        return queryDonHangs(COL_ORDER_USER_ID + " = ?", new String[]{String.valueOf(userId)});
    }

    @Nullable
    public DonHang layDonHangTheoMa(long userId, @Nullable String maDon) {
        if (userId <= 0 || TextUtils.isEmpty(maDon)) {
            return null;
        }
        List<DonHang> ketQua = queryDonHangs(
                COL_ORDER_USER_ID + " = ? AND " + COL_ORDER_CODE + " = ?",
                new String[]{String.valueOf(userId), maDon.trim()}
        );
        return ketQua.isEmpty() ? null : ketQua.get(0);
    }

    public List<DonHang> layTatCaDonHang() {
        return queryDonHangs(null, null);
    }

    public boolean capNhatTrangThaiDonHang(long orderId, DonHang.TrangThai status) {
        if (orderId <= 0 || status == null) {
            return false;
        }

        DonHang.TrangThai currentStatus = layTrangThaiDonHangTheoId(orderId);
        if (currentStatus == null || !coTheChuyenTrangThaiDonHang(currentStatus, status)) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ORDER_STATUS, status.name());
        int rows = db.update(TABLE_ORDER, values, COL_ORDER_ID + " = ?", new String[]{String.valueOf(orderId)});
        if (rows > 0) {
            dongBoTrangThaiDatBanTheoDonHang(orderId, status);
            return true;
        }
        return false;
    }

    public boolean capNhatThanhToanDonHang(long orderId,
                                           DonHang.TrangThaiThanhToan paymentStatus,
                                           DonHang.PhuongThucThanhToan paymentMethod) {
        if (orderId <= 0 || paymentStatus == null || paymentMethod == null) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();
        return capNhatThanhToanDonHangTrongGiaoDich(db, orderId, paymentStatus, paymentMethod);
    }

    public int demTatCaDonHang() {
        return demSoBanGhi(TABLE_ORDER, null, null);
    }

    public int demDonHangTheoTrangThai(DonHang.TrangThai status) {
        if (status == null) {
            return demTatCaDonHang();
        }
        return demSoBanGhi(TABLE_ORDER, COL_ORDER_STATUS + " = ?", new String[]{status.name()});
    }

    public boolean huyDonHang(long orderId) {
        return capNhatTrangThaiDonHang(orderId, DonHang.TrangThai.DA_HUY);
    }

    public List<DatBan> layDatBanTheoNguoiDung(int userId) {
        return layDatBanTheoNguoiDung((long) userId);
    }

    public List<DatBan> layDatBanTheoNguoiDung(long userId) {
        return queryReservations(COL_RESERVATION_USER_ID + " = ?", new String[]{String.valueOf(userId)});
    }

    public List<DatBan> layTatCaDatBan() {
        return queryReservations(null, null);
    }

    public List<String> layDanhSachBanDaDat(String thoiGianDatBan) {
        return layDanhSachBanDaDat(thoiGianDatBan, 0);
    }

    public List<String> layDanhSachBanDaDat(String thoiGianDatBan, long reservationIdBoQua) {
        List<String> occupiedTables = new ArrayList<>();
        long thoiGianMucTieu = parseDonHangTimeToMillis(thoiGianDatBan);
        if (thoiGianMucTieu <= 0L) {
            return occupiedTables;
        }

        for (DatBan datBan : layTatCaDatBan()) {
            if (datBan == null || TextUtils.isEmpty(datBan.laySoBan()) || datBan.layId() == reservationIdBoQua) {
                continue;
            }
            DatBan.TrangThai trangThaiHieuLuc = xacDinhTrangThaiDatBanHieuLuc(datBan.layTrangThai(), datBan.layThoiGian(), datBan.layIdDonHangLienKet());
            dongBoTrangThaiDatBanNeuCan(datBan.layId(), datBan.layTrangThai(), trangThaiHieuLuc, datBan.layIdDonHangLienKet());
            if (!laDatBanChiemKhungGio(trangThaiHieuLuc, datBan.layThoiGian(), thoiGianMucTieu)) {
                continue;
            }
            if (!occupiedTables.contains(datBan.laySoBan())) {
                occupiedTables.add(datBan.laySoBan());
            }
        }
        return occupiedTables;
    }

    public boolean capNhatTrangThaiDatBan(long reservationId, DatBan.TrangThai status) {
        if (reservationId <= 0 || status == null) {
            return false;
        }

        DatBan.TrangThai currentStatus = layTrangThaiDatBanTheoId(reservationId);
        if (currentStatus == null || !coTheChuyenTrangThaiDatBan(currentStatus, status)) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_RESERVATION_STATUS, status.name());
        int rows = db.update(TABLE_RESERVATION, values, COL_RESERVATION_ID + " = ?", new String[]{String.valueOf(reservationId)});
        return rows > 0;
    }

    public int demDatBanTheoTrangThai(DatBan.TrangThai status) {
        if (status == null) {
            return demSoBanGhi(TABLE_RESERVATION, null, null);
        }
        return demSoBanGhi(TABLE_RESERVATION, COL_RESERVATION_STATUS + " = ?", new String[]{status.name()});
    }

    public long themDatBan(int userId,
                           String time,
                           String tableNumber,
                           int peopleCount,
                           String notes) {
        return themDatBan(
                (long) userId,
                taoMaDatBan(),
                time,
                tableNumber,
                peopleCount,
                notes,
                DatBan.TrangThai.PENDING,
                0
        );
    }

    public long themDatBan(long userId,
                           String time,
                           String tableNumber,
                           int guestCount,
                           @Nullable String note,
                           DatBan.TrangThai status) {
        return themDatBan(userId, taoMaDatBan(), time, tableNumber, guestCount, note, status, 0);
    }

    public long themDatBan(long userId,
                           String reservationCode,
                           String time,
                           String tableNumber,
                           int guestCount,
                           @Nullable String note,
                           DatBan.TrangThai status,
                           long linkedOrderId) {
        if (userId <= 0
                || TextUtils.isEmpty(time)
                || TextUtils.isEmpty(tableNumber)
                || guestCount <= 0
                || guestCount > SO_KHACH_DAT_BAN_TOI_DA
                || status == null
                || !laThoiGianDatBanHopLe(time)
                || !banCoTheDatTrongKhungGio(time, tableNumber)
                || coDatBanHieuLucTheoNguoiDung(userId)) {
            return -1;
        }

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_RESERVATION_USER_ID, userId);
        values.put(COL_RESERVATION_CODE, TextUtils.isEmpty(reservationCode) ? taoMaDatBan() : reservationCode);
        values.put(COL_RESERVATION_TIME, time);
        values.put(COL_RESERVATION_TABLE_NUMBER, tableNumber);
        values.put(COL_RESERVATION_GUEST_COUNT, guestCount);
        values.put(COL_RESERVATION_NOTE, note == null ? "" : note.trim());
        values.put(COL_RESERVATION_STATUS, status.name());
        values.put(COL_RESERVATION_LINKED_ORDER_ID, Math.max(linkedOrderId, 0));
        return db.insert(TABLE_RESERVATION, null, values);
    }

    public boolean huyDatBan(long reservationId) {
        return capNhatTrangThaiDatBan(reservationId, DatBan.TrangThai.CANCELLED);
    }

    public boolean capNhatBanDatBan(long reservationId, @Nullable String soBanMoi) {
        if (reservationId <= 0 || TextUtils.isEmpty(soBanMoi)) {
            return false;
        }

        DatBan datBan = layDatBanTheoId(reservationId);
        if (datBan == null || !datBan.laDangHieuLuc()) {
            return false;
        }

        String soBanDaLamSach = soBanMoi.trim();
        if (TextUtils.isEmpty(soBanDaLamSach)) {
            return false;
        }
        if (soBanDaLamSach.equalsIgnoreCase(datBan.laySoBan())) {
            return true;
        }
        if (!banCoTheDatTrongKhungGio(datBan.layThoiGian(), soBanDaLamSach, reservationId)) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_RESERVATION_TABLE_NUMBER, soBanDaLamSach);
        int rows = db.update(TABLE_RESERVATION, values, COL_RESERVATION_ID + " = ?", new String[]{String.valueOf(reservationId)});
        return rows > 0;
    }

    @Nullable
    public DatBan layDatBanActiveTheoNguoiDung(long userId) {
        return timDatBanTheoTrangThai(layDatBanTheoNguoiDung(userId), DatBan.TrangThai.ACTIVE);
    }

    @Nullable
    public DatBan layDatBanPendingTheoNguoiDung(long userId) {
        return timDatBanTheoTrangThai(layDatBanTheoNguoiDung(userId), DatBan.TrangThai.PENDING);
    }

    @Nullable
    public DatBan layDatBanHieuLucTheoNguoiDung(long userId) {
        return timDatBanHieuLucTheoNguoiDung(userId, null, null);
    }

    @Nullable
    public DatBan timDatBanHieuLucTheoNguoiDung(long userId,
                                                @Nullable String soBan,
                                                @Nullable String thoiGianDonHang) {
        if (userId <= 0) {
            return null;
        }

        String soBanDaLamSach = soBan == null ? "" : soBan.trim();
        long thoiGianMucTieu = parseDonHangTimeToMillis(thoiGianDonHang);
        DatBan datBanUuTien = null;
        long khoangCachNhoNhat = Long.MAX_VALUE;

        for (DatBan datBan : layDatBanTheoNguoiDung(userId)) {
            if (datBan == null || datBan.daKetThuc() || datBan.layIdDonHangLienKet() > 0) {
                continue;
            }
            if (!TextUtils.isEmpty(soBanDaLamSach)
                    && !soBanDaLamSach.equalsIgnoreCase(datBan.laySoBan())) {
                continue;
            }

            DatBan.TrangThai trangThaiHieuLuc = xacDinhTrangThaiDatBanHieuLuc(datBan.layTrangThai(), datBan.layThoiGian(), datBan.layIdDonHangLienKet());
            dongBoTrangThaiDatBanNeuCan(datBan.layId(), datBan.layTrangThai(), trangThaiHieuLuc, datBan.layIdDonHangLienKet());
            if (trangThaiHieuLuc != DatBan.TrangThai.PENDING && trangThaiHieuLuc != DatBan.TrangThai.ACTIVE) {
                continue;
            }

            if (thoiGianMucTieu > 0L) {
                long thoiGianDatBan = parseDonHangTimeToMillis(datBan.layThoiGian());
                long khoangCach = thoiGianDatBan <= 0L ? Long.MAX_VALUE : Math.abs(thoiGianMucTieu - thoiGianDatBan);
                if (khoangCach <= CUA_SO_KICH_HOAT_DAT_BAN_PHUT * 60_000L && khoangCach < khoangCachNhoNhat) {
                    khoangCachNhoNhat = khoangCach;
                    datBanUuTien = datBan;
                }
                continue;
            }

            if (datBanUuTien == null || trangThaiHieuLuc == DatBan.TrangThai.ACTIVE) {
                datBanUuTien = datBan;
            }
        }
        return datBanUuTien;
    }

    public boolean huyYeuCauPhucVu(long requestId) {
        return capNhatTrangThaiYeuCauPhucVu(requestId, YeuCauPhucVu.TrangThai.DA_HUY);
    }

    public List<YeuCauPhucVu> layYeuCauTheoNguoiDung(long userId) {
        return queryServiceRequests(COL_SERVICE_REQUEST_USER_ID + " = ?", new String[]{String.valueOf(userId)});
    }

    public List<YeuCauPhucVu> layTatCaYeuCauPhucVu() {
        return queryServiceRequests(null, null);
    }

    public boolean capNhatTrangThaiYeuCauPhucVu(long requestId, YeuCauPhucVu.TrangThai status) {
        if (requestId <= 0 || status == null) {
            return false;
        }

        YeuCauPhucVu yeuCau = layYeuCauPhucVuTheoId(requestId);
        if (yeuCau == null || !coTheChuyenTrangThaiYeuCau(yeuCau.layTrangThai(), status)) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(COL_SERVICE_REQUEST_STATUS, status.name());
            if (status == YeuCauPhucVu.TrangThai.DA_XU_LY || status == YeuCauPhucVu.TrangThai.DA_HUY) {
                values.put(COL_SERVICE_REQUEST_HANDLED_TIME, layThoiGianHienTai());
            }
            int rows = db.update(TABLE_SERVICE_REQUEST, values, COL_SERVICE_REQUEST_ID + " = ?", new String[]{String.valueOf(requestId)});
            if (rows <= 0) {
                return false;
            }
            if (yeuCau.layLoaiYeuCau() == YeuCauPhucVu.LoaiYeuCau.THANH_TOAN
                    && yeuCau.coDonHangLienQuan()
                    && !dongBoTrangThaiThanhToanTheoYeuCau(db, yeuCau.layIdDonHang(), status)) {
                return false;
            }
            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
        }
    }

    public boolean coYeuCauDangXuLyGanDay(long userId,
                                          YeuCauPhucVu.LoaiYeuCau loaiYeuCau,
                                          @Nullable String soBan) {
        if (userId <= 0 || loaiYeuCau == null) {
            return false;
        }

        String soBanDaLamSach = soBan == null ? "" : soBan.trim();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            String selection = COL_SERVICE_REQUEST_USER_ID + " = ? AND "
                    + COL_SERVICE_REQUEST_TYPE + " = ? AND "
                    + COL_SERVICE_REQUEST_STATUS + " IN (?, ?)";
            List<String> selectionArgs = new ArrayList<>();
            selectionArgs.add(String.valueOf(userId));
            selectionArgs.add(loaiYeuCau.name());
            selectionArgs.add(YeuCauPhucVu.TrangThai.DANG_CHO.name());
            selectionArgs.add(YeuCauPhucVu.TrangThai.DANG_XU_LY.name());
            if (TextUtils.isEmpty(soBanDaLamSach)) {
                selection += " AND (" + COL_SERVICE_REQUEST_TABLE_NUMBER + " IS NULL OR TRIM(" + COL_SERVICE_REQUEST_TABLE_NUMBER + ") = '')";
            } else {
                selection += " AND TRIM(" + COL_SERVICE_REQUEST_TABLE_NUMBER + ") = ?";
                selectionArgs.add(soBanDaLamSach);
            }

            cursor = db.query(
                    TABLE_SERVICE_REQUEST,
                    new String[]{COL_SERVICE_REQUEST_SENT_TIME},
                    selection,
                    selectionArgs.toArray(new String[0]),
                    null,
                    null,
                    COL_SERVICE_REQUEST_ID + " DESC",
                    "1"
            );
            if (!cursor.moveToFirst()) {
                return false;
            }

            String thoiGianGui = cursor.getString(cursor.getColumnIndexOrThrow(COL_SERVICE_REQUEST_SENT_TIME));
            long mocGui = parseDonHangTimeToMillis(thoiGianGui);
            if (mocGui <= 0) {
                return false;
            }
            long chenhlechPhut = (System.currentTimeMillis() - mocGui) / 60000L;
            return chenhlechPhut < SO_PHUT_CHAN_GUI_TRUNG_YEU_CAU;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public int demYeuCauTheoTrangThai(YeuCauPhucVu.TrangThai status) {
        if (status == null) {
            return demSoBanGhi(TABLE_SERVICE_REQUEST, null, null);
        }
        return demSoBanGhi(TABLE_SERVICE_REQUEST, COL_SERVICE_REQUEST_STATUS + " = ?", new String[]{status.name()});
    }

    public boolean coYeuCauThanhToanDangHoatDongTheoBan(long userId, @Nullable String soBan) {
        if (userId <= 0 || TextUtils.isEmpty(soBan)) {
            return false;
        }

        String soBanDaLamSach = soBan.trim();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(
                    TABLE_SERVICE_REQUEST,
                    new String[]{COL_SERVICE_REQUEST_ID},
                    COL_SERVICE_REQUEST_USER_ID + " = ? AND TRIM(" + COL_SERVICE_REQUEST_TABLE_NUMBER + ") = ? AND "
                            + COL_SERVICE_REQUEST_TYPE + " = ? AND "
                            + COL_SERVICE_REQUEST_STATUS + " IN (?, ?)",
                    new String[]{
                            String.valueOf(userId),
                            soBanDaLamSach,
                            YeuCauPhucVu.LoaiYeuCau.THANH_TOAN.name(),
                            YeuCauPhucVu.TrangThai.DANG_CHO.name(),
                            YeuCauPhucVu.TrangThai.DANG_XU_LY.name()
                    },
                    null,
                    null,
                    COL_SERVICE_REQUEST_ID + " DESC",
                    "1"
            );
            return cursor.moveToFirst();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public ThongKeTongQuanQuanTri layThongKeTongQuanQuanTri() {
        return new ThongKeTongQuanQuanTri(
                countAllUsers(),
                countUsersByRole(VaiTroNguoiDung.KHACH_HANG),
                countUsersByRole(VaiTroNguoiDung.NHAN_VIEN),
                countUsersByRole(VaiTroNguoiDung.ADMIN),
                countAllDishes(),
                demTatCaDonHang(),
                demDonHangTheoTrangThai(DonHang.TrangThai.CHO_XAC_NHAN),
                demDatBanTheoTrangThai(DatBan.TrangThai.PENDING),
                demYeuCauTheoTrangThai(YeuCauPhucVu.TrangThai.DANG_CHO)
                        + demYeuCauTheoTrangThai(YeuCauPhucVu.TrangThai.DANG_XU_LY)
        );
    }

    @Nullable
    private DonHang.TrangThai layTrangThaiDonHangTheoId(long orderId) {
        return getEnumStatusById(TABLE_ORDER, COL_ORDER_ID, COL_ORDER_STATUS, orderId, this::parseDonHangStatus);
    }

    @Nullable
    private DatBan.TrangThai layTrangThaiDatBanTheoId(long reservationId) {
        return getEnumStatusById(TABLE_RESERVATION, COL_RESERVATION_ID, COL_RESERVATION_STATUS, reservationId, this::parseReservationStatus);
    }

    @Nullable
    private YeuCauPhucVu.TrangThai layTrangThaiYeuCauTheoId(long requestId) {
        return getEnumStatusById(TABLE_SERVICE_REQUEST, COL_SERVICE_REQUEST_ID, COL_SERVICE_REQUEST_STATUS, requestId, this::parseServiceRequestStatus);
    }

    @Nullable
    public DonHang layDonHangTheoId(long orderId) {
        if (orderId <= 0) {
            return null;
        }
        List<DonHang> ketQua = queryDonHangs(COL_ORDER_ID + " = ?", new String[]{String.valueOf(orderId)});
        return ketQua.isEmpty() ? null : ketQua.get(0);
    }

    @Nullable
    public YeuCauPhucVu layYeuCauPhucVuTheoId(long requestId) {
        if (requestId <= 0) {
            return null;
        }
        List<YeuCauPhucVu> ketQua = queryServiceRequests(COL_SERVICE_REQUEST_ID + " = ?", new String[]{String.valueOf(requestId)});
        return ketQua.isEmpty() ? null : ketQua.get(0);
    }

    private boolean coTheChuyenTrangThaiDonHang(@Nullable DonHang.TrangThai current, @Nullable DonHang.TrangThai next) {
        if (current == null || next == null || current == next) {
            return false;
        }
        if (current == DonHang.TrangThai.CHO_XAC_NHAN) {
            return next == DonHang.TrangThai.DANG_CHUAN_BI || next == DonHang.TrangThai.DA_HUY;
        }
        if (current == DonHang.TrangThai.DANG_CHUAN_BI) {
            return next == DonHang.TrangThai.SAN_SANG_PHUC_VU || next == DonHang.TrangThai.DA_HUY;
        }
        if (current == DonHang.TrangThai.SAN_SANG_PHUC_VU) {
            return next == DonHang.TrangThai.HOAN_THANH || next == DonHang.TrangThai.DA_HUY;
        }
        return false;
    }

    private boolean coTheChuyenTrangThaiDatBan(@Nullable DatBan.TrangThai current, @Nullable DatBan.TrangThai next) {
        if (current == null || next == null || current == next) {
            return false;
        }
        if (current == DatBan.TrangThai.PENDING) {
            return next == DatBan.TrangThai.ACTIVE
                    || next == DatBan.TrangThai.CANCELLED
                    || next == DatBan.TrangThai.EXPIRED;
        }
        if (current == DatBan.TrangThai.ACTIVE) {
            return next == DatBan.TrangThai.COMPLETED
                    || next == DatBan.TrangThai.EXPIRED
                    || next == DatBan.TrangThai.CANCELLED;
        }
        return false;
    }

    private boolean coTheChuyenTrangThaiYeuCau(@Nullable YeuCauPhucVu.TrangThai current, @Nullable YeuCauPhucVu.TrangThai next) {
        if (current == null || next == null || current == next) {
            return false;
        }
        if (current == YeuCauPhucVu.TrangThai.DANG_CHO) {
            return next == YeuCauPhucVu.TrangThai.DANG_XU_LY
                    || next == YeuCauPhucVu.TrangThai.DA_XU_LY
                    || next == YeuCauPhucVu.TrangThai.DA_HUY;
        }
        if (current == YeuCauPhucVu.TrangThai.DANG_XU_LY) {
            return next == YeuCauPhucVu.TrangThai.DA_XU_LY || next == YeuCauPhucVu.TrangThai.DA_HUY;
        }
        return false;
    }

    private boolean capNhatThanhToanDonHangTrongGiaoDich(SQLiteDatabase db,
                                                         long orderId,
                                                         DonHang.TrangThaiThanhToan paymentStatus,
                                                         DonHang.PhuongThucThanhToan paymentMethod) {
        if (db == null || orderId <= 0 || paymentStatus == null || paymentMethod == null) {
            return false;
        }
        ContentValues values = new ContentValues();
        values.put(COL_ORDER_PAYMENT_STATUS, paymentStatus.name());
        values.put(COL_ORDER_PAYMENT_METHOD, paymentMethod.name());
        int rows = db.update(TABLE_ORDER, values, COL_ORDER_ID + " = ?", new String[]{String.valueOf(orderId)});
        return rows > 0;
    }

    private boolean dongBoTrangThaiThanhToanTheoYeuCau(SQLiteDatabase db,
                                                       long orderId,
                                                       YeuCauPhucVu.TrangThai trangThaiYeuCau) {
        DonHang donHang = layDonHangTheoId(orderId);
        if (donHang == null) {
            return false;
        }
        if (trangThaiYeuCau == YeuCauPhucVu.TrangThai.DA_HUY) {
            return capNhatThanhToanDonHangTrongGiaoDich(
                    db,
                    orderId,
                    DonHang.TrangThaiThanhToan.CHUA_THANH_TOAN,
                    donHang.layPhuongThucThanhToan() == DonHang.PhuongThucThanhToan.CHUA_CHON
                            ? DonHang.PhuongThucThanhToan.TAI_QUAY
                            : donHang.layPhuongThucThanhToan()
            );
        }
        if (trangThaiYeuCau == YeuCauPhucVu.TrangThai.DANG_CHO || trangThaiYeuCau == YeuCauPhucVu.TrangThai.DANG_XU_LY) {
            return capNhatThanhToanDonHangTrongGiaoDich(
                    db,
                    orderId,
                    DonHang.TrangThaiThanhToan.DA_GOI_THANH_TOAN,
                    donHang.layPhuongThucThanhToan() == DonHang.PhuongThucThanhToan.CHUA_CHON
                            ? DonHang.PhuongThucThanhToan.TAI_QUAY
                            : donHang.layPhuongThucThanhToan()
            );
        }
        return capNhatThanhToanDonHangTrongGiaoDich(
                db,
                orderId,
                DonHang.TrangThaiThanhToan.DA_THANH_TOAN_MO_PHONG,
                donHang.layPhuongThucThanhToan() == DonHang.PhuongThucThanhToan.CHUA_CHON
                        ? DonHang.PhuongThucThanhToan.TAI_QUAY
                        : donHang.layPhuongThucThanhToan()
        );
    }

    public ThongKeTongQuanNhanVien layThongKeTongQuanNhanVien() {
        return new ThongKeTongQuanNhanVien(
                demDonHangTheoTrangThai(DonHang.TrangThai.CHO_XAC_NHAN),
                demDatBanTheoTrangThai(DatBan.TrangThai.PENDING),
                demYeuCauTheoTrangThai(YeuCauPhucVu.TrangThai.DANG_CHO)
                        + demYeuCauTheoTrangThai(YeuCauPhucVu.TrangThai.DANG_XU_LY)
        );
    }

    public long insertServiceRequest(long userId,
                                     String noiDung,
                                     String thoiGianGui,
                                     YeuCauPhucVu.TrangThai trangThai) {
        return themYeuCauPhucVu(
                userId,
                YeuCauPhucVu.LoaiYeuCau.GOI_NHAN_VIEN,
                noiDung,
                null,
                0,
                thoiGianGui,
                trangThai
        );
    }

    public long themYeuCauPhucVu(long userId,
                                 String noiDung,
                                 String thoiGianGui,
                                 YeuCauPhucVu.TrangThai trangThai) {
        return insertServiceRequest(userId, noiDung, thoiGianGui, trangThai);
    }

    public long themYeuCauPhucVu(long userId,
                                 YeuCauPhucVu.LoaiYeuCau loaiYeuCau,
                                 String noiDung,
                                 @Nullable String soBan,
                                 long orderId,
                                 String thoiGianGui,
                                 YeuCauPhucVu.TrangThai trangThai) {
        if (userId <= 0
                || loaiYeuCau == null
                || TextUtils.isEmpty(noiDung)
                || TextUtils.isEmpty(thoiGianGui)
                || trangThai == null) {
            return -1;
        }

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(COL_SERVICE_REQUEST_USER_ID, userId);
            values.put(COL_SERVICE_REQUEST_TYPE, loaiYeuCau.name());
            values.put(COL_SERVICE_REQUEST_CONTENT, noiDung);
            values.put(COL_SERVICE_REQUEST_TABLE_NUMBER, soBan == null ? "" : soBan.trim());
            values.put(COL_SERVICE_REQUEST_ORDER_ID, Math.max(orderId, 0));
            values.put(COL_SERVICE_REQUEST_SENT_TIME, thoiGianGui);
            values.put(COL_SERVICE_REQUEST_STATUS, trangThai.name());
            values.put(COL_SERVICE_REQUEST_HANDLED_TIME, "");
            long idYeuCau = db.insert(TABLE_SERVICE_REQUEST, null, values);
            if (idYeuCau <= 0) {
                return -1;
            }
            if (loaiYeuCau == YeuCauPhucVu.LoaiYeuCau.THANH_TOAN
                    && orderId > 0
                    && !dongBoTrangThaiThanhToanTheoYeuCau(db, orderId, trangThai)) {
                return -1;
            }
            db.setTransactionSuccessful();
            return idYeuCau;
        } finally {
            db.endTransaction();
        }
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
                TEN_ANH_MON_LAU,
                true,
                context.getString(R.string.category_hotpot),
                93
        );
        insertDish(
                db,
                context.getString(R.string.dish_salad_ca_hoi),
                context.getString(R.string.price_129k),
                context.getString(R.string.menu_desc_salad_ca_hoi),
                TEN_ANH_SALAD,
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
        values.put(COL_DISH_IMAGE_RES_NAME, layTenAnhMacDinhTheoMon(tenMon));

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
                VaiTroNguoiDung.KHACH_HANG,
                true
        );
        ensureSeedUser(
                db,
                appContext.getString(R.string.db_test_employee_name),
                EMAIL_TAI_KHOAN_TEST_NHAN_VIEN,
                SDT_TAI_KHOAN_TEST_NHAN_VIEN,
                MAT_KHAU_TAI_KHOAN_TEST,
                VaiTroNguoiDung.NHAN_VIEN,
                true
        );
        ensureSeedUser(
                db,
                appContext.getString(R.string.db_test_admin_name),
                EMAIL_TAI_KHOAN_TEST_ADMIN,
                SDT_TAI_KHOAN_TEST_ADMIN,
                MAT_KHAU_TAI_KHOAN_TEST,
                VaiTroNguoiDung.ADMIN,
                true
        );
    }

    private void ensureSeedUser(SQLiteDatabase db,
                                String name,
                                String email,
                                String phone,
                                String password,
                                VaiTroNguoiDung role,
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

    private void damBaoDuLieuTongQuanMau(SQLiteDatabase db) {
        long idKhachHang = layIdNguoiDungTheoEmail(db, EMAIL_TAI_KHOAN_TEST_KHACH_HANG);
        if (idKhachHang <= 0) {
            return;
        }

        damBaoMonAnMauBoSung(db);
        damBaoDatBanMau(db, idKhachHang);
        damBaoDonHangMau(db, idKhachHang);
        damBaoYeuCauPhucVuMau(db, idKhachHang);
        damBaoTaiKhoanMauBoSung(db);
    }

    private void damBaoMonAnMauBoSung(SQLiteDatabase db) {
        taoMonAnNeuChuaCo(
                db,
                "Cơm chiên hải sản",
                "98.000 đ",
                "Cơm chiên tơi hạt cùng tôm, mực và rau củ.",
                TEN_ANH_MAC_DINH,
                true,
                appContext.getString(R.string.category_main_course),
                88
        );
        taoMonAnNeuChuaCo(
                db,
                "Mì Ý bò bằm",
                "115.000 đ",
                "Mì Ý sốt bò bằm đậm vị, dùng kèm phô mai bào.",
                TEN_ANH_MON_LAU,
                true,
                appContext.getString(R.string.category_main_course),
                84
        );
        taoMonAnNeuChuaCo(
                db,
                "Gỏi tôm xoài xanh",
                "92.000 đ",
                "Tôm tươi, xoài xanh bào sợi, rau thơm và nước mắm chua ngọt.",
                TEN_ANH_SALAD,
                true,
                appContext.getString(R.string.category_salad),
                80
        );
        taoMonAnNeuChuaCo(
                db,
                "Nước cam ép",
                "39.000 đ",
                "Nước cam tươi nguyên chất, không dùng syrup.",
                TEN_ANH_DO_UONG,
                true,
                appContext.getString(R.string.category_drink),
                76
        );
    }

    private void damBaoTaiKhoanMauBoSung(SQLiteDatabase db) {
        ensureSeedUser(
                db,
                "Nguyễn Minh Anh",
                "kh_demo_2",
                "0123456792",
                MAT_KHAU_TAI_KHOAN_TEST,
                VaiTroNguoiDung.KHACH_HANG,
                true
        );
        ensureSeedUser(
                db,
                "Lê Thu Hà",
                "kh_demo_3",
                "0123456793",
                MAT_KHAU_TAI_KHOAN_TEST,
                VaiTroNguoiDung.KHACH_HANG,
                true
        );
        ensureSeedUser(
                db,
                "Phạm Hoàng Long",
                "nv_demo_2",
                "0123456794",
                MAT_KHAU_TAI_KHOAN_TEST,
                VaiTroNguoiDung.NHAN_VIEN,
                true
        );
        ensureSeedUser(
                db,
                "Admin dự phòng",
                "admin_demo_2",
                "0123456795",
                MAT_KHAU_TAI_KHOAN_TEST,
                VaiTroNguoiDung.ADMIN,
                true
        );
    }

    private void damBaoDatBanMau(SQLiteDatabase db, long userId) {
        taoDatBanNeuChuaCo(
                db,
                userId,
                "#GB10001",
                "20/04/2026 18:30",
                "Bàn 05",
                4,
                "Sinh nhật gia đình, ưu tiên khu yên tĩnh.",
                DatBan.TrangThai.PENDING,
                0
        );
        taoDatBanNeuChuaCo(
                db,
                userId,
                "#GB10002",
                "19/04/2026 19:00",
                "Bàn 02",
                2,
                "Khách đã tới quán.",
                DatBan.TrangThai.ACTIVE,
                0
        );
        taoDatBanNeuChuaCo(
                db,
                userId,
                "#GB10003",
                "18/04/2026 18:00",
                "Bàn 08",
                6,
                "Đã dùng bữa xong.",
                DatBan.TrangThai.COMPLETED,
                0
        );
        taoDatBanNeuChuaCo(
                db,
                userId,
                "#GB10004",
                "17/04/2026 20:00",
                "Bàn 10",
                3,
                "Khách báo bận nên hủy.",
                DatBan.TrangThai.CANCELLED,
                0
        );
    }

    private void damBaoDonHangMau(SQLiteDatabase db, long userId) {
        List<DishRecord> danhSachMon = layTatCaMonAn(db);
        DishRecord boLucLac = timMonTheoTen(danhSachMon, appContext.getString(R.string.dish_bo_luc_lac));
        DishRecord lauThai = timMonTheoTen(danhSachMon, appContext.getString(R.string.dish_lau_thai));
        DishRecord salad = timMonTheoTen(danhSachMon, appContext.getString(R.string.dish_salad_ca_hoi));
        DishRecord traDao = timMonTheoTen(danhSachMon, appContext.getString(R.string.dish_tra_dao));
        if (boLucLac == null || lauThai == null || salad == null || traDao == null) {
            return;
        }

        long idDatBanDangHoatDong = layIdDatBanTheoMa(db, "#GB10002");
        taoDonHangNeuChuaCo(
                db,
                userId,
                "#DH10001",
                "19/04/2026 18:45",
                DonHang.TrangThai.CHO_XAC_NHAN,
                DonHang.HinhThucDon.MANG_DI,
                "",
                "Ít đá, giao ngay khi xong.",
                DonHang.TrangThaiThanhToan.CHUA_THANH_TOAN,
                DonHang.PhuongThucThanhToan.TIEN_MAT_KHI_NHAN,
                0,
                taoDanhSachMonMau(
                        taoMonTrongDon(boLucLac, 1),
                        taoMonTrongDon(traDao, 2)
                )
        );
        taoDonHangNeuChuaCo(
                db,
                userId,
                "#DH10002",
                "19/04/2026 19:05",
                DonHang.TrangThai.DANG_CHUAN_BI,
                DonHang.HinhThucDon.AN_TAI_QUAN,
                "Bàn 02",
                "Đang phục vụ món chính.",
                DonHang.TrangThaiThanhToan.CHUA_THANH_TOAN,
                DonHang.PhuongThucThanhToan.TAI_QUAY,
                idDatBanDangHoatDong,
                taoDanhSachMonMau(
                        taoMonTrongDon(lauThai, 1),
                        taoMonTrongDon(traDao, 2)
                )
        );
        taoDonHangNeuChuaCo(
                db,
                userId,
                "#DH10003",
                "18/04/2026 18:20",
                DonHang.TrangThai.SAN_SANG_PHUC_VU,
                DonHang.HinhThucDon.AN_TAI_QUAN,
                "Bàn 08",
                "Báo khách món đã lên đủ.",
                DonHang.TrangThaiThanhToan.DA_GOI_THANH_TOAN,
                DonHang.PhuongThucThanhToan.TAI_QUAY,
                0,
                taoDanhSachMonMau(
                        taoMonTrongDon(boLucLac, 2),
                        taoMonTrongDon(salad, 1),
                        taoMonTrongDon(traDao, 3)
                )
        );
        taoDonHangNeuChuaCo(
                db,
                userId,
                "#DH10004",
                "17/04/2026 12:10",
                DonHang.TrangThai.HOAN_THANH,
                DonHang.HinhThucDon.MANG_DI,
                "",
                "Khách đã nhận món.",
                DonHang.TrangThaiThanhToan.DA_THANH_TOAN_MO_PHONG,
                DonHang.PhuongThucThanhToan.THANH_TOAN_NGAY_MO_PHONG,
                0,
                taoDanhSachMonMau(
                        taoMonTrongDon(boLucLac, 1),
                        taoMonTrongDon(salad, 1)
                )
        );
        taoDonHangNeuChuaCo(
                db,
                userId,
                "#DH10005",
                "16/04/2026 20:15",
                DonHang.TrangThai.DA_HUY,
                DonHang.HinhThucDon.MANG_DI,
                "",
                "Khách đổi ý sau khi đặt.",
                DonHang.TrangThaiThanhToan.CHUA_THANH_TOAN,
                DonHang.PhuongThucThanhToan.CHUA_CHON,
                0,
                taoDanhSachMonMau(
                        taoMonTrongDon(lauThai, 1),
                        taoMonTrongDon(traDao, 1)
                )
        );
    }

    private void damBaoYeuCauPhucVuMau(SQLiteDatabase db, long userId) {
        long idDonDangChuanBi = layIdDonHangTheoMa(db, "#DH10002");
        long idDonSanSang = layIdDonHangTheoMa(db, "#DH10003");
        taoYeuCauNeuChuaCo(
                db,
                userId,
                YeuCauPhucVu.LoaiYeuCau.GOI_NHAN_VIEN,
                "Khách cần thêm chén và đĩa.",
                "Bàn 02",
                idDonDangChuanBi,
                "19/04/2026 19:08",
                YeuCauPhucVu.TrangThai.DANG_CHO,
                ""
        );
        taoYeuCauNeuChuaCo(
                db,
                userId,
                YeuCauPhucVu.LoaiYeuCau.THEM_NUOC,
                "Xin thêm nước lọc cho bàn 02.",
                "Bàn 02",
                idDonDangChuanBi,
                "19/04/2026 19:10",
                YeuCauPhucVu.TrangThai.DANG_XU_LY,
                "19/04/2026 19:12"
        );
        taoYeuCauNeuChuaCo(
                db,
                userId,
                YeuCauPhucVu.LoaiYeuCau.THANH_TOAN,
                "Yêu cầu thanh toán cho bàn 08.",
                "Bàn 08",
                idDonSanSang,
                "18/04/2026 19:15",
                YeuCauPhucVu.TrangThai.DA_XU_LY,
                "18/04/2026 19:20"
        );
        taoYeuCauNeuChuaCo(
                db,
                userId,
                YeuCauPhucVu.LoaiYeuCau.GOI_NHAN_VIEN,
                "Khách đã tự xử lý nên không cần hỗ trợ nữa.",
                "Bàn 08",
                idDonSanSang,
                "18/04/2026 18:40",
                YeuCauPhucVu.TrangThai.DA_HUY,
                "18/04/2026 18:45"
        );
    }

    private long layIdNguoiDungTheoEmail(SQLiteDatabase db, String email) {
        Cursor cursor = null;
        try {
            cursor = db.query(
                    TABLE_USER,
                    new String[]{COL_USER_ID},
                    COL_USER_EMAIL + " = ?",
                    new String[]{email},
                    null,
                    null,
                    null,
                    "1"
            );
            if (!cursor.moveToFirst()) {
                return -1;
            }
            return cursor.getLong(cursor.getColumnIndexOrThrow(COL_USER_ID));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private long layIdDatBanTheoMa(SQLiteDatabase db, String maDatBan) {
        Cursor cursor = null;
        try {
            cursor = db.query(
                    TABLE_RESERVATION,
                    new String[]{COL_RESERVATION_ID},
                    COL_RESERVATION_CODE + " = ?",
                    new String[]{maDatBan},
                    null,
                    null,
                    null,
                    "1"
            );
            if (!cursor.moveToFirst()) {
                return 0;
            }
            return cursor.getLong(cursor.getColumnIndexOrThrow(COL_RESERVATION_ID));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private long layIdDonHangTheoMa(SQLiteDatabase db, String maDonHang) {
        Cursor cursor = null;
        try {
            cursor = db.query(
                    TABLE_ORDER,
                    new String[]{COL_ORDER_ID},
                    COL_ORDER_CODE + " = ?",
                    new String[]{maDonHang},
                    null,
                    null,
                    null,
                    "1"
            );
            if (!cursor.moveToFirst()) {
                return 0;
            }
            return cursor.getLong(cursor.getColumnIndexOrThrow(COL_ORDER_ID));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void taoDatBanNeuChuaCo(SQLiteDatabase db,
                                    long userId,
                                    String maDatBan,
                                    String thoiGian,
                                    String soBan,
                                    int soKhach,
                                    String ghiChu,
                                    DatBan.TrangThai trangThai,
                                    long idDonHangLienKet) {
        if (layIdDatBanTheoMa(db, maDatBan) > 0) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(COL_RESERVATION_USER_ID, userId);
        values.put(COL_RESERVATION_CODE, maDatBan);
        values.put(COL_RESERVATION_TIME, thoiGian);
        values.put(COL_RESERVATION_TABLE_NUMBER, soBan);
        values.put(COL_RESERVATION_GUEST_COUNT, soKhach);
        values.put(COL_RESERVATION_NOTE, ghiChu == null ? "" : ghiChu.trim());
        values.put(COL_RESERVATION_STATUS, trangThai.name());
        values.put(COL_RESERVATION_LINKED_ORDER_ID, Math.max(idDonHangLienKet, 0));
        db.insert(TABLE_RESERVATION, null, values);
    }

    private void taoDonHangNeuChuaCo(SQLiteDatabase db,
                                     long userId,
                                     String maDonHang,
                                     String thoiGian,
                                     DonHang.TrangThai trangThai,
                                     DonHang.HinhThucDon hinhThucDon,
                                     String soBan,
                                     String ghiChu,
                                     DonHang.TrangThaiThanhToan trangThaiThanhToan,
                                     DonHang.PhuongThucThanhToan phuongThucThanhToan,
                                     long idDatBanLienKet,
                                     List<DonHang.MonTrongDon> danhSachMon) {
        if (layIdDonHangTheoMa(db, maDonHang) > 0 || danhSachMon == null || danhSachMon.isEmpty()) {
            return;
        }

        long tongTien = 0;
        for (DonHang.MonTrongDon monTrongDon : danhSachMon) {
            if (monTrongDon == null || monTrongDon.layMonAn() == null) {
                continue;
            }
            tongTien += tachGiaTienTuChuoi(monTrongDon.layMonAn().layGiaBan()) * monTrongDon.laySoLuong();
        }
        if (tongTien <= 0) {
            return;
        }

        ContentValues valuesDon = new ContentValues();
        valuesDon.put(COL_ORDER_USER_ID, userId);
        valuesDon.put(COL_ORDER_CODE, maDonHang);
        valuesDon.put(COL_ORDER_TIME, thoiGian);
        valuesDon.put(COL_ORDER_TOTAL_PRICE, dinhDangTienViet(tongTien));
        valuesDon.put(COL_ORDER_STATUS, trangThai.name());
        valuesDon.put(COL_ORDER_TYPE, hinhThucDon.name());
        valuesDon.put(COL_ORDER_TABLE_NUMBER, soBan == null ? "" : soBan.trim());
        valuesDon.put(COL_ORDER_NOTE, ghiChu == null ? "" : ghiChu.trim());
        valuesDon.put(COL_ORDER_PAYMENT_STATUS, trangThaiThanhToan.name());
        valuesDon.put(COL_ORDER_PAYMENT_METHOD, phuongThucThanhToan.name());
        valuesDon.put(COL_ORDER_RESERVATION_ID, Math.max(idDatBanLienKet, 0));
        long idDonHang = db.insert(TABLE_ORDER, null, valuesDon);
        if (idDonHang <= 0) {
            return;
        }

        for (DonHang.MonTrongDon monTrongDon : danhSachMon) {
            if (monTrongDon == null || monTrongDon.layMonAn() == null) {
                continue;
            }
            MonAnDeXuat monAn = monTrongDon.layMonAn();
            ContentValues valuesChiTiet = new ContentValues();
            valuesChiTiet.put(COL_ORDER_ITEM_ORDER_ID, idDonHang);
            valuesChiTiet.put(COL_ORDER_ITEM_DISH_NAME, monAn.layTenMon());
            valuesChiTiet.put(COL_ORDER_ITEM_DISH_PRICE, monAn.layGiaBan());
            valuesChiTiet.put(COL_ORDER_ITEM_IMAGE_RES_NAME, resolveImageResName(monAn.layIdAnhTaiNguyen()));
            valuesChiTiet.put(COL_ORDER_ITEM_IS_AVAILABLE, monAn.laConPhucVu() ? 1 : 0);
            valuesChiTiet.put(COL_ORDER_ITEM_QUANTITY, monTrongDon.laySoLuong());
            db.insert(TABLE_ORDER_ITEM, null, valuesChiTiet);
        }

        if (idDatBanLienKet > 0) {
            ContentValues valuesDatBan = new ContentValues();
            valuesDatBan.put(COL_RESERVATION_LINKED_ORDER_ID, idDonHang);
            db.update(
                    TABLE_RESERVATION,
                    valuesDatBan,
                    COL_RESERVATION_ID + " = ?",
                    new String[]{String.valueOf(idDatBanLienKet)}
            );
        }
    }

    private void taoYeuCauNeuChuaCo(SQLiteDatabase db,
                                    long userId,
                                    YeuCauPhucVu.LoaiYeuCau loaiYeuCau,
                                    String noiDung,
                                    String soBan,
                                    long idDonHang,
                                    String thoiGianGui,
                                    YeuCauPhucVu.TrangThai trangThai,
                                    String thoiGianXuLy) {
        if (daCoYeuCauMau(db, userId, loaiYeuCau, noiDung, thoiGianGui)) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(COL_SERVICE_REQUEST_USER_ID, userId);
        values.put(COL_SERVICE_REQUEST_TYPE, loaiYeuCau.name());
        values.put(COL_SERVICE_REQUEST_CONTENT, noiDung);
        values.put(COL_SERVICE_REQUEST_TABLE_NUMBER, soBan == null ? "" : soBan.trim());
        values.put(COL_SERVICE_REQUEST_ORDER_ID, Math.max(idDonHang, 0));
        values.put(COL_SERVICE_REQUEST_SENT_TIME, thoiGianGui);
        values.put(COL_SERVICE_REQUEST_STATUS, trangThai.name());
        values.put(COL_SERVICE_REQUEST_HANDLED_TIME, thoiGianXuLy == null ? "" : thoiGianXuLy.trim());
        db.insert(TABLE_SERVICE_REQUEST, null, values);
    }

    private boolean daCoYeuCauMau(SQLiteDatabase db,
                                  long userId,
                                  YeuCauPhucVu.LoaiYeuCau loaiYeuCau,
                                  String noiDung,
                                  String thoiGianGui) {
        Cursor cursor = null;
        try {
            cursor = db.query(
                    TABLE_SERVICE_REQUEST,
                    new String[]{COL_SERVICE_REQUEST_ID},
                    COL_SERVICE_REQUEST_USER_ID + " = ? AND "
                            + COL_SERVICE_REQUEST_TYPE + " = ? AND "
                            + COL_SERVICE_REQUEST_CONTENT + " = ? AND "
                            + COL_SERVICE_REQUEST_SENT_TIME + " = ?",
                    new String[]{
                            String.valueOf(userId),
                            loaiYeuCau.name(),
                            noiDung,
                            thoiGianGui
                    },
                    null,
                    null,
                    null,
                    "1"
            );
            return cursor.moveToFirst();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Nullable
    private DishRecord timMonTheoTen(List<DishRecord> danhSachMon, String tenMon) {
        if (danhSachMon == null || TextUtils.isEmpty(tenMon)) {
            return null;
        }
        for (DishRecord banGhi : danhSachMon) {
            if (banGhi != null && banGhi.layMonAn() != null
                    && TextUtils.equals(tenMon, banGhi.layMonAn().layTenMon())) {
                return banGhi;
            }
        }
        return null;
    }

    @Nullable
    private DonHang.MonTrongDon taoMonTrongDon(@Nullable DishRecord banGhiMon, int soLuong) {
        if (banGhiMon == null || banGhiMon.layMonAn() == null || soLuong <= 0) {
            return null;
        }
        return new DonHang.MonTrongDon(banGhiMon.layMonAn(), soLuong);
    }

    private List<DonHang.MonTrongDon> taoDanhSachMonMau(@Nullable DonHang.MonTrongDon... danhSachMon) {
        List<DonHang.MonTrongDon> ketQua = new ArrayList<>();
        if (danhSachMon == null) {
            return ketQua;
        }
        for (DonHang.MonTrongDon monTrongDon : danhSachMon) {
            if (monTrongDon != null) {
                ketQua.add(monTrongDon);
            }
        }
        return ketQua;
    }

    private void taoMonAnNeuChuaCo(SQLiteDatabase db,
                                   String tenMon,
                                   String giaBan,
                                   String moTa,
                                   String tenAnh,
                                   boolean conPhucVu,
                                   String danhMuc,
                                   int diemDeXuat) {
        if (TextUtils.isEmpty(tenMon)) {
            return;
        }
        Cursor cursor = null;
        try {
            cursor = db.query(
                    TABLE_DISH,
                    new String[]{COL_DISH_ID},
                    COL_DISH_NAME + " = ?",
                    new String[]{tenMon},
                    null,
                    null,
                    null,
                    "1"
            );
            if (cursor.moveToFirst()) {
                return;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        insertDish(db, tenMon, giaBan, moTa, tenAnh, conPhucVu, danhMuc, diemDeXuat);
    }

    private long tachGiaTienTuChuoi(@Nullable String chuoiGia) {
        if (chuoiGia == null || chuoiGia.isEmpty()) {
            return 0;
        }
        String chuoiDaLamSach = chuoiGia.replaceAll("[^0-9]", "");
        try {
            return Long.parseLong(chuoiDaLamSach);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private String dinhDangTienViet(long soTien) {
        return String.format(Locale.forLanguageTag("vi-VN"), "%,d đ", soTien).replace(',', '.');
    }

    private boolean isHashedPassword(@Nullable String stored) {
        return stored != null && stored.startsWith(PASSWORD_PREFIX_SHA256);
    }

    private String hashPassword(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest((raw == null ? "" : raw).getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder(PASSWORD_PREFIX_SHA256);
            for (byte b : bytes) {
                builder.append(String.format(Locale.US, "%02x", b));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 không khả dụng", ex);
        }
    }

    private boolean verifyPassword(String input, @Nullable String stored) {
        if (stored == null) {
            return false;
        }
        if (isHashedPassword(stored)) {
            return TextUtils.equals(hashPassword(input), stored);
        }
        return TextUtils.equals(input, stored);
    }

    private void migrateLegacyPasswordHash(long userId, String rawPassword) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_PASSWORD, hashPassword(rawPassword));
        db.update(TABLE_USER, values, COL_USER_ID + " = ?", new String[]{String.valueOf(userId)});
    }

    @Nullable
    private NguoiDung mapUser(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_USER_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_NAME));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_EMAIL));
        String phone = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PHONE));
        String roleValue = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_ROLE));
        boolean isActive = cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_IS_ACTIVE)) == 1;
        return new NguoiDung(id, name, email, phone, VaiTroNguoiDung.tuChuoi(roleValue), isActive);
    }

    private List<DonHang> queryDonHangs(@Nullable String selection, @Nullable String[] selectionArgs) {
        List<DonHang> orders = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(
                    TABLE_ORDER,
                    new String[]{
                            COL_ORDER_ID,
                            COL_ORDER_CODE,
                            COL_ORDER_TIME,
                            COL_ORDER_TOTAL_PRICE,
                            COL_ORDER_STATUS,
                            COL_ORDER_TYPE,
                            COL_ORDER_TABLE_NUMBER,
                            COL_ORDER_NOTE,
                            COL_ORDER_PAYMENT_STATUS,
                            COL_ORDER_PAYMENT_METHOD,
                            COL_ORDER_RESERVATION_ID
                    },
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
                String orderTypeRaw = cursor.getString(cursor.getColumnIndexOrThrow(COL_ORDER_TYPE));
                String tableNumber = cursor.getString(cursor.getColumnIndexOrThrow(COL_ORDER_TABLE_NUMBER));
                String note = cursor.getString(cursor.getColumnIndexOrThrow(COL_ORDER_NOTE));
                String paymentStatusRaw = cursor.getString(cursor.getColumnIndexOrThrow(COL_ORDER_PAYMENT_STATUS));
                String paymentMethodRaw = cursor.getString(cursor.getColumnIndexOrThrow(COL_ORDER_PAYMENT_METHOD));
                long reservationId = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ORDER_RESERVATION_ID));
                List<DonHang.MonTrongDon> dishes = getDonHangItemsByDonHangId(orderId);
                orders.add(new DonHang(
                        orderId,
                        code,
                        time,
                        totalPrice,
                        parseOrderType(orderTypeRaw),
                        tableNumber,
                        note,
                        parseDonHangStatus(statusRaw),
                        parsePaymentStatus(paymentStatusRaw),
                        parsePaymentMethod(paymentMethodRaw),
                        reservationId,
                        dishes
                ));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        orders.sort((first, second) -> Long.compare(
                parseDonHangTimeToMillis(second.layThoiGian()),
                parseDonHangTimeToMillis(first.layThoiGian())
        ));
        return orders;
    }

    private List<DatBan> queryReservations(@Nullable String selection, @Nullable String[] selectionArgs) {
        List<DatBan> reservations = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(
                    TABLE_RESERVATION,
                    new String[]{
                            COL_RESERVATION_ID,
                            COL_RESERVATION_CODE,
                            COL_RESERVATION_TIME,
                            COL_RESERVATION_TABLE_NUMBER,
                            COL_RESERVATION_GUEST_COUNT,
                            COL_RESERVATION_NOTE,
                            COL_RESERVATION_STATUS,
                            COL_RESERVATION_LINKED_ORDER_ID
                    },
                    selection,
                    selectionArgs,
                    null,
                    null,
                    COL_RESERVATION_ID + " DESC"
            );

            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_RESERVATION_ID));
                String reservationCode = cursor.getString(cursor.getColumnIndexOrThrow(COL_RESERVATION_CODE));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(COL_RESERVATION_TIME));
                String tableNumber = cursor.getString(cursor.getColumnIndexOrThrow(COL_RESERVATION_TABLE_NUMBER));
                int guestCount = cursor.getInt(cursor.getColumnIndexOrThrow(COL_RESERVATION_GUEST_COUNT));
                String note = cursor.getString(cursor.getColumnIndexOrThrow(COL_RESERVATION_NOTE));
                String statusRaw = cursor.getString(cursor.getColumnIndexOrThrow(COL_RESERVATION_STATUS));
                long linkedOrderId = cursor.getLong(cursor.getColumnIndexOrThrow(COL_RESERVATION_LINKED_ORDER_ID));
                DatBan.TrangThai trangThaiGoc = parseReservationStatus(statusRaw);
                DatBan.TrangThai trangThaiHieuLuc = xacDinhTrangThaiDatBanHieuLuc(trangThaiGoc, time, linkedOrderId);
                dongBoTrangThaiDatBanNeuCan(id, trangThaiGoc, trangThaiHieuLuc, linkedOrderId);
                reservations.add(new DatBan(
                        id,
                        reservationCode,
                        time,
                        tableNumber,
                        guestCount,
                        note,
                        trangThaiHieuLuc,
                        linkedOrderId
                ));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return reservations;
    }

    private List<YeuCauPhucVu> queryServiceRequests(@Nullable String selection, @Nullable String[] selectionArgs) {
        List<YeuCauPhucVu> requests = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(
                    TABLE_SERVICE_REQUEST,
                    new String[]{
                            COL_SERVICE_REQUEST_ID,
                            COL_SERVICE_REQUEST_TYPE,
                            COL_SERVICE_REQUEST_CONTENT,
                            COL_SERVICE_REQUEST_SENT_TIME,
                            COL_SERVICE_REQUEST_STATUS,
                            COL_SERVICE_REQUEST_TABLE_NUMBER,
                            COL_SERVICE_REQUEST_ORDER_ID,
                            COL_SERVICE_REQUEST_HANDLED_TIME
                    },
                    selection,
                    selectionArgs,
                    null,
                    null,
                    COL_SERVICE_REQUEST_ID + " DESC"
            );

            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_SERVICE_REQUEST_ID));
                String typeRaw = cursor.getString(cursor.getColumnIndexOrThrow(COL_SERVICE_REQUEST_TYPE));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(COL_SERVICE_REQUEST_CONTENT));
                String sentTime = cursor.getString(cursor.getColumnIndexOrThrow(COL_SERVICE_REQUEST_SENT_TIME));
                String statusRaw = cursor.getString(cursor.getColumnIndexOrThrow(COL_SERVICE_REQUEST_STATUS));
                String tableNumber = cursor.getString(cursor.getColumnIndexOrThrow(COL_SERVICE_REQUEST_TABLE_NUMBER));
                long orderId = cursor.getLong(cursor.getColumnIndexOrThrow(COL_SERVICE_REQUEST_ORDER_ID));
                String handledTime = cursor.getString(cursor.getColumnIndexOrThrow(COL_SERVICE_REQUEST_HANDLED_TIME));
                requests.add(new YeuCauPhucVu(
                        id,
                        parseServiceRequestType(typeRaw),
                        content,
                        sentTime,
                        handledTime,
                        tableNumber,
                        orderId,
                        parseServiceRequestStatus(statusRaw)
                ));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        requests.sort((first, second) -> Long.compare(
                parseDonHangTimeToMillis(second.layThoiGianGui()),
                parseDonHangTimeToMillis(first.layThoiGianGui())
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

    @Nullable
    private <T> T getEnumStatusById(String table,
                                    String idColumn,
                                    String statusColumn,
                                    long id,
                                    EnumParser<T> parser) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(
                    table,
                    new String[]{statusColumn},
                    idColumn + " = ?",
                    new String[]{String.valueOf(id)},
                    null,
                    null,
                    null,
                    "1"
            );
            if (!cursor.moveToFirst()) {
                return null;
            }

            String rawStatus = cursor.getString(cursor.getColumnIndexOrThrow(statusColumn));
            return parser.parse(rawStatus);
        } catch (IllegalArgumentException ex) {
            Log.w(TAG, "getEnumStatusById: trạng thái không hợp lệ cho bảng " + table + ", id=" + id, ex);
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private List<DonHang.MonTrongDon> getDonHangItemsByDonHangId(long orderId) {
        List<DonHang.MonTrongDon> dishes = new ArrayList<>();
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

                MonAnDeXuat dishItem = new MonAnDeXuat(
                        resolveImageResId(imageResName),
                        dishName,
                        dishPrice,
                        isAvailable,
                        "",
                        0
                );
                dishes.add(new DonHang.MonTrongDon(dishItem, quantity));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return dishes;
    }

    private interface EnumParser<T> {
        T parse(String rawValue) throws IllegalArgumentException;
    }

    private String layTenAnhMacDinhTheoMon(@Nullable String tenMon) {
        if (TextUtils.equals(tenMon, appContext.getString(R.string.dish_lau_thai))) {
            return TEN_ANH_MON_LAU;
        }
        if (TextUtils.equals(tenMon, appContext.getString(R.string.dish_salad_ca_hoi))) {
            return TEN_ANH_SALAD;
        }
        if (TextUtils.equals(tenMon, appContext.getString(R.string.dish_tra_dao))) {
            return TEN_ANH_DO_UONG;
        }
        return TEN_ANH_MAC_DINH;
    }

    private int resolveImageResId(String imageResName) {
        if (TextUtils.isEmpty(imageResName)) {
            return R.drawable.menu_1;
        }

        int resId = appContext.getResources().getIdentifier(
                imageResName,
                "drawable",
                appContext.getPackageName()
        );
        return resId == 0 ? R.drawable.menu_1 : resId;
    }

    private String taoMaDatBan() {
        return "#GB" + (System.currentTimeMillis() % 100000);
    }

    private String layThoiGianHienTai() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
    }

    private boolean laThoiGianDatBanHopLe(@Nullable String time) {
        long reservationTime = parseDonHangTimeToMillis(time);
        if (reservationTime <= 0L) {
            return false;
        }
        long toiThieu = System.currentTimeMillis() + DAT_BAN_TOI_THIEU_TRUOC_PHUT * 60_000L;
        return reservationTime >= toiThieu;
    }

    private boolean banCoTheDatTrongKhungGio(@Nullable String time, @Nullable String tableNumber) {
        return banCoTheDatTrongKhungGio(time, tableNumber, 0);
    }

    private boolean banCoTheDatTrongKhungGio(@Nullable String time,
                                             @Nullable String tableNumber,
                                             long reservationIdBoQua) {
        if (TextUtils.isEmpty(time) || TextUtils.isEmpty(tableNumber)) {
            return false;
        }
        return !layDanhSachBanDaDat(time, reservationIdBoQua).contains(tableNumber.trim());
    }

    private boolean laDatBanChiemKhungGio(@Nullable DatBan.TrangThai trangThai,
                                          @Nullable String thoiGianDatBan,
                                          long thoiGianMucTieu) {
        if ((trangThai != DatBan.TrangThai.PENDING && trangThai != DatBan.TrangThai.ACTIVE)
                || thoiGianMucTieu <= 0L) {
            return false;
        }
        long thoiGianDat = parseDonHangTimeToMillis(thoiGianDatBan);
        if (thoiGianDat <= 0L) {
            return false;
        }
        return Math.abs(thoiGianDat - thoiGianMucTieu) <= CUA_SO_KICH_HOAT_DAT_BAN_PHUT * 60_000L;
    }

    private boolean coDatBanHieuLucTheoNguoiDung(long userId) {
        return layDatBanHieuLucTheoNguoiDung(userId) != null;
    }

    private long timReservationIdPhuHopChoDonTaiQuan(long userId,
                                                     @Nullable String soBan,
                                                     @Nullable String thoiGianDonHang,
                                                     long reservationIdUuTien) {
        String soBanDaLamSach = soBan == null ? "" : soBan.trim();
        if (userId <= 0 || TextUtils.isEmpty(soBanDaLamSach)) {
            return 0;
        }

        if (reservationIdUuTien > 0) {
            DatBan datBanUuTien = timDatBanHieuLucTheoNguoiDung(userId, soBanDaLamSach, thoiGianDonHang);
            if (datBanUuTien != null && datBanUuTien.layId() == reservationIdUuTien) {
                return datBanUuTien.layId();
            }
        }

        DatBan datBanPhuHop = timDatBanHieuLucTheoNguoiDung(userId, soBanDaLamSach, thoiGianDonHang);
        return datBanPhuHop == null ? 0 : datBanPhuHop.layId();
    }

    @Nullable
    private DatBan layDatBanTheoId(long reservationId) {
        List<DatBan> reservations = queryReservations(COL_RESERVATION_ID + " = ?", new String[]{String.valueOf(reservationId)});
        return reservations.isEmpty() ? null : reservations.get(0);
    }

    @Nullable
    private DatBan timDatBanTheoTrangThai(@Nullable List<DatBan> danhSachDatBan, DatBan.TrangThai trangThai) {
        if (danhSachDatBan == null || trangThai == null) {
            return null;
        }
        for (DatBan datBan : danhSachDatBan) {
            if (datBan != null && datBan.layTrangThai() == trangThai) {
                return datBan;
            }
        }
        return null;
    }

    private DatBan.TrangThai xacDinhTrangThaiDatBanHieuLuc(@Nullable DatBan.TrangThai trangThaiHienTai,
                                                           @Nullable String thoiGianDat,
                                                           long linkedOrderId) {
        if (trangThaiHienTai == null) {
            return DatBan.TrangThai.PENDING;
        }
        if (trangThaiHienTai == DatBan.TrangThai.CANCELLED
                || trangThaiHienTai == DatBan.TrangThai.EXPIRED
                || trangThaiHienTai == DatBan.TrangThai.COMPLETED) {
            return trangThaiHienTai;
        }
        if (linkedOrderId > 0) {
            return DatBan.TrangThai.ACTIVE;
        }

        long reservationTime = parseDonHangTimeToMillis(thoiGianDat);
        long now = System.currentTimeMillis();
        long startActive = reservationTime - CUA_SO_KICH_HOAT_DAT_BAN_PHUT * 60_000L;
        long expireAt = reservationTime + CUA_SO_KICH_HOAT_DAT_BAN_PHUT * 60_000L;

        if (reservationTime <= 0L) {
            return DatBan.TrangThai.EXPIRED;
        }
        if (now > expireAt) {
            return DatBan.TrangThai.EXPIRED;
        }
        if (now >= startActive) {
            return DatBan.TrangThai.ACTIVE;
        }
        return DatBan.TrangThai.PENDING;
    }

    private void dongBoTrangThaiDatBanNeuCan(long reservationId,
                                             @Nullable DatBan.TrangThai trangThaiCu,
                                             @Nullable DatBan.TrangThai trangThaiMoi,
                                             long linkedOrderId) {
        if (reservationId <= 0 || trangThaiCu == null || trangThaiMoi == null || trangThaiCu == trangThaiMoi) {
            return;
        }
        if ((trangThaiMoi == DatBan.TrangThai.COMPLETED && linkedOrderId <= 0)
                || (!coTheChuyenTrangThaiDatBan(trangThaiCu, trangThaiMoi)
                && !(trangThaiCu == DatBan.TrangThai.PENDING && trangThaiMoi == DatBan.TrangThai.ACTIVE && linkedOrderId > 0))) {
            return;
        }
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_RESERVATION_STATUS, trangThaiMoi.name());
        db.update(TABLE_RESERVATION, values, COL_RESERVATION_ID + " = ?", new String[]{String.valueOf(reservationId)});
    }

    private void dongBoTrangThaiDatBanTheoDonHang(long orderId, @Nullable DonHang.TrangThai trangThaiDonHang) {
        if (orderId <= 0 || trangThaiDonHang == null) {
            return;
        }

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(
                    TABLE_ORDER,
                    new String[]{COL_ORDER_RESERVATION_ID},
                    COL_ORDER_ID + " = ?",
                    new String[]{String.valueOf(orderId)},
                    null,
                    null,
                    null,
                    "1"
            );
            if (!cursor.moveToFirst()) {
                return;
            }
            long reservationId = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ORDER_RESERVATION_ID));
            if (reservationId <= 0) {
                return;
            }
            if (trangThaiDonHang == DonHang.TrangThai.HOAN_THANH || trangThaiDonHang == DonHang.TrangThai.DA_HUY) {
                capNhatTrangThaiDatBan(reservationId,
                        trangThaiDonHang == DonHang.TrangThai.HOAN_THANH
                                ? DatBan.TrangThai.COMPLETED
                                : DatBan.TrangThai.CANCELLED);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
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

    private DonHang.TrangThai parseDonHangStatus(String statusRaw) {
        if (TextUtils.isEmpty(statusRaw)) {
            return DonHang.TrangThai.CHO_XAC_NHAN;
        }

        if ("PENDING_CONFIRMATION".equals(statusRaw)) {
            return DonHang.TrangThai.CHO_XAC_NHAN;
        }
        if ("CONFIRMED".equals(statusRaw)) {
            return DonHang.TrangThai.DANG_CHUAN_BI;
        }
        if ("COMPLETED".equals(statusRaw)) {
            return DonHang.TrangThai.HOAN_THANH;
        }
        if ("CANCELED".equals(statusRaw)) {
            return DonHang.TrangThai.DA_HUY;
        }

        try {
            return DonHang.TrangThai.valueOf(statusRaw);
        } catch (IllegalArgumentException ex) {
            return DonHang.TrangThai.CHO_XAC_NHAN;
        }
    }

    private DonHang.HinhThucDon parseOrderType(@Nullable String orderTypeRaw) {
        if (TextUtils.isEmpty(orderTypeRaw)) {
            return DonHang.HinhThucDon.MANG_DI;
        }
        try {
            return DonHang.HinhThucDon.valueOf(orderTypeRaw);
        } catch (IllegalArgumentException ex) {
            return DonHang.HinhThucDon.MANG_DI;
        }
    }

    private DonHang.TrangThaiThanhToan parsePaymentStatus(@Nullable String paymentStatusRaw) {
        if (TextUtils.isEmpty(paymentStatusRaw)) {
            return DonHang.TrangThaiThanhToan.CHUA_THANH_TOAN;
        }
        try {
            return DonHang.TrangThaiThanhToan.valueOf(paymentStatusRaw);
        } catch (IllegalArgumentException ex) {
            return DonHang.TrangThaiThanhToan.CHUA_THANH_TOAN;
        }
    }

    private DonHang.PhuongThucThanhToan parsePaymentMethod(@Nullable String paymentMethodRaw) {
        if (TextUtils.isEmpty(paymentMethodRaw)) {
            return DonHang.PhuongThucThanhToan.CHUA_CHON;
        }
        try {
            return DonHang.PhuongThucThanhToan.valueOf(paymentMethodRaw);
        } catch (IllegalArgumentException ex) {
            return DonHang.PhuongThucThanhToan.CHUA_CHON;
        }
    }

    private long parseDonHangTimeToMillis(@Nullable String timeRaw) {
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

    private DatBan.TrangThai parseReservationStatus(String statusRaw) {
        if (TextUtils.isEmpty(statusRaw)) {
            return DatBan.TrangThai.PENDING;
        }

        if ("PENDING_APPROVAL".equals(statusRaw)) {
            return DatBan.TrangThai.PENDING;
        }
        if ("CONFIRMED".equals(statusRaw)) {
            return DatBan.TrangThai.ACTIVE;
        }
        if ("COMPLETED".equals(statusRaw)) {
            return DatBan.TrangThai.COMPLETED;
        }
        if ("CANCELED".equals(statusRaw)) {
            return DatBan.TrangThai.CANCELLED;
        }
        if ("EXPIRED".equals(statusRaw)) {
            return DatBan.TrangThai.EXPIRED;
        }

        try {
            return DatBan.TrangThai.valueOf(statusRaw);
        } catch (IllegalArgumentException ex) {
            return DatBan.TrangThai.PENDING;
        }
    }

    public boolean coYeuCauThanhToanDangXuLy(long userId, long orderId) {
        if (userId <= 0 || orderId <= 0) {
            return false;
        }

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(
                    TABLE_SERVICE_REQUEST,
                    new String[]{COL_SERVICE_REQUEST_ID},
                    COL_SERVICE_REQUEST_USER_ID + " = ? AND "
                            + COL_SERVICE_REQUEST_ORDER_ID + " = ? AND "
                            + COL_SERVICE_REQUEST_TYPE + " = ? AND "
                            + COL_SERVICE_REQUEST_STATUS + " = ?",
                    new String[]{
                            String.valueOf(userId),
                            String.valueOf(orderId),
                            YeuCauPhucVu.LoaiYeuCau.THANH_TOAN.name(),
                            YeuCauPhucVu.TrangThai.DANG_XU_LY.name()
                    },
                    null,
                    null,
                    COL_SERVICE_REQUEST_ID + " DESC",
                    "1"
            );
            return cursor.moveToFirst();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private YeuCauPhucVu.TrangThai parseServiceRequestStatus(String statusRaw) {
        if (TextUtils.isEmpty(statusRaw)) {
            return YeuCauPhucVu.TrangThai.DANG_CHO;
        }

        if ("PENDING".equals(statusRaw)) {
            return YeuCauPhucVu.TrangThai.DANG_CHO;
        }
        if ("PROCESSING".equals(statusRaw)) {
            return YeuCauPhucVu.TrangThai.DANG_XU_LY;
        }
        if ("DONE".equals(statusRaw)) {
            return YeuCauPhucVu.TrangThai.DA_XU_LY;
        }

        try {
            return YeuCauPhucVu.TrangThai.valueOf(statusRaw);
        } catch (IllegalArgumentException ex) {
            return YeuCauPhucVu.TrangThai.DANG_CHO;
        }
    }

    private YeuCauPhucVu.LoaiYeuCau parseServiceRequestType(@Nullable String typeRaw) {
        if (TextUtils.isEmpty(typeRaw)) {
            return YeuCauPhucVu.LoaiYeuCau.GOI_NHAN_VIEN;
        }
        try {
            return YeuCauPhucVu.LoaiYeuCau.valueOf(typeRaw);
        } catch (IllegalArgumentException ex) {
            return YeuCauPhucVu.LoaiYeuCau.GOI_NHAN_VIEN;
        }
    }

    public static class DishRecord {
        private final long id;
        private final MonAnDeXuat dishItem;
        private final String description;
        private final String imageResName;

        public DishRecord(long id, MonAnDeXuat dishItem, String description, String imageResName) {
            this.id = id;
            this.dishItem = dishItem;
            this.description = description;
            this.imageResName = imageResName;
        }

        public long layId() {
            return id;
        }

        public MonAnDeXuat layMonAn() {
            return dishItem;
        }

        public String layMoTa() {
            return description;
        }

        public String layTenAnhTaiNguyen() {
            return imageResName;
        }
    }
}
