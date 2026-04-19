package com.example.quanlynhahang;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.ContextCompat;

import com.example.quanlynhahang.adapter.BoDieuHopMonQuanTri;
import com.example.quanlynhahang.adapter.BoDieuHopNguoiDungQuanTri;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.helper.DieuHuongVaiTroHelper;
import com.example.quanlynhahang.model.DonHang;
import com.example.quanlynhahang.model.ThongKeTongQuanQuanTri;
import com.example.quanlynhahang.model.NguoiDung;
import com.example.quanlynhahang.model.VaiTroNguoiDung;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class QuanTriActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;

    private MaterialButton btnTabOverview;
    private MaterialButton btnTabDishes;
    private MaterialButton btnTabUsers;
    private View drawerLayoutContainer;
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
    private TextView tvCurrentDate;
    private TextView tvRestaurantName;
    private TextView tvSoLuongTatDonHang;
    private LinearLayout layoutDonGanDay;
    private TextView tvDonGanDayRong;
    private EditText etTimMon;
    private Spinner spinnerLocVaiTro;
    private TextView tvMonRong;
    private TextView tvNguoiDungRong;

    private BoDieuHopMonQuanTri boDieuHopMon;
    private BoDieuHopNguoiDungQuanTri boDieuHopNguoiDung;

    private String tuKhoaMonHienTai = "";
    private VaiTroNguoiDung vaiTroLocHienTai = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quan_tri);

        sessionManager = new SessionManager(this);
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.chuanBiCoSoDuLieu();
        sessionManager.chuyenDuLieuDangNhapCuNeuCan(databaseHelper);
        sessionManager.damBaoVaiTroSession(databaseHelper);

        if (!xacThucPhienQuanTri(true)) {
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

    @Override
    protected void onResume() {
        super.onResume();
        if (!xacThucPhienQuanTri(false)) {
            return;
        }
        lamMoiToanBoDuLieuAdmin();
    }

    private void khoiTaoView() {
        drawerLayoutContainer = findViewById(R.id.drawerAdminRoot);
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
        tvCurrentDate = findViewById(R.id.tvAdminCurrentDate);
        tvRestaurantName = findViewById(R.id.tvAdminRestaurantName);
        tvSoLuongTatDonHang = findViewById(R.id.tvAdminOrdersShortcutBadge);
        layoutDonGanDay = findViewById(R.id.layoutAdminRecentOrders);
        tvDonGanDayRong = findViewById(R.id.tvAdminRecentOrdersEmpty);
        etTimMon = findViewById(R.id.etAdminDishSearch);
        spinnerLocVaiTro = findViewById(R.id.spinnerAdminLocVaiTro);
        tvMonRong = findViewById(R.id.tvAdminDishesEmpty);
        tvNguoiDungRong = findViewById(R.id.tvAdminUsersEmpty);
    }

    private void thietLapRecyclerView() {
        RecyclerView rvDishes = findViewById(R.id.rvAdminDishes);
        RecyclerView rvUsers = findViewById(R.id.rvAdminUsers);
        rvDishes.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.setLayoutManager(new LinearLayoutManager(this));

        boDieuHopMon = new BoDieuHopMonQuanTri(new BoDieuHopMonQuanTri.HanhDongListener() {
            @Override
            public void khiSua(DatabaseHelper.DishRecord banGhiMon) {
                hienDialogMonAn(banGhiMon);
            }

            @Override
            public void khiXoa(DatabaseHelper.DishRecord banGhiMon) {
                xacNhanXoaMon(banGhiMon);
            }

            @Override
            public void khiBatTatTrangThaiPhucVu(DatabaseHelper.DishRecord banGhiMon) {
                boolean daCapNhat = databaseHelper.capNhatTrangThaiPhucVuMon(banGhiMon.layId(), !banGhiMon.layMonAn().laConPhucVu());
                Toast.makeText(QuanTriActivity.this, daCapNhat ? R.string.admin_dish_availability_success : R.string.admin_action_failed, Toast.LENGTH_SHORT).show();
                if (daCapNhat) {
                    lamMoiToanBoDuLieuAdmin();
                }
            }
        });

        boDieuHopNguoiDung = new BoDieuHopNguoiDungQuanTri(new BoDieuHopNguoiDungQuanTri.HanhDongListener() {
            @Override
            public void khiDoiVaiTro(NguoiDung nguoiDung) {
                hienDialogDoiVaiTro(nguoiDung);
            }

            @Override
            public void khiBatTatTrangThaiHoatDong(NguoiDung nguoiDung) {
                xuLyBatTatTrangThaiNguoiDung(nguoiDung);
            }
        });

        rvDishes.setAdapter(boDieuHopMon);
        rvUsers.setAdapter(boDieuHopNguoiDung);
    }

    private void thietLapTab() {
        btnTabOverview.setOnClickListener(v -> {
            hienTabTongQuan();
            dongSidebarNeuCan();
        });
        btnTabDishes.setOnClickListener(v -> {
            hienTabMonAn();
            dongSidebarNeuCan();
        });
        btnTabUsers.setOnClickListener(v -> {
            hienTabTaiKhoan();
            dongSidebarNeuCan();
        });
    }

    private void thietLapTimKiemMon() {
        etTimMon.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tuKhoaMonHienTai = s == null ? "" : s.toString().trim();
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
        ArrayAdapter<String> boDieuHopLuaChon = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, labels);
        boDieuHopLuaChon.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocVaiTro.setAdapter(boDieuHopLuaChon);
        spinnerLocVaiTro.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    vaiTroLocHienTai = VaiTroNguoiDung.KHACH_HANG;
                } else if (position == 2) {
                    vaiTroLocHienTai = VaiTroNguoiDung.NHAN_VIEN;
                } else if (position == 3) {
                    vaiTroLocHienTai = VaiTroNguoiDung.ADMIN;
                } else {
                    vaiTroLocHienTai = null;
                }
                taiDanhSachNguoiDung();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                vaiTroLocHienTai = null;
                taiDanhSachNguoiDung();
            }
        });
    }

    private void thietLapHanhDong() {
        ImageView btnOpenSidebar = findViewById(R.id.btnAdminOpenSidebar);
        if (btnOpenSidebar != null) {
            btnOpenSidebar.setOnClickListener(v -> moSidebar());
        }
        findViewById(R.id.btnAdminOrdersShortcut).setOnClickListener(v -> moManNhanVien(NhanVienActivity.TAB_DON_HANG));
        findViewById(R.id.btnAdminReservationsShortcut).setOnClickListener(v -> moManNhanVien(NhanVienActivity.TAB_DAT_BAN));
        findViewById(R.id.btnAdminSettingsShortcut).setOnClickListener(v -> hienThiCaiDatQuanTri());
        findViewById(R.id.btnAdminAddDish).setOnClickListener(v -> hienDialogMonAn(null));
        findViewById(R.id.cardAdminAddDishShortcut).setOnClickListener(v -> hienDialogMonAn(null));
        findViewById(R.id.cardAdminReservationShortcut).setOnClickListener(v -> moManNhanVien(NhanVienActivity.TAB_DAT_BAN));
    }

    private void moSidebar() {
        if (drawerLayoutContainer instanceof DrawerLayout) {
            ((DrawerLayout) drawerLayoutContainer).openDrawer(GravityCompat.START);
        }
    }

    private void dongSidebarNeuCan() {
        if (drawerLayoutContainer instanceof DrawerLayout) {
            DrawerLayout drawerLayout = (DrawerLayout) drawerLayoutContainer;
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        }
    }

    private void moManNhanVien(String tabMucTieu) {
        Intent intent = new Intent(this, NhanVienActivity.class);
        intent.putExtra(NhanVienActivity.EXTRA_TAB_MUC_TIEU, tabMucTieu);
        startActivity(intent);
    }

    private void hienThiCaiDatQuanTri() {
        String[] luaChon = new String[]{
                getString(R.string.admin_settings_customer_view),
                getString(R.string.admin_settings_employee_orders),
                getString(R.string.account_logout)
        };
        new AlertDialog.Builder(this)
                .setTitle(R.string.admin_settings_title)
                .setItems(luaChon, (dialog, which) -> {
                    if (which == 0) {
                        moGiaoDienKhachHang();
                    } else if (which == 1) {
                        moManNhanVien(NhanVienActivity.TAB_DON_HANG);
                    } else if (which == 2) {
                        thucHienDangXuat();
                    }
                })
                .setNegativeButton(R.string.dialog_close, null)
                .show();
    }

    private void moGiaoDienKhachHang() {
        Intent intent = new Intent(this, CustomerLauncherActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void thucHienDangXuat() {
        sessionManager.xoaPhienDangNhap();
        Intent intent = new Intent(this, CustomerLauncherActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private boolean xacThucPhienQuanTri(boolean hienToast) {
        if (!sessionManager.damBaoNguoiDungConHoatDong(databaseHelper)) {
            if (hienToast) {
                Toast.makeText(this, getString(R.string.session_invalid), Toast.LENGTH_SHORT).show();
            }
            dieuHuongSaiVaiTro();
            return false;
        }

        if (!sessionManager.daDangNhap() || sessionManager.layVaiTroSessionHopLe() != VaiTroNguoiDung.ADMIN) {
            if (hienToast) {
                Toast.makeText(this, getString(R.string.role_guard_admin_denied), Toast.LENGTH_SHORT).show();
            }
            dieuHuongSaiVaiTro();
            return false;
        }
        return true;
    }

    private void thietLapDangXuat() {
        MaterialButton btnLogout = findViewById(R.id.btnAdminLogout);
        btnLogout.setOnClickListener(v -> {
            dongSidebarNeuCan();
            thucHienDangXuat();
        });
    }

    private void lamMoiToanBoDuLieuAdmin() {
        ThongKeTongQuanQuanTri thongKe = databaseHelper.layThongKeTongQuanQuanTri();
        capNhatHeaderAdmin(thongKe);
        taiThongKeTongQuan(thongKe);
        taiDonHangGanDay();
        taiDanhSachMon();
        taiDanhSachNguoiDung();
    }

    private void capNhatHeaderAdmin(ThongKeTongQuanQuanTri thongKe) {
        if (tvCurrentDate != null) {
            String ngayHienTai = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
            tvCurrentDate.setText(getString(R.string.admin_current_date_format, ngayHienTai));
        }
        if (tvRestaurantName != null) {
            tvRestaurantName.setText(R.string.admin_restaurant_name);
        }
        capNhatBadgeDonHang(thongKe.laySoDonHangChoXacNhan());
    }

    private void taiThongKeTongQuan(ThongKeTongQuanQuanTri thongKe) {
        tvTotalUsersCount.setText(String.valueOf(thongKe.layTongNguoiDung()));
        tvTotalDishesCount.setText(String.valueOf(thongKe.layTongMonAn()));
        tvTotalDonHangsCount.setText(String.valueOf(thongKe.layTongDonHang()));
        tvPendingDonHangsCount.setText(String.valueOf(thongKe.laySoDonHangChoXacNhan()));
        tvPendingReservationsCount.setText(String.valueOf(thongKe.laySoDatBanChoDuyet()));
        tvProcessingRequestsCount.setText(String.valueOf(thongKe.laySoYeuCauDangXuLy()));
        tvCustomerCount.setText(String.valueOf(thongKe.laySoKhachHang()));
        tvEmployeeCount.setText(String.valueOf(thongKe.laySoNhanVien()));
        tvAdminCount.setText(String.valueOf(thongKe.laySoQuanTriVien()));
    }

    private void capNhatBadgeDonHang(int soLuongChoXacNhan) {
        if (tvSoLuongTatDonHang == null) {
            return;
        }
        if (soLuongChoXacNhan <= 0) {
            tvSoLuongTatDonHang.setVisibility(View.GONE);
            tvSoLuongTatDonHang.setText("");
            return;
        }
        tvSoLuongTatDonHang.setVisibility(View.VISIBLE);
        if (soLuongChoXacNhan > 99) {
            tvSoLuongTatDonHang.setText(R.string.admin_orders_shortcut_badge_overflow);
            return;
        }
        tvSoLuongTatDonHang.setText(String.valueOf(soLuongChoXacNhan));
    }

    private void taiDonHangGanDay() {
        if (layoutDonGanDay == null || tvDonGanDayRong == null) {
            return;
        }
        layoutDonGanDay.removeAllViews();
        List<DonHang> danhSachDon = layBaDonHangGanDay();
        if (danhSachDon.isEmpty()) {
            tvDonGanDayRong.setVisibility(View.VISIBLE);
            return;
        }
        tvDonGanDayRong.setVisibility(View.GONE);
        for (int i = 0; i < danhSachDon.size(); i++) {
            layoutDonGanDay.addView(taoTheDonGanDay(danhSachDon.get(i), i > 0));
        }
    }

    private List<DonHang> layBaDonHangGanDay() {
        List<DonHang> tatCaDonHang = new ArrayList<>(databaseHelper.layTatCaDonHang());
        if (tatCaDonHang.size() <= 3) {
            return tatCaDonHang;
        }
        return new ArrayList<>(tatCaDonHang.subList(0, 3));
    }

    private View taoTheDonGanDay(DonHang donHang, boolean themKhoangCachTop) {
        LinearLayout wrapper = new LinearLayout(this);
        wrapper.setOrientation(LinearLayout.HORIZONTAL);
        wrapper.setGravity(android.view.Gravity.CENTER_VERTICAL);
        wrapper.setPadding(dp(20), dp(20), dp(20), dp(20));

        com.google.android.material.card.MaterialCardView cardView = new com.google.android.material.card.MaterialCardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        if (themKhoangCachTop) {
            cardParams.topMargin = dp(12);
        }
        cardView.setLayoutParams(cardParams);
        cardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.surface));
        cardView.setRadius(dp(22));
        cardView.setCardElevation(0f);

        View statusDot = new View(this);
        LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(dp(14), dp(14));
        statusDot.setLayoutParams(dotParams);
        statusDot.setBackgroundColor(ContextCompat.getColor(this, mauTrangThaiDon(donHang)));

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        contentParams.leftMargin = dp(14);
        content.setLayoutParams(contentParams);

        TextView title = new TextView(this);
        title.setText(taoTieuDeDonGanDay(donHang));
        title.setTextColor(ContextCompat.getColor(this, R.color.on_surface));
        title.setTextSize(16);
        title.setTypeface(title.getTypeface(), android.graphics.Typeface.BOLD);

        TextView subtitle = new TextView(this);
        subtitle.setText(taoPhuDeDonGanDay(donHang));
        subtitle.setTextColor(ContextCompat.getColor(this, R.color.on_surface_variant));
        subtitle.setTextSize(13);

        TextView amount = new TextView(this);
        amount.setText(rutGonGia(donHang.layTongTien()));
        amount.setTextColor(ContextCompat.getColor(this, R.color.admin_stat_highlight));
        amount.setTextSize(16);
        amount.setTypeface(amount.getTypeface(), android.graphics.Typeface.BOLD);

        content.addView(title);
        content.addView(subtitle);
        wrapper.addView(statusDot);
        wrapper.addView(content);
        wrapper.addView(amount);
        cardView.addView(wrapper);
        return cardView;
    }

    private String taoTieuDeDonGanDay(DonHang donHang) {
        String ban = donHang.coBanAn() ? donHang.laySoBan() : getString(R.string.admin_takeaway_label);
        String tenMon = donHang.layDanhSachMon().isEmpty() ? donHang.layMaDon() : donHang.layDanhSachMon().get(0).layMonAn().layTenMon();
        return getString(R.string.admin_recent_order_title_format, ban, tenMon);
    }

    private String taoPhuDeDonGanDay(DonHang donHang) {
        return getString(R.string.admin_recent_order_subtitle_format, nhanTrangThaiDon(donHang), donHang.layThoiGian());
    }

    private String nhanTrangThaiDon(DonHang donHang) {
        switch (donHang.layTrangThai()) {
            case DANG_CHUAN_BI:
                return getString(R.string.admin_status_preparing);
            case SAN_SANG_PHUC_VU:
                return getString(R.string.admin_status_ready_to_serve);
            case HOAN_THANH:
                return getString(R.string.admin_status_completed);
            case DA_HUY:
                return getString(R.string.admin_status_cancelled);
            case CHO_XAC_NHAN:
            default:
                return getString(R.string.admin_status_pending_confirmation);
        }
    }

    private int mauTrangThaiDon(DonHang donHang) {
        switch (donHang.layTrangThai()) {
            case HOAN_THANH:
                return R.color.admin_success;
            case DA_HUY:
                return R.color.error;
            case DANG_CHUAN_BI:
            case SAN_SANG_PHUC_VU:
                return R.color.admin_warning;
            case CHO_XAC_NHAN:
            default:
                return R.color.admin_stat_highlight;
        }
    }

    private String rutGonGia(String giaGoc) {
        if (giaGoc == null) {
            return getString(R.string.admin_price_zero);
        }
        String so = giaGoc.replace(".000 đ", "k").replace(" đ", "đ");
        return so;
    }

    private int dp(int value) {
        return Math.round(getResources().getDisplayMetrics().density * value);
    }

    private void taiDanhSachMon() {
        List<DatabaseHelper.DishRecord> danhSachMon = tuKhoaMonHienTai.isEmpty()
                ? databaseHelper.layTatCaMonAn()
                : databaseHelper.timKiemMonAn(tuKhoaMonHienTai);
        boDieuHopMon.capNhatDanhSach(danhSachMon);
        tvMonRong.setVisibility(danhSachMon.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void taiDanhSachNguoiDung() {
        List<NguoiDung> danhSachNguoiDung = vaiTroLocHienTai == null ? databaseHelper.layTatCaNguoiDung() : databaseHelper.layNguoiDungTheoVaiTro(vaiTroLocHienTai);
        boDieuHopNguoiDung.capNhatDanhSach(danhSachNguoiDung);
        tvNguoiDungRong.setVisibility(danhSachNguoiDung.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void hienTabTongQuan() {
        layoutOverview.setVisibility(View.VISIBLE);
        layoutDishes.setVisibility(View.GONE);
        layoutUsers.setVisibility(View.GONE);
        capNhatTrangThaiTab(btnTabOverview, true, R.drawable.ic_menu_24);
        capNhatTrangThaiTab(btnTabDishes, false, R.drawable.ic_restaurant_24);
        capNhatTrangThaiTab(btnTabUsers, false, R.drawable.ic_account_24);
    }

    private void hienTabMonAn() {
        layoutOverview.setVisibility(View.GONE);
        layoutDishes.setVisibility(View.VISIBLE);
        layoutUsers.setVisibility(View.GONE);
        capNhatTrangThaiTab(btnTabOverview, false, R.drawable.ic_menu_24);
        capNhatTrangThaiTab(btnTabDishes, true, R.drawable.ic_restaurant_24);
        capNhatTrangThaiTab(btnTabUsers, false, R.drawable.ic_account_24);
    }

    private void hienTabTaiKhoan() {
        layoutOverview.setVisibility(View.GONE);
        layoutDishes.setVisibility(View.GONE);
        layoutUsers.setVisibility(View.VISIBLE);
        capNhatTrangThaiTab(btnTabOverview, false, R.drawable.ic_menu_24);
        capNhatTrangThaiTab(btnTabDishes, false, R.drawable.ic_restaurant_24);
        capNhatTrangThaiTab(btnTabUsers, true, R.drawable.ic_account_24);
    }

    private void capNhatTrangThaiTab(MaterialButton button, boolean duocChon, int iconRes) {
        if (button == null) {
            return;
        }
        button.setIconResource(iconRes);
        button.setCheckable(false);
        button.setClickable(!duocChon);
        button.setEnabled(true);
        button.setBackgroundResource(duocChon ? R.drawable.bg_button_orange : android.R.color.transparent);
        int textColor = ContextCompat.getColor(this, duocChon ? R.color.white : R.color.admin_sidebar_text);
        button.setTextColor(textColor);
        button.setIconTint(ColorStateList.valueOf(textColor));
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
            if (tachGiaTien(giaBan) <= 0) {
                Toast.makeText(this, R.string.admin_dish_validation_price, Toast.LENGTH_SHORT).show();
                return;
            }
            if (moTa.length() < 10) {
                Toast.makeText(this, R.string.admin_dish_validation_description, Toast.LENGTH_SHORT).show();
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
        RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroupAdminChonVaiTro);
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

    private long tachGiaTien(String chuoiGia) {
        if (chuoiGia == null || chuoiGia.trim().isEmpty()) {
            return 0L;
        }
        String chuoiDaLamSach = chuoiGia.replaceAll("[^0-9]", "");
        if (chuoiDaLamSach.isEmpty()) {
            return 0L;
        }
        try {
            return Long.parseLong(chuoiDaLamSach);
        } catch (NumberFormatException ex) {
            return 0L;
        }
    }

    private void dieuHuongSaiVaiTro() {
        Intent intent = DieuHuongVaiTroHelper.taoIntentSaiVaiTro(this, sessionManager, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
