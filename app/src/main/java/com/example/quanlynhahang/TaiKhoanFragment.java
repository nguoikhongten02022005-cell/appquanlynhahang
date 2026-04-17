package com.example.quanlynhahang;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.helper.DieuHuongVaiTroHelper;
import com.example.quanlynhahang.model.NguoiDung;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TaiKhoanFragment extends Fragment {

    private static final int DO_DAI_MAT_KHAU_TOI_THIEU = 6;
    private static final int DO_DAI_SO_DIEN_THOAI = 10;

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    private NguoiDung nguoiDungHienTai;

    private View layoutAccountLoggedIn;
    private View btnOpenAdmin;
    private View btnOpenEmployee;

    private TextView tvAccountName;
    private TextView tvAccountEmail;
    private TextView tvAccountPhone;

    private View layoutEditProfile;
    private View layoutChangePassword;

    private EditText etEditName;
    private EditText etEditPhone;

    private EditText etCurrentPassword;
    private EditText etNewPassword;
    private EditText etConfirmPassword;

    private final ActivityResultLauncher<Intent> boMoDangNhap = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                capNhatGiaoDienTrangThaiDangNhap();
                lamMoiTrangThaiHeader();
                if (!isAdded() || sessionManager == null || sessionManager.daDangNhap()) {
                    return;
                }
                dieuHuongDenTabTrangChu();
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        databaseHelper = new DatabaseHelper(requireContext());
        sessionManager = new SessionManager(requireContext());
        databaseHelper.chuanBiCoSoDuLieu();
        sessionManager.migrateLegacyAuthIfNeeded(databaseHelper);

        return inflater.inflate(R.layout.fragment_tai_khoan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        khoiTaoView(view);
        thietLapHanhDong(view);
        capNhatGiaoDienTrangThaiDangNhap();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!xacThucPhienKhachHang(false)) {
            return;
        }
        capNhatGiaoDienTrangThaiDangNhap();
    }

    private void khoiTaoView(View view) {
        layoutAccountLoggedIn = view.findViewById(R.id.layoutAccountLoggedIn);
        btnOpenAdmin = view.findViewById(R.id.btnOpenAdmin);
        btnOpenEmployee = view.findViewById(R.id.btnOpenEmployee);

        tvAccountName = view.findViewById(R.id.tvAccountName);
        tvAccountEmail = view.findViewById(R.id.tvAccountEmail);
        tvAccountPhone = view.findViewById(R.id.tvAccountPhone);

        layoutEditProfile = view.findViewById(R.id.layoutEditProfile);
        layoutChangePassword = view.findViewById(R.id.layoutChangePassword);

        etEditName = view.findViewById(R.id.etEditName);
        etEditPhone = view.findViewById(R.id.etEditPhone);

        etCurrentPassword = view.findViewById(R.id.etCurrentPassword);
        etNewPassword = view.findViewById(R.id.etNewPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
    }

    private void thietLapHanhDong(View view) {
        MaterialButton btnEditProfile = view.findViewById(R.id.btnEditProfile);
        MaterialButton btnSaveProfileChanges = view.findViewById(R.id.btnSaveProfileChanges);
        MaterialButton btnCancelEditProfile = view.findViewById(R.id.btnCancelEditProfile);
        MaterialButton btnOpenChangePassword = view.findViewById(R.id.btnOpenChangePassword);
        MaterialButton btnSubmitChangePassword = view.findViewById(R.id.btnSubmitChangePassword);
        MaterialButton btnCancelChangePassword = view.findViewById(R.id.btnCancelChangePassword);
        MaterialButton btnContactSupport = view.findViewById(R.id.btnContactSupport);
        MaterialButton btnMoQuanTri = view.findViewById(R.id.btnOpenAdmin);
        MaterialButton btnMoNhanVien = view.findViewById(R.id.btnOpenEmployee);
        MaterialButton btnLogout = view.findViewById(R.id.btnLogout);

        btnEditProfile.setOnClickListener(v -> hienFormSuaThongTin());
        btnSaveProfileChanges.setOnClickListener(v -> luuThayDoiThongTin());
        btnCancelEditProfile.setOnClickListener(v -> anFormSuaThongTin());
        btnOpenChangePassword.setOnClickListener(v -> hienFormDoiMatKhau());
        btnSubmitChangePassword.setOnClickListener(v -> guiYeuCauDoiMatKhau());
        btnCancelChangePassword.setOnClickListener(v -> anFormDoiMatKhau());

        btnContactSupport.setOnClickListener(v -> moKenhHoTro());
        btnMoQuanTri.setOnClickListener(v -> moManQuanTri());
        btnMoNhanVien.setOnClickListener(v -> moManNhanVien());

        btnLogout.setOnClickListener(v -> hienXacNhanDangXuat());
    }

    private void moManQuanTri() {
        if (!isAdded() || sessionManager == null || !sessionManager.laAdmin()) {
            return;
        }
        Intent intent = DieuHuongVaiTroHelper.taoIntentTheoVaiTro(requireContext(), sessionManager.layVaiTroHienTai());
        startActivity(intent);
    }

    private void moManNhanVien() {
        if (!isAdded() || sessionManager == null || (!sessionManager.laNhanVien() && !sessionManager.laAdmin())) {
            return;
        }
        Intent intent = new Intent(requireContext(), NhanVienActivity.class);
        startActivity(intent);
    }

    private void hienFormSuaThongTin() {
        if (nguoiDungHienTai == null) {
            Toast.makeText(requireContext(), getString(R.string.account_user_not_found), Toast.LENGTH_SHORT).show();
            return;
        }

        layoutEditProfile.setVisibility(View.VISIBLE);
        anFormDoiMatKhau();

        etEditName.setText(nguoiDungHienTai.layTen());
        etEditPhone.setText(nguoiDungHienTai.laySoDienThoai());
    }

    private void anFormSuaThongTin() {
        layoutEditProfile.setVisibility(View.GONE);
        xoaFormSuaThongTin();
    }

    private void luuThayDoiThongTin() {
        if (nguoiDungHienTai == null) {
            Toast.makeText(requireContext(), getString(R.string.account_user_not_found), Toast.LENGTH_SHORT).show();
            return;
        }

        String ten = layTextDaCatKhoangTrang(etEditName);
        String soDienThoai = layTextDaCatKhoangTrang(etEditPhone);

        if (TextUtils.isEmpty(ten) || TextUtils.isEmpty(soDienThoai)) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.account_profile_validation_required),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (!laSoDienThoaiHopLe(soDienThoai)) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.validation_phone_invalid),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (databaseHelper.soDienThoaiDaDuocSuDung(soDienThoai, nguoiDungHienTai.layId())) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.account_phone_in_use),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        boolean daCapNhat = databaseHelper.capNhatThongTinNguoiDung(nguoiDungHienTai.layId(), ten, soDienThoai);
        if (!daCapNhat) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.db_operation_failed),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        NguoiDung nguoiDungDaLamMoi = databaseHelper.layNguoiDungTheoId(nguoiDungHienTai.layId());
        if (nguoiDungDaLamMoi == null) {
            Toast.makeText(requireContext(), getString(R.string.account_user_not_found), Toast.LENGTH_SHORT).show();
            sessionManager.xoaPhienDangNhap();
            capNhatGiaoDienTrangThaiDangNhap();
            lamMoiTrangThaiHeader();
            return;
        }

        nguoiDungHienTai = nguoiDungDaLamMoi;
        ganDuLieuNguoiDung(nguoiDungHienTai);
        anFormSuaThongTin();
        lamMoiTrangThaiHeader();

        Toast.makeText(
                requireContext(),
                getString(R.string.account_profile_update_success),
                Toast.LENGTH_SHORT
        ).show();
    }

    private void hienFormDoiMatKhau() {
        if (nguoiDungHienTai == null) {
            Toast.makeText(requireContext(), getString(R.string.account_user_not_found), Toast.LENGTH_SHORT).show();
            return;
        }
        layoutChangePassword.setVisibility(View.VISIBLE);
        anFormSuaThongTin();
    }

    private void anFormDoiMatKhau() {
        layoutChangePassword.setVisibility(View.GONE);
        xoaFormDoiMatKhau();
    }

    private void guiYeuCauDoiMatKhau() {
        if (nguoiDungHienTai == null) {
            Toast.makeText(requireContext(), getString(R.string.account_user_not_found), Toast.LENGTH_SHORT).show();
            return;
        }

        String matKhauHienTai = layTextDaCatKhoangTrang(etCurrentPassword);
        String matKhauMoi = layTextDaCatKhoangTrang(etNewPassword);
        String xacNhanMatKhau = layTextDaCatKhoangTrang(etConfirmPassword);

        if (TextUtils.isEmpty(matKhauHienTai)
                || TextUtils.isEmpty(matKhauMoi)
                || TextUtils.isEmpty(xacNhanMatKhau)) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.account_password_validation_required),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        NguoiDung nguoiDungKhop = databaseHelper.kiemTraDangNhap(nguoiDungHienTai.layEmail(), matKhauHienTai);
        if (nguoiDungKhop == null) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.account_password_validation_old_wrong),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (TextUtils.equals(matKhauHienTai, matKhauMoi)) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.account_password_validation_same_as_old),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (matKhauMoi.length() < DO_DAI_MAT_KHAU_TOI_THIEU) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.validation_password_too_short, DO_DAI_MAT_KHAU_TOI_THIEU),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (!matKhauMoi.equals(xacNhanMatKhau)) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.account_password_validation_confirm_mismatch),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        boolean daCapNhat = databaseHelper.capNhatMatKhauNguoiDung(nguoiDungHienTai.layId(), matKhauMoi);
        if (!daCapNhat) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.db_operation_failed),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        anFormDoiMatKhau();
        lamMoiTrangThaiHeader();

        Toast.makeText(
                requireContext(),
                getString(R.string.account_password_change_success),
                Toast.LENGTH_SHORT
        ).show();
    }

    private void moKenhHoTro() {
        String phoneNumber = getString(R.string.account_support_phone_number_plain);
        Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        if (phoneIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            startActivity(phoneIntent);
            return;
        }

        Toast.makeText(
                requireContext(),
                getString(R.string.account_support_fallback, getString(R.string.account_support_phone_number_display)),
                Toast.LENGTH_LONG
        ).show();
    }

    private void xoaFormSuaThongTin() {
        etEditName.setText("");
        etEditPhone.setText("");
    }

    private void xoaFormDoiMatKhau() {
        etCurrentPassword.setText("");
        etNewPassword.setText("");
        etConfirmPassword.setText("");
    }

    public void khiTabTaiKhoanDuocChon() {
        if (!xacThucPhienKhachHang(true)) {
            return;
        }
        capNhatGiaoDienTrangThaiDangNhap();
        lamMoiTrangThaiHeader();
        if (sessionManager == null || sessionManager.daDangNhap()) {
            return;
        }
        moDangNhap();
    }

    private void moDangNhap() {
        if (!isAdded()) {
            return;
        }
        Intent intent = new Intent(requireContext(), DangNhapActivity.class);
        intent.putExtra(DangNhapActivity.EXTRA_RETURN_TO_CALLER, true);
        boMoDangNhap.launch(intent);
    }

    public void capNhatGiaoDienTrangThaiDangNhap() {
        if (!isAdded() || layoutAccountLoggedIn == null) {
            return;
        }

        if (!sessionManager.daDangNhap()) {
            xoaGiaoDienKhiDangXuat();
            return;
        }

        if (!sessionManager.damBaoNguoiDungConHoatDong(databaseHelper)) {
            xoaGiaoDienKhiDangXuat();
            dieuHuongDenTabTrangChu();
            Toast.makeText(requireContext(), getString(R.string.session_invalid), Toast.LENGTH_SHORT).show();
            return;
        }

        long idNguoiDungHienTai = sessionManager.layIdNguoiDungHienTai();
        if (idNguoiDungHienTai <= 0) {
            sessionManager.xoaPhienDangNhap();
            xoaGiaoDienKhiDangXuat();
            Toast.makeText(requireContext(), getString(R.string.session_invalid), Toast.LENGTH_SHORT).show();
            return;
        }

        NguoiDung user = databaseHelper.layNguoiDungTheoId(idNguoiDungHienTai);
        if (user == null) {
            sessionManager.xoaPhienDangNhap();
            xoaGiaoDienKhiDangXuat();
            Toast.makeText(requireContext(), getString(R.string.account_user_not_found), Toast.LENGTH_SHORT).show();
            return;
        }

        nguoiDungHienTai = user;
        layoutAccountLoggedIn.setVisibility(View.VISIBLE);
        ganDuLieuNguoiDung(nguoiDungHienTai);
        if (btnOpenAdmin != null) {
            btnOpenAdmin.setVisibility(sessionManager.laAdmin() ? View.VISIBLE : View.GONE);
        }
        if (btnOpenEmployee != null) {
            btnOpenEmployee.setVisibility((sessionManager.laNhanVien() || sessionManager.laAdmin()) ? View.VISIBLE : View.GONE);
        }
    }

    private boolean xacThucPhienKhachHang(boolean hienToast) {
        if (!isAdded() || sessionManager == null || databaseHelper == null) {
            return false;
        }
        if (!sessionManager.daDangNhap()) {
            return true;
        }
        if (!sessionManager.damBaoNguoiDungConHoatDong(databaseHelper)) {
            xoaGiaoDienKhiDangXuat();
            lamMoiTrangThaiHeader();
            dieuHuongDenTabTrangChu();
            if (hienToast) {
                Toast.makeText(requireContext(), getString(R.string.session_invalid), Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        return true;
    }

    private void xoaGiaoDienKhiDangXuat() {
        nguoiDungHienTai = null;
        layoutAccountLoggedIn.setVisibility(View.GONE);
        if (btnOpenAdmin != null) {
            btnOpenAdmin.setVisibility(View.GONE);
        }
        if (btnOpenEmployee != null) {
            btnOpenEmployee.setVisibility(View.GONE);
        }
        layoutEditProfile.setVisibility(View.GONE);
        layoutChangePassword.setVisibility(View.GONE);
        xoaFormSuaThongTin();
        xoaFormDoiMatKhau();
    }

    private void dieuHuongDenTabTrangChu() {
        if (!(requireActivity() instanceof MainActivity)) {
            return;
        }

        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    private void ganDuLieuNguoiDung(NguoiDung user) {
        tvAccountName.setText(user.layTen());
        tvAccountEmail.setText(user.layEmail());
        tvAccountPhone.setText(user.laySoDienThoai());
    }

    private void lamMoiTrangThaiHeader() {
        if (requireActivity() instanceof MainActivity) {
            ((MainActivity) requireActivity()).lamMoiTrangThaiHeader();
        }
    }

    private void hienXacNhanDangXuat() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.account_logout_confirm_title)
                .setMessage(R.string.account_logout_confirm_message)
                .setNegativeButton(R.string.account_cancel_action, null)
                .setPositiveButton(R.string.account_logout, (dialog, which) -> thucHienDangXuat())
                .show();
    }

    private void thucHienDangXuat() {
        sessionManager.xoaPhienDangNhap();
        xoaGiaoDienKhiDangXuat();
        lamMoiTrangThaiHeader();
        dieuHuongDenTabTrangChu();

        Toast.makeText(
                requireContext(),
                getString(R.string.account_logout_success),
                Toast.LENGTH_SHORT
        ).show();
    }

    private boolean laSoDienThoaiHopLe(String phone) {
        return !TextUtils.isEmpty(phone)
                && phone.length() == DO_DAI_SO_DIEN_THOAI
                && phone.startsWith("0")
                && TextUtils.isDigitsOnly(phone);
    }

    private String layTextDaCatKhoangTrang(EditText editText) {
        if (editText.getText() == null) {
            return "";
        }
        return editText.getText().toString().trim();
    }
}
