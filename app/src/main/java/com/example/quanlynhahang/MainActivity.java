package com.example.quanlynhahang;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.quanlynhahang.data.CartManager;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.helper.DieuHuongVaiTroHelper;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_CHO_PHEP_XEM_GIAO_DIEN_KHACH = "extra_allow_customer_preview";
    private static final String TAG = "MainActivity";
    public static final String EXTRA_MO_TAB_TRUNG_TAM_HOAT_DONG = "extra_open_activity_hub_tab";
    private static final int SO_LUONG_BADGE_TOI_DA = 99;
    private static final String TAG_TRANG_CHU = "home";
    private static final String TAG_MENU = "menu";
    private static final String TAG_TRUNG_TAM_HOAT_DONG = "activity_hub";
    private static final String TAG_TAI_KHOAN = "account";
    private static final String KEY_TAB_HOAT_DONG_CHO = "pending_activity_tab";
    private static final String KEY_CO_DIEU_HUONG_MENU_CHO = "has_pending_menu_navigation";

    private TextView tvCartBadge;
    private TextView tvGreeting;
    private BottomNavigationView bottomNavigationView;

    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;

    private int tabTrungTamHoatDongCho = TrungTamHoatDongFragment.TAB_ORDERS;
    private String tenDanhMucMenuCho;
    private boolean moTimKiemMenuCho;
    private String tuKhoaMenuCho;
    private boolean coDieuHuongMenuCho;
    private boolean choPhepXemGiaoDienKhach;

    private final CartManager.CartListener cartListener = this::capNhatBadgeGioHang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        Log.i(TAG, "Bắt đầu khởi tạo SessionManager và DatabaseHelper.");
        sessionManager = new SessionManager(this);
        databaseHelper = new DatabaseHelper(this);
        Log.i(TAG, "Bắt đầu mở cơ sở dữ liệu và chạy migration phiên đăng nhập cũ.");
        databaseHelper.chuanBiCoSoDuLieu();
        sessionManager.migrateLegacyAuthIfNeeded(databaseHelper);
        sessionManager.damBaoVaiTroSession(databaseHelper);
        choPhepXemGiaoDienKhach = getIntent().getBooleanExtra(EXTRA_CHO_PHEP_XEM_GIAO_DIEN_KHACH, false);
        if (sessionManager.daDangNhap() && !sessionManager.laKhachHang() && !choPhepXemGiaoDienKhach) {
            startActivity(DieuHuongVaiTroHelper.taoIntentTheoVaiTro(this, sessionManager.layVaiTroHienTai())
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }
        Log.i(TAG, "Hoàn tất chuẩn bị cơ sở dữ liệu và migration phiên đăng nhập.");

        tvCartBadge = findViewById(R.id.tvCartBadge);
        tvGreeting = findViewById(R.id.tvGreeting);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        if (savedInstanceState != null) {
            tenDanhMucMenuCho = savedInstanceState.getString(ThucDonFragment.ARG_TEN_DANH_MUC);
            moTimKiemMenuCho = savedInstanceState.getBoolean(ThucDonFragment.ARG_MO_TIM_KIEM, false);
            tuKhoaMenuCho = savedInstanceState.getString(ThucDonFragment.ARG_TU_KHOA_TIM_KIEM);
            coDieuHuongMenuCho = savedInstanceState.getBoolean(KEY_CO_DIEU_HUONG_MENU_CHO, false);
            tabTrungTamHoatDongCho = savedInstanceState.getInt(KEY_TAB_HOAT_DONG_CHO, TrungTamHoatDongFragment.TAB_ORDERS);
        } else {
            tabTrungTamHoatDongCho = getIntent().getIntExtra(
                    EXTRA_MO_TAB_TRUNG_TAM_HOAT_DONG,
                    TrungTamHoatDongFragment.TAB_ORDERS
            );
        }

        thietLapDieuHuongDuoi();
        thietLapHanhDongHeader();
        lamMoiTrangThaiHeader();

        if (savedInstanceState == null) {
            hienTrangChu();
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        CartManager.getInstance().themLangNghe(cartListener);
        lamMoiTrangThaiHeader();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!xacThucPhienKhachHang(true)) {
            return;
        }
        lamMoiTrangThaiHeader();
        capNhatTaiKhoanNeuCan();
    }

    @Override
    protected void onStop() {
        CartManager.getInstance().xoaLangNghe(cartListener);
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ThucDonFragment.ARG_TEN_DANH_MUC, tenDanhMucMenuCho);
        outState.putBoolean(ThucDonFragment.ARG_MO_TIM_KIEM, moTimKiemMenuCho);
        outState.putString(ThucDonFragment.ARG_TU_KHOA_TIM_KIEM, tuKhoaMenuCho);
        outState.putBoolean(KEY_CO_DIEU_HUONG_MENU_CHO, coDieuHuongMenuCho);
        outState.putInt(KEY_TAB_HOAT_DONG_CHO, tabTrungTamHoatDongCho);
    }

    private void thietLapDieuHuongDuoi() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == bottomNavigationView.getSelectedItemId()) {
                if (itemId == R.id.nav_menu) {
                    hienMenu();
                } else if (itemId == R.id.nav_orders) {
                    hienTrungTamHoatDong();
                } else if (itemId == R.id.nav_account) {
                    hienTaiKhoan();
                }
                return itemId != R.id.nav_cart;
            }

            if (itemId == R.id.nav_home) {
                hienTrangChu();
                return true;
            }
            if (itemId == R.id.nav_menu) {
                hienMenu();
                return true;
            }
            if (itemId == R.id.nav_orders) {
                hienTrungTamHoatDong();
                return true;
            }
            if (itemId == R.id.nav_cart) {
                moGioHang();
                return false;
            }
            if (itemId == R.id.nav_account) {
                hienTaiKhoan();
                return true;
            }
            return false;
        });
    }

    private void thietLapHanhDongHeader() {
        findViewById(R.id.layoutCartIcon).setOnClickListener(v -> moGioHang());
        View nutTimKiem = findViewById(R.id.layoutSearchAction);
        if (nutTimKiem != null) {
            nutTimKiem.setOnClickListener(v -> dieuHuongDenMenu(null, true, null));
        }

        View avatar = findViewById(R.id.layoutAvatarAction);
        if (avatar != null) {
            avatar.setOnClickListener(v -> {
                if (bottomNavigationView.getSelectedItemId() == R.id.nav_account) {
                    hienTaiKhoan();
                    return;
                }
                bottomNavigationView.setSelectedItemId(R.id.nav_account);
            });
        }
    }

    private void hienTrangChu() {
        hienFragment(timHoacTaoTrangChuFragment(), TAG_TRANG_CHU);
    }

    private void hienMenu() {
        ThucDonFragment fragment = timHoacTaoThucDonFragment();
        if (coDieuHuongMenuCho) {
            fragment.apDungTrangThaiDieuHuongTuTrangChu(tenDanhMucMenuCho, moTimKiemMenuCho, tuKhoaMenuCho);
        }
        hienFragment(fragment, TAG_MENU);
        coDieuHuongMenuCho = false;
    }

    private void hienTrungTamHoatDong() {
        TrungTamHoatDongFragment fragment = timTrungTamHoatDongFragment();
        if (fragment == null) {
            fragment = TrungTamHoatDongFragment.newInstance(tabTrungTamHoatDongCho);
        }
        hienFragment(fragment, TAG_TRUNG_TAM_HOAT_DONG);
        fragment.chonTab(tabTrungTamHoatDongCho);
        tabTrungTamHoatDongCho = TrungTamHoatDongFragment.TAB_ORDERS;
    }

    private void hienTaiKhoan() {
        TaiKhoanFragment fragment = timTaiKhoanFragment();
        if (fragment == null) {
            fragment = new TaiKhoanFragment();
        }
        hienFragment(fragment, TAG_TAI_KHOAN);
        getSupportFragmentManager().executePendingTransactions();
        fragment.khiTabTaiKhoanDuocChon();
    }

    private void hienFragment(Fragment fragment, String tag) {
        Fragment fragmentHienTai = getSupportFragmentManager().findFragmentById(R.id.mainFragmentContainer);
        if (fragmentHienTai == fragment || (fragmentHienTai != null && tag.equals(fragmentHienTai.getTag()))) {
            return;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFragmentContainer, fragment, tag)
                .commit();
    }

    private TrangChuFragment timHoacTaoTrangChuFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_TRANG_CHU);
        return fragment instanceof TrangChuFragment ? (TrangChuFragment) fragment : new TrangChuFragment();
    }

    private ThucDonFragment timHoacTaoThucDonFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_MENU);
        if (fragment instanceof ThucDonFragment) {
            return (ThucDonFragment) fragment;
        }
        return ThucDonFragment.newInstance(tenDanhMucMenuCho, moTimKiemMenuCho, tuKhoaMenuCho);
    }

    private TrungTamHoatDongFragment timTrungTamHoatDongFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_TRUNG_TAM_HOAT_DONG);
        return fragment instanceof TrungTamHoatDongFragment ? (TrungTamHoatDongFragment) fragment : null;
    }

    private TaiKhoanFragment timTaiKhoanFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_TAI_KHOAN);
        return fragment instanceof TaiKhoanFragment ? (TaiKhoanFragment) fragment : null;
    }

    public void dieuHuongDenMenu() {
        dieuHuongDenMenu(null, false, null);
    }

    public void dieuHuongDenMenu(@Nullable String tenDanhMuc, boolean moTimKiem) {
        dieuHuongDenMenu(tenDanhMuc, moTimKiem, null);
    }

    public void dieuHuongDenMenu(@Nullable String tenDanhMuc, boolean moTimKiem, @Nullable String tuKhoaTimKiem) {
        tenDanhMucMenuCho = TextUtils.isEmpty(tenDanhMuc) ? null : tenDanhMuc;
        moTimKiemMenuCho = moTimKiem;
        tuKhoaMenuCho = TextUtils.isEmpty(tuKhoaTimKiem) ? null : tuKhoaTimKiem;
        coDieuHuongMenuCho = true;
        if (bottomNavigationView.getSelectedItemId() == R.id.nav_menu) {
            hienMenu();
            return;
        }
        bottomNavigationView.setSelectedItemId(R.id.nav_menu);
    }

    public void moTrungTamHoatDong(int tab) {
        tabTrungTamHoatDongCho = tab;
        if (bottomNavigationView.getSelectedItemId() == R.id.nav_orders) {
            hienTrungTamHoatDong();
            return;
        }
        bottomNavigationView.setSelectedItemId(R.id.nav_orders);
    }

    public void moTrungTamTheoDoi() {
        moTrungTamHoatDong(TrungTamHoatDongFragment.TAB_ORDERS);
    }

    public void lamMoiTrangThaiHeader() {
        capNhatLoiChao();
        capNhatBadgeGioHang();
    }

    private boolean xacThucPhienKhachHang(boolean hienToast) {
        if (!sessionManager.daDangNhap()) {
            return true;
        }

        if (!sessionManager.damBaoNguoiDungConHoatDong(databaseHelper)) {
            if (hienToast) {
                android.widget.Toast.makeText(this, getString(R.string.session_invalid), android.widget.Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(this, CustomerLauncherActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return false;
        }

        if (!sessionManager.laKhachHang() && !choPhepXemGiaoDienKhach) {
            startActivity(DieuHuongVaiTroHelper.taoIntentTheoVaiTro(this, sessionManager.layVaiTroHienTai())
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return false;
        }
        return true;
    }

    private void capNhatTaiKhoanNeuCan() {
        TaiKhoanFragment taiKhoanFragment = timTaiKhoanFragment();
        if (taiKhoanFragment != null && taiKhoanFragment.isAdded()) {
            taiKhoanFragment.capNhatGiaoDienTrangThaiDangNhap();
        }
    }

    private void moGioHang() {
        startActivity(new Intent(this, GioHangActivity.class));
    }

    private void capNhatLoiChao() {
        if (tvGreeting == null) {
            return;
        }

        tvGreeting.setText(R.string.home_greeting);
    }

    private void capNhatBadgeGioHang() {
        int tongSoLuong = CartManager.getInstance().layTongSoLuong();
        String chuoiBadge = dinhDangSoLuongBadge(tongSoLuong);
        boolean hienBadge = chuoiBadge != null;

        if (tvCartBadge != null) {
            tvCartBadge.setVisibility(hienBadge ? View.VISIBLE : View.GONE);
            if (hienBadge) {
                tvCartBadge.setText(chuoiBadge);
            }
        }

        if (bottomNavigationView == null) {
            return;
        }

        if (!hienBadge) {
            bottomNavigationView.removeBadge(R.id.nav_cart);
            return;
        }

        BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.nav_cart);
        badgeDrawable.setVisible(true);
        badgeDrawable.setMaxCharacterCount(3);
        badgeDrawable.setNumber(tongSoLuong);
    }

    @Nullable
    private String dinhDangSoLuongBadge(int totalQuantity) {
        if (totalQuantity <= 0) {
            return null;
        }
        return totalQuantity > SO_LUONG_BADGE_TOI_DA
                ? getString(R.string.cart_badge_overflow)
                : String.valueOf(totalQuantity);
    }
}
