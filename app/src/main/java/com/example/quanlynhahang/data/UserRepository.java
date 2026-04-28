package com.example.quanlynhahang.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.quanlynhahang.helper.PasswordHelper;
import com.example.quanlynhahang.model.NguoiDung;
import com.example.quanlynhahang.model.VaiTroNguoiDung;

import java.util.ArrayList;
import java.util.List;

final class UserRepository {

    private static final String TAG = "UserRepository";

    private final DatabaseHelper databaseHelper;

    UserRepository(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    long insertUser(String name,
                    String email,
                    String phone,
                    String password,
                    @Nullable VaiTroNguoiDung role,
                    boolean isActive) {
        if (TextUtils.isEmpty(name)
                || TextUtils.isEmpty(email)
                || TextUtils.isEmpty(phone)
                || TextUtils.isEmpty(password)) {
            return -1;
        }
        return insertUser(databaseHelper.getWritableDatabase(), name, email, phone, password, role, isActive);
    }

    long insertUser(SQLiteDatabase db,
                    String name,
                    String email,
                    String phone,
                    String password,
                    @Nullable VaiTroNguoiDung role,
                    boolean isActive) {
        if (isPhoneInUse(db, phone, -1)) {
            return -1;
        }

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_USER_NAME, name);
        values.put(DatabaseHelper.COL_USER_EMAIL, email);
        values.put(DatabaseHelper.COL_USER_PHONE, phone);
        values.put(DatabaseHelper.COL_USER_PASSWORD, PasswordHelper.hashPassword(password));
        values.put(DatabaseHelper.COL_USER_ROLE, role != null ? role.name() : VaiTroNguoiDung.KHACH_HANG.name());
        values.put(DatabaseHelper.COL_USER_IS_ACTIVE, isActive ? 1 : 0);

        try {
            return db.insertOrThrow(DatabaseHelper.TABLE_USER, null, values);
        } catch (SQLiteConstraintException ex) {
            Log.w(TAG, "insertUser: thông tin định danh đã tồn tại hoặc vi phạm ràng buộc.", ex);
            return -1;
        } catch (SQLiteException ex) {
            Log.e(TAG, "insertUser: lỗi khi thêm người dùng.", ex);
            throw ex;
        }
    }

