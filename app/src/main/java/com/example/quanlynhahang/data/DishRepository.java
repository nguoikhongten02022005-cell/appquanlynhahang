package com.example.quanlynhahang.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.example.quanlynhahang.R;
import com.example.quanlynhahang.model.MonAnDeXuat;

import java.util.ArrayList;
import java.util.List;

final class DishRepository {

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
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(price) || TextUtils.isEmpty(description) || TextUtils.isEmpty(category)) {
            return -1;
        }
        ContentValues values = taoGiaTriMonAn(name, price, description, imageResName, isAvailable, category, recommendScore);
        return databaseHelper.getWritableDatabase().insert(DatabaseHelper.TABLE_DISH, null, values);
    }

    boolean capNhatBanGhiMonAn(long dishId,
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
        ContentValues values = taoGiaTriMonAn(name, price, description, imageResName, isAvailable, category, recommendScore);
        int rows = databaseHelper.getWritableDatabase().update(
                DatabaseHelper.TABLE_DISH,
                values,
                DatabaseHelper.COL_DISH_ID + " = ?",
                new String[]{String.valueOf(dishId)}
        );
        return rows > 0;
    }

    boolean xoaMonAnTheoId(long dishId) {
        if (dishId <= 0) {
            return false;
        }
        int rows = databaseHelper.getWritableDatabase().delete(
                DatabaseHelper.TABLE_DISH,
                DatabaseHelper.COL_DISH_ID + " = ?",
                new String[]{String.valueOf(dishId)}
        );
        return rows > 0;
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
                DatabaseHelper.COL_DISH_ID + " = ?",
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
                    null,
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
                    selection,
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
        values.put(DatabaseHelper.COL_DISH_NAME, name != null ? name.trim() : "");
        values.put(DatabaseHelper.COL_DISH_PRICE, price != null ? price.trim() : "");
        values.put(DatabaseHelper.COL_DISH_DESCRIPTION, description != null ? description.trim() : "");
        values.put(DatabaseHelper.COL_DISH_IMAGE_RES_NAME, TextUtils.isEmpty(imageResName) ? TEN_ANH_MAC_DINH : imageResName.trim());
        values.put(DatabaseHelper.COL_DISH_IS_AVAILABLE, isAvailable ? 1 : 0);
        values.put(DatabaseHelper.COL_DISH_CATEGORY, category != null ? category.trim() : "");
        values.put(DatabaseHelper.COL_DISH_RECOMMEND_SCORE, Math.max(recommendScore, 0));
        return values;
    }

    int resolveImageResId(String imageResName) {
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
