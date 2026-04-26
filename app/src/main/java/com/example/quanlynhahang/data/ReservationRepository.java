package com.example.quanlynhahang.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.quanlynhahang.helper.DateTimeUtils;
import com.example.quanlynhahang.model.DatBan;

import java.util.ArrayList;
import java.util.List;

final class ReservationRepository {

    private static final String TAG = "ReservationRepository";
    private static final String BAN_MAC_DINH = "Bàn 01";
    private static final int SO_KHACH_DAT_BAN_TOI_DA = 20;
    private static final long DAT_BAN_TOI_THIEU_TRUOC_PHUT = 30L;
    private static final long CUA_SO_KICH_HOAT_DAT_BAN_PHUT = 30L;

    private final DatabaseHelper databaseHelper;

    ReservationRepository(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    List<DatBan> layDatBanTheoNguoiDung(long userId) {
        return queryReservations(DatabaseHelper.COL_RESERVATION_USER_ID + " = ?", new String[]{String.valueOf(userId)});
    }

    List<DatBan> layTatCaDatBan() {
        return queryReservations(null, null);
    }

    @Nullable
    DatBan layDatBanTheoId(long reservationId) {
        List<DatBan> reservations = queryReservations(DatabaseHelper.COL_RESERVATION_ID + " = ?", new String[]{String.valueOf(reservationId)});
        return reservations.isEmpty() ? null : reservations.get(0);
    }

    boolean capNhatTrangThaiDatBan(long reservationId, DatBan.TrangThai status) {
        if (reservationId <= 0 || status == null) {
            return false;
        }

        DatBan.TrangThai currentStatus = layTrangThaiDatBanTheoId(reservationId);
        if (currentStatus == null || !coTheChuyenTrangThaiDatBan(currentStatus, status)) {
            return false;
        }

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_RESERVATION_STATUS, status.name());
        int rows = db.update(DatabaseHelper.TABLE_RESERVATION, values, DatabaseHelper.COL_RESERVATION_ID + " = ?", new String[]{String.valueOf(reservationId)});
        return rows > 0;
    }

    int demDatBanTheoTrangThai(DatBan.TrangThai status) {
        if (status == null) {
            return demSoBanGhi(DatabaseHelper.TABLE_RESERVATION, null, null);
        }
        return demSoBanGhi(DatabaseHelper.TABLE_RESERVATION, DatabaseHelper.COL_RESERVATION_STATUS + " = ?", new String[]{status.name()});
    }

    long themDatBan(long userId,
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

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_RESERVATION_USER_ID, userId);
        values.put(DatabaseHelper.COL_RESERVATION_CODE, TextUtils.isEmpty(reservationCode) ? taoMaDatBan() : reservationCode);
        values.put(DatabaseHelper.COL_RESERVATION_TIME, time);
        values.put(DatabaseHelper.COL_RESERVATION_TABLE_NUMBER, tableNumber);
        values.put(DatabaseHelper.COL_RESERVATION_GUEST_COUNT, guestCount);
        values.put(DatabaseHelper.COL_RESERVATION_NOTE, note == null ? "" : note.trim());
        values.put(DatabaseHelper.COL_RESERVATION_STATUS, status.name());
        values.put(DatabaseHelper.COL_RESERVATION_LINKED_ORDER_ID, Math.max(linkedOrderId, 0));
        return db.insert(DatabaseHelper.TABLE_RESERVATION, null, values);
    }

    boolean huyDatBan(long reservationId) {
        return capNhatTrangThaiDatBan(reservationId, DatBan.TrangThai.CANCELLED);
    }

    boolean capNhatBanDatBan(long reservationId, @Nullable String soBanMoi) {
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

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_RESERVATION_TABLE_NUMBER, soBanDaLamSach);
        int rows = db.update(DatabaseHelper.TABLE_RESERVATION, values, DatabaseHelper.COL_RESERVATION_ID + " = ?", new String[]{String.valueOf(reservationId)});
        return rows > 0;
    }

    @Nullable
    DatBan layDatBanActiveTheoNguoiDung(long userId) {
        return timDatBanTheoTrangThai(layDatBanTheoNguoiDung(userId), DatBan.TrangThai.ACTIVE);
    }

    @Nullable
    DatBan layDatBanPendingTheoNguoiDung(long userId) {
        return timDatBanTheoTrangThai(layDatBanTheoNguoiDung(userId), DatBan.TrangThai.PENDING);
    }

    @Nullable
    DatBan layDatBanHieuLucTheoNguoiDung(long userId, @Nullable String soBan, @Nullable String thoiGianDonHang) {
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

    List<String> layDanhSachBanDaDat(String thoiGianDatBan, long reservationIdBoQua) {
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

    private List<DatBan> queryReservations(@Nullable String selection, @Nullable String[] selectionArgs) {
        List<DatBan> reservations = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(
                    DatabaseHelper.TABLE_RESERVATION,
                    new String[]{
                            DatabaseHelper.COL_RESERVATION_ID,
                            DatabaseHelper.COL_RESERVATION_CODE,
                            DatabaseHelper.COL_RESERVATION_TIME,
                            DatabaseHelper.COL_RESERVATION_TABLE_NUMBER,
                            DatabaseHelper.COL_RESERVATION_GUEST_COUNT,
                            DatabaseHelper.COL_RESERVATION_NOTE,
                            DatabaseHelper.COL_RESERVATION_STATUS,
                            DatabaseHelper.COL_RESERVATION_LINKED_ORDER_ID
                    },
                    selection,
                    selectionArgs,
                    null,
                    null,
                    DatabaseHelper.COL_RESERVATION_ID + " DESC"
            );

            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RESERVATION_ID));
                String reservationCode = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RESERVATION_CODE));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RESERVATION_TIME));
                String tableNumber = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RESERVATION_TABLE_NUMBER));
                int guestCount = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RESERVATION_GUEST_COUNT));
                String note = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RESERVATION_NOTE));
                String statusRaw = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RESERVATION_STATUS));
                long linkedOrderId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RESERVATION_LINKED_ORDER_ID));
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

    @Nullable
    private DatBan.TrangThai layTrangThaiDatBanTheoId(long reservationId) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(
                    DatabaseHelper.TABLE_RESERVATION,
                    new String[]{DatabaseHelper.COL_RESERVATION_STATUS},
                    DatabaseHelper.COL_RESERVATION_ID + " = ?",
                    new String[]{String.valueOf(reservationId)},
                    null,
                    null,
                    null,
                    "1"
            );
            if (!cursor.moveToFirst()) {
                return null;
            }
            String rawStatus = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RESERVATION_STATUS));
            return parseReservationStatus(rawStatus);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
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

    private boolean banCoTheDatTrongKhungGio(@Nullable String time, @Nullable String tableNumber, long reservationIdBoQua) {
        if (TextUtils.isEmpty(time) || TextUtils.isEmpty(tableNumber)) {
            return false;
        }
        return !layDanhSachBanDaDat(time, reservationIdBoQua).contains(tableNumber.trim());
    }

    private boolean banCoTheDatTrongKhungGio(@Nullable String time, @Nullable String tableNumber) {
        return banCoTheDatTrongKhungGio(time, tableNumber, 0);
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
        return layDatBanHieuLucTheoNguoiDung(userId, null, null) != null;
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
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_RESERVATION_STATUS, trangThaiMoi.name());
        db.update(DatabaseHelper.TABLE_RESERVATION, values, DatabaseHelper.COL_RESERVATION_ID + " = ?", new String[]{String.valueOf(reservationId)});
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
}