    @Nullable
    NguoiDung getUserByEmail(String email) {
        Cursor cursor = null;
        try {
            cursor = databaseHelper.getReadableDatabase().query(
                    DatabaseHelper.TABLE_USER,
                    new String[]{
                            DatabaseHelper.COL_USER_ID,
                            DatabaseHelper.COL_USER_NAME,
                            DatabaseHelper.COL_USER_EMAIL,
                            DatabaseHelper.COL_USER_PHONE,
                            DatabaseHelper.COL_USER_ROLE,
                            DatabaseHelper.COL_USER_IS_ACTIVE
                    },
                    DatabaseHelper.COL_USER_EMAIL + " = ?",
                    new String[]{email},
                    null,
                    null,
                    null,
                    "1"
            );
            if (cursor.moveToFirst()) {
                return mapUser(cursor);
            }
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Nullable
    NguoiDung getUserById(long userId) {
        Cursor cursor = null;
        try {
            cursor = databaseHelper.getReadableDatabase().query(
                    DatabaseHelper.TABLE_USER,
                    new String[]{
                            DatabaseHelper.COL_USER_ID,
                            DatabaseHelper.COL_USER_NAME,
                            DatabaseHelper.COL_USER_EMAIL,
                            DatabaseHelper.COL_USER_PHONE,
                            DatabaseHelper.COL_USER_ROLE,
                            DatabaseHelper.COL_USER_IS_ACTIVE
                    },
                    DatabaseHelper.COL_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)},
                    null,
                    null,
                    null,
                    "1"
            );
            if (cursor.moveToFirst()) {
                return mapUser(cursor);
            }
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Nullable
    NguoiDung getUserByPhone(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return null;
        }

        Cursor cursor = null;
        try {
            cursor = databaseHelper.getReadableDatabase().query(
                    DatabaseHelper.TABLE_USER,
                    new String[]{
                            DatabaseHelper.COL_USER_ID,
                            DatabaseHelper.COL_USER_NAME,
                            DatabaseHelper.COL_USER_EMAIL,
                            DatabaseHelper.COL_USER_PHONE,
                            DatabaseHelper.COL_USER_ROLE,
                            DatabaseHelper.COL_USER_IS_ACTIVE
                    },
                    DatabaseHelper.COL_USER_PHONE + " = ?",
                    new String[]{phone},
                    null,
                    null,
                    null,
                    "1"
            );
            if (cursor.moveToFirst()) {
                return mapUser(cursor);
            }
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    boolean isPhoneInUse(String phone, long excludeUserId) {
        return isPhoneInUse(databaseHelper.getReadableDatabase(), phone, excludeUserId);
    }

    boolean updateUserProfile(long userId, String name, String phone) {
        if (isPhoneInUse(phone, userId)) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_USER_NAME, name);
        values.put(DatabaseHelper.COL_USER_PHONE, phone);
        int rows = databaseHelper.getWritableDatabase().update(
                DatabaseHelper.TABLE_USER,
                values,
                DatabaseHelper.COL_USER_ID + " = ?",
                new String[]{String.valueOf(userId)}
        );
        return rows > 0;
    }

    boolean updateUserPassword(long userId, String newPassword) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_USER_PASSWORD, PasswordHelper.hashPassword(newPassword));
        int rows = databaseHelper.getWritableDatabase().update(
                DatabaseHelper.TABLE_USER,
                values,
                DatabaseHelper.COL_USER_ID + " = ?",
                new String[]{String.valueOf(userId)}
        );
        return rows > 0;
    }

    List<NguoiDung> getUsersByRole(@Nullable VaiTroNguoiDung role) {
        List<NguoiDung> users = new ArrayList<>();
        Cursor cursor = null;
        try {
            String selection = null;
            String[] selectionArgs = null;
            if (role != null) {
                selection = DatabaseHelper.COL_USER_ROLE + " = ?";
                selectionArgs = new String[]{role.name()};
            }

            cursor = databaseHelper.getReadableDatabase().query(
                    DatabaseHelper.TABLE_USER,
                    new String[]{
                            DatabaseHelper.COL_USER_ID,
                            DatabaseHelper.COL_USER_NAME,
                            DatabaseHelper.COL_USER_EMAIL,
                            DatabaseHelper.COL_USER_PHONE,
                            DatabaseHelper.COL_USER_ROLE,
                            DatabaseHelper.COL_USER_IS_ACTIVE
                    },
                    selection,
                    selectionArgs,
                    null,
                    null,
                    DatabaseHelper.COL_USER_ROLE + " ASC, "
                            + DatabaseHelper.COL_USER_NAME + " COLLATE NOCASE ASC, "
                            + DatabaseHelper.COL_USER_ID + " ASC"
            );

            while (cursor.moveToNext()) {
                users.add(mapUser(cursor));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return users;
    }

    boolean updateVaiTroNguoiDung(long userId, @Nullable VaiTroNguoiDung role) {
        if (userId <= 0 || role == null) {
            return false;
        }
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_USER_ROLE, role.name());
        int rows = databaseHelper.getWritableDatabase().update(
                DatabaseHelper.TABLE_USER,
                values,
                DatabaseHelper.COL_USER_ID + " = ?",
                new String[]{String.valueOf(userId)}
        );
        return rows > 0;
    }

    boolean updateUserActive(long userId, boolean isActive) {
        if (userId <= 0) {
            return false;
        }
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_USER_IS_ACTIVE, isActive ? 1 : 0);
        int rows = databaseHelper.getWritableDatabase().update(
                DatabaseHelper.TABLE_USER,
                values,
                DatabaseHelper.COL_USER_ID + " = ?",
                new String[]{String.valueOf(userId)}
        );
        return rows > 0;
    }

