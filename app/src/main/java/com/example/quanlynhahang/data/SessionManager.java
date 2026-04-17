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
    private static final String KEY_INTERNAL_ROLE = "internal_role";

    private static final String LEGACY_KEY_REGISTERED_EMAIL = "registered_email";
    private static final String LEGACY_KEY_REGISTERED_PASSWORD = "registered_password";

    private final Context appContext;
    private final SharedPreferences sharedPreferences;

    public SessionManager(Context context) {
        appContext = context.getApplicationContext();
        sharedPreferences = appContext.getSharedPreferences(PREFS_AUTH, Context.MODE_PRIVATE);
    }

    public void migrateLegacyAuthIfNeeded(DatabaseHelper databaseHelper) {
        Log.d(TAG, "Kiểm tra migration dữ liệu đăng nhập cũ.");
        if (sharedPreferences.getBoolean(KEY_LEGACY_AUTH_MIGRATED, false)) {
            Log.d(TAG, "Migration dữ liệu đăng nhập cũ đã chạy trước đó, bỏ qua.");
            return;
        }

        String legacyEmail = sharedPreferences.getString(LEGACY_KEY_REGISTERED_EMAIL, "");
        String legacyPassword = sharedPreferences.getString(LEGACY_KEY_REGISTERED_PASSWORD, "");
        boolean legacyLoggedIn = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);

        long currentSessionUserId = sharedPreferences.getLong(KEY_CURRENT_USER_ID, -1);
        if (currentSessionUserId > 0) {
            NguoiDung currentUser = databaseHelper.getUserById(currentSessionUserId);
            if (currentUser != null) {
                Log.i(TAG, "Giữ nguyên phiên đăng nhập hiện tại vì người dùng đã tồn tại trong cơ sở dữ liệu.");
                sharedPreferences.edit()
                        .putBoolean(KEY_IS_LOGGED_IN, legacyLoggedIn)
                        .putString(KEY_CURRENT_USER_ROLE, currentUser.layVaiTro().name())
                        .putBoolean(KEY_LEGACY_AUTH_MIGRATED, true)
                        .apply();
                return;
            }
            sharedPreferences.edit().remove(KEY_CURRENT_USER_ROLE).apply();
        }

        long mappedUserId = -1;

        if (!TextUtils.isEmpty(legacyEmail) && !TextUtils.isEmpty(legacyPassword)) {
            Log.i(TAG, "Tìm hoặc tạo người dùng tương ứng cho dữ liệu đăng nhập cũ. email=" + legacyEmail);
            NguoiDung existingUser = databaseHelper.getUserByEmail(legacyEmail);
            if (existingUser != null) {
                mappedUserId = existingUser.layId();
            } else {
                long insertedId = databaseHelper.insertUser(
                        appContext.getString(R.string.account_default_name),
                        legacyEmail,
                        appContext.getString(R.string.account_default_phone),
                        legacyPassword
                );
                if (insertedId > 0) {
                    mappedUserId = insertedId;
                } else {
                    NguoiDung fallbackUser = databaseHelper.getUserByEmail(legacyEmail);
                    if (fallbackUser != null) {
                        mappedUserId = fallbackUser.layId();
                    }
                }
            }
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (legacyLoggedIn && mappedUserId > 0) {
            Log.i(TAG, "Migration dữ liệu đăng nhập cũ thành công. userId=" + mappedUserId);
            editor.putBoolean(KEY_IS_LOGGED_IN, true);
            editor.putLong(KEY_CURRENT_USER_ID, mappedUserId);
            editor.putString(KEY_CURRENT_USER_ROLE, VaiTroNguoiDung.KHACH_HANG.name());
        } else {
            Log.i(TAG, "Không thể khôi phục phiên đăng nhập cũ, đánh dấu chưa đăng nhập.");
            editor.putBoolean(KEY_IS_LOGGED_IN, false);
            editor.remove(KEY_CURRENT_USER_ID);
            editor.remove(KEY_CURRENT_USER_ROLE);
        }
        editor.putBoolean(KEY_LEGACY_AUTH_MIGRATED, true);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false) && getCurrentUserId() > 0;
    }

    public boolean daDangNhap() {
        return isLoggedIn();
    }

    public long getCurrentUserId() {
        return sharedPreferences.getLong(KEY_CURRENT_USER_ID, -1);
    }

    public long layIdNguoiDungHienTai() {
        return getCurrentUserId();
    }

    public VaiTroNguoiDung getVaiTroHienTai() {
        String roleValue = sharedPreferences.getString(KEY_CURRENT_USER_ROLE, null);
        if (!TextUtils.isEmpty(roleValue)) {
            return VaiTroNguoiDung.tuChuoi(roleValue);
        }
        return VaiTroNguoiDung.KHACH_HANG;
    }

    public VaiTroNguoiDung layVaiTroHienTai() {
        return getVaiTroHienTai();
    }

    public void damBaoVaiTroSession(DatabaseHelper databaseHelper) {
        if (!isLoggedIn()) {
            return;
        }
        if (!TextUtils.isEmpty(sharedPreferences.getString(KEY_CURRENT_USER_ROLE, null))) {
            return;
        }

        NguoiDung currentUser = databaseHelper.getUserById(getCurrentUserId());
        if (currentUser != null) {
            sharedPreferences.edit()
                    .putString(KEY_CURRENT_USER_ROLE, currentUser.layVaiTro().name())
                    .apply();
            return;
        }

        sharedPreferences.edit()
                .putString(KEY_CURRENT_USER_ROLE, VaiTroNguoiDung.KHACH_HANG.name())
                .apply();
    }

    public boolean damBaoNguoiDungConHoatDong(DatabaseHelper databaseHelper) {
        if (!isLoggedIn()) {
            return false;
        }

        NguoiDung currentUser = databaseHelper.getUserById(getCurrentUserId());
        if (currentUser == null || !currentUser.dangHoatDong()) {
            xoaPhienDangNhap();
            xoaVaiTroNoiBo();
            return false;
        }

        if (layVaiTroHienTai() != currentUser.layVaiTro()) {
            sharedPreferences.edit()
                    .putString(KEY_CURRENT_USER_ROLE, currentUser.layVaiTro().name())
                    .apply();
        }
        return true;
    }

    public boolean laKhachHang() {
        return layVaiTroHienTai() == VaiTroNguoiDung.KHACH_HANG;
    }

    public boolean laNhanVien() {
        return layVaiTroHienTai() == VaiTroNguoiDung.NHAN_VIEN;
    }

    public boolean laAdmin() {
        return layVaiTroHienTai() == VaiTroNguoiDung.ADMIN;
    }

    public void saveLoginSession(long userId, VaiTroNguoiDung vaiTro) {
        sharedPreferences.edit()
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .putLong(KEY_CURRENT_USER_ID, userId)
                .putString(KEY_CURRENT_USER_ROLE, vaiTro != null ? vaiTro.name() : VaiTroNguoiDung.KHACH_HANG.name())
                .apply();
    }

    public void luuPhienDangNhap(long userId, VaiTroNguoiDung vaiTro) {
        saveLoginSession(userId, vaiTro);
    }

    public void clearSession() {
        sharedPreferences.edit()
                .putBoolean(KEY_IS_LOGGED_IN, false)
                .remove(KEY_CURRENT_USER_ID)
                .remove(KEY_CURRENT_USER_ROLE)
                .remove(KEY_CURRENT_TABLE)
                .apply();
    }

    public void xoaPhienDangNhap() {
        clearSession();
    }

    public void xoaVaiTroNoiBo() {
        sharedPreferences.edit()
                .remove(KEY_INTERNAL_ROLE)
                .apply();
    }

    public void luuBanHienTai(@Nullable String soBan) {
        sharedPreferences.edit()
                .putString(KEY_CURRENT_TABLE, soBan == null ? "" : soBan.trim())
                .apply();
    }

    public String layBanHienTai() {
        return sharedPreferences.getString(KEY_CURRENT_TABLE, "");
    }

    public boolean coBanHienTai() {
        return !TextUtils.isEmpty(layBanHienTai());
    }

    public void xoaBanHienTai() {
        sharedPreferences.edit().remove(KEY_CURRENT_TABLE).apply();
    }

    public void luuVaiTroNoiBo(@Nullable VaiTroNguoiDung vaiTro) {
        if (vaiTro == null || (vaiTro != VaiTroNguoiDung.NHAN_VIEN && vaiTro != VaiTroNguoiDung.ADMIN)) {
            xoaVaiTroNoiBo();
            return;
        }

        sharedPreferences.edit()
                .putString(KEY_INTERNAL_ROLE, vaiTro.name())
                .apply();
    }

    @Nullable
    public VaiTroNguoiDung layVaiTroNoiBo() {
        String roleValue = sharedPreferences.getString(KEY_INTERNAL_ROLE, null);
        if (TextUtils.isEmpty(roleValue)) {
            return null;
        }
        VaiTroNguoiDung vaiTro = VaiTroNguoiDung.tuChuoi(roleValue);
        if (vaiTro == VaiTroNguoiDung.NHAN_VIEN || vaiTro == VaiTroNguoiDung.ADMIN) {
            return vaiTro;
        }
        return null;
    }
}
