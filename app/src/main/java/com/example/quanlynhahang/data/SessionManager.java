package com.example.quanlynhahang.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.quanlynhahang.R;
import com.example.quanlynhahang.model.User;

public class SessionManager {

    private static final String PREFS_AUTH = "auth_prefs";

    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_CURRENT_USER_ID = "current_user_id";
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
        if (sharedPreferences.getBoolean(KEY_LEGACY_AUTH_MIGRATED, false)) {
            return;
        }

        String legacyEmail = sharedPreferences.getString(LEGACY_KEY_REGISTERED_EMAIL, "");
        String legacyPassword = sharedPreferences.getString(LEGACY_KEY_REGISTERED_PASSWORD, "");
        boolean legacyLoggedIn = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);

        long currentSessionUserId = sharedPreferences.getLong(KEY_CURRENT_USER_ID, -1);
        if (currentSessionUserId > 0 && databaseHelper.getUserById(currentSessionUserId) != null) {
            sharedPreferences.edit()
                    .putBoolean(KEY_IS_LOGGED_IN, legacyLoggedIn)
                    .putBoolean(KEY_LEGACY_AUTH_MIGRATED, true)
                    .apply();
            return;
        }

        long mappedUserId = -1;

        if (!TextUtils.isEmpty(legacyEmail) && !TextUtils.isEmpty(legacyPassword)) {
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
            editor.putBoolean(KEY_IS_LOGGED_IN, true);
            editor.putLong(KEY_CURRENT_USER_ID, mappedUserId);
        } else {
            editor.putBoolean(KEY_IS_LOGGED_IN, false);
            editor.remove(KEY_CURRENT_USER_ID);
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

    public void saveLoginSession(long userId) {
        sharedPreferences.edit()
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .putLong(KEY_CURRENT_USER_ID, userId)
                .apply();
    }

    public void clearSession() {
        sharedPreferences.edit()
                .putBoolean(KEY_IS_LOGGED_IN, false)
                .remove(KEY_CURRENT_USER_ID)
                .apply();
    }
}
