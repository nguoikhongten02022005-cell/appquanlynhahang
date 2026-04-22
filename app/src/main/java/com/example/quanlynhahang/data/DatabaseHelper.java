package com.example.quanlynhahang.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.quanlynhahang.helper.DateTimeUtils;
import com.example.quanlynhahang.model.ThongKeTongQuanQuanTri;
import com.example.quanlynhahang.model.ThongKeTongQuanNhanVien;
import com.example.quanlynhahang.model.BanAn;
import com.example.quanlynhahang.model.DonHang;
import com.example.quanlynhahang.model.MonAnDeXuat;
import com.example.quanlynhahang.model.DatBan;
import com.example.quanlynhahang.model.YeuCauPhucVu;
import com.example.quanlynhahang.model.NguoiDung;
import com.example.quanlynhahang.model.VaiTroNguoiDung;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String DATABASE_NAME = "restaurant.db";
    private static final int DATABASE_VERSION = 9;

    private static final String TEN_ANH_MAC_DINH = "menu_1";
    private static final String BAN_MAC_DINH = "Bàn 01";
    private static final int SO_PHUT_CHAN_GUI_TRUNG_YEU_CAU = 5;
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

    static final String COL_USER_ID = "id";
    static final String COL_USER_NAME = "name";
    static final String COL_USER_EMAIL = "email";
    static final String COL_USER_PHONE = "phone";
    static final String COL_USER_PASSWORD = "password";
    static final String COL_USER_ROLE = "role";
    static final String COL_USER_IS_ACTIVE = "is_active";

    static final String COL_DISH_ID = "id";
    static final String COL_DISH_NAME = "name";
    static final String COL_DISH_PRICE = "price";
    static final String COL_DISH_DESCRIPTION = "description";
    static final String COL_DISH_IMAGE_RES_NAME = "image_res_name";
    static final String COL_DISH_IS_AVAILABLE = "is_available";
    static final String COL_DISH_CATEGORY = "category";
    static final String COL_DISH_RECOMMEND_SCORE = "recommend_score";

    static final String COL_ORDER_ID = "id";
    static final String COL_ORDER_USER_ID = "user_id";
    static final String COL_ORDER_CODE = "code";
    static final String COL_ORDER_TIME = "time";
    static final String COL_ORDER_TOTAL_PRICE = "total_price";
    static final String COL_ORDER_STATUS = "status";
    static final String COL_ORDER_TYPE = "order_type";
    static final String COL_ORDER_TABLE_NUMBER = "table_number";
    static final String COL_ORDER_NOTE = "note";
    static final String COL_ORDER_PAYMENT_STATUS = "payment_status";
    static final String COL_ORDER_PAYMENT_METHOD = "payment_method";
    static final String COL_ORDER_RESERVATION_ID = "reservation_id";

    static final String COL_ORDER_ITEM_ID = "id";
    static final String COL_ORDER_ITEM_ORDER_ID = "order_id";
    static final String COL_ORDER_ITEM_DISH_NAME = "dish_name";
    static final String COL_ORDER_ITEM_DISH_PRICE = "dish_price";
    static final String COL_ORDER_ITEM_IMAGE_RES_NAME = "image_res_name";
    static final String COL_ORDER_ITEM_IS_AVAILABLE = "is_available";
    static final String COL_ORDER_ITEM_QUANTITY = "quantity";

    static final String COL_RESERVATION_ID = "id";
    static final String COL_RESERVATION_USER_ID = "user_id";
    static final String COL_RESERVATION_TIME = "time";
    static final String COL_RESERVATION_TABLE_NUMBER = "table_number";
    static final String COL_RESERVATION_GUEST_COUNT = "guest_count";
    static final String COL_RESERVATION_NOTE = "note";
    static final String COL_RESERVATION_STATUS = "status";
    static final String COL_RESERVATION_CODE = "reservation_code";
    static final String COL_RESERVATION_LINKED_ORDER_ID = "linked_order_id";

    static final String COL_SERVICE_REQUEST_ID = "id";
    static final String COL_SERVICE_REQUEST_USER_ID = "user_id";
    static final String COL_SERVICE_REQUEST_CONTENT = "content";
    static final String COL_SERVICE_REQUEST_SENT_TIME = "sent_time";
    static final String COL_SERVICE_REQUEST_STATUS = "status";
    static final String COL_SERVICE_REQUEST_TYPE = "request_type";
    static final String COL_SERVICE_REQUEST_TABLE_NUMBER = "table_number";
    static final String COL_SERVICE_REQUEST_ORDER_ID = "order_id";
    static final String COL_SERVICE_REQUEST_HANDLED_TIME = "handled_time";

    static final String COL_BAN_AN_ID = "id";
    static final String COL_BAN_AN_MA_BAN = "ma_ban";
    static final String COL_BAN_AN_TEN_BAN = "ten_ban";
    static final String COL_BAN_AN_SO_CHO = "so_cho";
    static final String COL_BAN_AN_KHU_VUC = "khu_vuc";
    static final String COL_BAN_AN_TRANG_THAI = "trang_thai";

    private final Context appContext;
    private final UserRepository userRepository;
    private final DishRepository dishRepository;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        appContext = context.getApplicationContext();
        userRepository = new UserRepository(this);
        dishRepository = new DishRepository(this, appContext);
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
        SeedDataHelper.damBaoDuLieuMacDinh(this, appContext, db);
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

    private String taoBangBanAn() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_BAN_AN + " ("
                + COL_BAN_AN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_BAN_AN_MA_BAN + " TEXT NOT NULL UNIQUE, "
                + COL_BAN_AN_TEN_BAN + " TEXT NOT NULL, "
                + COL_BAN_AN_SO_CHO + " INTEGER NOT NULL DEFAULT 4, "
                + COL_BAN_AN_KHU_VUC + " TEXT NOT NULL DEFAULT '', "
                + COL_BAN_AN_TRANG_THAI + " TEXT NOT NULL DEFAULT '" + BanAn.TrangThai.TRONG.name() + "'"
                + ")";
    }

    public List<BanAn> layTatCaBanAn() {
        List<BanAn> danhSachBan = queryBanAn();
        if (danhSachBan.isEmpty()) {
            SQLiteDatabase db = getWritableDatabase();
            SeedDataHelper.damBaoBanAnMau(db);
            danhSachBan = queryBanAn();
        }
        return danhSachBan;
    }

    public long themBanAn(String maBan,
                          String tenBan,
                          int soCho,
                          @Nullable String khuVuc,
                          BanAn.TrangThai trangThai) {
        if (TextUtils.isEmpty(maBan) || TextUtils.isEmpty(tenBan) || soCho <= 0 || TextUtils.isEmpty(khuVuc)) {
            return -1;
        }
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = taoGiaTriBanAn(maBan, tenBan, soCho, khuVuc, trangThai);
        return db.insert(TABLE_BAN_AN, null, values);
    }

    public boolean capNhatBanAn(long idBan,
                                String maBan,
                                String tenBan,
                                int soCho,
                                @Nullable String khuVuc,
                                BanAn.TrangThai trangThai) {
        if (idBan <= 0 || TextUtils.isEmpty(maBan) || TextUtils.isEmpty(tenBan) || soCho <= 0 || TextUtils.isEmpty(khuVuc)) {
            return false;
        }
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = taoGiaTriBanAn(maBan, tenBan, soCho, khuVuc, trangThai);
        int rows = db.update(TABLE_BAN_AN, values, COL_BAN_AN_ID + " = ?", new String[]{String.valueOf(idBan)});
        return rows > 0;
    }

    public boolean xoaBanAnNeuTrong(long idBan) {
        if (idBan <= 0) {
            return false;
        }
        BanAn banAn = layBanAnTheoId(idBan);
        if (banAn == null || banAn.layTrangThai() != BanAn.TrangThai.TRONG) {
            return false;
        }
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(TABLE_BAN_AN, COL_BAN_AN_ID + " = ?", new String[]{String.valueOf(idBan)});
        return rows > 0;
    }

    @Nullable
    public BanAn layBanAnTheoId(long idBan) {
        if (idBan <= 0) {
            return null;
        }
        List<BanAn> ketQua = queryBanAn(COL_BAN_AN_ID + " = ?", new String[]{String.valueOf(idBan)});
        return ketQua.isEmpty() ? null : ketQua.get(0);
    }

    private ContentValues taoGiaTriBanAn(String maBan,
                                         String tenBan,
                                         int soCho,
                                         @Nullable String khuVuc,
                                         @Nullable BanAn.TrangThai trangThai) {
        ContentValues values = new ContentValues();
        values.put(COL_BAN_AN_MA_BAN, maBan == null ? "" : maBan.trim());
        values.put(COL_BAN_AN_TEN_BAN, tenBan == null ? "" : tenBan.trim());
        values.put(COL_BAN_AN_SO_CHO, Math.max(soCho, 1));
        values.put(COL_BAN_AN_KHU_VUC, khuVuc == null ? "" : khuVuc.trim());
        values.put(COL_BAN_AN_TRANG_THAI, (trangThai == null ? BanAn.TrangThai.TRONG : trangThai).name());
        return values;
    }

    private List<BanAn> queryBanAn() {
        return queryBanAn(null, null);
    }

    private List<BanAn> queryBanAn(@Nullable String selection, @Nullable String[] selectionArgs) {
        List<BanAn> danhSachBan = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().query(
                    TABLE_BAN_AN,
                    new String[]{
                            COL_BAN_AN_ID,
                            COL_BAN_AN_MA_BAN,
                            COL_BAN_AN_TEN_BAN,
                            COL_BAN_AN_SO_CHO,
                            COL_BAN_AN_KHU_VUC,
                            COL_BAN_AN_TRANG_THAI
                    },
                    selection,
                    selectionArgs,
                    null,
                    null,
                    COL_BAN_AN_ID + " ASC"
            );
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_BAN_AN_ID));
                String maBan = cursor.getString(cursor.getColumnIndexOrThrow(COL_BAN_AN_MA_BAN));
                String tenBan = cursor.getString(cursor.getColumnIndexOrThrow(COL_BAN_AN_TEN_BAN));
                int soCho = cursor.getInt(cursor.getColumnIndexOrThrow(COL_BAN_AN_SO_CHO));
                String khuVuc = cursor.getString(cursor.getColumnIndexOrThrow(COL_BAN_AN_KHU_VUC));
                BanAn.TrangThai trangThai = parseBanAnTrangThai(cursor.getString(cursor.getColumnIndexOrThrow(COL_BAN_AN_TRANG_THAI)));
                danhSachBan.add(new BanAn(id, maBan, tenBan, soCho, khuVuc, xacDinhTrangThaiBanAn(tenBan, trangThai)));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return danhSachBan;
    }

    private BanAn.TrangThai xacDinhTrangThaiBanAn(@Nullable String maBan, @Nullable BanAn.TrangThai trangThaiLuu) {
        String ban = maBan == null ? "" : maBan.trim();
        if (!TextUtils.isEmpty(ban)) {
            for (DonHang donHang : queryDonHangs(COL_ORDER_TABLE_NUMBER + " = ?", new String[]{ban})) {
                if (donHang.laAnTaiQuan()
                        && (donHang.layTrangThai() == DonHang.TrangThai.DANG_CHUAN_BI
                        || donHang.layTrangThai() == DonHang.TrangThai.SAN_SANG_PHUC_VU)) {
                    return BanAn.TrangThai.DANG_PHUC_VU;
                }
            }
            for (DatBan datBan : layTatCaDatBan()) {
                if (ban.equalsIgnoreCase(datBan.laySoBan()) && datBan.layTrangThai() == DatBan.TrangThai.PENDING) {
                    return BanAn.TrangThai.DA_DAT;
                }
            }
        }
        return trangThaiLuu == null ? BanAn.TrangThai.TRONG : trangThaiLuu;
    }

    private BanAn.TrangThai parseBanAnTrangThai(@Nullable String raw) {
        if (TextUtils.isEmpty(raw)) {
            return BanAn.TrangThai.TRONG;
        }
        try {
            return BanAn.TrangThai.valueOf(raw);
        } catch (IllegalArgumentException ex) {
            return BanAn.TrangThai.TRONG;
        }
    }


    public long insertUser(String name, String email, String phone, String password) {
        return insertUser(name, email, phone, password, VaiTroNguoiDung.KHACH_HANG, true);
    }

    public long insertUser(String name, String email, String phone, String password, VaiTroNguoiDung role, boolean isActive) {
        return userRepository.insertUser(name, email, phone, password, role, isActive);
    }

    long insertUser(SQLiteDatabase db, String name, String email, String phone, String password, VaiTroNguoiDung role, boolean isActive) {
        return userRepository.insertUser(db, name, email, phone, password, role, isActive);
    }

    @Nullable
    public NguoiDung getUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    @Nullable
    public NguoiDung getUserById(long userId) {
        return userRepository.getUserById(userId);
    }

    @Nullable
    public NguoiDung layNguoiDungTheoId(long userId) {
        return userRepository.getUserById(userId);
    }

    @Nullable
    public NguoiDung getUserByPhone(String phone) {
        return userRepository.getUserByPhone(phone);
    }

    @Nullable
    public NguoiDung layNguoiDungTheoSoDienThoai(String phone) {
        return userRepository.getUserByPhone(phone);
    }

    public boolean isPhoneInUse(String phone, long excludeUserId) {
        return userRepository.isPhoneInUse(phone, excludeUserId);
    }

    public boolean soDienThoaiDaDuocSuDung(String phone, long excludeUserId) {
        return userRepository.isPhoneInUse(phone, excludeUserId);
    }

    @Nullable
    public NguoiDung checkLogin(String usernameOrEmail, String password) {
        return userRepository.checkLogin(usernameOrEmail, password);
    }

    @Nullable
    public NguoiDung kiemTraDangNhap(String usernameOrEmail, String password) {
        return userRepository.checkLogin(usernameOrEmail, password);
    }

    public boolean updateUserProfile(long userId, String name, String phone) {
        return userRepository.updateUserProfile(userId, name, phone);
    }

    public boolean capNhatThongTinNguoiDung(long userId, String name, String phone) {
        return userRepository.updateUserProfile(userId, name, phone);
    }

    public boolean updateUserPassword(long userId, String newPassword) {
        return userRepository.updateUserPassword(userId, newPassword);
    }

    public boolean capNhatMatKhauNguoiDung(long userId, String newPassword) {
        return userRepository.updateUserPassword(userId, newPassword);
    }

    public List<NguoiDung> getAllUsers() {
        return userRepository.getUsersByRole(null);
    }

    public List<NguoiDung> layTatCaNguoiDung() {
        return userRepository.getUsersByRole(null);
    }

    public List<NguoiDung> getUsersByRole(@Nullable VaiTroNguoiDung role) {
        return userRepository.getUsersByRole(role);
    }

    public List<NguoiDung> layNguoiDungTheoVaiTro(@Nullable VaiTroNguoiDung role) {
        return userRepository.getUsersByRole(role);
    }

    public boolean updateVaiTroNguoiDung(long userId, VaiTroNguoiDung role) {
        return userRepository.updateVaiTroNguoiDung(userId, role);
    }

    public boolean capNhatVaiTroNguoiDung(long userId, VaiTroNguoiDung role) {
        return userRepository.updateVaiTroNguoiDung(userId, role);
    }

    public boolean updateUserActive(long userId, boolean isActive) {
        return userRepository.updateUserActive(userId, isActive);
    }

    public boolean capNhatTrangThaiHoatDongNguoiDung(long userId, boolean isActive) {
        return userRepository.updateUserActive(userId, isActive);
    }

    public int countAllUsers() {
        return userRepository.countAllUsers();
    }

    public int countUsersByRole(VaiTroNguoiDung role) {
        return userRepository.countUsersByRole(role);
    }

    public void seedDishesIfEmpty(Context context) {
        SQLiteDatabase db = getWritableDatabase();
        SeedDataHelper.seedDishesIfEmpty(this, context == null ? appContext : context.getApplicationContext(), db);
    }

    public List<DishRecord> layTatCaMonAn() {
        return dishRepository.layTatCaMonAn();
    }

    List<DishRecord> layTatCaMonAn(SQLiteDatabase db) {
        return dishRepository.layTatCaMonAn(db);
    }

    public List<DishRecord> timKiemMonAn(@Nullable String keyword) {
        return dishRepository.timKiemMonAn(keyword);
    }

    public long themBanGhiMonAn(String name,
                                String price,
                                String description,
                                @Nullable String imageResName,
                                boolean isAvailable,
                                @Nullable String category,
                                int recommendScore) {
        return dishRepository.themBanGhiMonAn(name, price, description, imageResName, isAvailable, category, recommendScore);
    }

    public boolean capNhatBanGhiMonAn(long dishId,
                                      String name,
                                      String price,
                                      String description,
                                      @Nullable String imageResName,
                                      boolean isAvailable,
                                      @Nullable String category,
                                      int recommendScore) {
        return dishRepository.capNhatBanGhiMonAn(dishId, name, price, description, imageResName, isAvailable, category, recommendScore);
    }

    public boolean xoaMonAnTheoId(long dishId) {
        return dishRepository.xoaMonAnTheoId(dishId);
    }

    public boolean capNhatTrangThaiPhucVuMon(long dishId, boolean isAvailable) {
        return dishRepository.capNhatTrangThaiPhucVuMon(dishId, isAvailable);
    }

    public int countAllDishes() {
        return dishRepository.countAllDishes();
    }

    public List<MonAnDeXuat> layTatCaMonHienThi() {
        return dishRepository.layTatCaMonHienThi();
    }

    public List<MonAnDeXuat> layDanhSachMonTheoDanhMuc(@Nullable String tenDanhMuc) {
        return dishRepository.layDanhSachMonTheoDanhMuc(tenDanhMuc);
    }

    public List<MonAnDeXuat> layMonDeXuatTrangChu(int soLuongToiDa) {
        return dishRepository.layMonDeXuatTrangChu(soLuongToiDa);
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
        long thoiGianMucTieu = DateTimeUtils.parseDonHangTimeToMillis(thoiGianDatBan);
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
        long thoiGianMucTieu = DateTimeUtils.parseDonHangTimeToMillis(thoiGianDonHang);
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
                long thoiGianDatBan = DateTimeUtils.parseDonHangTimeToMillis(datBan.layThoiGian());
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
                values.put(COL_SERVICE_REQUEST_HANDLED_TIME, DateTimeUtils.layThoiGianHienTai());
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
            long mocGui = DateTimeUtils.parseDonHangTimeToMillis(thoiGianGui);
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

    boolean hasAnyDish(SQLiteDatabase db) {
        return dishRepository.hasAnyDish(db);
    }

    void insertDish(SQLiteDatabase db,
                    String name,
                    String price,
                    String description,
                    String imageResName,
                    boolean isAvailable,
                    String tenDanhMuc,
                    int diemDeXuat) {
        dishRepository.insertDish(db, name, price, description, imageResName, isAvailable, tenDanhMuc, diemDeXuat);
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
                DateTimeUtils.parseDonHangTimeToMillis(second.layThoiGian()),
                DateTimeUtils.parseDonHangTimeToMillis(first.layThoiGian())
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
                DateTimeUtils.parseDonHangTimeToMillis(second.layThoiGianGui()),
                DateTimeUtils.parseDonHangTimeToMillis(first.layThoiGianGui())
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
                        dishRepository.resolveImageResId(imageResName),
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


    private String taoMaDatBan() {
        return "#GB" + (System.currentTimeMillis() % 100000);
    }

    private boolean laThoiGianDatBanHopLe(@Nullable String time) {
        long reservationTime = DateTimeUtils.parseDonHangTimeToMillis(time);
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
        long thoiGianDat = DateTimeUtils.parseDonHangTimeToMillis(thoiGianDatBan);
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

        long reservationTime = DateTimeUtils.parseDonHangTimeToMillis(thoiGianDat);
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
