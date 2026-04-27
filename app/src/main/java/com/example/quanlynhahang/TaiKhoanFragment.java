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
import com.example.quanlynhahang.databinding.FragmentTaiKhoanBinding;
import com.example.quanlynhahang.model.NguoiDung;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TaiKhoanFragment extends Fragment {

    private static final int DO_DAI_MAT_KHAU_TOI_THIEU = 6;
    private static final int DO_DAI_SO_DIEN_THOAI = 10;

    private FragmentTaiKhoanBinding binding;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    private NguoiDung nguoiDungHienTai;

    private final ActivityResultLauncher<Intent> boMoDangNhap = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                capNhatGiaoDienTrangThaiDangNhap();
                lamMoiTrangThaiHeader();
                if (!isAdded() || sessionManager == null || coPhienDangNhapHienTai()) {
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
        sessionManager.chuyenDuLieuDangNhapCuNeuCan(databaseHelper);

        binding = FragmentTaiKhoanBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        thietLapHanhDong();
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

    private boolean coCheDoPreviewKhach() {
        return isAdded()
                && requireActivity().getIntent().getBooleanExtra(MainActivity.EXTRA_CHE_DO_PREVIEW_KHACH, false);
    }

    private boolean coPhienDangNhapHienTai() {
        if (sessionManager == null) {
            return false;
        }
        return coCheDoPreviewKhach() ? sessionManager.daDangNhapKhachHang() : sessionManager.daDangNhap();
    }

    private void thietLapHanhDong() {
        binding.btnEditProfile.setOnClickListener(v -> hienFormSuaThongTin());
        binding.layoutEditProfile.btnSaveProfileChanges.setOnClickListener(v -> luuThayDoiThongTin());
        binding.layoutEditProfile.btnCancelEditProfile.setOnClickListener(v -> anFormSuaThongTin());
        binding.btnOpenChangePassword.setOnClickListener(v -> hienFormDoiMatKhau());
        binding.layoutChangePassword.btnSubmitChangePassword.setOnClickListener(v -> guiYeuCauDoiMatKhau());
        binding.layoutChangePassword.btnCancelChangePassword.setOnClickListener(v -> anFormDoiMatKhau());

        binding.btnContactSupport.setOnClickListener(v -> moKenhHoTro());
        binding.btnLogout.setOnClickListener(v -> hienXacNhanDangXuat());
    }

    private void hienFormSuaThongTin() {
        if (nguoiDungHienTai == null) {
            Toast.makeText(requireContext(), getString(R.string.account_user_not_found), Toast.LENGTH_SHORT).show();
            return;
        }

        binding.layoutEditProfile.getRoot().setVisibility(View.VISIBLE);
        anFormDoiMatKhau();

        binding.layoutEditProfile.etEditName.setText(nguoiDungHienTai.layHoTen());
        binding.layoutEditProfile.etEditPhone.setText(nguoiDungHienTai.laySoDienThoai());
    }

    private void anFormSuaThongTin() {
        binding.layoutEditProfile.getRoot().setVisibility(View.GONE);
        xoaFormSuaThongTin();
    }

    private void luuThayDoiThongTin() {
        if (nguoiDungHienTai == null) {
            Toast.makeText(requireContext(), getString(R.string.account_user_not_found), Toast.LENGTH_SHORT).show();
            return;
        }

        String ten = layTextDaCatKhoangTrang(binding.layoutEditProfile.etEditName);
        String soDienThoai = layTextDaCatKhoangTrang(binding.layoutEditProfile.etEditPhone);

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
            xoaPhienKhachHangPhuHopTheoCheDo();
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
        binding.layoutChangePassword.getRoot().setVisibility(View.VISIBLE);
        anFormSuaThongTin();
    }

    private void anFormDoiMatKhau() {
        binding.layoutChangePassword.getRoot().setVisibility(View.GONE);
        xoaFormDoiMatKhau();
    }

    private void guiYeuCauDoiMatKhau() {
        if (nguoiDungHienTai == null) {
            Toast.makeText(requireContext(), getString(R.string.account_user_not_found), Toast.LENGTH_SHORT).show();
            return;
        }

        String matKhauHienTai = layTextDaCatKhoangTrang(binding.layoutChangePassword.etCurrentPassword);
        String matKhauMoi = layTextDaCatKhoangTrang(binding.layoutChangePassword.etNewPassword);
        String xacNhanMatKhau = layTextDaCatKhoangTrang(binding.layoutChangePassword.etConfirmPassword);

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
        if (binding == null) {
            return;
        }
        binding.layoutEditProfile.etEditName.setText("");
        binding.layoutEditProfile.etEditPhone.setText("");
    }

    private void xoaFormDoiMatKhau() {
        if (binding == null) {
            return;
        }
        binding.layoutChangePassword.etCurrentPassword.setText("");
        binding.layoutChangePassword.etNewPassword.setText("");
        binding.layoutChangePassword.etConfirmPassword.setText("");
    }

    public void khiTabTaiKhoanDuocChon() {
        if (!xacThucPhienKhachHang(true)) {
            return;
        }
        capNhatGiaoDienTrangThaiDangNhap();
        lamMoiTrangThaiHeader();
        if (coPhienDangNhapHienTai()) {
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
        if (requireActivity().getIntent().getBooleanExtra(MainActivity.EXTRA_CHE_DO_PREVIEW_KHACH, false)) {
            intent.putExtra(DangNhapActivity.EXTRA_ONLY_CUSTOMER_SESSION, true);
        }
        boMoDangNhap.launch(intent);
    }

    public void capNhatGiaoDienTrangThaiDangNhap() {
        if (!isAdded() || binding == null || sessionManager == null || databaseHelper == null) {
            return;
        }

        NguoiDung user = taiNguoiDungHienTaiHopLe(false);
        if (user == null) {
            xoaGiaoDienKhiDangXuat();
            return;
        }
        capNhatGiaoDienChoNguoiDungHienTai(user);
    }

    private boolean xacThucPhienKhachHang(boolean hienToast) {
        if (!isAdded() || sessionManager == null || databaseHelper == null) {
            return false;
        }
        if (taiNguoiDungHienTaiHopLe(hienToast) != null) {
            return true;
        }
        if (!coPhienDangNhapHienTai()) {
            return true;
        }
        xoaGiaoDienKhiDangXuat();
        lamMoiTrangThaiHeader();
        dieuHuongDenTabTrangChu();
        return false;
    }

    private void xoaPhienKhachHangPhuHopTheoCheDo() {
        if (coCheDoPreviewKhach()) {
            sessionManager.xoaPhienKhachHang();
            return;
        }
        sessionManager.xoaPhienDangNhap();
    }

    @Nullable
    private NguoiDung taiNguoiDungHienTaiHopLe(boolean hienToast) {
        if (coCheDoPreviewKhach()) {
            if (!sessionManager.daDangNhapKhachHang()) {
                return null;
            }
            return taiNguoiDungTheoId(sessionManager.layIdKhachHangHienTai(), true, hienToast);
        }
        if (!sessionManager.daDangNhap()) {
            return null;
        }
        return taiNguoiDungTheoId(sessionManager.layIdNguoiDungHienTai(), false, hienToast);
    }

    @Nullable
    private NguoiDung taiNguoiDungTheoId(long idNguoiDung, boolean chiXoaPhienKhach, boolean hienToast) {
        if (!sessionManager.damBaoNguoiDungConHoatDong(databaseHelper)) {
            if (chiXoaPhienKhach) {
                sessionManager.xoaPhienKhachHang();
            }
            if (hienToast) {
                Toast.makeText(requireContext(), getString(R.string.session_invalid), Toast.LENGTH_SHORT).show();
            }
            return null;
        }
        if (idNguoiDung <= 0) {
            if (chiXoaPhienKhach) {
                sessionManager.xoaPhienKhachHang();
            } else {
                xoaPhienKhachHangPhuHopTheoCheDo();
            }
            if (hienToast) {
                Toast.makeText(requireContext(), getString(R.string.session_invalid), Toast.LENGTH_SHORT).show();
            }
            return null;
        }
        NguoiDung user = databaseHelper.layNguoiDungTheoId(idNguoiDung);
        if (user == null) {
            if (chiXoaPhienKhach) {
                sessionManager.xoaPhienKhachHang();
            } else {
                xoaPhienKhachHangPhuHopTheoCheDo();
            }
            if (hienToast) {
                Toast.makeText(requireContext(), getString(R.string.account_user_not_found), Toast.LENGTH_SHORT).show();
            }
        }
        return user;
    }

    private boolean capNhatGiaoDienChoNguoiDungHienTai(@NonNull NguoiDung user) {
        nguoiDungHienTai = user;
        binding.layoutAccountLoggedIn.setVisibility(View.VISIBLE);
        ganDuLieuNguoiDung(user);
        return true;
    }

    private void xoaGiaoDienKhiDangXuat() {
        nguoiDungHienTai = null;
        binding.layoutAccountLoggedIn.setVisibility(View.GONE);
        binding.layoutEditProfile.getRoot().setVisibility(View.GONE);
        binding.layoutChangePassword.getRoot().setVisibility(View.GONE);
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
        binding.tvAccountName.setText(user.layHoTen());
        binding.tvAccountEmail.setText(user.layEmail());
        binding.tvAccountPhone.setText(user.laySoDienThoai());
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
        xoaPhienKhachHangPhuHopTheoCheDo();
        xoaGiaoDienKhiDangXuat();
        lamMoiTrangThaiHeader();
        dieuHuongDenTabTrangChu();

        Toast.makeText(
                requireContext(),
                getString(R.string.account_logout_success),
                Toast.LENGTH_SHORT
        ).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
