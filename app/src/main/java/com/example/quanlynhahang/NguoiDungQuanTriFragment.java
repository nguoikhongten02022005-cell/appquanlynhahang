package com.example.quanlynhahang;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.adapter.BoDieuHopNguoiDungQuanTri;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.model.NguoiDung;
import com.example.quanlynhahang.model.VaiTroNguoiDung;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NguoiDungQuanTriFragment extends Fragment {

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private BoDieuHopNguoiDungQuanTri boDieuHopNguoiDung;
    private final List<NguoiDung> danhSachTatCaNguoiDung = new ArrayList<>();
    private TextView tvEmptyState;
    private TextView tvTongQuan;
    private TextView tvSoTatCa;
    private TextView tvSoAdmin;
    private TextView tvSoNhanVien;
    private EditText etTimKiem;

    private static final int VI_TRI_LOC_TAT_CA = 0;
    private static final int VI_TRI_LOC_NHAN_VIEN = 1;
    private static final int VI_TRI_LOC_ADMIN = 2;

    private View layoutNoiDungQuanTri;
    private TextView tvKhongCoQuyen;
    private TextView tvLoadingState;
    private TextView tvErrorState;
    private Spinner spinnerLocVaiTro;
    private Button btnThemTaiKhoan;
    private ArrayAdapter<String> adapterLocVaiTro;
    private int viTriLocVaiTro = VI_TRI_LOC_TAT_CA;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nguoi_dung_quan_tri, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = new DatabaseHelper(requireContext());
        databaseHelper.chuanBiCoSoDuLieu();
        sessionManager = new SessionManager(requireContext());

        layoutNoiDungQuanTri = view.findViewById(R.id.layoutNguoiDungQuanTriContent);
        tvKhongCoQuyen = view.findViewById(R.id.tvNguoiDungQuanTriUnauthorized);
        RecyclerView recyclerView = view.findViewById(R.id.rvNguoiDungQuanTri);
        tvLoadingState = view.findViewById(R.id.tvNguoiDungQuanTriLoading);
        tvErrorState = view.findViewById(R.id.tvNguoiDungQuanTriError);
        tvEmptyState = view.findViewById(R.id.tvNguoiDungQuanTriEmpty);
        tvTongQuan = view.findViewById(R.id.tvAdminUserSummary);
        tvSoTatCa = view.findViewById(R.id.tvAdminUserStatAllCount);
        tvSoAdmin = view.findViewById(R.id.tvAdminUserStatAdminCount);
        tvSoNhanVien = view.findViewById(R.id.tvAdminUserStatEmployeeCount);
        etTimKiem = view.findViewById(R.id.etAdminUserSearch);
        spinnerLocVaiTro = view.findViewById(R.id.spinnerAdminUserRoleFilter);
        btnThemTaiKhoan = view.findViewById(R.id.btnAdminAddUser);

        if (!kiemTraQuyenAdmin()) {
            return;
        }

        boDieuHopNguoiDung = new BoDieuHopNguoiDungQuanTri(new BoDieuHopNguoiDungQuanTri.HanhDongListener() {
            @Override
            public void khiSua(NguoiDung nguoiDung) {
                hienMenuHanhDongNguoiDung(nguoiDung);
            }

            @Override
            public void khiBatTatTrangThaiHoatDong(NguoiDung nguoiDung) {
                xuLyBatTatTrangThai(nguoiDung);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(boDieuHopNguoiDung);
        thietLapBoLocVaiTro();
        btnThemTaiKhoan.setOnClickListener(v -> hienDialogThemTaiKhoan());
        etTimKiem.addTextChangedListener(new TextWatcher() {
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
        if (layoutNoiDungQuanTri != null) {
            layoutNoiDungQuanTri.setVisibility(coQuyenAdmin ? View.VISIBLE : View.GONE);
        }
        if (tvKhongCoQuyen != null) {
            tvKhongCoQuyen.setVisibility(coQuyenAdmin ? View.GONE : View.VISIBLE);
        }
        return coQuyenAdmin;
    }

    private void thietLapBoLocVaiTro() {
        String[] luaChonLoc = new String[]{
                getString(R.string.admin_filter_all_roles_format, 0),
                getString(R.string.admin_filter_employees_format, 0),
                getString(R.string.admin_filter_admins_format, 0)
        };
        adapterLocVaiTro = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, luaChonLoc);
        adapterLocVaiTro.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocVaiTro.setAdapter(adapterLocVaiTro);
        spinnerLocVaiTro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viTriLocVaiTro = position;
                apDungBoLocNguoiDung();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                viTriLocVaiTro = VI_TRI_LOC_TAT_CA;
                apDungBoLocNguoiDung();
            }
        });
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
            boDieuHopNguoiDung.capNhatDanhSach(new ArrayList<>());
        }
    }

    private void apDungBoLocNguoiDung() {
        if (boDieuHopNguoiDung == null || etTimKiem == null) {
            return;
        }
        String tuKhoa = etTimKiem.getText() == null ? "" : chuanHoaTuKhoa(etTimKiem.getText().toString());
        List<NguoiDung> ketQua = new ArrayList<>();
        for (NguoiDung nguoiDung : danhSachTatCaNguoiDung) {
            if (!khopVaiTroDangLoc(nguoiDung) || !khopTuKhoa(nguoiDung, tuKhoa)) {
                continue;
            }
            ketQua.add(nguoiDung);
        }
        boDieuHopNguoiDung.capNhatDanhSach(ketQua);
        capNhatTrangThaiDanhSach(false, false, ketQua.isEmpty());
    }

    private boolean khopVaiTroDangLoc(NguoiDung nguoiDung) {
        if (viTriLocVaiTro == VI_TRI_LOC_NHAN_VIEN) {
            return nguoiDung.laNhanVien();
        }
        if (viTriLocVaiTro == VI_TRI_LOC_ADMIN) {
            return nguoiDung.laAdmin();
        }
        return true;
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
        if (tvLoadingState != null) {
            tvLoadingState.setVisibility(dangTai ? View.VISIBLE : View.GONE);
        }
        if (tvErrorState != null) {
            tvErrorState.setVisibility(loi ? View.VISIBLE : View.GONE);
        }
        if (tvEmptyState != null) {
            tvEmptyState.setVisibility(!dangTai && !loi && rong ? View.VISIBLE : View.GONE);
        }
    }

    private String layNhanVaiTroHienThi(NguoiDung nguoiDung) {
        if (nguoiDung.laAdmin()) {
            return getString(R.string.admin_role_admin);
        }
        if (nguoiDung.laNhanVien()) {
            return getString(R.string.admin_role_employee);
        }
        return getString(R.string.admin_role_customer);
    }

    private void hienMenuHanhDongNguoiDung(NguoiDung nguoiDung) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_admin_user_actions, null, false);
        TextView tvTen = dialogView.findViewById(R.id.tvAdminActionUserName);
        TextView tvEmail = dialogView.findViewById(R.id.tvAdminActionUserEmail);
        TextView tvSoDienThoai = dialogView.findViewById(R.id.tvAdminActionUserPhone);
        TextView tvTrangThai = dialogView.findViewById(R.id.tvAdminActionUserStatus);
        TextView btnChinhSua = dialogView.findViewById(R.id.btnAdminUserEditAccount);
        TextView btnBatTat = dialogView.findViewById(R.id.btnAdminUserToggleStatus);
        TextView btnXoaTaiKhoan = dialogView.findViewById(R.id.btnAdminUserDeleteAccount);

        tvTen.setText(nguoiDung.layHoTen());
        tvEmail.setText(nguoiDung.layEmail());
        tvSoDienThoai.setText(getString(R.string.admin_user_phone_format, nguoiDung.laySoDienThoai()));
        tvTrangThai.setText(getString(
                nguoiDung.dangHoatDong() ? R.string.admin_user_status_active : R.string.admin_user_status_locked));
        btnBatTat.setText(nguoiDung.dangHoatDong() ? R.string.admin_user_lock_account : R.string.admin_user_unlock_account);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();
        btnChinhSua.setOnClickListener(v -> {
            dialog.dismiss();
            Toast.makeText(requireContext(), R.string.admin_user_edit_unavailable, Toast.LENGTH_SHORT).show();
        });
        btnBatTat.setOnClickListener(v -> {
            dialog.dismiss();
            xuLyBatTatTrangThai(nguoiDung);
        });
        btnXoaTaiKhoan.setOnClickListener(v -> {
            dialog.dismiss();
            Toast.makeText(requireContext(), R.string.admin_action_failed, Toast.LENGTH_SHORT).show();
        });
        dialog.show();
    }

    private void hienDialogChiTietNguoiDung(NguoiDung nguoiDung) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.admin_user_detail_title)
                .setMessage(getString(
                        R.string.admin_user_detail_content,
                        nguoiDung.layHoTen(),
                        nguoiDung.layEmail(),
                        nguoiDung.laySoDienThoai(),
                        layNhanVaiTroHienThi(nguoiDung),
                        getString(nguoiDung.dangHoatDong() ? R.string.admin_user_status_active : R.string.admin_user_status_locked)
                ))
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void hienDialogDatLaiMatKhau(NguoiDung nguoiDung) {
        EditText etMatKhauMoi = new EditText(requireContext());
        etMatKhauMoi.setHint(R.string.admin_user_reset_password_hint);
        etMatKhauMoi.setSingleLine(true);
        etMatKhauMoi.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        int paddingNgang = getResources().getDimensionPixelSize(R.dimen.space_md);
        etMatKhauMoi.setPadding(paddingNgang, paddingNgang / 2, paddingNgang, paddingNgang / 2);

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.admin_user_reset_password_title)
                .setView(etMatKhauMoi)
                .setNegativeButton(R.string.account_cancel_action, null)
                .setPositiveButton(R.string.admin_save, (dialog, which) -> {
                    String matKhauMoi = etMatKhauMoi.getText() == null ? "" : etMatKhauMoi.getText().toString().trim();
                    if (TextUtils.isEmpty(matKhauMoi)) {
                        Toast.makeText(requireContext(), R.string.admin_user_reset_password_required, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    boolean daCapNhat = databaseHelper.capNhatMatKhauNguoiDung(nguoiDung.layId(), matKhauMoi);
                    Toast.makeText(requireContext(), daCapNhat ? R.string.admin_user_reset_password_success : R.string.admin_action_failed, Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void hienDialogThemTaiKhoan() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_edit_user, null, false);
        EditText etTen = dialogView.findViewById(R.id.etAdminUserName);
        EditText etEmail = dialogView.findViewById(R.id.etAdminUserEmail);
        EditText etSoDienThoai = dialogView.findViewById(R.id.etAdminUserPhone);
        EditText etMatKhau = dialogView.findViewById(R.id.etAdminUserPassword);
        Spinner spinnerVaiTro = dialogView.findViewById(R.id.spinnerAdminUserRole);
        Spinner spinnerTrangThai = dialogView.findViewById(R.id.spinnerAdminUserStatus);
        String[] tenVaiTro = new String[]{
                getString(R.string.admin_role_employee),
                getString(R.string.admin_role_admin)
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, tenVaiTro);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVaiTro.setAdapter(adapter);
        String[] tenTrangThai = new String[]{
                getString(R.string.admin_user_status_active),
                getString(R.string.admin_user_status_locked)
        };
        ArrayAdapter<String> adapterTrangThai = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, tenTrangThai);
        adapterTrangThai.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTrangThai.setAdapter(adapterTrangThai);

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.admin_dialog_add_user_title)
                .setView(dialogView)
                .setNegativeButton(R.string.account_cancel_action, null)
                .setPositiveButton(R.string.admin_save, (dialog, which) -> {
                    VaiTroNguoiDung vaiTro = layVaiTroTuViTri(spinnerVaiTro.getSelectedItemPosition());
                    boolean hoatDong = spinnerTrangThai.getSelectedItemPosition() == 0;
                    themTaiKhoan(etTen.getText(), etEmail.getText(), etSoDienThoai.getText(), etMatKhau.getText(), vaiTro, hoatDong);
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
        tvTongQuan.setText(getString(R.string.admin_user_summary_format, danhSachNguoiDung.size(), soDangHoatDong));
        tvSoTatCa.setText(String.valueOf(danhSachNguoiDung.size()));
        tvSoAdmin.setText(String.valueOf(soAdminHienTai));
        tvSoNhanVien.setText(String.valueOf(soNhanVienHienTai));
        capNhatNhanBoLocVaiTro(danhSachNguoiDung);
    }

    private void capNhatNhanBoLocVaiTro(List<NguoiDung> danhSachNguoiDung) {
        if (adapterLocVaiTro == null) {
            return;
        }
        int soAdminHienTai = 0;
        int soNhanVienHienTai = 0;
        for (NguoiDung nguoiDung : danhSachNguoiDung) {
            if (nguoiDung.laAdmin()) {
                soAdminHienTai++;
            } else if (nguoiDung.laNhanVien()) {
                soNhanVienHienTai++;
            }
        }
        adapterLocVaiTro.clear();
        adapterLocVaiTro.add(getString(R.string.admin_filter_all_roles_format, danhSachNguoiDung.size()));
        adapterLocVaiTro.add(getString(R.string.admin_filter_employees_format, soNhanVienHienTai));
        adapterLocVaiTro.add(getString(R.string.admin_filter_admins_format, soAdminHienTai));
        adapterLocVaiTro.notifyDataSetChanged();
        if (spinnerLocVaiTro != null && spinnerLocVaiTro.getSelectedItemPosition() != viTriLocVaiTro) {
            spinnerLocVaiTro.setSelection(viTriLocVaiTro, false);
        }
    }

    private void hienDialogDoiVaiTro(NguoiDung nguoiDung) {
        if (!isAdded()) {
            return;
        }

        final VaiTroNguoiDung[] luaChonVaiTro = new VaiTroNguoiDung[]{
                VaiTroNguoiDung.KHACH_HANG,
                VaiTroNguoiDung.NHAN_VIEN,
                VaiTroNguoiDung.ADMIN
        };
        final String[] tenLuaChon = new String[]{
                getString(R.string.admin_role_customer),
                getString(R.string.admin_role_employee),
                getString(R.string.admin_role_admin)
        };
        final int[] viTriDangChon = new int[]{nguoiDung.laAdmin() ? 2 : nguoiDung.laNhanVien() ? 1 : 0};

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.admin_change_role_title)
                .setMessage(R.string.admin_change_role_message)
                .setSingleChoiceItems(tenLuaChon, viTriDangChon[0], (dialog, which) -> viTriDangChon[0] = which)
                .setNegativeButton(R.string.account_cancel_action, null)
                .setPositiveButton(R.string.admin_change_role, (dialog, which) -> capNhatVaiTro(nguoiDung, luaChonVaiTro[viTriDangChon[0]]))
                .show();
    }

    private void capNhatVaiTro(NguoiDung nguoiDung, VaiTroNguoiDung vaiTroMoi) {
        long idNguoiDungHienTai = sessionManager.layIdNguoiDungHienTai();
        if (nguoiDung.layId() == idNguoiDungHienTai && vaiTroMoi != VaiTroNguoiDung.ADMIN) {
            Toast.makeText(requireContext(), R.string.admin_self_demote_blocked, Toast.LENGTH_SHORT).show();
            return;
        }
        boolean daCapNhat = databaseHelper.capNhatVaiTroNguoiDung(nguoiDung.layId(), vaiTroMoi);
        Toast.makeText(requireContext(), daCapNhat ? R.string.admin_user_role_update_success : R.string.admin_action_failed, Toast.LENGTH_SHORT).show();
        if (daCapNhat) {
            taiDanhSachNguoiDung();
        }
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
}
