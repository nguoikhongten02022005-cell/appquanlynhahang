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

import java.util.ArrayList;
import java.util.List;

final class SeedDataHelper {

    private static final String TAG = "SeedDataHelper";
    private static final String TEN_ANH_MAC_DINH = "menu_1";
    private static final String TEN_ANH_MON_LAU = "dish_6";
    private static final String TEN_ANH_SALAD = "menu_2";
    private static final String TEN_ANH_DO_UONG = "image3";
    private static final String EMAIL_TAI_KHOAN_TEST_KHACH_HANG = "kh1";
    private static final String SDT_TAI_KHOAN_TEST_KHACH_HANG = "0123456789";
    private static final String EMAIL_TAI_KHOAN_TEST_NHAN_VIEN = "nv1";
    private static final String SDT_TAI_KHOAN_TEST_NHAN_VIEN = "0123456790";
    private static final String EMAIL_TAI_KHOAN_TEST_ADMIN = "admin1";
    private static final String SDT_TAI_KHOAN_TEST_ADMIN = "0123456791";
    private static final String MAT_KHAU_TAI_KHOAN_TEST = "1";

    private SeedDataHelper() {
    }

    static void damBaoDuLieuMacDinh(DatabaseHelper databaseHelper, Context appContext, SQLiteDatabase db) {
        seedDishesIfEmpty(databaseHelper, appContext, db);
        chuanHoaSeedMonAnSaiDanhMuc(appContext, db);
        ensureTestUserExists(databaseHelper, appContext, db);
        damBaoDuLieuTongQuanMau(databaseHelper, appContext, db);
    }

