package com.example.quanlynhahang.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.quanlynhahang.helper.DateTimeUtils;
import com.example.quanlynhahang.model.DonHang;
import com.example.quanlynhahang.model.MonAnDeXuat;

import java.util.ArrayList;
import java.util.List;

final class OrderRepository {

    private static final String TAG = "OrderRepository";
    private static final String TEN_ANH_MAC_DINH = "menu_1";

    private final DatabaseHelper databaseHelper;
    private final DishRepository dishRepository;

    OrderRepository(DatabaseHelper databaseHelper, DishRepository dishRepository) {
        this.databaseHelper = databaseHelper;
        this.dishRepository = dishRepository;
    }

    long themDonHang(int userId,
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

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues orderValues = new ContentValues();
            orderValues.put(DatabaseHelper.COL_ORDER_USER_ID, userId);
            orderValues.put(DatabaseHelper.COL_ORDER_CODE, code);
            orderValues.put(DatabaseHelper.COL_ORDER_TIME, time);
            orderValues.put(DatabaseHelper.COL_ORDER_TOTAL_PRICE, totalPrice);
            orderValues.put(DatabaseHelper.COL_ORDER_STATUS, status.name());
            orderValues.put(DatabaseHelper.COL_ORDER_TYPE, hinhThucDon.name());
            orderValues.put(DatabaseHelper.COL_ORDER_TABLE_NUMBER, soBanDaLamSach);
            orderValues.put(DatabaseHelper.COL_ORDER_NOTE, note == null ? "" : note.trim());
            orderValues.put(DatabaseHelper.COL_ORDER_PAYMENT_STATUS, paymentStatus.name());
            orderValues.put(DatabaseHelper.COL_ORDER_PAYMENT_METHOD, paymentMethod.name());
            orderValues.put(DatabaseHelper.COL_ORDER_RESERVATION_ID, Math.max(reservationId, 0));

            long orderId = db.insert(DatabaseHelper.TABLE_ORDER, null, orderValues);
            if (orderId <= 0) {
                return -1;
            }

            for (DonHang.MonTrongDon orderDish : dishes) {
                if (orderDish == null || orderDish.layMonAn() == null) {
                    return -1;
                }

                MonAnDeXuat dishItem = orderDish.layMonAn();
                ContentValues itemValues = new ContentValues();
                itemValues.put(DatabaseHelper.COL_ORDER_ITEM_ORDER_ID, orderId);
                itemValues.put(DatabaseHelper.COL_ORDER_ITEM_DISH_NAME, dishItem.layTenMon());
                itemValues.put(DatabaseHelper.COL_ORDER_ITEM_DISH_PRICE, dishItem.layGiaBan());
                itemValues.put(DatabaseHelper.COL_ORDER_ITEM_IMAGE_RES_NAME, resolveImageResName(dishItem.layIdAnhTaiNguyen()));
                itemValues.put(DatabaseHelper.COL_ORDER_ITEM_IS_AVAILABLE, dishItem.laConPhucVu() ? 1 : 0);
                itemValues.put(DatabaseHelper.COL_ORDER_ITEM_QUANTITY, orderDish.laySoLuong());

                long itemId = db.insert(DatabaseHelper.TABLE_ORDER_ITEM, null, itemValues);
                if (itemId <= 0) {
                    return -1;
                }
            }

            if (reservationId > 0) {
                ContentValues reservationValues = new ContentValues();
                reservationValues.put(DatabaseHelper.COL_RESERVATION_LINKED_ORDER_ID, orderId);
                reservationValues.put(DatabaseHelper.COL_RESERVATION_STATUS, "ACTIVE");
                db.update(
                        DatabaseHelper.TABLE_RESERVATION,
                        reservationValues,
                        DatabaseHelper.COL_RESERVATION_ID + " = ?",
                        new String[]{String.valueOf(reservationId)}
                );
            }

            db.setTransactionSuccessful();
            return orderId;
        } finally {
            db.endTransaction();
        }
    }

    List<DonHang> layDonHangTheoNguoiDung(long userId) {
        return queryDonHangs(DatabaseHelper.COL_ORDER_USER_ID + " = ?", new String[]{String.valueOf(userId)});
    }

    @Nullable
    DonHang layDonHangTheoMa(long userId, @Nullable String maDon) {
        if (userId <= 0 || TextUtils.isEmpty(maDon)) {
            return null;
        }
        List<DonHang> ketQua = queryDonHangs(
                DatabaseHelper.COL_ORDER_USER_ID + " = ? AND " + DatabaseHelper.COL_ORDER_CODE + " = ?",
                new String[]{String.valueOf(userId), maDon.trim()}
        );
        return ketQua.isEmpty() ? null : ketQua.get(0);
    }

    List<DonHang> layTatCaDonHang() {
        return queryDonHangs(null, null);
    }

    @Nullable
    DonHang layDonHangTheoId(long orderId) {
        if (orderId <= 0) {
            return null;
        }
        List<DonHang> ketQua = queryDonHangs(DatabaseHelper.COL_ORDER_ID + " = ?", new String[]{String.valueOf(orderId)});
        return ketQua.isEmpty() ? null : ketQua.get(0);
    }

    boolean capNhatTrangThaiDonHang(long orderId, DonHang.TrangThai status) {
        if (orderId <= 0 || status == null) {
            return false;
        }

        DonHang.TrangThai currentStatus = layTrangThaiDonHangTheoId(orderId);
        if (currentStatus == null || !coTheChuyenTrangThaiDonHang(currentStatus, status)) {
            return false;
        }

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_ORDER_STATUS, status.name());
        int rows = db.update(DatabaseHelper.TABLE_ORDER, values, DatabaseHelper.COL_ORDER_ID + " = ?", new String[]{String.valueOf(orderId)});
        return rows > 0;
    }

    boolean capNhatThanhToanDonHang(long orderId,
                                    DonHang.TrangThaiThanhToan paymentStatus,
                                    DonHang.PhuongThucThanhToan paymentMethod) {
        if (orderId <= 0 || paymentStatus == null || paymentMethod == null) {
            return false;
        }

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_ORDER_PAYMENT_STATUS, paymentStatus.name());
        values.put(DatabaseHelper.COL_ORDER_PAYMENT_METHOD, paymentMethod.name());
        int rows = db.update(DatabaseHelper.TABLE_ORDER, values, DatabaseHelper.COL_ORDER_ID + " = ?", new String[]{String.valueOf(orderId)});
        return rows > 0;
    }

    int demTatCaDonHang() {
        return demSoBanGhi(DatabaseHelper.TABLE_ORDER, null, null);
    }

    int demDonHangTheoTrangThai(DonHang.TrangThai status) {
        if (status == null) {
            return demTatCaDonHang();
        }
        return demSoBanGhi(DatabaseHelper.TABLE_ORDER, DatabaseHelper.COL_ORDER_STATUS + " = ?", new String[]{status.name()});
    }

    @Nullable
    DonHang.TrangThai layTrangThaiDonHangTheoId(long orderId) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(
                    DatabaseHelper.TABLE_ORDER,
                    new String[]{DatabaseHelper.COL_ORDER_STATUS},
                    DatabaseHelper.COL_ORDER_ID + " = ?",
                    new String[]{String.valueOf(orderId)},
                    null,
                    null,
                    null,
                    "1"
            );
            if (!cursor.moveToFirst()) {
                return null;
            }
            String rawStatus = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ORDER_STATUS));
            return parseDonHangStatus(rawStatus);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private List<DonHang> queryDonHangs(@Nullable String selection, @Nullable String[] selectionArgs) {
        List<DonHang> orders = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(
                    DatabaseHelper.TABLE_ORDER,
                    new String[]{
                            DatabaseHelper.COL_ORDER_ID,
                            DatabaseHelper.COL_ORDER_CODE,
                            DatabaseHelper.COL_ORDER_TIME,
                            DatabaseHelper.COL_ORDER_TOTAL_PRICE,
                            DatabaseHelper.COL_ORDER_STATUS,
                            DatabaseHelper.COL_ORDER_TYPE,
                            DatabaseHelper.COL_ORDER_TABLE_NUMBER,
                            DatabaseHelper.COL_ORDER_NOTE,
                            DatabaseHelper.COL_ORDER_PAYMENT_STATUS,
                            DatabaseHelper.COL_ORDER_PAYMENT_METHOD,
                            DatabaseHelper.COL_ORDER_RESERVATION_ID
                    },
                    selection,
                    selectionArgs,
                    null,
                    null,
                    DatabaseHelper.COL_ORDER_ID + " DESC"
            );

            while (cursor.moveToNext()) {
                long orderId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ORDER_ID));
                String code = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ORDER_CODE));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ORDER_TIME));
                String totalPrice = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ORDER_TOTAL_PRICE));
                String statusRaw = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ORDER_STATUS));
                String orderTypeRaw = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ORDER_TYPE));
                String tableNumber = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ORDER_TABLE_NUMBER));
                String note = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ORDER_NOTE));
                String paymentStatusRaw = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ORDER_PAYMENT_STATUS));
                String paymentMethodRaw = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ORDER_PAYMENT_METHOD));
                long reservationId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ORDER_RESERVATION_ID));
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

    private List<DonHang.MonTrongDon> getDonHangItemsByDonHangId(long orderId) {
        List<DonHang.MonTrongDon> dishes = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(
                    DatabaseHelper.TABLE_ORDER_ITEM,
                    new String[]{
                            DatabaseHelper.COL_ORDER_ITEM_DISH_NAME,
                            DatabaseHelper.COL_ORDER_ITEM_DISH_PRICE,
                            DatabaseHelper.COL_ORDER_ITEM_IMAGE_RES_NAME,
                            DatabaseHelper.COL_ORDER_ITEM_IS_AVAILABLE,
                            DatabaseHelper.COL_ORDER_ITEM_QUANTITY
                    },
                    DatabaseHelper.COL_ORDER_ITEM_ORDER_ID + " = ?",
                    new String[]{String.valueOf(orderId)},
                    null,
                    null,
                    DatabaseHelper.COL_ORDER_ITEM_ID + " ASC"
            );

            while (cursor.moveToNext()) {
                String dishName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ORDER_ITEM_DISH_NAME));
                String dishPrice = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ORDER_ITEM_DISH_PRICE));
                String imageResName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ORDER_ITEM_IMAGE_RES_NAME));
                boolean isAvailable = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ORDER_ITEM_IS_AVAILABLE)) == 1;
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ORDER_ITEM_QUANTITY));

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

    private int demSoBanGhi(String table, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
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

    private String resolveImageResName(int imageResId) {
        if (imageResId == 0) {
            return TEN_ANH_MAC_DINH;
        }
        return TEN_ANH_MAC_DINH;
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
        if ("DA_THANH_TOAN_MO_PHONG".equals(paymentStatusRaw)) {
            return DonHang.TrangThaiThanhToan.DA_THANH_TOAN;
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
        if ("THANH_TOAN_NGAY_MO_PHONG".equals(paymentMethodRaw)) {
            return DonHang.PhuongThucThanhToan.THANH_TOAN_NGAY;
        }
        try {
            return DonHang.PhuongThucThanhToan.valueOf(paymentMethodRaw);
        } catch (IllegalArgumentException ex) {
            return DonHang.PhuongThucThanhToan.CHUA_CHON;
        }
    }
}
