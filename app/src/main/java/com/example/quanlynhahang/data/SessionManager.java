package com.example.quanlynhahang.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.quanlynhahang.R;
import com.example.quanlynhahang.model.NguoiDung;
import com.example.quanlynhahang.model.VaiTroNguoiDung;

public class SessionManager {

    private static final String TAG = "SessionManager";
    private static final String PREFS_AUTH = "auth_prefs";

    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_CURRENT_USER_ID = "current_user_id";
    private static final String KEY_CURRENT_USER_ROLE = "current_user_role";
    private static final String KEY_LEGACY_AUTH_MIGRATED = "legacy_auth_migrated";
    private static final String KEY_CURRENT_TABLE = "current_table";

    private static final String LEGACY_KEY_REGISTERED_EMAIL = "registered_email";
    private static final String LEGACY_KEY_REGISTERED_PASSWORD = "registered_password";

    private final Context ungDungContext;
    private final SharedPreferences boNhoChiaSe;

    public SessionManager(Context context) {
        ungDungContext = context.getApplicationContext();
        boNhoChiaSe = ungDungContext.getSharedPreferences(PREFS_AUTH, Context.MODE_PRIVATE);
    }

    public void chuyenDuLieuDangNhapCuNeuCan(DatabaseHelper databaseHelper) {
        Log.d(TAG, "Kiểm tra migration dữ liệu đăng nhập cũ.");
        if (boNhoChiaSe.getBoolean(KEY_LEGACY_AUTH_MIGRATED, false)) {
            Log.d(TAG, "Migration dữ liệu đăng nhập cũ đã chạy trước đó, bỏ qua.");
            return;
        }

        String emailCu = boNhoChiaSe.getString(LEGACY_KEY_REGISTERED_EMAIL, "");
        String matKhauCu = boNhoChiaSe.getString(LEGACY_KEY_REGISTERED_PASSWORD, "");
        boolean daDangNhapCu = boNhoChiaSe.getBoolean(KEY_IS_LOGGED_IN, false);

        long idNguoiDungPhienHienTai = boNhoChiaSe.getLong(KEY_CURRENT_USER_ID, -1);
        if (idNguoiDungPhienHienTai > 0) {
            NguoiDung nguoiDungHienTai = databaseHelper.getUserById(idNguoiDungPhienHienTai);
            if (nguoiDungHienTai != null) {
                Log.i(TAG, "Giữ nguyên phiên đăng nhập hiện tại vì người dùng đã tồn tại trong cơ sở dữ liệu.");
                boNhoChiaSe.edit()
                        .putBoolean(KEY_IS_LOGGED_IN, daDangNhapCu)
                        .putString(KEY_CURRENT_USER_ROLE, nguoiDungHienTai.layVaiTro().name())
                        .putBoolean(KEY_LEGACY_AUTH_MIGRATED, true)
                        .apply();
                return;
            }
            boNhoChiaSe.edit().remove(KEY_CURRENT_USER_ROLE).apply();
        }

        long idNguoiDungAnhXa = -1;

        if (!TextUtils.isEmpty(emailCu) && !TextUtils.isEmpty(matKhauCu)) {
            Log.i(TAG, "Tìm hoặc tạo người dùng tương ứng cho dữ liệu đăng nhập cũ. email=" + emailCu);
            NguoiDung nguoiDungDaTonTai = databaseHelper.getUserByEmail(emailCu);
            if (nguoiDungDaTonTai != null) {
                idNguoiDungAnhXa = nguoiDungDaTonTai.layId();
            } else {
                long idMoi = databaseHelper.insertUser(
                        ungDungContext.getString(R.string.account_default_name),
                        emailCu,
                        ungDungContext.getString(R.string.account_default_phone),
                        matKhauCu
                );
                if (idMoi > 0) {
                    idNguoiDungAnhXa = idMoi;
                } else {
                    NguoiDung nguoiDungDuPhong = databaseHelper.getUserByEmail(emailCu);
                    if (nguoiDungDuPhong != null) {
                        idNguoiDungAnhXa = nguoiDungDuPhong.layId();
                    }
                }
            }
        }

        SharedPreferences.Editor trinhSua = boNhoChiaSe.edit();
        if (daDangNhapCu && idNguoiDungAnhXa > 0) {
            Log.i(TAG, "Migration dữ liệu đăng nhập cũ thành công. userId=" + idNguoiDungAnhXa);
            trinhSua.putBoolean(KEY_IS_LOGGED_IN, true);
            trinhSua.putLong(KEY_CURRENT_USER_ID, idNguoiDungAnhXa);
            trinhSua.putString(KEY_CURRENT_USER_ROLE, VaiTroNguoiDung.KHACH_HANG.name());
        } else {
            Log.i(TAG, "Không thể khôi phục phiên đăng nhập cũ, đánh dấu chưa đăng nhập.");
            trinhSua.putBoolean(KEY_IS_LOGGED_IN, false);
            trinhSua.remove(KEY_CURRENT_USER_ID);
            trinhSua.remove(KEY_CURRENT_USER_ROLE);
        }
        trinhSua.putBoolean(KEY_LEGACY_AUTH_MIGRATED, true);
        trinhSua.apply();
    }