    static void seedDishesIfEmpty(DatabaseHelper databaseHelper, Context context, SQLiteDatabase db) {
        if (databaseHelper.hasAnyDish(db)) {
            return;
        }

        databaseHelper.insertDish(
                db,
                context.getString(R.string.dish_bo_luc_lac),
                context.getString(R.string.price_145k),
                context.getString(R.string.menu_desc_bo_luc_lac),
                TEN_ANH_MAC_DINH,
                true,
                context.getString(R.string.category_main_course),
                96
        );
        databaseHelper.insertDish(
                db,
                context.getString(R.string.dish_lau_thai),
                context.getString(R.string.price_259k),
                context.getString(R.string.menu_desc_lau_thai),
                TEN_ANH_MON_LAU,
                true,
                context.getString(R.string.category_hotpot),
                93
        );
        databaseHelper.insertDish(
                db,
                context.getString(R.string.dish_salad_ca_hoi),
                context.getString(R.string.price_129k),
                context.getString(R.string.menu_desc_salad_ca_hoi),
                TEN_ANH_SALAD,
                true,
                context.getString(R.string.category_salad),
                89
        );
        databaseHelper.insertDish(
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

    static void chuanHoaSeedMonAnSaiDanhMuc(Context appContext, SQLiteDatabase db) {
        chuanHoaMonAnMacDinh(
                appContext,
                db,
                appContext.getString(R.string.dish_bo_luc_lac),
                appContext.getString(R.string.category_main_course),
                96,
                true,
                null
        );
        chuanHoaMonAnMacDinh(
                appContext,
                db,
                appContext.getString(R.string.dish_lau_thai),
                appContext.getString(R.string.category_hotpot),
                93,
                true,
                null
        );
        chuanHoaMonAnMacDinh(
                appContext,
                db,
                appContext.getString(R.string.dish_salad_ca_hoi),
                appContext.getString(R.string.category_salad),
                89,
                true,
                appContext.getString(R.string.category_main_course)
        );
        chuanHoaMonAnMacDinh(
                appContext,
                db,
                appContext.getString(R.string.dish_tra_dao),
                appContext.getString(R.string.category_drink),
                82,
                true,
                null
        );
    }

    static void ensureTestUserExists(DatabaseHelper databaseHelper, Context appContext, SQLiteDatabase db) {
        ensureSeedUser(
                databaseHelper,
                db,
                appContext.getString(R.string.db_test_customer_name),
                EMAIL_TAI_KHOAN_TEST_KHACH_HANG,
                SDT_TAI_KHOAN_TEST_KHACH_HANG,
                MAT_KHAU_TAI_KHOAN_TEST,
                VaiTroNguoiDung.KHACH_HANG,
                true
        );
        ensureSeedUser(
                databaseHelper,
                db,
                appContext.getString(R.string.db_test_employee_name),
                EMAIL_TAI_KHOAN_TEST_NHAN_VIEN,
                SDT_TAI_KHOAN_TEST_NHAN_VIEN,
                MAT_KHAU_TAI_KHOAN_TEST,
                VaiTroNguoiDung.NHAN_VIEN,
                true
        );
        ensureSeedUser(
                databaseHelper,
                db,
                appContext.getString(R.string.db_test_admin_name),
                EMAIL_TAI_KHOAN_TEST_ADMIN,
                SDT_TAI_KHOAN_TEST_ADMIN,
                MAT_KHAU_TAI_KHOAN_TEST,
                VaiTroNguoiDung.ADMIN,
                true
        );
    }

    static void damBaoDuLieuTongQuanMau(DatabaseHelper databaseHelper, Context appContext, SQLiteDatabase db) {
        long idKhachHang = layIdNguoiDungTheoEmail(db, EMAIL_TAI_KHOAN_TEST_KHACH_HANG);
        if (idKhachHang <= 0) {
            return;
        }

        damBaoMonAnMauBoSung(databaseHelper, appContext, db);
        damBaoBanAnMau(db);
        damBaoDatBanMau(db, idKhachHang);
        damBaoDonHangMau(databaseHelper, appContext, db, idKhachHang);
        damBaoYeuCauPhucVuMau(db, idKhachHang);
        damBaoTaiKhoanMauBoSung(databaseHelper, db);
    }

    static void damBaoMonAnMauBoSung(DatabaseHelper databaseHelper, Context appContext, SQLiteDatabase db) {
        taoMonAnNeuChuaCo(
                databaseHelper,
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
                databaseHelper,
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
                databaseHelper,
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
                databaseHelper,
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

    static void damBaoTaiKhoanMauBoSung(DatabaseHelper databaseHelper, SQLiteDatabase db) {
        ensureSeedUser(
                databaseHelper,
                db,
                "Nguyễn Minh Anh",
                "kh_demo_2",
                "0123456792",
                MAT_KHAU_TAI_KHOAN_TEST,
                VaiTroNguoiDung.KHACH_HANG,
                true
        );
        ensureSeedUser(
                databaseHelper,
                db,
                "Lê Thu Hà",
                "kh_demo_3",
                "0123456793",
                MAT_KHAU_TAI_KHOAN_TEST,
                VaiTroNguoiDung.KHACH_HANG,
                true
        );
        ensureSeedUser(
                databaseHelper,
                db,
                "Phạm Hoàng Long",
                "nv_demo_2",
                "0123456794",
                MAT_KHAU_TAI_KHOAN_TEST,
                VaiTroNguoiDung.NHAN_VIEN,
                true
        );
        ensureSeedUser(
                databaseHelper,
                db,
                "Admin dự phòng",
                "admin_demo_2",
                "0123456795",
                MAT_KHAU_TAI_KHOAN_TEST,
                VaiTroNguoiDung.ADMIN,
                true
        );
    }

    static void damBaoBanAnMau(SQLiteDatabase db) {
        taoBanAnNeuChuaCo(db, "B01", "Bàn 01", 4, "Tầng trệt", BanAn.TrangThai.TRONG);
        taoBanAnNeuChuaCo(db, "B02", "Bàn 02", 4, "Tầng trệt", BanAn.TrangThai.DANG_PHUC_VU);
        taoBanAnNeuChuaCo(db, "B05", "Bàn 05", 6, "Ban công", BanAn.TrangThai.DA_DAT);
        taoBanAnNeuChuaCo(db, "B08", "Bàn 08", 8, "Phòng riêng", BanAn.TrangThai.TRONG);
    }

    static void damBaoDatBanMau(SQLiteDatabase db, long userId) {
        taoDatBanNeuChuaCo(db, userId, "#GB10001", "20/04/2026 18:30", "Bàn 05", 4,
                "Sinh nhật gia đình, ưu tiên khu yên tĩnh.", DatBan.TrangThai.PENDING, 0);
        taoDatBanNeuChuaCo(db, userId, "#GB10002", "19/04/2026 19:00", "Bàn 02", 2,
                "Khách đã tới quán.", DatBan.TrangThai.ACTIVE, 0);
        taoDatBanNeuChuaCo(db, userId, "#GB10003", "18/04/2026 18:00", "Bàn 08", 6,
                "Đã dùng bữa xong.", DatBan.TrangThai.COMPLETED, 0);
        taoDatBanNeuChuaCo(db, userId, "#GB10004", "17/04/2026 20:00", "Bàn 10", 3,
                "Khách báo bận nên hủy.", DatBan.TrangThai.CANCELLED, 0);
    }

    static void damBaoDonHangMau(DatabaseHelper databaseHelper, Context appContext, SQLiteDatabase db, long userId) {
        List<DatabaseHelper.DishRecord> danhSachMon = databaseHelper.layTatCaMonAn(db);
        DatabaseHelper.DishRecord boLucLac = timMonTheoTen(danhSachMon, appContext.getString(R.string.dish_bo_luc_lac));
        DatabaseHelper.DishRecord lauThai = timMonTheoTen(danhSachMon, appContext.getString(R.string.dish_lau_thai));
        DatabaseHelper.DishRecord salad = timMonTheoTen(danhSachMon, appContext.getString(R.string.dish_salad_ca_hoi));
        DatabaseHelper.DishRecord traDao = timMonTheoTen(danhSachMon, appContext.getString(R.string.dish_tra_dao));
        if (boLucLac == null || lauThai == null || salad == null || traDao == null) {
            return;
        }

        long idDatBanDangHoatDong = layIdDatBanTheoMa(db, "#GB10002");
        taoDonHangNeuChuaCo(
                appContext,
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
                appContext,
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
                appContext,
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
                appContext,
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
                appContext,
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

    static void damBaoYeuCauPhucVuMau(SQLiteDatabase db, long userId) {
        long idDonDangChuanBi = layIdDonHangTheoMa(db, "#DH10002");
        long idDonSanSang = layIdDonHangTheoMa(db, "#DH10003");
        taoYeuCauNeuChuaCo(db, userId, YeuCauPhucVu.LoaiYeuCau.GOI_NHAN_VIEN,
                "Khách cần thêm chén và đĩa.", "Bàn 02", idDonDangChuanBi,
                "19/04/2026 19:08", YeuCauPhucVu.TrangThai.DANG_CHO, "");
        taoYeuCauNeuChuaCo(db, userId, YeuCauPhucVu.LoaiYeuCau.THEM_NUOC,
                "Xin thêm nước lọc cho bàn 02.", "Bàn 02", idDonDangChuanBi,
                "19/04/2026 19:10", YeuCauPhucVu.TrangThai.DANG_XU_LY, "19/04/2026 19:12");
        taoYeuCauNeuChuaCo(db, userId, YeuCauPhucVu.LoaiYeuCau.THANH_TOAN,
                "Yêu cầu thanh toán cho bàn 08.", "Bàn 08", idDonSanSang,
                "18/04/2026 19:15", YeuCauPhucVu.TrangThai.DA_XU_LY, "18/04/2026 19:20");
        taoYeuCauNeuChuaCo(db, userId, YeuCauPhucVu.LoaiYeuCau.GOI_NHAN_VIEN,
                "Khách đã tự xử lý nên không cần hỗ trợ nữa.", "Bàn 08", idDonSanSang,
                "18/04/2026 18:40", YeuCauPhucVu.TrangThai.DA_HUY, "18/04/2026 18:45");
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
                values.put(DatabaseHelper.COL_USER_ROLE, role.name());
                values.put(DatabaseHelper.COL_USER_IS_ACTIVE, isActive ? 1 : 0);
                db.update(
                        DatabaseHelper.TABLE_USER,
                        values,
                        DatabaseHelper.COL_USER_ID + " = ?",
                        new String[]{String.valueOf(userId)}
                );
                return;
            }

            Log.i(TAG, "Tạo tài khoản thử nghiệm: " + email);
            databaseHelper.insertUser(db, name, email, phone, password, role, isActive);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    static long layIdNguoiDungTheoEmail(SQLiteDatabase db, String email) {
        Cursor cursor = null;
        try {
            cursor = db.query(
                    DatabaseHelper.TABLE_USER,
                    new String[]{DatabaseHelper.COL_USER_ID},
                    DatabaseHelper.COL_USER_EMAIL + " = ?",
                    new String[]{email},
                    null,
                    null,
                    null,
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
            cursor = db.query(
                    DatabaseHelper.TABLE_RESERVATION,
                    new String[]{DatabaseHelper.COL_RESERVATION_ID},
                    DatabaseHelper.COL_RESERVATION_CODE + " = ?",
                    new String[]{maDatBan},
                    null,
                    null,
                    null,
                    "1"
            );
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

    static long layIdDonHangTheoMa(SQLiteDatabase db, String maDonHang) {
        Cursor cursor = null;
        try {
            cursor = db.query(
                    DatabaseHelper.TABLE_ORDER,
                    new String[]{DatabaseHelper.COL_ORDER_ID},
                    DatabaseHelper.COL_ORDER_CODE + " = ?",
                    new String[]{maDonHang},
                    null,
                    null,
                    null,
                    "1"
            );
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

    static void taoDatBanNeuChuaCo(SQLiteDatabase db,
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

    static void taoDonHangNeuChuaCo(Context appContext,
                                    SQLiteDatabase db,
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
            db.update(
                    DatabaseHelper.TABLE_RESERVATION,
                    valuesDatBan,
                    DatabaseHelper.COL_RESERVATION_ID + " = ?",
                    new String[]{String.valueOf(idDatBanLienKet)}
            );
        }
    }

    static void taoYeuCauNeuChuaCo(SQLiteDatabase db,
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

    static boolean daCoYeuCauMau(SQLiteDatabase db,
                                 long userId,
                                 YeuCauPhucVu.LoaiYeuCau loaiYeuCau,
                                 String noiDung,
                                 String thoiGianGui) {
        Cursor cursor = null;
        try {
            cursor = db.query(
                    DatabaseHelper.TABLE_SERVICE_REQUEST,
                    new String[]{DatabaseHelper.COL_SERVICE_REQUEST_ID},
                    DatabaseHelper.COL_SERVICE_REQUEST_USER_ID + " = ? AND "
                            + DatabaseHelper.COL_SERVICE_REQUEST_TYPE + " = ? AND "
                            + DatabaseHelper.COL_SERVICE_REQUEST_CONTENT + " = ? AND "
                            + DatabaseHelper.COL_SERVICE_REQUEST_SENT_TIME + " = ?",
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
    static DatabaseHelper.DishRecord timMonTheoTen(List<DatabaseHelper.DishRecord> danhSachMon, String tenMon) {
        if (danhSachMon == null || TextUtils.isEmpty(tenMon)) {
            return null;
        }
        for (DatabaseHelper.DishRecord banGhi : danhSachMon) {
            if (banGhi != null && banGhi.layMonAn() != null
                    && TextUtils.equals(tenMon, banGhi.layMonAn().layTenMon())) {
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

    static List<DonHang.MonTrongDon> taoDanhSachMonMau(@Nullable DonHang.MonTrongDon... danhSachMon) {
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

    static void taoMonAnNeuChuaCo(DatabaseHelper databaseHelper,
                                  SQLiteDatabase db,
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
                    DatabaseHelper.TABLE_DISH,
                    new String[]{DatabaseHelper.COL_DISH_ID},
                    DatabaseHelper.COL_DISH_NAME + " = ?",
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
        databaseHelper.insertDish(db, tenMon, giaBan, moTa, tenAnh, conPhucVu, danhMuc, diemDeXuat);
    }

    private static void chuanHoaMonAnMacDinh(Context appContext,
                                             SQLiteDatabase db,
                                             String tenMon,
                                             String danhMucDung,
                                             int diemDeXuat,
                                             boolean conPhucVu,
                                             @Nullable String danhMucCuSai) {
        if (TextUtils.isEmpty(tenMon)) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_DISH_CATEGORY, danhMucDung);
        values.put(DatabaseHelper.COL_DISH_RECOMMEND_SCORE, diemDeXuat);
        values.put(DatabaseHelper.COL_DISH_IS_AVAILABLE, conPhucVu ? 1 : 0);
        values.put(DatabaseHelper.COL_DISH_IMAGE_RES_NAME, layTenAnhMacDinhTheoMon(appContext, tenMon));

        if (!TextUtils.isEmpty(danhMucCuSai)) {
            db.update(
                    DatabaseHelper.TABLE_DISH,
                    values,
                    DatabaseHelper.COL_DISH_NAME + " = ? AND " + DatabaseHelper.COL_DISH_CATEGORY + " = ?",
                    new String[]{tenMon, danhMucCuSai}
            );
        }

        db.update(
                DatabaseHelper.TABLE_DISH,
                values,
                DatabaseHelper.COL_DISH_NAME + " = ?",
                new String[]{tenMon}
        );
    }

    private static String layTenAnhMacDinhTheoMon(Context appContext, @Nullable String tenMon) {
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

    private static void taoBanAnNeuChuaCo(SQLiteDatabase db,
                                          String maBan,
                                          String tenBan,
                                          int soCho,
                                          @Nullable String khuVuc,
                                          @Nullable BanAn.TrangThai trangThai) {
        Cursor cursor = null;
        try {
            cursor = db.query(
                    DatabaseHelper.TABLE_BAN_AN,
                    new String[]{DatabaseHelper.COL_BAN_AN_ID},
                    DatabaseHelper.COL_BAN_AN_MA_BAN + " = ?",
                    new String[]{maBan},
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
        db.insert(DatabaseHelper.TABLE_BAN_AN, null, taoGiaTriBanAn(maBan, tenBan, soCho, khuVuc, trangThai));
    }

    private static ContentValues taoGiaTriBanAn(String maBan,
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
