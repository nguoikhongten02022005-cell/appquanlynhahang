package com.example.quanlynhahang.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.example.quanlynhahang.R;
import com.example.quanlynhahang.model.User;
import com.example.quanlynhahang.model.UserRole;

public class SessionManager {

    private static final String TAG = "SessionManager";
    private static final String PREFS_AUTH = "auth_prefs";

    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_CURRENT_USER_ID = "current_user_id";
    private static final String KEY_CURRENT_USER_ROLE = "current_user_role";
    private static final String KEY_LEGACY_AUTH_MIGRATED = "legacy_auth_migrated";

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
            User currentUser = databaseHelper.getUserById(currentSessionUserId);
            if (currentUser != null) {
                Log.i(TAG, "Giữ nguyên phiên đăng nhập hiện tại vì người dùng đã tồn tại trong cơ sở dữ liệu.");
                sharedPreferences.edit()
                        .putBoolean(KEY_IS_LOGGED_IN, legacyLoggedIn)
                        .putString(KEY_CURRENT_USER_ROLE, currentUser.getRole().name())
                        .putBoolean(KEY_LEGACY_AUTH_MIGRATED, true)
                        .apply();
                return;
            }
            sharedPreferences.edit().remove(KEY_CURRENT_USER_ROLE).apply();
        }

        long mappedUserId = -1;

        if (!TextUtils.isEmpty(legacyEmail) && !TextUtils.isEmpty(legacyPassword)) {
            Log.i(TAG, "Tìm hoặc tạo người dùng tương ứng cho dữ liệu đăng nhập cũ. email=" + legacyEmail);
            User existingUser = databaseHelper.getUserByEmail(legacyEmail);
            if (existingUser != null) {
                mappedUserId = existingUser.getId();
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
                    User fallbackUser = databaseHelper.getUserByEmail(legacyEmail);
                    if (fallbackUser != null) {
                        mappedUserId = fallbackUser.getId();
                    }
                }
            }
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (legacyLoggedIn && mappedUserId > 0) {
            Log.i(TAG, "Migration dữ liệu đăng nhập cũ thành công. userId=" + mappedUserId);
            editor.putBoolean(KEY_IS_LOGGED_IN, true);
            editor.putLong(KEY_CURRENT_USER_ID, mappedUserId);
            editor.putString(KEY_CURRENT_USER_ROLE, UserRole.KHACH_HANG.name());
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

    public long getCurrentUserId() {
        return sharedPreferences.getLong(KEY_CURRENT_USER_ID, -1);
    }

    public UserRole getVaiTroHienTai() {
        String roleValue = sharedPreferences.getString(KEY_CURRENT_USER_ROLE, null);
        if (!TextUtils.isEmpty(roleValue)) {
            return UserRole.tuChuoi(roleValue);
        }
        return UserRole.KHACH_HANG;
    }

    public void damBaoVaiTroSession(DatabaseHelper databaseHelper) {
        if (!isLoggedIn()) {
            return;
        }
        if (!TextUtils.isEmpty(sharedPreferences.getString(KEY_CURRENT_USER_ROLE, null))) {
            return;
        }

        User currentUser = databaseHelper.getUserById(getCurrentUserId());
        if (currentUser != null) {
            sharedPreferences.edit()
                    .putString(KEY_CURRENT_USER_ROLE, currentUser.getRole().name())
                    .apply();
            return;
        }

        sharedPreferences.edit()
                .putString(KEY_CURRENT_USER_ROLE, UserRole.KHACH_HANG.name())
                .apply();
    }

    public boolean laKhachHang() {
        return getVaiTroHienTai() == UserRole.KHACH_HANG;
    }

    public boolean laNhanVien() {
        return getVaiTroHienTai() == UserRole.NHAN_VIEN;
    }

    public boolean laAdmin() {
        return getVaiTroHienTai() == UserRole.ADMIN;
    }

    public void saveLoginSession(long userId, UserRole vaiTro) {
        sharedPreferences.edit()
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .putLong(KEY_CURRENT_USER_ID, userId)
                .putString(KEY_CURRENT_USER_ROLE, vaiTro != null ? vaiTro.name() : UserRole.KHACH_HANG.name())
                .apply();
    }

    public void clearSession() {
        sharedPreferences.edit()
                .putBoolean(KEY_IS_LOGGED_IN, false)
                .remove(KEY_CURRENT_USER_ID)
                .remove(KEY_CURRENT_USER_ROLE)
                .apply();
    }
}
