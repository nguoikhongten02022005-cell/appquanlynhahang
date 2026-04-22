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
    private static final String PREFS_LEGACY_AUTH = "auth_prefs";
    private static final String PREFS_INTERNAL_SESSION = "internal_session_prefs";
    private static final String PREFS_CUSTOMER_SESSION = "customer_session_prefs";

    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_CURRENT_USER_ID = "current_user_id";
    private static final String KEY_CURRENT_USER_ROLE = "current_user_role";
    private static final String KEY_CURRENT_TABLE = "current_table";
    private static final String KEY_LAST_ROUTE = "last_route";
    private static final String KEY_PREVIEW_SOURCE_ROUTE = "preview_source_route";
    private static final String KEY_LEGACY_AUTH_MIGRATED = "legacy_auth_migrated";

    private static final String LEGACY_KEY_REGISTERED_EMAIL = "registered_email";
    private static final String LEGACY_KEY_REGISTERED_PASSWORD = "registered_password";
    private static final String TIEN_TO_PHIEN_KHACH = "customer:";
    private static final String KHOA_PHIEN_KHACH_VANG_LAI = TIEN_TO_PHIEN_KHACH + "guest";

    private final Context ungDungContext;
    private final SharedPreferences boNhoCu;
    private final SharedPreferences boNhoNoiBo;
    private final SharedPreferences boNhoKhachHang;

    public SessionManager(Context context) {
        ungDungContext = context.getApplicationContext();
        boNhoCu = ungDungContext.getSharedPreferences(PREFS_LEGACY_AUTH, Context.MODE_PRIVATE);
        boNhoNoiBo = ungDungContext.getSharedPreferences(PREFS_INTERNAL_SESSION, Context.MODE_PRIVATE);
        boNhoKhachHang = ungDungContext.getSharedPreferences(PREFS_CUSTOMER_SESSION, Context.MODE_PRIVATE);
    }

    public void chuyenDuLieuDangNhapCuNeuCan(DatabaseHelper databaseHelper) {
        Log.d(TAG, "Kiểm tra migration dữ liệu đăng nhập cũ.");
        if (boNhoCu.getBoolean(KEY_LEGACY_AUTH_MIGRATED, false)) {
            Log.d(TAG, "Migration dữ liệu đăng nhập cũ đã chạy trước đó, bỏ qua.");
            return;
        }

        String emailCu = boNhoCu.getString(LEGACY_KEY_REGISTERED_EMAIL, "");
        String matKhauCu = boNhoCu.getString(LEGACY_KEY_REGISTERED_PASSWORD, "");
        boolean daDangNhapCu = boNhoCu.getBoolean(KEY_IS_LOGGED_IN, false);

        long idNguoiDungPhienHienTai = boNhoCu.getLong(KEY_CURRENT_USER_ID, -1);
        if (idNguoiDungPhienHienTai > 0) {
            NguoiDung nguoiDungHienTai = databaseHelper.getUserById(idNguoiDungPhienHienTai);
            if (nguoiDungHienTai != null) {
                Log.i(TAG, "Giữ nguyên phiên đăng nhập hiện tại vì người dùng đã tồn tại trong cơ sở dữ liệu.");
                luuPhienDangNhap(idNguoiDungPhienHienTai, nguoiDungHienTai.layVaiTro());
                danhDauDaMigrationCu();
                return;
            }
            boNhoCu.edit().remove(KEY_CURRENT_USER_ROLE).apply();
        }

        long idNguoiDungAnhXa = timHoacTaoNguoiDungTuDuLieuCu(databaseHelper, emailCu, matKhauCu);
        if (daDangNhapCu && idNguoiDungAnhXa > 0) {
            Log.i(TAG, "Migration dữ liệu đăng nhập cũ thành công. userId=" + idNguoiDungAnhXa);
            luuPhienKhachHang(idNguoiDungAnhXa);
        } else {
            Log.i(TAG, "Không thể khôi phục phiên đăng nhập cũ, xóa phiên mới.");
            xoaPhienDangNhap();
        }
        danhDauDaMigrationCu();
    }

    public boolean daDangNhap() {
        return daDangNhapNoiBo() || daDangNhapKhachHang();
    }

    public boolean daDangNhapNoiBo() {
        return coPhienHopLe(boNhoNoiBo, layIdNguoiDungNoiBo());
    }

    public boolean daDangNhapKhachHang() {
        return coPhienHopLe(boNhoKhachHang, layIdKhachHangHienTai());
    }

    public long layIdNguoiDungNoiBo() {
        return boNhoNoiBo.getLong(KEY_CURRENT_USER_ID, -1);
    }

    public long layIdKhachHangHienTai() {
        return boNhoKhachHang.getLong(KEY_CURRENT_USER_ID, -1);
    }

    public long layIdNguoiDungHienTai() {
        if (daDangNhapNoiBo()) {
            return layIdNguoiDungNoiBo();
        }
        if (daDangNhapKhachHang()) {
            return layIdKhachHangHienTai();
        }
        return -1;
    }

    @Nullable
    public VaiTroNguoiDung layVaiTroNoiBoHopLe() {
        return VaiTroNguoiDung.tuChuoiNghiemNhat(boNhoNoiBo.getString(KEY_CURRENT_USER_ROLE, null));
    }

    @Nullable
    public VaiTroNguoiDung layVaiTroSessionHopLe() {
        if (daDangNhapNoiBo()) {
            return layVaiTroNoiBoHopLe();
        }
        if (daDangNhapKhachHang()) {
            return VaiTroNguoiDung.KHACH_HANG;
        }
        return null;
    }

    public void damBaoVaiTroSession(DatabaseHelper databaseHelper) {
        if (!daDangNhap()) {
            return;
        }
        if (daDangNhapNoiBo()) {
            damBaoPhienNoiBoHopLe(databaseHelper);
            return;
        }
        if (daDangNhapKhachHang()) {
            damBaoPhienKhachHangHopLe(databaseHelper);
        }
    }

    public boolean damBaoNguoiDungConHoatDong(DatabaseHelper databaseHelper) {
        if (!daDangNhap()) {
            return false;
        }
        if (daDangNhapNoiBo()) {
            return damBaoPhienNoiBoHopLe(databaseHelper);
        }
        if (!daDangNhapKhachHang()) {
            return false;
        }
        return damBaoPhienKhachHangHopLe(databaseHelper);
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

    public void luuPhienNoiBo(long idNguoiDung, VaiTroNguoiDung vaiTro) {
        VaiTroNguoiDung vaiTroHopLe = vaiTro != null ? vaiTro : VaiTroNguoiDung.NHAN_VIEN;
        boNhoNoiBo.edit()
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .putLong(KEY_CURRENT_USER_ID, idNguoiDung)
                .putString(KEY_CURRENT_USER_ROLE, vaiTroHopLe.name())
                .apply();
    }

    public void luuPhienKhachHang(long idKhachHang) {
        boNhoKhachHang.edit()
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .putLong(KEY_CURRENT_USER_ID, idKhachHang)
                .apply();
    }

    public void luuPhienDangNhap(long idNguoiDung, VaiTroNguoiDung vaiTro) {
        VaiTroNguoiDung vaiTroHopLe = vaiTro != null ? vaiTro : VaiTroNguoiDung.KHACH_HANG;
        if (laVaiTroNoiBo(vaiTroHopLe)) {
            luuPhienNoiBo(idNguoiDung, vaiTroHopLe);
            return;
        }
        luuPhienKhachHang(idNguoiDung);
    }

    public void luuDuongDanNoiBoCuoi(@Nullable String duongDan) {
        boNhoNoiBo.edit()
                .putString(KEY_LAST_ROUTE, duongDan == null ? "" : duongDan.trim())
                .apply();
    }

    public void luuDuongDanKhachHangCuoi(@Nullable String duongDan) {
        boNhoKhachHang.edit()
                .putString(KEY_LAST_ROUTE, duongDan == null ? "" : duongDan.trim())
                .apply();
    }

    public String layDuongDanNoiBoCuoi() {
        return boNhoNoiBo.getString(KEY_LAST_ROUTE, "");
    }

    public String layDuongDanKhachHangCuoi() {
        return boNhoKhachHang.getString(KEY_LAST_ROUTE, "");
    }

    public void luuNguonPreviewKhachHang(@Nullable String duongDan) {
        boNhoKhachHang.edit()
                .putString(KEY_PREVIEW_SOURCE_ROUTE, duongDan == null ? "" : duongDan.trim())
                .apply();
    }

    public String layNguonPreviewKhachHang() {
        return boNhoKhachHang.getString(KEY_PREVIEW_SOURCE_ROUTE, "");
    }

    public String layKhoaPhienKhachHang() {
        long idKhachHang = layIdKhachHangHienTai();
        if (daDangNhapKhachHang() && idKhachHang > 0) {
            return TIEN_TO_PHIEN_KHACH + idKhachHang;
        }
        return KHOA_PHIEN_KHACH_VANG_LAI;
    }

    public void xoaPhienNoiBo() {
        boNhoNoiBo.edit()
                .putBoolean(KEY_IS_LOGGED_IN, false)
                .remove(KEY_CURRENT_USER_ID)
                .remove(KEY_CURRENT_USER_ROLE)
                .remove(KEY_LAST_ROUTE)
                .apply();
    }

    public void xoaPhienKhachHang() {
        boNhoKhachHang.edit()
                .putBoolean(KEY_IS_LOGGED_IN, false)
                .remove(KEY_CURRENT_USER_ID)
                .remove(KEY_CURRENT_TABLE)
                .remove(KEY_LAST_ROUTE)
                .remove(KEY_PREVIEW_SOURCE_ROUTE)
                .apply();
    }

    public void xoaPhienDangNhap() {
        xoaPhienNoiBo();
        xoaPhienKhachHang();
    }

    public void luuBanHienTai(@Nullable String soBan) {
        boNhoKhachHang.edit()
                .putString(KEY_CURRENT_TABLE, soBan == null ? "" : soBan.trim())
                .apply();
    }

    public String layBanHienTai() {
        return boNhoKhachHang.getString(KEY_CURRENT_TABLE, "");
    }

    public boolean coBanHienTai() {
        return !TextUtils.isEmpty(layBanHienTai());
    }

    public void xoaBanHienTai() {
        boNhoKhachHang.edit().remove(KEY_CURRENT_TABLE).apply();
    }

    private void danhDauDaMigrationCu() {
        boNhoCu.edit().putBoolean(KEY_LEGACY_AUTH_MIGRATED, true).apply();
    }

    private long timHoacTaoNguoiDungTuDuLieuCu(DatabaseHelper databaseHelper,
                                               @Nullable String emailCu,
                                               @Nullable String matKhauCu) {
        if (TextUtils.isEmpty(emailCu) || TextUtils.isEmpty(matKhauCu)) {
            return -1;
        }

        Log.i(TAG, "Tìm hoặc tạo người dùng tương ứng cho dữ liệu đăng nhập cũ. email=" + emailCu);
        NguoiDung nguoiDungDaTonTai = databaseHelper.getUserByEmail(emailCu);
        if (nguoiDungDaTonTai != null) {
            return nguoiDungDaTonTai.layId();
        }

        long idMoi = databaseHelper.insertUser(
                ungDungContext.getString(R.string.account_default_name),
                emailCu,
                ungDungContext.getString(R.string.account_default_phone),
                matKhauCu
        );
        if (idMoi > 0) {
            return idMoi;
        }

        NguoiDung nguoiDungDuPhong = databaseHelper.getUserByEmail(emailCu);
        return nguoiDungDuPhong != null ? nguoiDungDuPhong.layId() : -1;
    }

    private boolean coPhienHopLe(SharedPreferences boNho, long idNguoiDung) {
        return boNho.getBoolean(KEY_IS_LOGGED_IN, false) && idNguoiDung > 0;
    }

    private boolean laVaiTroNoiBo(@Nullable VaiTroNguoiDung vaiTro) {
        return vaiTro == VaiTroNguoiDung.ADMIN || vaiTro == VaiTroNguoiDung.NHAN_VIEN;
    }

    private boolean damBaoPhienNoiBoHopLe(DatabaseHelper databaseHelper) {
        NguoiDung nguoiDungHienTai = databaseHelper.getUserById(layIdNguoiDungNoiBo());
        if (nguoiDungHienTai == null || !nguoiDungHienTai.dangHoatDong()) {
            xoaPhienNoiBo();
            return false;
        }

        VaiTroNguoiDung vaiTroTuDb = nguoiDungHienTai.layVaiTro();
        if (!laVaiTroNoiBo(vaiTroTuDb)) {
            xoaPhienNoiBo();
            return false;
        }
        if (layVaiTroNoiBoHopLe() != vaiTroTuDb) {
            boNhoNoiBo.edit().putString(KEY_CURRENT_USER_ROLE, vaiTroTuDb.name()).apply();
        }
        return true;
    }

    private boolean damBaoPhienKhachHangHopLe(DatabaseHelper databaseHelper) {
        NguoiDung nguoiDungHienTai = databaseHelper.getUserById(layIdKhachHangHienTai());
        if (nguoiDungHienTai == null || !nguoiDungHienTai.dangHoatDong()) {
            xoaPhienKhachHang();
            return false;
        }
        return true;
    }
}