    public boolean daDangNhap() {
        return boNhoChiaSe.getBoolean(KEY_IS_LOGGED_IN, false) && layIdNguoiDungHienTai() > 0;
    }

    public long layIdNguoiDungHienTai() {
        return boNhoChiaSe.getLong(KEY_CURRENT_USER_ID, -1);
    }

    @Nullable
    public VaiTroNguoiDung layVaiTroSessionHopLe() {
        return VaiTroNguoiDung.tuChuoiNghiemNhat(boNhoChiaSe.getString(KEY_CURRENT_USER_ROLE, null));
    }

    public void damBaoVaiTroSession(DatabaseHelper databaseHelper) {
        if (!daDangNhap()) {
            return;
        }

        NguoiDung nguoiDungHienTai = databaseHelper.getUserById(layIdNguoiDungHienTai());
        if (nguoiDungHienTai == null || !nguoiDungHienTai.dangHoatDong()) {
            xoaPhienDangNhap();
            return;
        }

        VaiTroNguoiDung vaiTroPhien = layVaiTroSessionHopLe();
        VaiTroNguoiDung vaiTroTuDb = nguoiDungHienTai.layVaiTro();
        if (vaiTroPhien != vaiTroTuDb) {
            boNhoChiaSe.edit()
                    .putString(KEY_CURRENT_USER_ROLE, vaiTroTuDb.name())
                    .apply();
        }
    }

    public boolean damBaoNguoiDungConHoatDong(DatabaseHelper databaseHelper) {
        if (!daDangNhap()) {
            return false;
        }

        NguoiDung nguoiDungHienTai = databaseHelper.getUserById(layIdNguoiDungHienTai());
        if (nguoiDungHienTai == null || !nguoiDungHienTai.dangHoatDong()) {
            xoaPhienDangNhap();
            return false;
        }

        if (layVaiTroSessionHopLe() != nguoiDungHienTai.layVaiTro()) {
            boNhoChiaSe.edit()
                    .putString(KEY_CURRENT_USER_ROLE, nguoiDungHienTai.layVaiTro().name())
                    .apply();
        }
        return true;
    }

    public boolean laKhachHang() {
        return layVaiTroSessionHopLe() == VaiTroNguoiDung.KHACH_HANG;
    }

    public boolean laNhanVien() {
        return layVaiTroSessionHopLe() == VaiTroNguoiDung.NHAN_VIEN;
    }

    public boolean laAdmin() {
        return layVaiTroSessionHopLe() == VaiTroNguoiDung.ADMIN;
    }

    public void luuPhienDangNhap(long idNguoiDung, VaiTroNguoiDung vaiTro) {
        boNhoChiaSe.edit()
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .putLong(KEY_CURRENT_USER_ID, idNguoiDung)
                .putString(KEY_CURRENT_USER_ROLE, vaiTro != null ? vaiTro.name() : VaiTroNguoiDung.KHACH_HANG.name())
                .apply();
    }

    public void xoaPhienDangNhap() {
        boNhoChiaSe.edit()
                .putBoolean(KEY_IS_LOGGED_IN, false)
                .remove(KEY_CURRENT_USER_ID)
                .remove(KEY_CURRENT_USER_ROLE)
                .remove(KEY_CURRENT_TABLE)
                .apply();
    }

    public void luuBanHienTai(@Nullable String soBan) {
        boNhoChiaSe.edit()
                .putString(KEY_CURRENT_TABLE, soBan == null ? "" : soBan.trim())
                .apply();
    }

    public String layBanHienTai() {
        return boNhoChiaSe.getString(KEY_CURRENT_TABLE, "");
    }

    public boolean coBanHienTai() {
        return !TextUtils.isEmpty(layBanHienTai());
    }

    public void xoaBanHienTai() {
        boNhoChiaSe.edit().remove(KEY_CURRENT_TABLE).apply();
    }

}