    boolean deleteUser(long userId) {
        if (userId <= 0) {
            return false;
        }
        int rows = databaseHelper.getWritableDatabase().delete(
                DatabaseHelper.TABLE_USER,
                DatabaseHelper.COL_USER_ID + " = ?",
                new String[]{String.valueOf(userId)}
        );
        return rows > 0;
    }

    int countAllUsers() {
        return demSoBanGhi(null, null);
    }

    int countUsersByRole(@Nullable VaiTroNguoiDung role) {
        if (role == null) {
            return countAllUsers();
        }
        return demSoBanGhi(DatabaseHelper.COL_USER_ROLE + " = ?", new String[]{role.name()});
    }

    @Nullable
    NguoiDung checkLogin(String usernameOrEmail, String password) {
        Cursor cursor = null;
        try {
            cursor = databaseHelper.getReadableDatabase().query(
                    DatabaseHelper.TABLE_USER,
                    new String[]{
                            DatabaseHelper.COL_USER_ID,
                            DatabaseHelper.COL_USER_NAME,
                            DatabaseHelper.COL_USER_EMAIL,
                            DatabaseHelper.COL_USER_PHONE,
                            DatabaseHelper.COL_USER_ROLE,
                            DatabaseHelper.COL_USER_IS_ACTIVE,
                            DatabaseHelper.COL_USER_PASSWORD
                    },
                    "(" + DatabaseHelper.COL_USER_EMAIL + " = ? OR " + DatabaseHelper.COL_USER_PHONE + " = ?) AND "
                            + DatabaseHelper.COL_USER_IS_ACTIVE + " = 1",
                    new String[]{usernameOrEmail, usernameOrEmail},
                    null,
                    null,
                    null,
                    "1"
            );
            if (!cursor.moveToFirst()) {
                return null;
            }

            String storedPassword = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_PASSWORD));
            if (!PasswordHelper.verifyPassword(password, storedPassword)) {
                return null;
            }

            NguoiDung user = mapUser(cursor);
            if (user != null && !PasswordHelper.isHashedPassword(storedPassword)) {
                migrateLegacyPasswordHash(user.layId(), password);
            }
            return user;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private int demSoBanGhi(@Nullable String selection, @Nullable String[] selectionArgs) {
        Cursor cursor = null;
        try {
            cursor = databaseHelper.getReadableDatabase().query(
                    DatabaseHelper.TABLE_USER,
                    new String[]{"COUNT(*)"},
                    selection,
                    selectionArgs,
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

    private boolean isPhoneInUse(SQLiteDatabase db, String phone, long excludeUserId) {
        if (TextUtils.isEmpty(phone)) {
            return false;
        }

        Cursor cursor = null;
        try {
            String selection = DatabaseHelper.COL_USER_PHONE + " = ?";
            List<String> selectionArgs = new ArrayList<>();
            selectionArgs.add(phone);
            if (excludeUserId > 0) {
                selection += " AND " + DatabaseHelper.COL_USER_ID + " != ?";
                selectionArgs.add(String.valueOf(excludeUserId));
            }

            cursor = db.query(
                    DatabaseHelper.TABLE_USER,
                    new String[]{DatabaseHelper.COL_USER_ID},
                    selection,
                    selectionArgs.toArray(new String[0]),
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

    private void migrateLegacyPasswordHash(long userId, String rawPassword) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_USER_PASSWORD, PasswordHelper.hashPassword(rawPassword));
        databaseHelper.getWritableDatabase().update(
                DatabaseHelper.TABLE_USER,
                values,
                DatabaseHelper.COL_USER_ID + " = ?",
                new String[]{String.valueOf(userId)}
        );
    }

    @Nullable
    private NguoiDung mapUser(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_NAME));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_EMAIL));
        String phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_PHONE));
        String roleValue = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ROLE));
        boolean isActive = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_IS_ACTIVE)) == 1;
        return new NguoiDung(id, name, email, phone, VaiTroNguoiDung.tuChuoi(roleValue), isActive);
    }
}
