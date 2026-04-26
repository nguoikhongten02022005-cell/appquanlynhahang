package com.example.quanlynhahang.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.quanlynhahang.helper.DateTimeUtils;
import com.example.quanlynhahang.model.BanAn;
import com.example.quanlynhahang.model.DatBan;
import com.example.quanlynhahang.model.DonHang;

import java.util.ArrayList;
import java.util.List;

final class TableRepository {

    private static final String TAG = "TableRepository";
    private static final String BAN_MAC_DINH = "Bàn 01";

    private final DatabaseHelper databaseHelper;
    private final OrderRepository orderRepository;
    private final ReservationRepository reservationRepository;

    TableRepository(DatabaseHelper databaseHelper, OrderRepository orderRepository, ReservationRepository reservationRepository) {
        this.databaseHelper = databaseHelper;
        this.orderRepository = orderRepository;
        this.reservationRepository = reservationRepository;
    }

    List<BanAn> layTatCaBanAn() {
        List<BanAn> danhSachBan = queryBanAn();
        if (danhSachBan.isEmpty()) {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            SeedDataHelper.damBaoBanAnMau(databaseHelper.layAppContext(), db);
            danhSachBan = queryBanAn();
        }
        return danhSachBan;
    }

    long themBanAn(String maBan,
                   String tenBan,
                   int soCho,
                   @Nullable String khuVuc,
                   BanAn.TrangThai trangThai) {
        if (TextUtils.isEmpty(maBan) || TextUtils.isEmpty(tenBan) || soCho <= 0 || TextUtils.isEmpty(khuVuc)) {
            return -1;
        }
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = taoGiaTriBanAn(maBan, tenBan, soCho, khuVuc, trangThai);
        return db.insert(DatabaseHelper.TABLE_BAN_AN, null, values);
    }

    boolean capNhatBanAn(long idBan,
                         String maBan,
                         String tenBan,
                         int soCho,
                         @Nullable String khuVuc,
                         BanAn.TrangThai trangThai) {
        if (idBan <= 0 || TextUtils.isEmpty(maBan) || TextUtils.isEmpty(tenBan) || soCho <= 0 || TextUtils.isEmpty(khuVuc)) {
            return false;
        }
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = taoGiaTriBanAn(maBan, tenBan, soCho, khuVuc, trangThai);
        int rows = db.update(DatabaseHelper.TABLE_BAN_AN, values, DatabaseHelper.COL_BAN_AN_ID + " = ?", new String[]{String.valueOf(idBan)});
        return rows > 0;
    }

    boolean xoaBanAnNeuTrong(long idBan) {
        if (idBan <= 0) {
            return false;
        }
        BanAn banAn = layBanAnTheoId(idBan);
        if (banAn == null || banAn.layTrangThai() != BanAn.TrangThai.TRONG) {
            return false;
        }
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int rows = db.delete(DatabaseHelper.TABLE_BAN_AN, DatabaseHelper.COL_BAN_AN_ID + " = ?", new String[]{String.valueOf(idBan)});
        return rows > 0;
    }

    @Nullable
    BanAn layBanAnTheoId(long idBan) {
        if (idBan <= 0) {
            return null;
        }
        List<BanAn> ketQua = queryBanAn(DatabaseHelper.COL_BAN_AN_ID + " = ?", new String[]{String.valueOf(idBan)});
        return ketQua.isEmpty() ? null : ketQua.get(0);
    }

    private ContentValues taoGiaTriBanAn(String maBan,
                                         String tenBan,
                                         int soCho,
                                         @Nullable String khuVuc,
                                         @Nullable BanAn.TrangThai trangThai) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_BAN_AN_MA_BAN, maBan == null ? "" : maBan.trim());
        values.put(DatabaseHelper.COL_BAN_AN_TEN_BAN, tenBan == null ? "" : tenBan.trim());
        values.put(DatabaseHelper.COL_BAN_AN_SO_CHO, Math.max(soCho, 1));
        values.put(DatabaseHelper.COL_BAN_AN_KHU_VUC, khuVuc == null ? "" : khuVuc.trim());
        values.put(DatabaseHelper.COL_BAN_AN_TRANG_THAI, (trangThai == null ? BanAn.TrangThai.TRONG : trangThai).name());
        return values;
    }

    private List<BanAn> queryBanAn() {
        return queryBanAn(null, null);
    }

    private List<BanAn> queryBanAn(@Nullable String selection, @Nullable String[] selectionArgs) {
        List<BanAn> danhSachBan = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = databaseHelper.getReadableDatabase().query(
                    DatabaseHelper.TABLE_BAN_AN,
                    new String[]{
                            DatabaseHelper.COL_BAN_AN_ID,
                            DatabaseHelper.COL_BAN_AN_MA_BAN,
                            DatabaseHelper.COL_BAN_AN_TEN_BAN,
                            DatabaseHelper.COL_BAN_AN_SO_CHO,
                            DatabaseHelper.COL_BAN_AN_KHU_VUC,
                            DatabaseHelper.COL_BAN_AN_TRANG_THAI
                    },
                    selection,
                    selectionArgs,
                    null,
                    null,
                    DatabaseHelper.COL_BAN_AN_ID + " ASC"
            );
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BAN_AN_ID));
                String maBan = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BAN_AN_MA_BAN));
                String tenBan = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BAN_AN_TEN_BAN));
                int soCho = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BAN_AN_SO_CHO));
                String khuVuc = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BAN_AN_KHU_VUC));
                BanAn.TrangThai trangThai = parseBanAnTrangThai(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BAN_AN_TRANG_THAI)));
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
            for (DonHang donHang : orderRepository.layTatCaDonHang()) {
                if (ban.equals(donHang.laySoBan())
                        && donHang.laAnTaiQuan()
                        && (donHang.layTrangThai() == DonHang.TrangThai.DANG_CHUAN_BI
                        || donHang.layTrangThai() == DonHang.TrangThai.SAN_SANG_PHUC_VU)) {
                    return BanAn.TrangThai.DANG_PHUC_VU;
                }
            }
            for (DatBan datBan : reservationRepository.layTatCaDatBan()) {
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
}
