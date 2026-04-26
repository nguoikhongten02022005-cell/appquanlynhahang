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
import com.example.quanlynhahang.helper.DateTimeUtils;
import com.example.quanlynhahang.model.MonAnDeXuat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

final class DishRepository {

    private static final String TAG = "DishRepository";
    private static final String TEN_ANH_MAC_DINH = "menu_1";

    private final DatabaseHelper databaseHelper;
    private final Context appContext;

    DishRepository(DatabaseHelper databaseHelper, Context appContext) {
        this.databaseHelper = databaseHelper;
        this.appContext = appContext.getApplicationContext();
    }

    List<DatabaseHelper.DishRecord> layTatCaMonAn() {
        return queryDishes(null, null);
    }

    List<DatabaseHelper.DishRecord> layTatCaMonAn(SQLiteDatabase db) {
        return queryDishes(db, null, null);
    }

    List<DatabaseHelper.DishRecord> timKiemMonAn(@Nullable String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            return layTatCaMonAn();
        }
        String trimmedKeyword = keyword.trim();
        String likeValue = "%" + trimmedKeyword + "%";
        return queryDishes(
                DatabaseHelper.COL_DISH_NAME + " LIKE ? OR "
                        + DatabaseHelper.COL_DISH_CATEGORY + " LIKE ? OR "
                        + DatabaseHelper.COL_DISH_DESCRIPTION + " LIKE ?",
                new String[]{likeValue, likeValue, likeValue}
        );
    }

    long themBanGhiMonAn(String name,
                         String price,
                         String description,
                         @Nullable String imageResName,
                         boolean isAvailable,
                         @Nullable String category,
                         int recommendScore) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(price) || TextUtils.isEmpty(description) || TextUtils.isEmpty(category)
                || tenMonDangDuocDung(name, 0)) {
            return -1;
        }
        ContentValues values = taoGiaTriMonAn(name, price, description, imageResName, isAvailable, category, recommendScore);
        long idMon = databaseHelper.getWritableDatabase().insert(DatabaseHelper.TABLE_DISH, null, values);
        if (idMon > 0) {
            Log.i(TAG, "Thêm món ăn id=" + idMon + ", tên=" + chuanHoaChuoi(name));
        }
        return idMon;
    }

    boolean capNhatBanGhiMonAn(long dishId,
                               String name,
                               String price,
                               String description,
                               @Nullable String imageResName,
                               boolean isAvailable,
                               @Nullable String category,
                               int recommendScore) {
        if (dishId <= 0 || TextUtils.isEmpty(name) || TextUtils.isEmpty(price) || TextUtils.isEmpty(description) || TextUtils.isEmpty(category)
                || tenMonDangDuocDung(name, dishId)) {
            return false;
        }
        ContentValues values = taoGiaTriMonAn(name, price, description, imageResName, isAvailable, category, recommendScore);
        int rows = databaseHelper.getWritableDatabase().update(
                DatabaseHelper.TABLE_DISH,
                values,
                DatabaseHelper.COL_DISH_ID + " = ? AND " + DatabaseHelper.COL_DISH_IS_ARCHIVED + " = 0",
                new String[]{String.valueOf(dishId)}
        );
        if (rows > 0) {
            Log.i(TAG, "Cập nhật món ăn id=" + dishId + ", tên=" + chuanHoaChuoi(name));
        }
        return rows > 0;
    }

    boolean xoaMonAnTheoId(long dishId) {
        if (dishId <= 0) {
            return false;
        }
        String tenMon = layTenMonTheoId(dishId);
        if (TextUtils.isEmpty(tenMon)) {
            return false;
        }
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        if (coLichSuDonHangChoTenMon(tenMon)) {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COL_DISH_IS_ARCHIVED, 1);
            values.put(DatabaseHelper.COL_DISH_IS_AVAILABLE, 0);
            values.put(DatabaseHelper.COL_DISH_ARCHIVED_AT, DateTimeUtils.layThoiGianHienTai());
            int rows = db.update(
                    DatabaseHelper.TABLE_DISH,
                    values,
                    DatabaseHelper.COL_DISH_ID + " = ? AND " + DatabaseHelper.COL_DISH_IS_ARCHIVED + " = 0",
                    new String[]{String.valueOf(dishId)}
            );
            if (rows > 0) {
                Log.i(TAG, "Lưu trữ món ăn id=" + dishId + ", tên=" + tenMon);
            }
            return rows > 0;
        }
        int rows = db.delete(
                DatabaseHelper.TABLE_DISH,
                DatabaseHelper.COL_DISH_ID + " = ?",
                new String[]{String.valueOf(dishId)}
        );
        if (rows > 0) {
            Log.i(TAG, "Xóa vĩnh viễn món ăn id=" + dishId + ", tên=" + tenMon);
        }
        return rows > 0;
    }

    boolean tenMonDangDuocDung(String name, long excludeDishId) {
        String tenCanKiemTra = chuanHoaChuoi(name);
        if (TextUtils.isEmpty(tenCanKiemTra)) {
            return false;
        }
        Cursor cursor = null;
        try {
            cursor = databaseHelper.getReadableDatabase().query(
                    DatabaseHelper.TABLE_DISH,
                    new String[]{DatabaseHelper.COL_DISH_ID, DatabaseHelper.COL_DISH_NAME},
                    taoSelectionMonChuaLuuTru(null),
                    null,
                    null,
                    null,
                    null
            );
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DISH_ID));
                String tenDaLuu = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DISH_NAME));
                if (id != excludeDishId && tenCanKiemTra.equals(chuanHoaChuoi(tenDaLuu))) {
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

    boolean capNhatTrangThaiPhucVuMon(long dishId, boolean isAvailable) {
        if (dishId <= 0) {
            return false;
        }
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_DISH_IS_AVAILABLE, isAvailable ? 1 : 0);
        int rows = databaseHelper.getWritableDatabase().update(
                DatabaseHelper.TABLE_DISH,
                values,
                DatabaseHelper.COL_DISH_ID + " = ? AND " + DatabaseHelper.COL_DISH_IS_ARCHIVED + " = 0",
                new String[]{String.valueOf(dishId)}
        );
        return rows > 0;
    }

    int countAllDishes() {
        Cursor cursor = null;
        try {
            cursor = databaseHelper.getReadableDatabase().query(
                    DatabaseHelper.TABLE_DISH,
                    new String[]{"COUNT(*)"},
                    taoSelectionMonChuaLuuTru(null),
                    null,
                    null,
                    null,
                    null
            );
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

    List<MonAnDeXuat> layTatCaMonHienThi() {
        List<MonAnDeXuat> dishes = new ArrayList<>();
        for (DatabaseHelper.DishRecord record : layTatCaMonAn()) {
            dishes.add(record.layMonAn());
        }
        return dishes;
    }

    List<MonAnDeXuat> layDanhSachMonTheoDanhMuc(@Nullable String tenDanhMuc) {
        List<MonAnDeXuat> dishes = new ArrayList<>();
        for (DatabaseHelper.DishRecord record : layTatCaMonAn()) {
            MonAnDeXuat dishItem = record.layMonAn();
            if (TextUtils.isEmpty(tenDanhMuc) || TextUtils.equals(tenDanhMuc, dishItem.layTenDanhMuc())) {
                dishes.add(dishItem);
            }
        }
        return dishes;
    }

    List<MonAnDeXuat> layMonDeXuatTrangChu(int soLuongToiDa) {
        List<MonAnDeXuat> available = new ArrayList<>();
        List<MonAnDeXuat> fallback = new ArrayList<>();

        for (DatabaseHelper.DishRecord record : layTatCaMonAn()) {
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

    boolean hasAnyDish(SQLiteDatabase db) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_DISH, null);
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

    void insertDish(SQLiteDatabase db,
                    String name,
                    String price,
                    String description,
                    String imageResName,
                    boolean isAvailable,
                    String tenDanhMuc,
                    int diemDeXuat) {
        ContentValues values = taoGiaTriMonAn(name, price, description, imageResName, isAvailable, tenDanhMuc, diemDeXuat);
        db.insert(DatabaseHelper.TABLE_DISH, null, values);
    }

    private String taoSelectionMonChuaLuuTru(@Nullable String selection) {
        String dieuKienChuaLuuTru = DatabaseHelper.COL_DISH_IS_ARCHIVED + " = 0";
        if (TextUtils.isEmpty(selection)) {
            return dieuKienChuaLuuTru;
        }
        return "(" + selection + ") AND " + dieuKienChuaLuuTru;
    }

    @Nullable
    private String layTenMonTheoId(long dishId) {
        Cursor cursor = null;
        try {
            cursor = databaseHelper.getReadableDatabase().query(
                    DatabaseHelper.TABLE_DISH,
                    new String[]{DatabaseHelper.COL_DISH_NAME},
                    DatabaseHelper.COL_DISH_ID + " = ? AND " + DatabaseHelper.COL_DISH_IS_ARCHIVED + " = 0",
                    new String[]{String.valueOf(dishId)},
                    null,
                    null,
                    null
            );
            return cursor.moveToFirst() ? cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DISH_NAME)) : null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private boolean coLichSuDonHangChoTenMon(String tenMon) {
        String tenDaChuanHoa = chuanHoaChuoi(tenMon);
        Cursor cursor = null;
        try {
            cursor = databaseHelper.getReadableDatabase().query(
                    DatabaseHelper.TABLE_ORDER_ITEM,
                    new String[]{DatabaseHelper.COL_ORDER_ITEM_DISH_NAME},
                    null,
                    null,
                    null,
                    null,
                    null
            );
            while (cursor.moveToNext()) {
                String tenTrongDon = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ORDER_ITEM_DISH_NAME));
                if (tenDaChuanHoa.equals(chuanHoaChuoi(tenTrongDon))) {
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

    private String chuanHoaChuoi(@Nullable String raw) {
        return chuanHoaKhoangTrang(raw).toLowerCase(Locale.ROOT);
    }

    private String chuanHoaKhoangTrang(@Nullable String raw) {
        if (raw == null) {
            return "";
        }
        return raw.trim().replaceAll("\\s+", " ");
    }

    private List<DatabaseHelper.DishRecord> queryDishes(@Nullable String selection, @Nullable String[] selectionArgs) {
        return queryDishes(databaseHelper.getReadableDatabase(), selection, selectionArgs);
    }

    private List<DatabaseHelper.DishRecord> queryDishes(SQLiteDatabase db,
                                                        @Nullable String selection,
                                                        @Nullable String[] selectionArgs) {
        List<DatabaseHelper.DishRecord> dishRecords = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query(
                    DatabaseHelper.TABLE_DISH,
                    new String[]{
                            DatabaseHelper.COL_DISH_ID,
                            DatabaseHelper.COL_DISH_NAME,
                            DatabaseHelper.COL_DISH_PRICE,
                            DatabaseHelper.COL_DISH_DESCRIPTION,
                            DatabaseHelper.COL_DISH_IMAGE_RES_NAME,
                            DatabaseHelper.COL_DISH_IS_AVAILABLE,
                            DatabaseHelper.COL_DISH_CATEGORY,
                            DatabaseHelper.COL_DISH_RECOMMEND_SCORE
                    },
                    taoSelectionMonChuaLuuTru(selection),
                    selectionArgs,
                    null,
                    null,
                    DatabaseHelper.COL_DISH_ID + " ASC"
            );

            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DISH_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DISH_NAME));
                String price = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DISH_PRICE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DISH_DESCRIPTION));
                String imageResName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DISH_IMAGE_RES_NAME));
                boolean isAvailable = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DISH_IS_AVAILABLE)) == 1;
                String category = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DISH_CATEGORY));
                int recommendScore = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DISH_RECOMMEND_SCORE));

                MonAnDeXuat dishItem = new MonAnDeXuat(
                        resolveImageResId(imageResName),
                        name,
                        price,
                        isAvailable,
                        category,
                        recommendScore
                );
                dishRecords.add(new DatabaseHelper.DishRecord(id, dishItem, description, imageResName));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return dishRecords;
    }

    private ContentValues taoGiaTriMonAn(String name,
                                         String price,
                                         String description,
                                         @Nullable String imageResName,
                                         boolean isAvailable,
                                         @Nullable String category,
                                         int recommendScore) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_DISH_NAME, chuanHoaKhoangTrang(name));
        values.put(DatabaseHelper.COL_DISH_PRICE, chuanHoaKhoangTrang(price));
        values.put(DatabaseHelper.COL_DISH_DESCRIPTION, chuanHoaKhoangTrang(description));
        values.put(DatabaseHelper.COL_DISH_IMAGE_RES_NAME, chuanHoaKhoangTrang(imageResName));
        values.put(DatabaseHelper.COL_DISH_IS_AVAILABLE, isAvailable ? 1 : 0);
        values.put(DatabaseHelper.COL_DISH_CATEGORY, chuanHoaKhoangTrang(category));
        values.put(DatabaseHelper.COL_DISH_RECOMMEND_SCORE, Math.max(recommendScore, 0));
        values.put(DatabaseHelper.COL_DISH_IS_ARCHIVED, 0);
        values.put(DatabaseHelper.COL_DISH_ARCHIVED_AT, "");
        return values;
    }

    int resolveImageResId(String imageResName) {
        if (TextUtils.isEmpty(imageResName) || imageResName.startsWith("content://")) {
            return R.drawable.menu_1;
        }

        int resId = appContext.getResources().getIdentifier(
                imageResName,
                "drawable",
                appContext.getPackageName()
        );
        return resId == 0 ? R.drawable.menu_1 : resId;
    }

    @SuppressWarnings("unused")
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
}
