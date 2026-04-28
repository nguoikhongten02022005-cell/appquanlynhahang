package com.example.quanlynhahang;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.quanlynhahang.adapter.NguoiDungQuanTriAdapter;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.databinding.DialogAddEditUserBinding;
import com.example.quanlynhahang.databinding.FragmentNguoiDungQuanTriBinding;
import com.example.quanlynhahang.model.NguoiDung;
import com.example.quanlynhahang.model.VaiTroNguoiDung;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NguoiDungQuanTriFragment extends Fragment {

    private FragmentNguoiDungQuanTriBinding binding;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private NguoiDungQuanTriAdapter nguoiDungQuanTriAdapter;
    private final List<NguoiDung> danhSachTatCaNguoiDung = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentNguoiDungQuanTriBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = new DatabaseHelper(requireContext());
        databaseHelper.chuanBiCoSoDuLieu();
        sessionManager = new SessionManager(requireContext());

        if (!kiemTraQuyenAdmin()) {
            return;
        }

        nguoiDungQuanTriAdapter = new NguoiDungQuanTriAdapter(new NguoiDungQuanTriAdapter.HanhDongListener() {
            @Override
            public void khiSua(NguoiDung nguoiDung) {
                Toast.makeText(requireContext(), R.string.admin_user_edit_unavailable, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void khiXoa(NguoiDung nguoiDung) {
                xuLyXoaTaiKhoan(nguoiDung);
            }

            @Override
            public void khiBatTatTrangThaiHoatDong(NguoiDung nguoiDung) {
                xuLyBatTatTrangThai(nguoiDung);
            }
        });

        binding.rvNguoiDungQuanTri.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvNguoiDungQuanTri.setAdapter(nguoiDungQuanTriAdapter);
        binding.cardAdminAddUser.setOnClickListener(v -> hienDialogThemTaiKhoan());
        binding.etAdminUserSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                apDungBoLocNguoiDung();
            }
        });

        taiDanhSachNguoiDung();
    }

    private boolean kiemTraQuyenAdmin() {
        boolean coQuyenAdmin = sessionManager != null
                && sessionManager.damBaoNguoiDungConHoatDong(databaseHelper)
                && sessionManager.layVaiTroSessionHopLe() == VaiTroNguoiDung.ADMIN;
        if (binding != null) {
            binding.layoutNguoiDungQuanTriContent.setVisibility(coQuyenAdmin ? View.VISIBLE : View.GONE);
            binding.tvNguoiDungQuanTriUnauthorized.setVisibility(coQuyenAdmin ? View.GONE : View.VISIBLE);
        }
        return coQuyenAdmin;
    }

    private void taiDanhSachNguoiDung() {
        if (!kiemTraQuyenAdmin()) {
            return;
        }
        capNhatTrangThaiDanhSach(true, false, false);
        danhSachTatCaNguoiDung.clear();
        try {
            danhSachTatCaNguoiDung.addAll(databaseHelper.layTatCaNguoiDung());
            capNhatTongQuanNguoiDung(danhSachTatCaNguoiDung);
            capNhatTrangThaiDanhSach(false, false, false);
            apDungBoLocNguoiDung();
        } catch (RuntimeException ex) {
            capNhatTrangThaiDanhSach(false, true, false);
            nguoiDungQuanTriAdapter.capNhatDanhSach(new ArrayList<>());
        }
    }

    private void apDungBoLocNguoiDung() {
        if (nguoiDungQuanTriAdapter == null || binding == null) {
            return;
        }
        String tuKhoa = binding.etAdminUserSearch.getText() == null ? "" : chuanHoaTuKhoa(binding.etAdminUserSearch.getText().toString());
        List<NguoiDung> ketQua = new ArrayList<>();
        for (NguoiDung nguoiDung : danhSachTatCaNguoiDung) {
            if (!khopTuKhoa(nguoiDung, tuKhoa)) {
                continue;
            }
            ketQua.add(nguoiDung);
        }
        nguoiDungQuanTriAdapter.capNhatDanhSach(ketQua);
        capNhatTrangThaiDanhSach(false, false, ketQua.isEmpty());
    }

    private boolean khopTuKhoa(NguoiDung nguoiDung, String tuKhoa) {
        if (TextUtils.isEmpty(tuKhoa)) {
            return true;
        }
        String noiDungTimKiem = chuanHoaTuKhoa(nguoiDung.layHoTen()) + " "
                + chuanHoaTuKhoa(nguoiDung.layEmail()) + " "
                + chuanHoaTuKhoa(nguoiDung.laySoDienThoai());
        return noiDungTimKiem.contains(tuKhoa);
    }

    private String chuanHoaTuKhoa(String giaTri) {
        return giaTri == null ? "" : giaTri.trim().toLowerCase(Locale.ROOT);
    }

    private void capNhatTrangThaiDanhSach(boolean dangTai, boolean loi, boolean rong) {
        if (binding == null) {
            return;
        }
        binding.tvNguoiDungQuanTriLoading.setVisibility(dangTai ? View.VISIBLE : View.GONE);
        binding.tvNguoiDungQuanTriError.setVisibility(loi ? View.VISIBLE : View.GONE);
        binding.tvNguoiDungQuanTriEmpty.setVisibility(!dangTai && !loi && rong ? View.VISIBLE : View.GONE);
    }

    private void hienDialogThemTaiKhoan() {
        DialogAddEditUserBinding dialogBinding = DialogAddEditUserBinding.inflate(LayoutInflater.from(requireContext()));
        String[] tenVaiTro = new String[]{
                getString(R.string.admin_role_employee),
                getString(R.string.admin_role_admin)
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, tenVaiTro);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dialogBinding.spinnerAdminUserRole.setAdapter(adapter);
        String[] tenTrangThai = new String[]{
                getString(R.string.admin_user_status_active),
                getString(R.string.admin_user_status_locked)
        };
        ArrayAdapter<String> adapterTrangThai = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, tenTrangThai);
        adapterTrangThai.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dialogBinding.spinnerAdminUserStatus.setAdapter(adapterTrangThai);

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.admin_dialog_add_user_title)
                .setView(dialogBinding.getRoot())
                .setNegativeButton(R.string.account_cancel_action, null)
                .setPositiveButton(R.string.admin_save, (dialog, which) -> {
                    VaiTroNguoiDung vaiTro = layVaiTroTuViTri(dialogBinding.spinnerAdminUserRole.getSelectedItemPosition());
                    boolean hoatDong = dialogBinding.spinnerAdminUserStatus.getSelectedItemPosition() == 0;
                    themTaiKhoan(
                            dialogBinding.etAdminUserName.getText(),
                            dialogBinding.etAdminUserEmail.getText(),
                            dialogBinding.etAdminUserPhone.getText(),
                            dialogBinding.etAdminUserPassword.getText(),
                            vaiTro,
                            hoatDong
                    );
                })
                .show();
    }

    private VaiTroNguoiDung layVaiTroTuViTri(int viTri) {
        if (viTri == 1) {
            return VaiTroNguoiDung.ADMIN;
        }
        return VaiTroNguoiDung.NHAN_VIEN;
    }

    private void themTaiKhoan(CharSequence ten, CharSequence email, CharSequence soDienThoai, CharSequence matKhau, VaiTroNguoiDung vaiTro, boolean hoatDong) {
        String tenMoi = ten == null ? "" : ten.toString().trim();
        String emailMoi = email == null ? "" : email.toString().trim();
        String soDienThoaiMoi = soDienThoai == null ? "" : soDienThoai.toString().trim();
        String matKhauMoi = matKhau == null ? "" : matKhau.toString();
        if (TextUtils.isEmpty(tenMoi) || TextUtils.isEmpty(emailMoi) || TextUtils.isEmpty(soDienThoaiMoi) || TextUtils.isEmpty(matKhauMoi)) {
            Toast.makeText(requireContext(), R.string.admin_user_validation_required, Toast.LENGTH_SHORT).show();
            return;
        }
        long idMoi = databaseHelper.insertUser(tenMoi, emailMoi, soDienThoaiMoi, matKhauMoi, vaiTro, hoatDong);
        Toast.makeText(requireContext(), idMoi > 0 ? R.string.admin_user_create_success : R.string.admin_user_validation_duplicate, Toast.LENGTH_SHORT).show();
        if (idMoi > 0) {
            taiDanhSachNguoiDung();
        }
    }

    private void capNhatTongQuanNguoiDung(List<NguoiDung> danhSachNguoiDung) {
        int soDangHoatDong = 0;
        int soAdminHienTai = 0;
        int soNhanVienHienTai = 0;
        for (NguoiDung nguoiDung : danhSachNguoiDung) {
            if (nguoiDung.dangHoatDong()) {
                soDangHoatDong++;
            }
            if (nguoiDung.laAdmin()) {
                soAdminHienTai++;
            } else if (nguoiDung.laNhanVien()) {
                soNhanVienHienTai++;
            }
        }
        if (binding == null) {
            return;
        }
        binding.tvAdminUserSummary.setText(getString(R.string.admin_user_summary_format, danhSachNguoiDung.size(), soDangHoatDong));
        binding.tvAdminUserStatAllChip.setText(getString(R.string.admin_user_stat_chip_format, getString(R.string.admin_user_stat_all), danhSachNguoiDung.size()));
        binding.tvAdminUserStatAdminChip.setText(getString(R.string.admin_user_stat_chip_format, "Admin", soAdminHienTai));
        binding.tvAdminUserStatEmployeeChip.setText(getString(R.string.admin_user_stat_chip_format, getString(R.string.admin_role_employee), soNhanVienHienTai));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void xuLyBatTatTrangThai(NguoiDung nguoiDung) {
        long idNguoiDungHienTai = sessionManager.layIdNguoiDungHienTai();
        boolean trangThaiMoi = !nguoiDung.dangHoatDong();
        if (nguoiDung.layId() == idNguoiDungHienTai && !trangThaiMoi) {
            Toast.makeText(requireContext(), R.string.admin_self_lock_blocked, Toast.LENGTH_SHORT).show();
            return;
        }
        boolean daCapNhat = databaseHelper.capNhatTrangThaiHoatDongNguoiDung(nguoiDung.layId(), trangThaiMoi);
        Toast.makeText(requireContext(), daCapNhat ? R.string.admin_user_active_update_success : R.string.admin_action_failed, Toast.LENGTH_SHORT).show();
        if (daCapNhat) {
            taiDanhSachNguoiDung();
        }
    }

    private void xuLyXoaTaiKhoan(NguoiDung nguoiDung) {
        long idNguoiDungHienTai = sessionManager.layIdNguoiDungHienTai();
        if (nguoiDung.layId() == idNguoiDungHienTai) {
            Toast.makeText(requireContext(), R.string.admin_self_delete_blocked, Toast.LENGTH_SHORT).show();
            return;
        }
        boolean daXoa = databaseHelper.xoaNguoiDung(nguoiDung.layId());
        Toast.makeText(requireContext(), daXoa ? R.string.admin_user_delete_success : R.string.admin_action_failed, Toast.LENGTH_SHORT).show();
        if (daXoa) {
            taiDanhSachNguoiDung();
        }
    }
}
