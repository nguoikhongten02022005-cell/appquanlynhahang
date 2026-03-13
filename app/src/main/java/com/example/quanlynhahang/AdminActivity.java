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
import com.example.quanlynhahang.model.AdminDashboardStats;
import com.example.quanlynhahang.model.User;
import com.example.quanlynhahang.model.UserRole;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

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
    private TextView tvTotalOrdersCount;
    private TextView tvPendingOrdersCount;
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
    private UserRole currentRoleFilter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        sessionManager = new SessionManager(this);
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.chuanBiCoSoDuLieu();
        sessionManager.migrateLegacyAuthIfNeeded(databaseHelper);
        sessionManager.damBaoVaiTroSession(databaseHelper);

        if (!sessionManager.isLoggedIn() || !sessionManager.laAdmin()) {
            Toast.makeText(this, getString(R.string.role_guard_admin_denied), Toast.LENGTH_SHORT).show();
            dieuHuongSaiVaiTro();
            return;
        }

        initViews();
        setupRecyclerViews();
        setupTabs();
        setupDishSearch();
        setupRoleFilter();
        setupActions();
        setupLogout();
        refreshAllAdminData();
        showOverviewTab();
    }

    private void initViews() {
        btnTabOverview = findViewById(R.id.btnAdminTabOverview);
        btnTabDishes = findViewById(R.id.btnAdminTabDishes);
        btnTabUsers = findViewById(R.id.btnAdminTabUsers);
        layoutOverview = findViewById(R.id.layoutAdminOverview);
        layoutDishes = findViewById(R.id.layoutAdminDishes);
        layoutUsers = findViewById(R.id.layoutAdminUsers);
        tvTotalUsersCount = findViewById(R.id.tvAdminTotalUsersCount);
        tvTotalDishesCount = findViewById(R.id.tvAdminTotalDishesCount);
        tvTotalOrdersCount = findViewById(R.id.tvAdminTotalOrdersCount);
        tvPendingOrdersCount = findViewById(R.id.tvAdminPendingOrdersCount);
        tvPendingReservationsCount = findViewById(R.id.tvAdminPendingReservationsCount);
        tvProcessingRequestsCount = findViewById(R.id.tvAdminProcessingRequestsCount);
        tvCustomerCount = findViewById(R.id.tvAdminCustomerCount);
        tvEmployeeCount = findViewById(R.id.tvAdminEmployeeCount);
        tvAdminCount = findViewById(R.id.tvAdminAdminCount);
        etDishSearch = findViewById(R.id.etAdminDishSearch);
        spinnerRoleFilter = findViewById(R.id.spinnerAdminUserRoleFilter);
        tvDishesEmpty = findViewById(R.id.tvAdminDishesEmpty);
        tvUsersEmpty = findViewById(R.id.tvAdminUsersEmpty);
    }

    private void setupRecyclerViews() {
        RecyclerView rvDishes = findViewById(R.id.rvAdminDishes);
        RecyclerView rvUsers = findViewById(R.id.rvAdminUsers);
        rvDishes.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.setLayoutManager(new LinearLayoutManager(this));

        dishAdapter = new AdminDishAdapter(new AdminDishAdapter.ActionListener() {
            @Override
            public void onEdit(DatabaseHelper.DishRecord dishRecord) {
                showDishDialog(dishRecord);
            }

            @Override
            public void onDelete(DatabaseHelper.DishRecord dishRecord) {
                confirmDeleteDish(dishRecord);
            }

            @Override
            public void onToggleAvailability(DatabaseHelper.DishRecord dishRecord) {
                boolean updated = databaseHelper.updateDishAvailability(dishRecord.getId(), !dishRecord.getDishItem().isConPhucVu());
                Toast.makeText(AdminActivity.this, updated ? R.string.admin_dish_availability_success : R.string.admin_action_failed, Toast.LENGTH_SHORT).show();
                if (updated) {
                    refreshAllAdminData();
                }
            }
        });

        userAdapter = new AdminUserAdapter(new AdminUserAdapter.ActionListener() {
            @Override
            public void onChangeRole(User user) {
                showChangeRoleDialog(user);
            }

            @Override
            public void onToggleActive(User user) {
                handleToggleUserActive(user);
            }
        });

        rvDishes.setAdapter(dishAdapter);
        rvUsers.setAdapter(userAdapter);
    }

    private void setupTabs() {
        btnTabOverview.setOnClickListener(v -> showOverviewTab());
        btnTabDishes.setOnClickListener(v -> showDishesTab());
        btnTabUsers.setOnClickListener(v -> showUsersTab());
    }

    private void setupDishSearch() {
        etDishSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentDishKeyword = s == null ? "" : s.toString().trim();
                loadDishes();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupRoleFilter() {
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
                    currentRoleFilter = UserRole.KHACH_HANG;
                } else if (position == 2) {
                    currentRoleFilter = UserRole.NHAN_VIEN;
                } else if (position == 3) {
                    currentRoleFilter = UserRole.ADMIN;
                } else {
                    currentRoleFilter = null;
                }
                loadUsers();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                currentRoleFilter = null;
                loadUsers();
            }
        });
    }

    private void setupActions() {
        findViewById(R.id.btnAdminAddDish).setOnClickListener(v -> showDishDialog(null));
    }

    private void setupLogout() {
        MaterialButton btnLogout = findViewById(R.id.btnAdminLogout);
        btnLogout.setOnClickListener(v -> {
            sessionManager.clearSession();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void refreshAllAdminData() {
        loadDashboardStats();
        loadDishes();
        loadUsers();
    }

    private void loadDashboardStats() {
        AdminDashboardStats stats = databaseHelper.getAdminDashboardStats();
        tvTotalUsersCount.setText(String.valueOf(stats.getTotalUsers()));
        tvTotalDishesCount.setText(String.valueOf(stats.getTotalDishes()));
        tvTotalOrdersCount.setText(String.valueOf(stats.getTotalOrders()));
        tvPendingOrdersCount.setText(String.valueOf(stats.getPendingOrders()));
        tvPendingReservationsCount.setText(String.valueOf(stats.getPendingReservations()));
        tvProcessingRequestsCount.setText(String.valueOf(stats.getProcessingServiceRequests()));
        tvCustomerCount.setText(String.valueOf(stats.getCustomerCount()));
        tvEmployeeCount.setText(String.valueOf(stats.getEmployeeCount()));
        tvAdminCount.setText(String.valueOf(stats.getAdminCount()));
    }

    private void loadDishes() {
        List<DatabaseHelper.DishRecord> dishes = currentDishKeyword.isEmpty()
                ? databaseHelper.getAllDishes()
                : databaseHelper.searchDishes(currentDishKeyword);
        dishAdapter.submitList(dishes);
        tvDishesEmpty.setVisibility(dishes.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void loadUsers() {
        List<User> users = currentRoleFilter == null ? databaseHelper.getAllUsers() : databaseHelper.getUsersByRole(currentRoleFilter);
        userAdapter.submitList(users);
        tvUsersEmpty.setVisibility(users.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showOverviewTab() {
        layoutOverview.setVisibility(View.VISIBLE);
        layoutDishes.setVisibility(View.GONE);
        layoutUsers.setVisibility(View.GONE);
        btnTabOverview.setEnabled(false);
        btnTabDishes.setEnabled(true);
        btnTabUsers.setEnabled(true);
    }

    private void showDishesTab() {
        layoutOverview.setVisibility(View.GONE);
        layoutDishes.setVisibility(View.VISIBLE);
        layoutUsers.setVisibility(View.GONE);
        btnTabOverview.setEnabled(true);
        btnTabDishes.setEnabled(false);
        btnTabUsers.setEnabled(true);
    }

    private void showUsersTab() {
        layoutOverview.setVisibility(View.GONE);
        layoutDishes.setVisibility(View.GONE);
        layoutUsers.setVisibility(View.VISIBLE);
        btnTabOverview.setEnabled(true);
        btnTabDishes.setEnabled(true);
        btnTabUsers.setEnabled(false);
    }

    private void showDishDialog(DatabaseHelper.DishRecord dishRecord) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_dish, null);
        EditText etName = dialogView.findViewById(R.id.etAdminDishName);
        EditText etPrice = dialogView.findViewById(R.id.etAdminDishPrice);
        EditText etCategory = dialogView.findViewById(R.id.etAdminDishCategory);
        EditText etDescription = dialogView.findViewById(R.id.etAdminDishDescription);
        EditText etImage = dialogView.findViewById(R.id.etAdminDishImage);
        EditText etScore = dialogView.findViewById(R.id.etAdminDishScore);
        CheckBox cbAvailable = dialogView.findViewById(R.id.cbAdminDishAvailable);

        if (dishRecord != null) {
            etName.setText(dishRecord.getDishItem().getTenMon());
            etPrice.setText(dishRecord.getDishItem().getGiaBan());
            etCategory.setText(dishRecord.getDishItem().getTenDanhMuc());
            etDescription.setText(dishRecord.getDescription());
            etImage.setText(dishRecord.getImageResName());
            etScore.setText(String.valueOf(dishRecord.getDishItem().getDiemDeXuat()));
            cbAvailable.setChecked(dishRecord.getDishItem().isConPhucVu());
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
            String name = etName.getText().toString().trim();
            String price = etPrice.getText().toString().trim();
            String category = etCategory.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String image = etImage.getText().toString().trim();
            String scoreRaw = etScore.getText().toString().trim();
            if (name.isEmpty() || price.isEmpty() || category.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, R.string.admin_dish_validation_required, Toast.LENGTH_SHORT).show();
                return;
            }
            int score;
            try {
                score = scoreRaw.isEmpty() ? 0 : Integer.parseInt(scoreRaw);
                if (score < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                Toast.makeText(this, R.string.admin_dish_validation_score, Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success;
            if (dishRecord == null) {
                success = databaseHelper.insertDishRecord(name, price, description, image, cbAvailable.isChecked(), category, score) > 0;
                Toast.makeText(this, success ? R.string.admin_dish_create_success : R.string.admin_action_failed, Toast.LENGTH_SHORT).show();
            } else {
                success = databaseHelper.updateDishRecord(dishRecord.getId(), name, price, description, image, cbAvailable.isChecked(), category, score);
                Toast.makeText(this, success ? R.string.admin_dish_update_success : R.string.admin_action_failed, Toast.LENGTH_SHORT).show();
            }
            if (success) {
                dialog.dismiss();
                refreshAllAdminData();
            }
        }));
        dialog.show();
    }

    private void confirmDeleteDish(DatabaseHelper.DishRecord dishRecord) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.admin_delete_confirm_title)
                .setMessage(R.string.admin_delete_confirm_message)
                .setNegativeButton(R.string.account_cancel_action, null)
                .setPositiveButton(R.string.admin_delete_dish, (dialog, which) -> {
                    boolean deleted = databaseHelper.deleteDishById(dishRecord.getId());
                    Toast.makeText(this, deleted ? R.string.admin_dish_delete_success : R.string.admin_action_failed, Toast.LENGTH_SHORT).show();
                    if (deleted) {
                        refreshAllAdminData();
                    }
                })
                .show();
    }

    private void showChangeRoleDialog(User user) {
        long currentUserId = sessionManager.getCurrentUserId();
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_change_user_role, null);
        RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroupAdminUserRole);
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
            UserRole selectedRole = radioGroup.getCheckedRadioButtonId() == R.id.radioRoleAdmin
                    ? UserRole.ADMIN
                    : radioGroup.getCheckedRadioButtonId() == R.id.radioRoleEmployee
                    ? UserRole.NHAN_VIEN
                    : UserRole.KHACH_HANG;
            if (user.getId() == currentUserId && selectedRole != UserRole.ADMIN) {
                Toast.makeText(this, R.string.admin_self_demote_blocked, Toast.LENGTH_SHORT).show();
                return;
            }
            boolean updated = databaseHelper.updateUserRole(user.getId(), selectedRole);
            Toast.makeText(this, updated ? R.string.admin_user_role_update_success : R.string.admin_action_failed, Toast.LENGTH_SHORT).show();
            if (updated) {
                dialog.dismiss();
                refreshAllAdminData();
            }
        }));
        dialog.show();
    }

    private void handleToggleUserActive(User user) {
        long currentUserId = sessionManager.getCurrentUserId();
        boolean newActive = !user.isActive();
        if (user.getId() == currentUserId && !newActive) {
            Toast.makeText(this, R.string.admin_self_lock_blocked, Toast.LENGTH_SHORT).show();
            return;
        }
        boolean updated = databaseHelper.updateUserActive(user.getId(), newActive);
        Toast.makeText(this, updated ? R.string.admin_user_active_update_success : R.string.admin_action_failed, Toast.LENGTH_SHORT).show();
        if (updated) {
            refreshAllAdminData();
        }
    }

    private void dieuHuongSaiVaiTro() {
        Intent intent;
        if (sessionManager.isLoggedIn()) {
            intent = DieuHuongVaiTroHelper.taoIntentTheoVaiTro(this, sessionManager.getVaiTroHienTai());
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
