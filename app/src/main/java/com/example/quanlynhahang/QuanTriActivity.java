package com.example.quanlynhahang;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.adapter.AdminDishAdapter;
import com.example.quanlynhahang.adapter.AdminUserAdapter;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.helper.DieuHuongVaiTroHelper;
import com.example.quanlynhahang.model.ThongKeTongQuanAdmin;
import com.example.quanlynhahang.model.NguoiDung;
import com.example.quanlynhahang.model.VaiTroNguoiDung;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class QuanTriActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;

    private MaterialButton btnTabOverview;
    private MaterialButton btnTabDishes;
    private MaterialButton btnTabUsers;
    private View layoutOverview;
    private View layoutDishes;
    private View layoutUsers;
    private TextView tvTotalUsersCount;
    private TextView tvTotalDishesCount;
    private TextView tvTotalDonHangsCount;
    private TextView tvPendingDonHangsCount;
    private TextView tvPendingReservationsCount;
    private TextView tvProcessingRequestsCount;
    private TextView tvCustomerCount;
    private TextView tvEmployeeCount;
    private TextView tvAdminCount;
    private EditText etDishSearch;
    private Spinner spinnerRoleFilter;
    private TextView tvDishesEmpty;
    private TextView tvUsersEmpty;

    private AdminDishAdapter dishAdapter;
    private AdminUserAdapter userAdapter;

    private String currentDishKeyword = "";
    private VaiTroNguoiDung currentRoleFilter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quan_tri);

        sessionManager = new SessionManager(this);
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.chuanBiCoSoDuLieu();
        sessionManager.migrateLegacyAuthIfNeeded(databaseHelper);
        sessionManager.damBaoVaiTroSession(databaseHelper);

        if (!sessionManager.daDangNhap() || !sessionManager.laAdmin()) {
            Toast.makeText(this, getString(R.string.role_guard_admin_denied), Toast.LENGTH_SHORT).show();
            dieuHuongSaiVaiTro();
            return;
        }

        khoiTaoView();
        thietLapRecyclerView();
        thietLapTab();
        thietLapTimKiemMon();
        thietLapLocVaiTro();
        thietLapHanhDong();
        thietLapDangXuat();
        lamMoiToanBoDuLieuAdmin();
        hienTabTongQuan();
    }

    private void khoiTaoView() {
        btnTabOverview = findViewById(R.id.btnAdminTabOverview);
        btnTabDishes = findViewById(R.id.btnAdminTabDishes);
        btnTabUsers = findViewById(R.id.btnAdminTabUsers);
        layoutOverview = findViewById(R.id.layoutAdminOverview);
        layoutDishes = findViewById(R.id.layoutAdminDishes);
        layoutUsers = findViewById(R.id.layoutAdminUsers);
        tvTotalUsersCount = findViewById(R.id.tvAdminTotalUsersCount);
        tvTotalDishesCount = findViewById(R.id.tvAdminTotalDishesCount);
        tvTotalDonHangsCount = findViewById(R.id.tvAdminTotalDonHangsCount);
        tvPendingDonHangsCount = findViewById(R.id.tvAdminPendingDonHangsCount);
        tvPendingReservationsCount = findViewById(R.id.tvAdminPendingReservationsCount);
        tvProcessingRequestsCount = findViewById(R.id.tvAdminProcessingRequestsCount);
        tvCustomerCount = findViewById(R.id.tvAdminCustomerCount);
        tvEmployeeCount = findViewById(R.id.tvAdminEmployeeCount);
        tvAdminCount = findViewById(R.id.tvAdminAdminCount);
        etDishSearch = findViewById(R.id.etAdminDishSearch);
        spinnerRoleFilter = findViewById(R.id.spinnerAdminVaiTroNguoiDungFilter);
        tvDishesEmpty = findViewById(R.id.tvAdminDishesEmpty);
        tvUsersEmpty = findViewById(R.id.tvAdminUsersEmpty);
    }

    private void thietLapRecyclerView() {
        RecyclerView rvDishes = findViewById(R.id.rvAdminDishes);
        RecyclerView rvUsers = findViewById(R.id.rvAdminUsers);
        rvDishes.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.setLayoutManager(new LinearLayoutManager(this));

        dishAdapter = new AdminDishAdapter(new AdminDishAdapter.HanhDongListener() {
            @Override
            public void khiSua(DatabaseHelper.DishRecord dishRecord) {
                hienDialogMonAn(dishRecord);
            }

            @Override
            public void khiXoa(DatabaseHelper.DishRecord dishRecord) {
                xacNhanXoaMon(dishRecord);
            }

            @Override
            public void khiBatTatTrangThaiPhucVu(DatabaseHelper.DishRecord dishRecord) {
                boolean daCapNhat = databaseHelper.capNhatTrangThaiPhucVuMon(dishRecord.layId(), !dishRecord.layMonAn().laConPhucVu());
                Toast.makeText(QuanTriActivity.this, daCapNhat ? R.string.admin_dish_availability_success : R.string.admin_action_failed, Toast.LENGTH_SHORT).show();
                if (daCapNhat) {
                    lamMoiToanBoDuLieuAdmin();
                }
            }
        });

        userAdapter = new AdminUserAdapter(new AdminUserAdapter.HanhDongListener() {
            @Override
            public void khiDoiVaiTro(NguoiDung user) {
                hienDialogDoiVaiTro(user);
            }

            @Override
            public void khiBatTatTrangThaiHoatDong(NguoiDung user) {
                xuLyBatTatTrangThaiNguoiDung(user);
            }
        });

        rvDishes.setAdapter(dishAdapter);
        rvUsers.setAdapter(userAdapter);
    }

    private void thietLapTab() {
        btnTabOverview.setOnClickListener(v -> hienTabTongQuan());
        btnTabDishes.setOnClickListener(v -> hienTabMonAn());
        btnTabUsers.setOnClickListener(v -> hienTabTaiKhoan());
    }

    private void thietLapTimKiemMon() {
        etDishSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentDishKeyword = s == null ? "" : s.toString().trim();
                taiDanhSachMon();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void thietLapLocVaiTro() {
        List<String> labels = new ArrayList<>();
        labels.add(getString(R.string.admin_filter_all_roles));
        labels.add(getString(R.string.admin_filter_customers));
        labels.add(getString(R.string.admin_filter_employees));
        labels.add(getString(R.string.admin_filter_admins));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, labels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRoleFilter.setAdapter(adapter);
        spinnerRoleFilter.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    currentRoleFilter = VaiTroNguoiDung.KHACH_HANG;
                } else if (position == 2) {
                    currentRoleFilter = VaiTroNguoiDung.NHAN_VIEN;
                } else if (position == 3) {
                    currentRoleFilter = VaiTroNguoiDung.ADMIN;
                } else {
                    currentRoleFilter = null;
                }
                taiDanhSachNguoiDung();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                currentRoleFilter = null;
                taiDanhSachNguoiDung();
            }
        });
    }

    private void thietLapHanhDong() {
        findViewById(R.id.btnAdminAddDish).setOnClickListener(v -> hienDialogMonAn(null));
    }

    private void thietLapDangXuat() {
        MaterialButton btnLogout = findViewById(R.id.btnAdminLogout);
        btnLogout.setOnClickListener(v -> {
            sessionManager.xoaPhienDangNhap();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void lamMoiToanBoDuLieuAdmin() {
        taiThongKeTongQuan();
        taiDanhSachMon();
        taiDanhSachNguoiDung();
    }

    private void taiThongKeTongQuan() {
        ThongKeTongQuanAdmin thongKe = databaseHelper.layThongKeTongQuanAdmin();
        tvTotalUsersCount.setText(String.valueOf(thongKe.getTotalUsers()));
        tvTotalDishesCount.setText(String.valueOf(thongKe.getTotalDishes()));
        tvTotalDonHangsCount.setText(String.valueOf(thongKe.getTotalDonHangs()));
        tvPendingDonHangsCount.setText(String.valueOf(thongKe.getPendingDonHangs()));
        tvPendingReservationsCount.setText(String.valueOf(thongKe.getPendingReservations()));
        tvProcessingRequestsCount.setText(String.valueOf(thongKe.getProcessingServiceRequests()));
        tvCustomerCount.setText(String.valueOf(thongKe.getCustomerCount()));
        tvEmployeeCount.setText(String.valueOf(thongKe.getEmployeeCount()));
        tvAdminCount.setText(String.valueOf(thongKe.getAdminCount()));
    }

    private void taiDanhSachMon() {
        List<DatabaseHelper.DishRecord> danhSachMon = currentDishKeyword.isEmpty()
                ? databaseHelper.layTatCaMonAn()
                : databaseHelper.timKiemMonAn(currentDishKeyword);
        dishAdapter.capNhatDanhSach(danhSachMon);
        tvDishesEmpty.setVisibility(danhSachMon.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void taiDanhSachNguoiDung() {
        List<NguoiDung> danhSachNguoiDung = currentRoleFilter == null ? databaseHelper.layTatCaNguoiDung() : databaseHelper.layNguoiDungTheoVaiTro(currentRoleFilter);
        userAdapter.capNhatDanhSach(danhSachNguoiDung);
        tvUsersEmpty.setVisibility(danhSachNguoiDung.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void hienTabTongQuan() {
        layoutOverview.setVisibility(View.VISIBLE);
        layoutDishes.setVisibility(View.GONE);
        layoutUsers.setVisibility(View.GONE);
        btnTabOverview.setEnabled(false);
        btnTabDishes.setEnabled(true);
        btnTabUsers.setEnabled(true);
    }

    private void hienTabMonAn() {
        layoutOverview.setVisibility(View.GONE);
        layoutDishes.setVisibility(View.VISIBLE);
        layoutUsers.setVisibility(View.GONE);
        btnTabOverview.setEnabled(true);
        btnTabDishes.setEnabled(false);
        btnTabUsers.setEnabled(true);
    }

    private void hienTabTaiKhoan() {
        layoutOverview.setVisibility(View.GONE);
        layoutDishes.setVisibility(View.GONE);
        layoutUsers.setVisibility(View.VISIBLE);
        btnTabOverview.setEnabled(true);
        btnTabDishes.setEnabled(true);
        btnTabUsers.setEnabled(false);
    }

    private void hienDialogMonAn(DatabaseHelper.DishRecord dishRecord) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_dish, null);
        EditText etName = dialogView.findViewById(R.id.etAdminDishName);
        EditText etPrice = dialogView.findViewById(R.id.etAdminDishPrice);
        EditText etCategory = dialogView.findViewById(R.id.etAdminDishCategory);
        EditText etDescription = dialogView.findViewById(R.id.etAdminDishDescription);
        EditText etImage = dialogView.findViewById(R.id.etAdminDishImage);
        EditText etScore = dialogView.findViewById(R.id.etAdminDishScore);
        CheckBox cbAvailable = dialogView.findViewById(R.id.cbAdminDishAvailable);

        if (dishRecord != null) {
            etName.setText(dishRecord.layMonAn().layTenMon());
            etPrice.setText(dishRecord.layMonAn().layGiaBan());
            etCategory.setText(dishRecord.layMonAn().layTenDanhMuc());
            etDescription.setText(dishRecord.layMoTa());
            etImage.setText(dishRecord.layTenAnhTaiNguyen());
            etScore.setText(String.valueOf(dishRecord.layMonAn().layDiemDeXuat()));
            cbAvailable.setChecked(dishRecord.layMonAn().laConPhucVu());
        } else {
            cbAvailable.setChecked(true);
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(dishRecord == null ? R.string.admin_dialog_add_dish_title : R.string.admin_dialog_edit_dish_title)
                .setView(dialogView)
                .setNegativeButton(R.string.account_cancel_action, null)
                .setPositiveButton(R.string.admin_save, null)
                .create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String tenMon = etName.getText().toString().trim();
            String giaBan = etPrice.getText().toString().trim();
            String danhMuc = etCategory.getText().toString().trim();
            String moTa = etDescription.getText().toString().trim();
            String tenAnh = etImage.getText().toString().trim();
            String diemDeXuatRaw = etScore.getText().toString().trim();
            if (tenMon.isEmpty() || giaBan.isEmpty() || danhMuc.isEmpty() || moTa.isEmpty()) {
                Toast.makeText(this, R.string.admin_dish_validation_required, Toast.LENGTH_SHORT).show();
                return;
            }
            int diemDeXuat;
            try {
                diemDeXuat = diemDeXuatRaw.isEmpty() ? 0 : Integer.parseInt(diemDeXuatRaw);
                if (diemDeXuat < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                Toast.makeText(this, R.string.admin_dish_validation_score, Toast.LENGTH_SHORT).show();
                return;
            }

            boolean thanhCong;
            if (dishRecord == null) {
                thanhCong = databaseHelper.themBanGhiMonAn(tenMon, giaBan, moTa, tenAnh, cbAvailable.isChecked(), danhMuc, diemDeXuat) > 0;
                Toast.makeText(this, thanhCong ? R.string.admin_dish_create_success : R.string.admin_action_failed, Toast.LENGTH_SHORT).show();
            } else {
                thanhCong = databaseHelper.capNhatBanGhiMonAn(dishRecord.layId(), tenMon, giaBan, moTa, tenAnh, cbAvailable.isChecked(), danhMuc, diemDeXuat);
                Toast.makeText(this, thanhCong ? R.string.admin_dish_update_success : R.string.admin_action_failed, Toast.LENGTH_SHORT).show();
            }
            if (thanhCong) {
                dialog.dismiss();
                lamMoiToanBoDuLieuAdmin();
            }
        }));
        dialog.show();
    }

    private void xacNhanXoaMon(DatabaseHelper.DishRecord dishRecord) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.admin_delete_confirm_title)
                .setMessage(R.string.admin_delete_confirm_message)
                .setNegativeButton(R.string.account_cancel_action, null)
                .setPositiveButton(R.string.admin_delete_dish, (dialog, which) -> {
                    boolean daXoa = databaseHelper.xoaMonAnTheoId(dishRecord.layId());
                    Toast.makeText(this, daXoa ? R.string.admin_dish_delete_success : R.string.admin_action_failed, Toast.LENGTH_SHORT).show();
                    if (daXoa) {
                        lamMoiToanBoDuLieuAdmin();
                    }
                })
                .show();
    }

    private void hienDialogDoiVaiTro(NguoiDung user) {
        long idNguoiDungHienTai = sessionManager.layIdNguoiDungHienTai();
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_change_user_role, null);
        RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroupAdminVaiTroNguoiDung);
        RadioButton radioCustomer = dialogView.findViewById(R.id.radioRoleCustomer);
        RadioButton radioEmployee = dialogView.findViewById(R.id.radioRoleEmployee);
        RadioButton radioAdmin = dialogView.findViewById(R.id.radioRoleAdmin);

        if (user.laAdmin()) {
            radioAdmin.setChecked(true);
        } else if (user.laNhanVien()) {
            radioEmployee.setChecked(true);
        } else {
            radioCustomer.setChecked(true);
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.admin_change_role_title)
                .setMessage(R.string.admin_change_role_message)
                .setView(dialogView)
                .setNegativeButton(R.string.account_cancel_action, null)
                .setPositiveButton(R.string.admin_change_role, null)
                .create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            VaiTroNguoiDung vaiTroDaChon = radioGroup.getCheckedRadioButtonId() == R.id.radioRoleAdmin
                    ? VaiTroNguoiDung.ADMIN
                    : radioGroup.getCheckedRadioButtonId() == R.id.radioRoleEmployee
                    ? VaiTroNguoiDung.NHAN_VIEN
                    : VaiTroNguoiDung.KHACH_HANG;
            if (user.layId() == idNguoiDungHienTai && vaiTroDaChon != VaiTroNguoiDung.ADMIN) {
                Toast.makeText(this, R.string.admin_self_demote_blocked, Toast.LENGTH_SHORT).show();
                return;
            }
            boolean daCapNhat = databaseHelper.capNhatVaiTroNguoiDung(user.layId(), vaiTroDaChon);
            Toast.makeText(this, daCapNhat ? R.string.admin_user_role_update_success : R.string.admin_action_failed, Toast.LENGTH_SHORT).show();
            if (daCapNhat) {
                dialog.dismiss();
                lamMoiToanBoDuLieuAdmin();
            }
        }));
        dialog.show();
    }

    private void xuLyBatTatTrangThaiNguoiDung(NguoiDung user) {
        long idNguoiDungHienTai = sessionManager.layIdNguoiDungHienTai();
        boolean trangThaiMoi = !user.dangHoatDong();
        if (user.layId() == idNguoiDungHienTai && !trangThaiMoi) {
            Toast.makeText(this, R.string.admin_self_lock_blocked, Toast.LENGTH_SHORT).show();
            return;
        }
        boolean daCapNhat = databaseHelper.capNhatTrangThaiHoatDongNguoiDung(user.layId(), trangThaiMoi);
        Toast.makeText(this, daCapNhat ? R.string.admin_user_active_update_success : R.string.admin_action_failed, Toast.LENGTH_SHORT).show();
        if (daCapNhat) {
            lamMoiToanBoDuLieuAdmin();
        }
    }

    private void dieuHuongSaiVaiTro() {
        Intent intent;
        if (sessionManager.daDangNhap()) {
            intent = DieuHuongVaiTroHelper.taoIntentTheoVaiTro(this, sessionManager.layVaiTroHienTai());
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
